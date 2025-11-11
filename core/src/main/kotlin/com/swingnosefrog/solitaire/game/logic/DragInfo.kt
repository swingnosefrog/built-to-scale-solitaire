package com.swingnosefrog.solitaire.game.logic

import com.badlogic.gdx.math.Rectangle
import com.swingnosefrog.solitaire.game.Card
import com.swingnosefrog.solitaire.game.input.CardCursor
import com.swingnosefrog.solitaire.game.input.GameInput
import com.swingnosefrog.solitaire.game.input.MouseMode


sealed class DragInfo {

    class Deciding() : DragInfo() {
        
//        companion object {
//            
//            fun isZoneSelectionLegal(selection: ZoneSelection): Boolean {
//                return selection.zone.canDragFrom && !selection.zone.isFlippedOver
//            }
//        }

//        override var lastMouseWorldX: Float = initialSelection.zone.x.get()
//        override var lastMouseWorldY: Float =
//            initialSelection.zone.y.get() + initialSelection.zone.cardStack.cardList.size * initialSelection.zone.cardStack.stackDirection.yOffset


        override fun updateMousePosition(input: GameInput, worldX: Float, worldY: Float) {
            super.updateMousePosition(input, worldX, worldY)
            
//            val logic = input.logic
//            val selectedZoneCoordinates = logic.getSelectedZoneCoordinates(worldX, worldY)
//            val newSelection = selectedZoneCoordinates?.toZoneSelection()?.takeIf { sel ->
//                isZoneSelectionLegal(sel)
//            }
//            attemptSetNewSelection(newSelection)
        }
        
//        private fun attemptSetNewSelection(newSelection: ZoneSelection?) {
//            if (newSelection != null) {
//                isHoveringOverSelection = true
//                currentSelection = newSelection
//            } else {
//                isHoveringOverSelection = false
//            }
//        }
    }

    class Dragging(
        zoneCoords: ZoneCoordinates,
        cardList: List<Card>,
        initialMouseMode: MouseMode?,
        val initialCardCursor: CardCursor,
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
        

//        override var lastMouseWorldX: Float = x
//        override var lastMouseWorldY: Float = y

        override fun updateMousePosition(input: GameInput, worldX: Float, worldY: Float) {
            super.updateMousePosition(input, worldX, worldY)
            
            x = worldX - mouseOffsetX
            y = worldY - mouseOffsetY

            if (mouseMode == null) {
                mouseMode = MouseMode.CLICK_THEN_CLICK
            }

//            val newZone = getNearestOverlappingDraggingZone(input.logic)
            // TODO check if newZone is legal
//            attemptSetNewZone(newZone)
        }
        
        fun toOverlapCheckRectangle(): Rectangle = Rectangle(x, y, GameLogic.CARD_WIDTH, GameLogic.CARD_HEIGHT)

//        private fun attemptSetNewZone(newZone: CardZone?) {
//            if (newZone != null) {
//                isCurrentlyHoveringOverZone = true
//                hoveredZone = newZone
//            } else {
//                isCurrentlyHoveringOverZone = false
//            }
//        }
    }
    
//    abstract var lastMouseWorldX: Float
//        protected set
//    abstract var lastMouseWorldY: Float
//        protected set
    
    open fun updateMousePosition(input: GameInput, worldX: Float, worldY: Float) {
//        lastMouseWorldX = worldX
//        lastMouseWorldY = worldY
    }
    
}