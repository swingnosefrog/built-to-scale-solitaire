package com.swingnosefrog.solitaire.game.logic

import com.swingnosefrog.solitaire.game.Card


sealed class DragInfo {

    data object Nothing : DragInfo()

    class Dragging(
        zoneCoords: ZoneCoordinates,
        cardList: List<Card> = zoneCoords.getCardsToDrag(),
    ) : DragInfo() {

        val originalZone: CardZone = zoneCoords.zone

        val mouseOffsetX: Float = zoneCoords.offsetX
        val mouseOffsetY: Float = zoneCoords.offsetY

        var x: Float = zoneCoords.zone.x.get()
            private set
        var y: Float = zoneCoords.zone.y.get() + zoneCoords.index * zoneCoords.zone.cardStack.stackDirection.yOffset
            private set

        val cardStack: CardStack = CardStack(cardList.toMutableList(), StackDirection.DOWN)

        fun updatePosition(worldX: Float, worldY: Float) {
            x = worldX - mouseOffsetX
            y = worldY - mouseOffsetY
        }
    }

}