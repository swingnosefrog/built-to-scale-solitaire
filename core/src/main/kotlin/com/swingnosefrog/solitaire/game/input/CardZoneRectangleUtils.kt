package com.swingnosefrog.solitaire.game.input

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.swingnosefrog.solitaire.game.logic.CardZone
import com.swingnosefrog.solitaire.game.logic.GameLogic
import paintbox.util.gdxutils.maxX
import paintbox.util.gdxutils.maxY
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

fun Rectangle.setToCardZoneBounds(zone: CardZone, useActualHeight: Boolean): Rectangle = this.set(
    zone.x.get(),
    zone.y.get(),
    GameLogic.CARD_WIDTH,
    GameLogic.CARD_HEIGHT + (if (useActualHeight) (zone.cardStack.cardList.size) else (zone.maxStackSize - 1)) * zone.cardStack.stackDirection.yOffset
)

fun Rectangle.setToLastCardInZone(zone: CardZone): Rectangle = this.set(
    zone.x.get(),
    zone.y.get() + zone.cardStack.cardList.lastIndex.coerceAtLeast(0) * zone.cardStack.stackDirection.yOffset,
    GameLogic.CARD_WIDTH,
    GameLogic.CARD_HEIGHT
)

fun Rectangle.getNearestOverlappingCardZone(cardZones: List<CardZone>): CardZone? {
    val inputRect = this
    
    var nearest: CardZone? = null
    var mostArea = 0f

    val zoneRect = Rectangle()

    for (zone in cardZones) {
        zoneRect.setToCardZoneBounds(zone, useActualHeight = false)

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

fun Vector2.getDistanceToNearestRectangleSide(rectangle: Rectangle): Float {
    val dx = max(rectangle.x - this.x, max(0f, this.x - rectangle.maxX))
    val dy = max(rectangle.y - this.y, max(0f, this.y - rectangle.maxY))
    return hypot(dx, dy)
}
