package com.swingnosefrog.solitaire.inputmanager

import com.codedisaster.steamworks.SteamController
import com.codedisaster.steamworks.SteamControllerDigitalActionData
import com.codedisaster.steamworks.SteamControllerDigitalActionHandle
import com.codedisaster.steamworks.SteamControllerHandle
import com.swingnosefrog.solitaire.steamworks.SteamInterfaces
import com.swingnosefrog.solitaire.steamworks.getConnectedControllers

open class SteamInputActionSource(
    actions: List<IInputAction>,
    protected val steamInterfaces: SteamInterfaces
) : ActionSource(actions) {
    
    private val digitalActionDataObj: SteamControllerDigitalActionData = SteamControllerDigitalActionData()
            
    protected val allControllersHandle: SteamControllerHandle =
        SteamControllerHandle(SteamController.STEAM_CONTROLLER_HANDLE_ALL_CONTROLLERS)
    protected val firstControllerHandle: SteamControllerHandle = 
        steamInterfaces.input.getConnectedControllers().firstOrNull() ?: SteamControllerHandle(0L)

    protected val allDigitalActions: List<IDigitalInputAction> = actions.filterIsInstance<IDigitalInputAction>()
    protected val digitalStates: Map<IDigitalInputAction, DigitalState> by lazy {
        allDigitalActions.associateWith {
            DigitalState(it, steamInterfaces.input.getDigitalActionHandle(it.actionId))
        }
    }
    
    override val sourceName: String = "SteamInput"
    
    override fun getGlyphsForAction(action: IInputAction): List<IActionInputGlyph> {
        // TODO
        return emptyList()
    }

    override fun frameUpdate() {
        val input = steamInterfaces.input
        input.runFrame()

        val digitalActionData = digitalActionDataObj
        digitalStates.forEach { (_, state) ->
            input.getDigitalActionData(firstControllerHandle, state.handle, digitalActionData)
            state.isActive = digitalActionData.active && digitalActionData.state
        }
    }

    override fun isDigitalActionPressed(action: IDigitalInputAction): Boolean {
        val state = digitalStates[action] ?: return false
        return state.isActive
    }

    override fun isAnyInputActive(): Boolean {
        return digitalStates.any { it.value.isActive }
    }
    
    protected data class DigitalState(
        val action: IDigitalInputAction,
        val handle: SteamControllerDigitalActionHandle,
    ) {
        var isActive: Boolean = false
    }
}