package com.swingnosefrog.solitaire.game.input

import com.badlogic.gdx.math.Rectangle
import com.swingnosefrog.solitaire.game.logic.CardZone
import com.swingnosefrog.solitaire.game.logic.GameLogic
import paintbox.util.gdxutils.maxX
import paintbox.util.gdxutils.maxY
import kotlin.math.max
import kotlin.math.min


fun getNearestOverlappingCardZone(inputRect: Rectangle, cardZones: List<CardZone>): CardZone? {
    var nearest: CardZone? = null
    var mostArea = 0f

    val zoneRect = Rectangle()
    
    for (zone in cardZones) {
        zoneRect.set(
            zone.x.get(),
            zone.y.get(),
            GameLogic.CARD_WIDTH,
            GameLogic.CARD_HEIGHT + (zone.maxStackSize - 1) * zone.cardStack.stackDirection.yOffset
        )
        if (!inputRect.overlaps(zoneRect)) continue

        val minX = max(inputRect.x, zoneRect.x)
        val minY = max(inputRect.y, zoneRect.y)
        val maxX = min(inputRect.maxX, zoneRect.maxX)
        val maxY = min(inputRect.maxY, zoneRect.maxY)
        val overlapArea = (maxX - minX) * (maxY - minY)

        if (overlapArea > mostArea) {
            mostArea = overlapArea
            nearest = zone
        }
    }

    return nearest
}