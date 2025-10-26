package com.swingnosefrog.solitaire.soundsystem.beads

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.AudioDevice
import com.badlogic.gdx.backends.lwjgl3.audio.OpenALAudioDevice
import com.badlogic.gdx.backends.lwjgl3.audio.OpenALLwjgl3Audio
import com.badlogic.gdx.utils.Disposable
import com.swingnosefrog.solitaire.soundsystem.AudioDeviceSettings
import net.beadsproject.beads.core.AudioContext
import net.beadsproject.beads.core.AudioIO
import net.beadsproject.beads.core.UGen
import paintbox.util.gdxutils.disposeQuietly
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread
import kotlin.math.roundToInt

/**
 * This is an implementation of [AudioIO] that sends audio data to a [AudioDevice].
 * Note there there is a rough warmup time of as much [latency][AudioDevice.getLatency] in samples
 * where there may be OpenAL buffer underflows, i.e. unpredictable timings. It is best to not immediately play any audio
 * until that very brief period has elapsed.
 *
 * Also note that [OpenALAudioDevice] has behaviour that doesn't follow the libGDX documentation for [AudioDevice]:
 *   - The buffer size is in *bytes* and not samples, with 4 bytes per sample
 */
class OpenALAudioIO(val audioDeviceSettings: AudioDeviceSettings) : AudioIO() {

    private inner class Lifecycle(val audioDevice: OpenALAudioDevice) : Disposable {

        val bufferSize: Int = audioDeviceSettings.bufferSize
        val bufferCount: Int = audioDeviceSettings.bufferCount
        val forceKill: AtomicBoolean = AtomicBoolean(false)

        private val renderedSecondsGetter: (() -> Float)?

        init {
            val clazz = audioDevice.javaClass
            renderedSecondsGetter = try {
                val field = clazz.getDeclaredField("renderedSeconds")
                field.isAccessible = true

                val getter = { field.getFloat(audioDevice) }
                getter() // Provoke initialization at least once
                getter
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        
        fun getRenderedSeconds(): Float {
            return renderedSecondsGetter?.invoke() ?: -1f
        }

        override fun dispose() {
            forceKill.set(true)
            audioDevice.dispose()
        }
    }

    private val threadPriority: Int = Thread.MAX_PRIORITY

    @Volatile
    private var audioThread: Thread? = null

    @Volatile
    private var lifecycleInstance: Lifecycle? = null

    private fun create(): Lifecycle? {
        if (this.lifecycleInstance != null) {
            return null
        }

        val ctx = getContext()
        val ioAudioFormat = ctx.audioFormat
        val newAudioDevice = OpenALAudioDevice(
            Gdx.audio as OpenALLwjgl3Audio,
            ioAudioFormat.sampleRate.roundToInt(),
            ioAudioFormat.outputs == 1,
            audioDeviceSettings.bufferSize.coerceAtLeast(AudioDeviceSettings.MINIMUM_BUFFER_SIZE),
            audioDeviceSettings.bufferCount.coerceAtLeast(AudioDeviceSettings.MINIMUM_BUFFER_COUNT)
        )
        
        val lifecycle = Lifecycle(newAudioDevice)
        this.lifecycleInstance = lifecycle

        // FIXME Remove once libgdx issue #6977 is resolved
        newAudioDevice.writeSamples(
            FloatArray(2) { 0f },
            0,
            2
        ) // This grabs a new AL source on the same thread (GL thread hopefully)

        return lifecycle
    }

    /**
     * Update loop called from within audio thread (thread is created in [start] function).
     */
    private fun runRealTime() {
        val context = getContext()
        val ioAudioFormat = context.audioFormat
        val bufferSizeInFrames = context.bufferSize
        val numChannels = ioAudioFormat.outputs
        val sampleBufferSize = bufferSizeInFrames * numChannels
        val sampleBuffer = FloatArray(sampleBufferSize)

        val lifecycle = this.lifecycleInstance ?: return

        fun getFramesRenderedByContext(): Long = context.timeStep * bufferSizeInFrames
        var framesRendered = 0L
        while (context.isRunning && !lifecycle.forceKill.get()) {
            framesRendered += prepareLineBuffer(numChannels, sampleBuffer, bufferSizeInFrames)

            lifecycle.audioDevice.writeSamples(sampleBuffer, 0, sampleBufferSize)
        }
    }

    /**
     * Read audio from UGens and copy them into a buffer ready to write to Audio Line
     * @param interleavedSamples Interleaved samples as floats
     * @param bufferSizeInFrames The size of interleaved samples in frames
     */
    private fun prepareLineBuffer(numChannels: Int, interleavedSamples: FloatArray, bufferSizeInFrames: Int): Int {
        update() // This propagates update call to AudioContext from super-method
        
        val outUgen = context.out
        var frame = 0
        var counter = 0
        while (frame < bufferSizeInFrames) {
            for (ch in 0..<numChannels) {
                interleavedSamples[counter++] = outUgen.getValue(ch, frame)
            }
            ++frame
        }
        
        return frame
    }

    /**
     * Destroys and disposes of the active lifecycle.
     */
    private fun destroy(): Boolean {
        val lifecycle = this.lifecycleInstance
        lifecycle?.disposeQuietly()
        this.lifecycleInstance = null

        return true
    }

    @Synchronized
    override fun start(): Boolean {
        while (audioThread != null) { // Wait for audio thread to die if any
            this.lifecycleInstance?.forceKill?.set(true)
            Thread.sleep(10L)
        }

        create()
        audioThread = thread(start = true, isDaemon = true, name = "OpenALAudioIO", priority = threadPriority) {
            runRealTime()
            destroy()
            audioThread = null
        }
        return true
    }

    override fun stop(): Boolean {
        super.stop()

        this.lifecycleInstance?.forceKill?.set(true)
        while (audioThread != null) {
            Thread.sleep(10L)
        }
        return true
    }

    override fun getAudioInput(channels: IntArray): UGen {
        // NO-OP
        return NoOpAudioInput(context, channels.size)
    }

    private class NoOpAudioInput(context: AudioContext, outs: Int) : UGen(context, outs) {
        init {
            outputInitializationRegime = OutputInitializationRegime.ZERO
            pause(true)
        }

        override fun calculateBuffer() {
        }
    }
}