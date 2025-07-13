package com.swingnosefrog.solitaire

import paintbox.util.Version
import paintbox.util.WindowSize
import com.swingnosefrog.solitaire.soundsystem.AudioDeviceSettings
import java.io.File


object Solitaire {

    object CommandLineArguments {

        var logMissingLocalizations: Boolean = false
        var audioDeviceSettings: AudioDeviceSettings? = null
    }

    const val TITLE: String = "Built to Scale Solitaire"
    val VERSION: Version = Version(0, 1, 0, "dev_20250712b")
    val DEFAULT_SIZE: WindowSize = WindowSize(1280, 720)
    val MINIMUM_SIZE: WindowSize = WindowSize(1152, 648)

    const val DOT_DIRECTORY_NAME: String = ".built-to-scale-solitaire"
    val DOT_DIRECTORY: File by lazy {
        File(System.getProperty("user.home") + "/${DOT_DIRECTORY_NAME}/").apply {
            mkdirs()
        }
    }

    val isDevVersion: Boolean = VERSION.suffix.startsWith("dev")
    val isPrereleaseVersion: Boolean =
        listOf("beta", "alpha", "rc").any { VERSION.suffix.startsWith(it, ignoreCase = true) }
    val enableEarlyAccessMessage: Boolean = (isDevVersion || isPrereleaseVersion)

}
