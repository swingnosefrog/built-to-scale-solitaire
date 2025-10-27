package com.swingnosefrog.solitaire.steamworks

import com.codedisaster.steamworks.SteamController
import com.codedisaster.steamworks.SteamControllerHandle


private val controllerHandleArray: Array<SteamControllerHandle?> =
    arrayOfNulls(SteamController.STEAM_CONTROLLER_MAX_COUNT)

fun SteamController.getConnectedControllers(): List<SteamControllerHandle> {
    val handles = controllerHandleArray
    
    handles.fill(null)
    val num = this.getConnectedControllers(handles)

    return handles.slice(0 until num).filterNotNull()
}