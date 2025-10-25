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
        val maxKeysAssigned = allActions.maxOf { it.keycodes.size }
        
        val map: Map<Int, MutableList<DigitalActionMetadata>> = allActions.flatMap { it.keycodes }.toSet()
            .associateWith { mutableListOf() }
        
        repeat(maxKeysAssigned) { layer ->
            for (actionMetadata in allActions) {
                val keys = actionMetadata.keycodes
                if (layer >= keys.size) continue
                
                val key = keys[layer]
                map.getValue(key).add(actionMetadata)
            }
        }
        
        map
    }
    
    private val keycodesToAction: MutableMap<Int, DigitalActionMetadata> = HashMap()

    protected abstract fun IDigitalInputAction.mapToKeys(): LinkedHashSet<Int>

    override fun getGlyphsForAction(action: IInputAction): List<IActionInputGlyph> {
        return digitalActionsMetadata[action]?.glyphs ?: emptyList()
    }

    override fun frameUpdate() {
        val input = Gdx.input
        
        keysToDigitalActions.forEach { (keycode, actionMetadatas) ->
            val alreadyDown: DigitalActionMetadata? = keycodesToAction[keycode]
            
            if (input.isKeyPressed(keycode)) {
                if (alreadyDown == null) {
                    val firstAvailableActionMetadata = actionMetadatas.minBy { it.pressedKeys.size }

                    firstAvailableActionMetadata.pressedKeys.add(keycode)
                    keycodesToAction[keycode] = firstAvailableActionMetadata
                }
            } else {
                if (alreadyDown != null) {
                    alreadyDown.pressedKeys.remove(keycode)
                    keycodesToAction.remove(keycode)
                }
            }
        }
    }

    override fun isDigitalActionPressed(action: IDigitalInputAction): Boolean {
        val metadata = digitalActionsMetadata[action] ?: return false
        return metadata.pressedKeys.isNotEmpty()
    }

    override fun isAnyInputActive(): Boolean {
        return digitalActionsMetadata.values.any { it.pressedKeys.isNotEmpty() }
    }

    protected data class DigitalActionInputGlyph(
        override val glyphName: String,
        override val promptFontText: String,
    ) : IActionInputGlyph

    protected data class DigitalActionMetadata(
        val action: IDigitalInputAction,
        val keycodes: List<Int>,
        val glyphs: List<DigitalActionInputGlyph>,
    ) {
        val pressedKeys: LinkedHashSet<Int> = LinkedHashSet()
    }
}