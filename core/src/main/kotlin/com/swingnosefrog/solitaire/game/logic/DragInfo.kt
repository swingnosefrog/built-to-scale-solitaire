package com.swingnosefrog.solitaire.game.logic

import com.badlogic.gdx.math.Vector2
import com.swingnosefrog.solitaire.game.Card


sealed class DragInfo {

    data object Nothing : DragInfo()

    class Dragging(
        cardList: List<Card>,

        zoneCoords: ZoneCoordinates
    ) : DragInfo() {

        val oldZone: CardZone = zoneCoords.zone
        var x: Float = zoneCoords.zone.x.get()
        var y: Float = zoneCoords.zone.y.get() + zoneCoords.index * zoneCoords.zone.cardStack.stackDirection.yOffset
        
        val cardStack: CardStack = CardStack(cardList.toMutableList(), StackDirection.DOWN)
        val mouseOffset: Vector2 = Vector2(zoneCoords.offsetX, zoneCoords.offsetY)
    }

}