package com.swingnosefrog.solitaire.game.logic

import paintbox.binding.FloatVar


class CardZone(
    val name: String,
    initX: Float, initY: Float,
    val maxStackSize: Int = 999,
    val canDragFrom: Boolean = true,
    var isFlippedOver: Boolean = false,
) {
    
    val x: FloatVar = FloatVar(initX)
    val y: FloatVar = FloatVar(initY)
    
    val cardStack: CardStack = CardStack(mutableListOf())

    override fun toString(): String {
        return name
    }
}