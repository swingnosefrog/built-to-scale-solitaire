package com.swingnosefrog.solitaire.soundsystem.beads

import com.swingnosefrog.solitaire.soundsystem.sample.DelayableSamplePlayer
import com.swingnosefrog.solitaire.soundsystem.sample.MusicSample
import com.swingnosefrog.solitaire.soundsystem.sample.MusicSamplePlayer
import com.swingnosefrog.solitaire.soundsystem.sample.PlayerLike
import com.swingnosefrog.solitaire.soundsystem.sample.SamplePlayerWrapper
import net.beadsproject.beads.core.AudioContext
import net.beadsproject.beads.data.Sample
import net.beadsproject.beads.ugens.SamplePlayer


abstract class BeadsAudio(val channels: Int, val sampleRate: Float) {

    abstract fun createPlayer(context: AudioContext): PlayerLike

}

/**
 * An implementation of [BeadsAudio] that uses the Beads [Sample] as the data source.
 */
class BeadsSound(val sample: Sample) : BeadsAudio(sample.numChannels, sample.sampleRate) {

    override fun createPlayer(context: AudioContext): SamplePlayerWrapper {
        return SamplePlayerWrapper(DelayableSamplePlayer(context, sample))
    }

}

/**
 * An implementation of [BeadsAudio] that uses [MusicSample] as the data source.
 */
class BeadsMusic(val musicSample: MusicSample) : BeadsAudio(musicSample.nChannels, musicSample.sampleRate) {

    override fun createPlayer(context: AudioContext): MusicSamplePlayer {
        return MusicSamplePlayer(this.musicSample, context)
    }

}
