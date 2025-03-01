package com.swingnosefrog.solitaire

interface ILaunchArguments {

    val logMissingLocalizations: Boolean

    val audioDeviceBufferSize: Int?

    val audioDeviceBufferCount: Int?
}