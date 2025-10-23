package com.swingnosefrog.solitaire.inputmanager

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.swingnosefrog.solitaire.fonts.PromptFontConsts

abstract class GdxActionSource(actions: List<IInputAction>) : ActionSource(actions) {
    
    override val sourceName: String = "Gdx"
    
    protected val digitalActions: List<IDigitalInputAction> = actions.filterIsInstance<IDigitalInputAction>()
    protected val digitalActionsToKey: Map<IDigitalInputAction, Int> by lazy {
        digitalActions.associateWith { it.mapToKey() }
    }
    protected val digitalActionsToGlyphs: Map<IDigitalInputAction, DigitalActionInputGlyph> by lazy {
        digitalActionsToKey.map { (action, k) ->
            val keyName = Input.Keys.toString(k)
            action to DigitalActionInputGlyph(keyName, PromptFontConsts.attemptMapGdxKeyToPromptFont(k))
        }.toMap()
    }
    
    protected val digitalActionsState: MutableMap<IDigitalInputAction, Boolean> = digitalActions.associateWith { false }.toMutableMap()

    protected abstract fun IDigitalInputAction.mapToKey(): Int
    
    override fun getGlyphsForAction(action: IInputAction): List<IActionInputGlyph> {
        val glyph = digitalActionsToGlyphs[action] ?: return emptyList()
        return glyph.listOfJustSelf
    }

    override fun frameUpdate() {
        val input = Gdx.input
        digitalActions.forEach { action ->
            val key = digitalActionsToKey.getValue(action)
            digitalActionsState[action] = input.isKeyPressed(key)
        }
    }

    override fun isDigitalActionPressed(action: IDigitalInputAction): Boolean {
        return digitalActionsState[action] == true
    }

    override fun isAnyInputActive(): Boolean {
        return digitalActionsState.values.any()
    }

    data class DigitalActionInputGlyph(
        override val glyphName: String,
        override val promptFontText: String,
    ) : IActionInputGlyph {
        val listOfJustSelf: List<DigitalActionInputGlyph> = listOf(this)
    }
}