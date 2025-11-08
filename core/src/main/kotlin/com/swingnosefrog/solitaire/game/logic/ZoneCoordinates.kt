package com.swingnosefrog.solitaire.game.logic

import com.swingnosefrog.solitaire.game.Card
import com.swingnosefrog.solitaire.game.input.ZoneSelection

data class ZoneCoordinates(
    val zone: CardZone,
    val index: Int,
    val offsetX: Float,
    val offsetY: Float,
) {
    
    fun getCardsToDrag(): List<Card> = zone.cardStack.cardList.drop(index)
    
    fun toZoneSelection(): ZoneSelection = ZoneSelection(zone, zone.cardStack.cardList.size - index - 1)
}
