package com.swingnosefrog.solitaire.game.input

import com.swingnosefrog.solitaire.game.logic.CardZone
import com.swingnosefrog.solitaire.game.logic.DragInfo
import com.swingnosefrog.solitaire.game.logic.GameLogic
import com.swingnosefrog.solitaire.game.logic.ZoneCoordinates
import paintbox.binding.BooleanVar
import paintbox.binding.ReadOnlyBooleanVar
import paintbox.binding.Var

class GameInput(val logic: GameLogic) {
    
    val inputsDisabled: BooleanVar = BooleanVar(false)

    private val _isMouseBased: BooleanVar = BooleanVar(true)
    private val isMouseBased: ReadOnlyBooleanVar = _isMouseBased

    private val dragInfo: Var<DragInfo> =
        Var(DragInfo.Deciding(ZoneSelection(logic.zones.playerZones.first(), indexFromEnd = 0)))

    fun isDragging(): Boolean {
        return dragInfo.getOrCompute() !is DragInfo.Deciding
    }

    fun cancelDrag(): Boolean {
        val dragging = getDraggingInfo() ?: return false
        
        val myList = dragging.cardStack.cardList.toList()
        dragging.originalZone.cardStack.cardList.addAll(myList)

        logic.eventDispatcher.onCardStackPickupCancelled(logic, dragging.cardStack, dragging.originalZone)

        val zoneSelection = ZoneSelection(dragging.originalZone, indexFromEnd = (dragging.cardStack.cardList.size - 1).coerceAtLeast(0))
        dragInfo.set(DragInfo.Deciding(zoneSelection, dragging.isCurrentlyHoveringOverZone))

        logic.checkTableauAfterActivity()
        
        return true
    }

    fun endDrag(newZone: CardZone, isFromButtonInput: Boolean): Boolean {
        val dragging = getDraggingInfo() ?: return false
        
        if (isFromButtonInput && !canNonCancelButtonOperationInterruptDragging()) return false

        if (newZone == dragging.originalZone || !logic.canPlaceStackOnZone(dragging.cardStack, newZone)) {
            cancelDrag()
            return false
        }

        val myList = dragging.cardStack.cardList.toList()
        newZone.cardStack.cardList += myList

        logic.eventDispatcher.onCardStackPlacedDown(logic, dragging.cardStack, newZone)
        if (dragging.cardStack.cardList.size == 1 && newZone in logic.zones.foundationZones) {
            logic.eventDispatcher.onCardPlacedInFoundation(logic, dragging.cardStack.cardList.first(), newZone)
        }

        val initialZoneSelection = ZoneSelection(newZone, indexFromEnd = 0)
        val newDragInfo = DragInfo.Deciding(initialZoneSelection, dragging.isCurrentlyHoveringOverZone)
        dragInfo.set(newDragInfo)

        logic.checkTableauAfterActivity()

        return true
    }

    fun attemptStartDrag(zoneCoords: ZoneCoordinates, initialMouseMode: MouseMode?): Boolean {
        if (isDragging()) return false

        if (!zoneCoords.zone.canDragFrom || zoneCoords.zone.isFlippedOver) {
            return false
        }
        
        val zoneCardList = zoneCoords.zone.cardStack.cardList
        val newSet = zoneCoords.getCardsToDrag()

        if (!logic.isStackValidToMove(newSet)) {
            return false
        }

        val newDragging = DragInfo.Dragging(zoneCoords, newSet, initialMouseMode)
        repeat(newSet.size) {
            zoneCardList.removeAt(zoneCoords.index)
        }
        dragInfo.set(newDragging)
        
        logic.eventDispatcher.onCardStackPickedUp(logic, newDragging.cardStack, zoneCoords.zone)
        
        return true
    }

    fun updateMousePosition(worldX: Float, worldY: Float) {
        _isMouseBased.set(true)
        dragInfo.getOrCompute().updateMousePosition(this, worldX, worldY)
    }
    
    fun updateFromDirectionPress(direction: Direction): Boolean {
        if (inputsDisabled.get()) return false
        if (!canNonCancelButtonOperationInterruptDragging()) return false

        val currentDragInfo = getCurrentDragInfo()
        val isCurrentlyHoveringOverZone = currentDragInfo.isCurrentlyHoveringOverZone
        
        switchToButtonsFocus()
        if (!isCurrentlyHoveringOverZone) {
            snapToNearestZone()
        } else {
            // TODO different logic for deciding vs dragging, specifically for UP/DOWN
        }
        
        return true
    }
    
    fun switchToButtonsFocusAndSnapToNearestZoneIfNotHovering() {
        if (inputsDisabled.get()) return
        
        switchToButtonsFocus()
        
        val currentDragInfo = getCurrentDragInfo()
        if (!currentDragInfo.isCurrentlyHoveringOverZone) {
            snapToNearestZone()
        }
    }
    
    private fun snapToNearestZone() {
        val currentDragInfo = getCurrentDragInfo()
        
        val isCurrentlyHoveringOverZone = currentDragInfo.isCurrentlyHoveringOverZone
        if (isCurrentlyHoveringOverZone) {
            val zone = currentDragInfo.hoveredZone
            // TODO snap to it fully
        } else {
            // Use last hoveredZone and snap to it
            // TODO could this be smarter? i.e. proximity based
        }
    }

    private fun switchToButtonsFocus() {
        if (inputsDisabled.get()) return

        _isMouseBased.set(false)
    }
    
    private fun canNonCancelButtonOperationInterruptDragging(): Boolean {
        val dragging = getDraggingInfo() ?: return true

        return dragging.mouseMode != MouseMode.CLICK_AND_DRAG
    }
    
    fun getCurrentDragInfo(): DragInfo = dragInfo.getOrCompute()
    
    fun getDraggingInfo(): DragInfo.Dragging? = getCurrentDragInfo() as? DragInfo.Dragging
    
    fun getDecidingInfo(): DragInfo.Deciding? = getCurrentDragInfo() as? DragInfo.Deciding

}