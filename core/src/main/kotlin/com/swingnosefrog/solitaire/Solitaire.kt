package com.swingnosefrog.solitaire

import com.swingnosefrog.solitaire.soundsystem.AudioDeviceSettings
import paintbox.util.Version
import java.io.File


object Solitaire {

    object CommandLineArguments {

        var logMissingLocalizations: Boolean = false
        var audioDeviceSettings: AudioDeviceSettings? = null

    }

    const val TITLE: String = "Built to Scale Solitaire"
    val VERSION: Version = Version(1, 0, 3, "20260103a")

    const val DOT_DIRECTORY_NAME: String = ".built-to-scale-solitaire"
    val DOT_DIRECTORY: File by lazy {
        File(System.getProperty("user.home")).resolve(DOT_DIRECTORY_NAME).apply {
            mkdirs()
        }
    }

    val isDevVersion: Boolean = VERSION.suffix.startsWith("dev")
    val isPrereleaseVersion: Boolean =
        listOf("beta", "alpha", "rc").any { VERSION.suffix.startsWith(it, ignoreCase = true) }
    val isNonProductionVersion: Boolean = (isDevVersion || isPrereleaseVersion)

}
