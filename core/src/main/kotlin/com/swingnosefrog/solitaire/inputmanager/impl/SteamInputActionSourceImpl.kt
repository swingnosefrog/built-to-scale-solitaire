package com.swingnosefrog.solitaire.inputmanager.impl

import com.codedisaster.steamworks.SteamControllerActionSetHandle
import com.swingnosefrog.solitaire.inputmanager.SteamInputActionSource
import com.swingnosefrog.solitaire.steamworks.SteamInterfaces


class SteamInputActionSourceImpl(
    actions: List<InputActions>,
    steamInterfaces: SteamInterfaces,
) : SteamInputActionSource(actions, steamInterfaces) {

    private val actionSetGeneral: SteamControllerActionSetHandle

    init {
        val input = steamInterfaces.input
        actionSetGeneral = input.getActionSetHandle("actionSet_General")
    }

    override fun frameUpdate() {
        steamInterfaces.input.activateActionSet(allControllersHandle, actionSetGeneral)

        super.frameUpdate()
    }
}
