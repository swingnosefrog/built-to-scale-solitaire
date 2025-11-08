package com.swingnosefrog.solitaire.game.input

import com.swingnosefrog.solitaire.inputmanager.ActionSource
import com.swingnosefrog.solitaire.inputmanager.IDigitalInputAction
import com.swingnosefrog.solitaire.inputmanager.InputActionListener


class GameInputActionListener(private val input: GameInput) : InputActionListener.Adapter() {

    override fun handleDigitalActionPressed(actionSource: ActionSource, action: IDigitalInputAction): Boolean {
        println("GameInputActionListener pressed ${actionSource.sourceName} ${action.actionName}")
        return false
    }
}