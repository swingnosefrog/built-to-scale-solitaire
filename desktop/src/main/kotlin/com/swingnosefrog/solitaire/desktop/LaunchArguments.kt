package com.swingnosefrog.solitaire.desktop

import com.beust.jcommander.Parameter
import com.swingnosefrog.solitaire.ILaunchArguments
import paintbox.desktop.PaintboxArguments
import com.swingnosefrog.solitaire.soundsystem.AudioDeviceSettings

class LaunchArguments : PaintboxArguments(), ILaunchArguments {

    @Parameter(
        names = ["--log-missing-localizations"],
        description = "Logs any missing localizations. Other locales are checked against the default properties file."
    )
    override var logMissingLocalizations: Boolean = false

    @Parameter(
        names = ["--audio-device-buffer-size"],
        description = "Overrides the AudioDevice buffer size when using the OpenAL sound system. Should be a power of two and at least ${AudioDeviceSettings.MINIMUM_BUFFER_SIZE}. On Windows, defaults to ${AudioDeviceSettings.DEFAULT_BUFFER_SIZE_WINDOWS}; ${AudioDeviceSettings.DEFAULT_BUFFER_SIZE} on other platforms."
    )
    override var audioDeviceBufferSize: Int? = null

    @Parameter(
        names = ["--audio-device-buffer-count"],
        description = "Overrides the AudioDevice buffer count when using the OpenAL sound system. Should be at least ${AudioDeviceSettings.MINIMUM_BUFFER_COUNT}. On Windows, defaults to ${AudioDeviceSettings.DEFAULT_BUFFER_COUNT_WINDOWS}; ${AudioDeviceSettings.DEFAULT_BUFFER_COUNT} on other platforms."
    )
    override var audioDeviceBufferCount: Int? = null

}