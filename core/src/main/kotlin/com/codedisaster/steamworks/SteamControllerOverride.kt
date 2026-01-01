package com.codedisaster.steamworks

import paintbox.Paintbox
import kotlin.enums.EnumEntries

class SteamControllerOverride : SteamController() {

    private val allActionOrigins: EnumEntries<ActionOrigin> = ActionOrigin.entries
    
    private val actionOriginsTmp: IntArray = IntArray(STEAM_CONTROLLER_MAX_ORIGINS)
    
    private val unknownActionOrigins: MutableSet<Int> = mutableSetOf()
    
    private fun getActionOriginOrNone(originValue: Int): ActionOrigin {
        val lookedUp = allActionOrigins.getOrNull(originValue)
        if (lookedUp == null && originValue !in unknownActionOrigins) {
            unknownActionOrigins.add(originValue)
            Paintbox.LOGGER.warn("Unknown action origin returned by Steam: '$originValue'", tag = "SteamControllerOverride")
        }
        return lookedUp ?: ActionOrigin.None
    }
    
    override fun getDigitalActionOrigins(
        controller: SteamControllerHandle,
        actionSet: SteamControllerActionSetHandle,
        digitalAction: SteamControllerDigitalActionHandle,
        originsOut: Array<ActionOrigin>,
    ): Int {
        require(originsOut.size >= STEAM_CONTROLLER_MAX_ORIGINS) { "Array size must be at least STEAM_CONTROLLER_MAX_ORIGINS" }

        val count = SteamControllerNative.getDigitalActionOrigins(
            controller.handle,
            actionSet.handle, digitalAction.handle, actionOriginsTmp
        )

        for (i in 0..<count) {
            originsOut[i] = getActionOriginOrNone(actionOriginsTmp[i])
        }

        return count
    }
}