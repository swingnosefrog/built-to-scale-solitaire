package com.swingnosefrog.solitaire.inputmanager.impl

import com.swingnosefrog.solitaire.inputmanager.ActionSource
import com.swingnosefrog.solitaire.inputmanager.InputManager


class InputManagerFactory(
    val prioritizeController: Boolean,
) {
    
    fun create(): InputManager {
        val actions = InputActions.entries.toList()
        return InputManager(
            actions,
            createSources(actions)
        )
    }
    
    private fun createSources(actions: List<InputActions>): List<ActionSource> {
        var list = listOf(
            GdxActionSourceImpl(actions),
            SteamInputActionSourceImpl(actions)
        )
        
        if (prioritizeController) {
            list = list.reversed()
        }
        
        return list
    }
}