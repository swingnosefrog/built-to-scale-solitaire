package com.swingnosefrog.solitaire.game.input

import com.swingnosefrog.solitaire.game.logic.CardZone
import com.swingnosefrog.solitaire.game.logic.ZoneCoordinates


data class CardCursor(
    val zone: CardZone,
    val indexFromEnd: Int,

    val isMouseBased: Boolean,
    val lastMouseZoneCoordinates: ZoneCoordinates? = null,
) {

    val indexFromStart: Int get() = zone.cardStack.cardList.size - indexFromEnd - 1
}