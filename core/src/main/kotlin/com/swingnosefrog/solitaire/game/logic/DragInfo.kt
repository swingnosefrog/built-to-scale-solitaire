package com.swingnosefrog.solitaire.game.logic

import com.swingnosefrog.solitaire.game.Card
import com.swingnosefrog.solitaire.game.input.GameInput
import com.swingnosefrog.solitaire.game.input.ZoneSelection


sealed class DragInfo {

    class Deciding(
        initialSelection: ZoneSelection,
    ) : DragInfo() {

        var currentSelection: ZoneSelection = initialSelection
            private set
        var isHoveringOverSelection: Boolean = false // TODO this may be true on instantiation
            private set
        
        override fun updatePosition(input: GameInput, worldX: Float, worldY: Float) {
            val logic = input.logic
            val newSelection = logic.getSelectedZoneCoordinates(worldX, worldY)?.toZoneSelection()?.takeIf { sel ->
                logic.zones.isPlaceable(sel.zone)
            }
            if (newSelection != null) {
                isHoveringOverSelection = true
                currentSelection = newSelection
            } else {
                isHoveringOverSelection = false
            }
        }
    }

    class Dragging(
        zoneCoords: ZoneCoordinates,
        cardList: List<Card>,
    ) : DragInfo() {

        val originalZone: CardZone = zoneCoords.zone

        val mouseOffsetX: Float = zoneCoords.offsetX
        val mouseOffsetY: Float = zoneCoords.offsetY

        var x: Float = zoneCoords.zone.x.get()
            private set
        var y: Float = zoneCoords.zone.y.get() + zoneCoords.index * zoneCoords.zone.cardStack.stackDirection.yOffset
            private set

        val cardStack: CardStack = CardStack(cardList.toMutableList(), StackDirection.DOWN)

        override fun updatePosition(input: GameInput, worldX: Float, worldY: Float) {
            x = worldX - mouseOffsetX
            y = worldY - mouseOffsetY
        }
    }
    
    open fun updatePosition(input: GameInput, worldX: Float, worldY: Float) {
    }

}