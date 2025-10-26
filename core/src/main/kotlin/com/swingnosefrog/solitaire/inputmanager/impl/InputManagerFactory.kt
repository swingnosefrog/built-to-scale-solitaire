package com.swingnosefrog.solitaire.inputmanager.impl

import com.swingnosefrog.solitaire.inputmanager.ActionSource
import com.swingnosefrog.solitaire.inputmanager.InputManager
import com.swingnosefrog.solitaire.steamworks.SteamInterfaces


class InputManagerFactory(
    val prioritizeController: Boolean,
    private val steamInterfaces: SteamInterfaces?
) {
    
    fun create(): InputManager {
        val actions = InputActions.entries.toList()
        return InputManager(
            actions,
            createSources(actions)
        )
    }
    
    private fun createSources(actions: List<InputActions>): List<ActionSource> {
        val list = mutableListOf<ActionSource>(
            GdxActionSourceImpl(actions),
        )
        
        if (steamInterfaces != null) {
            val steamInputSource = SteamInputActionSourceImpl(actions, steamInterfaces)
            if (prioritizeController) {
                list.addFirst(steamInputSource)
            } else {
                list.add(steamInputSource)
            }
        }
        
        return list
    }
}