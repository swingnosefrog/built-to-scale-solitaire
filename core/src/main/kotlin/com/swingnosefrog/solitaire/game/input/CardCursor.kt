package com.swingnosefrog.solitaire.game.input

import com.swingnosefrog.solitaire.game.logic.CardZone


data class CardCursor(
    val zone: CardZone,
    val indexFromEnd: Int,
    val isMouseBased: Boolean,
) {

    val indexFromStart: Int get() = zone.cardStack.cardList.size - indexFromEnd - 1
}