package com.swingnosefrog.solitaire.soundsystem

import com.swingnosefrog.solitaire.soundsystem.beads.OpenALAudioIO
import net.beadsproject.beads.core.AudioIO
import paintbox.binding.ReadOnlyVar
import paintbox.binding.Var


sealed class RealtimeOutput {

    class OpenAL(val audioDeviceSettings: AudioDeviceSettings) : RealtimeOutput() {

        override fun getName(): ReadOnlyVar<String> {
            return Var("OpenAL{$audioDeviceSettings}")
        }

        override fun createAudioIO(): AudioIO {
            return OpenALAudioIO(audioDeviceSettings)
        }
    }

    abstract fun getName(): ReadOnlyVar<String>

    abstract fun createAudioIO(): AudioIO
}
