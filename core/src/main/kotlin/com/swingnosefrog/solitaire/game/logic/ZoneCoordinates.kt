package com.swingnosefrog.solitaire.game.logic

import com.swingnosefrog.solitaire.game.Card

data class ZoneCoordinates(
    val zone: CardZone,
    val index: Int,
    val offsetX: Float,
    val offsetY: Float,
) {
    
    fun getCardsToDrag(): List<Card> = zone.cardStack.cardList.drop(index)
    
}
