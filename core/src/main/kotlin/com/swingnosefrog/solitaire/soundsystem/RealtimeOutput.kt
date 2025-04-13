package com.swingnosefrog.solitaire.soundsystem

import com.swingnosefrog.solitaire.soundsystem.beads.OpenALAudioIO
import net.beadsproject.beads.core.AudioIO
import paintbox.binding.ReadOnlyVar
import paintbox.binding.Var
import paintbox.binding.toConstVar


sealed class RealtimeOutput {

    class OpenAL(val audioDeviceSettings: AudioDeviceSettings) : RealtimeOutput() {

        override fun getName(): ReadOnlyVar<String> {
            return "OpenAL{$audioDeviceSettings}".toConstVar()
        }

        override fun createAudioIO(): AudioIO {
            return OpenALAudioIO(audioDeviceSettings)
        }
    }

    abstract fun getName(): ReadOnlyVar<String>

    abstract fun createAudioIO(): AudioIO
}
