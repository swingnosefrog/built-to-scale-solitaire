package com.swingnosefrog.solitaire.inputmanager

import com.codedisaster.steamworks.SteamController
import com.codedisaster.steamworks.SteamControllerActionSetHandle
import com.codedisaster.steamworks.SteamControllerDigitalActionData
import com.codedisaster.steamworks.SteamControllerDigitalActionHandle
import com.codedisaster.steamworks.SteamControllerHandle
import com.codedisaster.steamworks.SteamNativeHandle
import com.swingnosefrog.solitaire.steamworks.SteamActionInputGlyph
import com.swingnosefrog.solitaire.steamworks.SteamInterfaces
import com.swingnosefrog.solitaire.steamworks.getActionInputGlyph
import com.swingnosefrog.solitaire.steamworks.getConnectedControllers
import paintbox.Paintbox

open class SteamInputActionSource(
    actions: List<IInputAction>,
    protected val steamInterfaces: SteamInterfaces
) : ActionSource(actions) {
    
    private val actionOriginsTmp: Array<SteamController.ActionOrigin> = Array(SteamController.STEAM_CONTROLLER_MAX_ORIGINS) { SteamController.ActionOrigin.None }
    
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
        if (action !is IDigitalInputAction) return emptyList()
        
        val digitalState = digitalStates[action] ?: return emptyList()

        val actionOriginsArray = actionOriginsTmp
        
        val input = steamInterfaces.input
        val controller = firstControllerHandle
        var actionSet = input.getCurrentActionSet(controller)
        
        if (actionSet.isNull() && !digitalState.lastActionSetHandle.isNull()) {
            actionSet = digitalState.lastActionSetHandle
        }
        
        val digitalActionHandle = digitalState.handle
        val num = input.getDigitalActionOrigins(controller, actionSet, digitalActionHandle, actionOriginsArray)
        
        val currentlyCached = digitalState.cachedGlyphs
        var isDirty = currentlyCached.size != num
        if (!isDirty) {
            // This method will be called multiple times every frame -- should try to be allocation-less normally
            for (i in 0 until currentlyCached.size) {
                if (currentlyCached[i].actionOrigin != actionOriginsArray[i]) {
                    isDirty = true
                    break
                }
            }
        }
        
        if (isDirty) {
            digitalState.cachedGlyphs = (0 until num).map { actionOriginsArray[it].getActionInputGlyph() }
        }
        
        if (!actionSet.isNull()) {
            digitalState.lastActionSetHandle = actionSet
        }
        
        return digitalState.cachedGlyphs
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
    
    protected fun SteamControllerActionSetHandle?.isNull(): Boolean = 
        this == null || SteamNativeHandle.getNativeHandle(this) == 0L
    
    protected data class DigitalState(
        val action: IDigitalInputAction,
        val handle: SteamControllerDigitalActionHandle,
    ) {
        var isActive: Boolean = false
        
        var lastActionSetHandle: SteamControllerActionSetHandle? = null
        var cachedGlyphs: List<SteamActionInputGlyph> = emptyList()
    }
}