package com.swingnosefrog.solitaire.game.input

import com.swingnosefrog.solitaire.game.logic.CardZone
import com.swingnosefrog.solitaire.game.logic.ZoneCoordinates


data class ZoneSelection(
    val zone: CardZone,
    val indexFromEnd: Int,
) {
    
    fun toZoneCoordinates(offsetX: Float, offsetY: Float): ZoneCoordinates {
        return ZoneCoordinates(zone, zone.cardStack.cardList.size - indexFromEnd - 1, offsetX, offsetY)
    }
}
