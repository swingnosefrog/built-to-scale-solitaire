package com.swingnosefrog.solitaire.inputmanager.impl

import com.badlogic.gdx.Input
import com.swingnosefrog.solitaire.inputmanager.GdxActionSource
import com.swingnosefrog.solitaire.inputmanager.IDigitalInputAction


class GdxActionSourceImpl(actions: List<InputActions>) : GdxActionSource(actions) {

    override fun IDigitalInputAction.mapToKeys(): LinkedHashSet<Int> {
        if (this !is InputActions)
            throw IllegalArgumentException("Input action must implement InputActions, was ${this::class.java.name}")
        
        return when (this) {
            InputActions.DirectionUp -> linkedSetOf(Input.Keys.UP, Input.Keys.W)
            InputActions.DirectionDown -> linkedSetOf(Input.Keys.DOWN, Input.Keys.S)
            InputActions.DirectionLeft -> linkedSetOf(Input.Keys.LEFT, Input.Keys.A)
            InputActions.DirectionRight -> linkedSetOf(Input.Keys.RIGHT, Input.Keys.D)
            InputActions.Select -> linkedSetOf(Input.Keys.Z, Input.Keys.SPACE, Input.Keys.ENTER)
            InputActions.Back -> linkedSetOf(Input.Keys.X, Input.Keys.ESCAPE)
            InputActions.Menu -> linkedSetOf(Input.Keys.ESCAPE)
            InputActions.NewGame -> linkedSetOf(Input.Keys.R)
            InputActions.HowToPlay -> linkedSetOf(Input.Keys.F1)
        }
    }
}