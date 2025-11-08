package com.swingnosefrog.solitaire.game.input

import com.swingnosefrog.solitaire.game.logic.CardZone


data class ZoneSelection(
    val zone: CardZone,
    val indexFromEnd: Int,
)
