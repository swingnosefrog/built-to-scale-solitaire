package com.swingnosefrog.solitaire.inputmanager

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.swingnosefrog.solitaire.fonts.PromptFontConsts

abstract class GdxActionSource(actions: List<IInputAction>) : ActionSource(actions) {

    override val sourceName: String = "Gdx"

    protected val digitalActions: List<IDigitalInputAction> = actions.filterIsInstance<IDigitalInputAction>()
    protected val digitalActionsMetadata: Map<IDigitalInputAction, DigitalActionMetadata> by lazy {
        digitalActions.associateWith { action ->
            val mappedKeys = action.mapToKeys()
            if (mappedKeys.isEmpty())
                throw IllegalStateException("Mapped keys for action \"${action.actionId}\" must not be empty")

            DigitalActionMetadata(
                action,
                mappedKeys.toList(),
                mappedKeys.map { k ->
                    val keyName = Input.Keys.toString(k)
                    DigitalActionInputGlyph(keyName, PromptFontConsts.attemptMapGdxKeyToPromptFont(k))
                }
            )
        }
    }
    protected val keysToDigitalActions: Map<Int, List<DigitalActionMetadata>> by lazy {
        val allActions = digitalActionsMetadata.values.toList()
        val maxKeysAssigned = allActions.maxOf { it.keys.size }
        
        val map: Map<Int, MutableList<DigitalActionMetadata>> = allActions.flatMap { it.keys }.toSet()
            .associateWith { mutableListOf() }
        
        repeat(maxKeysAssigned) { layer ->
            for (actionMetadata in allActions) {
                val keys = actionMetadata.keys
                if (layer >= keys.size) continue
                
                val key = keys[layer]
                map.getValue(key).add(actionMetadata)
            }
        }
        
        map
    }
    
    private val digitalActionsState: MutableMap<IDigitalInputAction, Boolean> =
        digitalActions.associateWith { false }.toMutableMap()
    private val keyStates: MutableMap<Int, IDigitalInputAction> = HashMap()

    protected abstract fun IDigitalInputAction.mapToKeys(): LinkedHashSet<Int>

    override fun getGlyphsForAction(action: IInputAction): List<IActionInputGlyph> {
        return digitalActionsMetadata[action]?.glyphs ?: emptyList()
    }

    override fun frameUpdate() {
        val input = Gdx.input
        
        keysToDigitalActions.forEach { (keycode, actionMetadatas) ->
            val alreadyDown: IDigitalInputAction? = keyStates[keycode]
            
            if (input.isKeyPressed(keycode)) {
                if (alreadyDown == null) {
                    val firstAvailableAction = actionMetadatas.firstOrNull { a ->
                        digitalActionsState[a.action] == true
                    }
                    if (firstAvailableAction != null) {
                        val action = firstAvailableAction.action
                        digitalActionsState[action] = true
                        keyStates[keycode] = action
                    }
                }
            } else {
                if (alreadyDown != null) {
                    digitalActionsState[alreadyDown] = false
                    keyStates.remove(keycode)
                }
            }
        }
    }

    override fun isDigitalActionPressed(action: IDigitalInputAction): Boolean {
        return digitalActionsState[action] == true
    }

    override fun isAnyInputActive(): Boolean {
        return digitalActionsState.values.any()
    }

    protected data class DigitalActionInputGlyph(
        override val glyphName: String,
        override val promptFontText: String,
    ) : IActionInputGlyph

    protected data class DigitalActionMetadata(
        val action: IDigitalInputAction,
        val keys: List<Int>,
        val glyphs: List<DigitalActionInputGlyph>,
    )
}