package com.swingnosefrog.solitaire.game.logic

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.swingnosefrog.solitaire.game.Card
import com.swingnosefrog.solitaire.game.input.GameInput
import com.swingnosefrog.solitaire.game.input.MouseMode
import com.swingnosefrog.solitaire.game.input.ZoneSelection
import paintbox.util.gdxutils.maxX
import paintbox.util.gdxutils.maxY
import kotlin.math.max
import kotlin.math.min


sealed class DragInfo {

    class Deciding(
        initialSelection: ZoneSelection,
    ) : DragInfo() {
        
        companion object {
            
            fun isZoneSelectionLegal(selection: ZoneSelection): Boolean {
                return selection.zone.canDragFrom && !selection.zone.isFlippedOver
            }
        }

        var currentSelection: ZoneSelection = initialSelection
            private set
        var isHoveringOverSelection: Boolean = false
            private set
        val lastKnownMouseOffset: Vector2 = Vector2(0f, 0f)

        override val hoveredZone: CardZone
            get() = currentSelection.zone
        override val isCurrentlyHoveringOverZone: Boolean
            get() = isHoveringOverSelection

        constructor(
            initialSelection: ZoneSelection,
            initialHovering: Boolean,
        ) : this(initialSelection) {
            isHoveringOverSelection = initialHovering && isZoneSelectionLegal(initialSelection)
        }

        override fun updateMousePosition(input: GameInput, worldX: Float, worldY: Float) {
            val logic = input.logic
            val selectedZoneCoordinates = logic.getSelectedZoneCoordinates(worldX, worldY)
            val newSelection = selectedZoneCoordinates?.toZoneSelection()?.takeIf { sel ->
                isZoneSelectionLegal(sel)
            }
            attemptSetNewSelection(newSelection)
            if (newSelection != null) {
                lastKnownMouseOffset.set(selectedZoneCoordinates.offsetX, selectedZoneCoordinates.offsetY)
            }
        }
        
        private fun attemptSetNewSelection(newSelection: ZoneSelection?) {
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
        initialMouseMode: MouseMode?
    ) : DragInfo() {

        val originalZone: CardZone = zoneCoords.zone

        val mouseOffsetX: Float = zoneCoords.offsetX
        val mouseOffsetY: Float = zoneCoords.offsetY

        var mouseMode: MouseMode? = initialMouseMode
            private set
        
        var x: Float = zoneCoords.zone.x.get()
            private set
        var y: Float = zoneCoords.zone.y.get() + zoneCoords.index * zoneCoords.zone.cardStack.stackDirection.yOffset
            private set

        val cardStack: CardStack = CardStack(cardList.toMutableList(), StackDirection.DOWN)

        
        override var hoveredZone: CardZone = originalZone
            private set
        override var isCurrentlyHoveringOverZone: Boolean = true
            private set
        
        override fun updateMousePosition(input: GameInput, worldX: Float, worldY: Float) {
            x = worldX - mouseOffsetX
            y = worldY - mouseOffsetY
            
            attemptSetNewZone(getNearestOverlappingDraggingZone(input.logic))
            
            if (mouseMode == null) {
                mouseMode = MouseMode.CLICK_THEN_CLICK
            }
        }
        
        fun getNearestOverlappingDraggingZone(logic: GameLogic): CardZone? {
            var nearest: CardZone? = null
            var mostArea = 0f

            val dragX = this.x
            val dragY = this.y
            val dragW = GameLogic.CARD_WIDTH
            val dragH = GameLogic.CARD_HEIGHT // Only the topmost card of the dragged stack counts for area checking
            val dragRect = Rectangle(dragX, dragY, dragW, dragH)

            for (zone in logic.zones.allPlaceableCardZones) {
                val zoneRect =
                    Rectangle(
                        zone.x.get(),
                        zone.y.get(),
                        GameLogic.CARD_WIDTH,
                        GameLogic.CARD_HEIGHT + (zone.maxStackSize - 1) * zone.cardStack.stackDirection.yOffset
                    )
                if (!dragRect.overlaps(zoneRect)) continue

                val minX = max(dragRect.x, zoneRect.x)
                val minY = max(dragRect.y, zoneRect.y)
                val maxX = min(dragRect.maxX, zoneRect.maxX)
                val maxY = min(dragRect.maxY, zoneRect.maxY)
                val overlapArea = (maxX - minX) * (maxY - minY)

                if (overlapArea > mostArea) {
                    mostArea = overlapArea
                    nearest = zone
                }
            }

            return nearest
        }

        private fun attemptSetNewZone(newZone: CardZone?) {
            if (newZone != null) {
                isCurrentlyHoveringOverZone = true
                hoveredZone = newZone
            } else {
                isCurrentlyHoveringOverZone = false
            }
        }
    }
    
    abstract val hoveredZone: CardZone
    abstract val isCurrentlyHoveringOverZone: Boolean
    
    open fun updateMousePosition(input: GameInput, worldX: Float, worldY: Float) {
    }
    
}