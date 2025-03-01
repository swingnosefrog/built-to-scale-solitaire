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
    val VERSION: Version = Version(0, 1, 0, "dev_20250125a")
    val DEFAULT_SIZE: WindowSize = WindowSize(1280, 720)
    val MINIMUM_SIZE: WindowSize = WindowSize(1152, 648)

    var portableMode: Boolean = false

    const val DOT_DIRECTORY_NAME: String = ".built-to-scale-solitaire"
    val DOT_DIRECTORY: File by lazy {
        (if (portableMode) File("${DOT_DIRECTORY_NAME}/") else File(System.getProperty("user.home") + "/${DOT_DIRECTORY_NAME}/")).apply {
            mkdirs()
        }
    }
    val commonResolutions: List<WindowSize> = listOf(
        WindowSize(1152, 648),
        WindowSize(1280, 720),
        WindowSize(1366, 768),
        WindowSize(1600, 900),
        WindowSize(1760, 990),
        WindowSize(1920, 1080),
        WindowSize(2240, 1260),
        WindowSize(2560, 1440),
        WindowSize(3200, 1800),
        WindowSize(3840, 2160),
    ).sortedBy { it.width }

    val isDevVersion: Boolean = VERSION.suffix.startsWith("dev")
    val isPrereleaseVersion: Boolean =
        listOf("beta", "alpha", "rc").any { VERSION.suffix.startsWith(it, ignoreCase = true) }
    val enableEarlyAccessMessage: Boolean = (isDevVersion || isPrereleaseVersion)

}
