package com.swingnosefrog.solitaire.steamworks

import com.codedisaster.steamworks.SteamController
import com.codedisaster.steamworks.SteamControllerHandle
import com.codedisaster.steamworks.SteamNativeHandle


fun SteamController.getConnectedControllers(): List<SteamControllerHandle> {
    val handles = Array(SteamController.STEAM_CONTROLLER_MAX_COUNT) { SteamControllerHandle(0L) }
    this.getConnectedControllers(handles)
    return handles.filter { SteamNativeHandle.getNativeHandle(it) != 0L }
}