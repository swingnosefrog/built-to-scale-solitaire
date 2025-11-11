package com.swingnosefrog.solitaire.game.input

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.swingnosefrog.solitaire.game.logic.CardZone
import com.swingnosefrog.solitaire.game.logic.DragInfo
import com.swingnosefrog.solitaire.game.logic.GameLogic
import com.swingnosefrog.solitaire.game.logic.ZoneCoordinates
import paintbox.binding.BooleanVar
import paintbox.binding.Var
import kotlin.math.hypot

class GameInput(val logic: GameLogic, initiallyMouseBased: Boolean) {
    
    val inputsDisabled: BooleanVar = BooleanVar(false)
    
    private val cardCursor: Var<CardCursor> =
        Var(CardCursor(logic.zones.playerZones.first(), indexFromEnd = 0, isMouseBased = initiallyMouseBased))

    private val dragInfo: Var<DragInfo> = Var(DragInfo.Deciding())
    
    private val lastIndexFromEndByZone: MutableMap<CardZone, Int> = mutableMapOf()
    private var lastMouseWorldX: Float = 0f
    private var lastMouseWorldY: Float = 0f
    

    init {
        cardCursor.addListener { v ->
            updateLastIndexFromEndForZone(v.getOrCompute())
        }
    }
    
    //region Actions

    fun cancelDrag(): Boolean {
        if (inputsDisabled.get()) return false
        val dragging = getDraggingInfo() ?: return false
        
        val myList = dragging.cardStack.cardList.toList()
        dragging.originalZone.cardStack.cardList.addAll(myList)

        logic.eventDispatcher.onCardStackPickupCancelled(logic, dragging.cardStack, dragging.originalZone)

        dragInfo.set(DragInfo.Deciding())

        // Reset card cursor to original position, keeping current mouse mode
        val originalCardCursor = dragging.initialCardCursor
        val currentCardCursor = cardCursor.getOrCompute()
        cardCursor.set(
            currentCardCursor.copy(
                zone = originalCardCursor.zone,
                indexFromEnd = originalCardCursor.indexFromEnd,
                lastMouseZoneCoordinates = null // Relative position will likely change after cancelling, so must be null
            )
        )

        return true
    }
    
    fun endDrag(isFromButtonInput: Boolean): Boolean {
        if (inputsDisabled.get()) return false
        if (!isDragging()) return false
        
        if (isFromButtonInput && !canNonCancelButtonOperationInterruptDragging()) return false
        
        val currentCardCursor = cardCursor.getOrCompute()
        endDrag(currentCardCursor.zone, isFromButtonInput)
        
        return true
    }

    fun attemptStartDrag(initialMouseMode: MouseMode?): Boolean {
        if (inputsDisabled.get()) return false
        if (isDragging()) return false

        val currentCardCursor = cardCursor.getOrCompute()

        val zoneCoords: ZoneCoordinates? = currentCardCursor.lastMouseZoneCoordinates
            ?: (if (!currentCardCursor.isMouseBased)
                ZoneCoordinates(currentCardCursor.zone, currentCardCursor.indexFromStart, 0f, 0f)
            else null)
        
        if (zoneCoords != null) {
            attemptStartDrag(zoneCoords, initialMouseMode)
        }
        
        return true
    }

    fun updateMousePosition(worldX: Float, worldY: Float) {
        lastMouseWorldX = worldX
        lastMouseWorldY = worldY
        
        switchToMouseFocus()

        val currentDragInfo = dragInfo.getOrCompute()
        currentDragInfo.updateMousePosition(this, worldX, worldY)

        var newCardCursor = cardCursor.getOrCompute()
        if (!newCardCursor.isMouseBased) {
            newCardCursor = newCardCursor.copy(isMouseBased = true)
        }

        val zoneCoords = when (currentDragInfo) {
            is DragInfo.Deciding -> {
                // Use just worldXY position
                logic.getSelectedZoneCoordinates(worldX, worldY)
            }

            is DragInfo.Dragging -> {
                // Use rectangle overlap check
                val cardRect = currentDragInfo.toOverlapCheckRectangle()
                val legalPlacementZones = getLegalCardZonesBasedOnCurrentDragInfo()
                val closestCardZone = cardRect.getNearestOverlappingCardZone(legalPlacementZones)

                closestCardZone?.let { zone ->
                    val lastIndex = zone.cardStack.cardList.lastIndex
                    ZoneCoordinates(zone, lastIndex, offsetX = 0f, offsetY = 0f)
                }
            }
        }

        if (zoneCoords != null && isZoneSelectionLegal(zoneCoords.zone, zoneCoords.indexFromEnd)) {
            newCardCursor = newCardCursor.copy(
                zone = zoneCoords.zone,
                indexFromEnd = zoneCoords.indexFromEnd,
                lastMouseZoneCoordinates = zoneCoords,
            )
        } else {
            newCardCursor = newCardCursor.copy(lastMouseZoneCoordinates = null)
        }
        
        cardCursor.set(newCardCursor)
    }

    fun updateFromDirectionPress(direction: Direction): Boolean {
        if (inputsDisabled.get()) return false
        if (!canNonCancelButtonOperationInterruptDragging()) return false
        
        val wasAlreadyHovering = cardCursor.getOrCompute().isHoveringOverZone()
        
        switchToButtonsFocus()

        if (!wasAlreadyHovering) {
            snapToNearestLegalZone()
        } else {
            if (direction == Direction.UP || direction == Direction.DOWN) {
                val currentCardCursor = cardCursor.getOrCompute()
                val predictedIndex = currentCardCursor.indexFromEnd + (if (direction == Direction.UP) 1 else -1)
                if (isZoneSelectionLegal(currentCardCursor.zone, predictedIndex)) {
                    cardCursor.set(currentCardCursor.copy(indexFromEnd = predictedIndex, lastMouseZoneCoordinates = null))
                } else {
                    snapToCardZoneInDirection(direction)
                }
            } else {
                snapToCardZoneInDirection(direction)
            }
        }
        
        return true
    }
    
    fun switchToButtonsFocusAndSnapToNearestLegalZone() {
        val wasAlreadyHovering = cardCursor.getOrCompute().isHoveringOverZone()
        
        switchToButtonsFocus()

        if (!wasAlreadyHovering) {
            snapToNearestLegalZone()
        }
    }

    fun switchToButtonsFocus() {
        val currentCardCursor = cardCursor.getOrCompute()
        if (currentCardCursor.isMouseBased) {
            cardCursor.set(currentCardCursor.copy(isMouseBased = false))
        }
    }

    //endregion

    //region Private functions
    
    private fun snapToNearestLegalZone() {
        val legalZones = getLegalCardZonesBasedOnCurrentDragInfo()
        snapToNearestCardZoneToMouse(legalZones)
    }
    
    private fun snapToNearestCardZoneToMouse(zones: List<CardZone>) {
        val mousePos = Vector2(lastMouseWorldX, lastMouseWorldY)
        val cardRect = Rectangle()
        val nearestZone: CardZone? = zones.minByOrNull { zone ->
            cardRect.setToLastCardInZone(zone)
            mousePos.getDistanceToNearestRectangleSide(cardRect)
        }
        
        val currentCardCursor = cardCursor.getOrCompute()
        cardCursor.set(
            currentCardCursor.copy(
                zone = nearestZone ?: logic.zones.playerZones.first(),
                indexFromEnd = 0,
                lastMouseZoneCoordinates = null
            )
        )
    }

    private fun snapToCardZoneInDirection(direction: Direction) {
        val legalZones = getLegalCardZonesBasedOnCurrentDragInfo()
        snapToCardZoneInDirection(legalZones, direction)
    }

    private fun snapToCardZoneInDirection(zones: List<CardZone>, direction: Direction) {
        val currentCardCursor = cardCursor.getOrCompute()
        if (currentCardCursor.isMouseBased) {
            snapToNearestCardZoneToMouse(zones)
            return
        }

        val currentZone = currentCardCursor.zone
        val currentZoneX = currentZone.x.get()
        val currentZoneY = currentZone.y.get()
        val zonesExcludingCurrent = zones.filterNot { it == currentZone }
        
        val zonesOnSide = zonesExcludingCurrent
            .filter {
                when (direction) {
                    Direction.UP -> it.y.get() < currentZoneY
                    Direction.DOWN -> it.y.get() > currentZoneY
                    Direction.LEFT -> it.x.get() < currentZoneX
                    Direction.RIGHT -> it.x.get() > currentZoneX
                }
            }

        val nearestZone: CardZone? = zonesOnSide.minByOrNull { zone ->
            hypot(currentZoneX - zone.x.get(), currentZoneY - zone.y.get())
        }
        if (nearestZone == null) {
            return
        }

        cardCursor.set(
            currentCardCursor.copy(
                zone = nearestZone,
                indexFromEnd = getValidLastCursorIndexFromEndForZone(nearestZone),
                lastMouseZoneCoordinates = null
            )
        )
    }
    
    private fun CardCursor.isHoveringOverZone(): Boolean {
        return this.lastMouseZoneCoordinates != null || !this.isMouseBased
    }

    private fun switchToMouseFocus() {
        val currentCardCursor = cardCursor.getOrCompute()
        if (!currentCardCursor.isMouseBased) {
            cardCursor.set(currentCardCursor.copy(isMouseBased = true))
        }
    }
    
    private fun canNonCancelButtonOperationInterruptDragging(): Boolean {
        val dragging = getDraggingInfo() ?: return true

        return dragging.mouseMode != MouseMode.CLICK_AND_DRAG
    }
    
    private fun endDrag(newZone: CardZone?, isFromButtonInput: Boolean): Boolean {
        val dragging = getDraggingInfo() ?: return false

        if (isFromButtonInput && !canNonCancelButtonOperationInterruptDragging()) return false

        if (newZone == null || newZone == dragging.originalZone || !logic.canPlaceStackOnZone(dragging.cardStack, newZone)) {
            cancelDrag()
            return false
        }

        val myList = dragging.cardStack.cardList.toList()
        newZone.cardStack.cardList += myList

        logic.eventDispatcher.onCardStackPlacedDown(logic, dragging.cardStack, newZone)
        if (dragging.cardStack.cardList.size == 1 && newZone in logic.zones.foundationZones) {
            logic.eventDispatcher.onCardPlacedInFoundation(logic, dragging.cardStack.cardList.first(), newZone)
        }

        val newDragInfo = DragInfo.Deciding()
        dragInfo.set(newDragInfo)

        // Set card cursor to new position within stack (always legal to pick up)
        val originalCardCursor = dragging.initialCardCursor
        val currentCardCursor = cardCursor.getOrCompute()
        val newMouseZoneCoordinates: ZoneCoordinates? =
            originalCardCursor.lastMouseZoneCoordinates?.copy(
                zone = newZone, index = newZone.cardStack.cardList.size - myList.size
            )
        cardCursor.set(
            currentCardCursor.copy(
                zone = newZone,
                indexFromEnd = (myList.lastIndex).coerceAtLeast(0),
                lastMouseZoneCoordinates = newMouseZoneCoordinates,
                isMouseBased = !isFromButtonInput
            )
        )

        logic.checkTableauAfterActivity()

        return true
    }

    private fun attemptStartDrag(zoneCoords: ZoneCoordinates, initialMouseMode: MouseMode?): Boolean {
        if (isDragging()) return false

        if (!zoneCoords.zone.canDragFrom || zoneCoords.zone.isFlippedOver) {
            return false
        }

        val zoneCardList = zoneCoords.zone.cardStack.cardList
        val newSet = zoneCoords.getCardsToDrag()

        if (!logic.isStackValidToMove(newSet)) {
            return false
        }

        val currentCardCursor = cardCursor.getOrCompute()
        val newDragging = DragInfo.Dragging(zoneCoords, newSet, initialMouseMode, currentCardCursor)
        repeat(newSet.size) {
            zoneCardList.removeAt(zoneCoords.index)
        }
        dragInfo.set(newDragging)
        
        // Set card cursor to end of original zone (always a legal spot to drop/cancel)
        // lastMouseZoneCoordinates are no longer relevant since index has changed
        cardCursor.set(currentCardCursor.copy(indexFromEnd = 0, lastMouseZoneCoordinates = null))

        logic.eventDispatcher.onCardStackPickedUp(logic, newDragging.cardStack, zoneCoords.zone)

        return true
    }

    private fun isZoneSelectionLegal(zone: CardZone, indexFromEnd: Int): Boolean {
        val zones = logic.zones
        val zoneCardList = zone.cardStack.cardList
        
        if (zone.isFlippedOver) {
            return false
        }
        
        when (val currentDragInfo = getCurrentDragInfo()) {
            is DragInfo.Deciding -> {
                if (zoneCardList.isEmpty() || indexFromEnd !in zoneCardList.indices) {
                    return false
                }

                if (zone in zones.freeCellZones && indexFromEnd == 0 && zoneCardList.isNotEmpty()) {
                    return true
                }
                
                if (zone in zones.playerZones && zoneCardList.isNotEmpty()) {
                    val indexFromStart = zoneCardList.size - indexFromEnd - 1
                    return logic.isStackValidToMove(zoneCardList.subList(indexFromStart, zoneCardList.size))
                }
            }

            is DragInfo.Dragging -> {
                if (indexFromEnd != 0) {
                    return false
                }
                
                if (zone == currentDragInfo.originalZone) {
                    return true
                }
                
                if (logic.canPlaceStackOnZone(currentDragInfo.cardStack, zone)) {
                    return true
                }
            }
        }
        
        return false
    }
    
    private fun getLegalCardZonesBasedOnCurrentDragInfo(): List<CardZone> {
        return logic.zones.allPlaceableCardZones.filter { z ->
            isZoneSelectionLegal(z, 0)
        }
    }
    
    private fun updateLastIndexFromEndForZone(cursor: CardCursor) {
        lastIndexFromEndByZone[cursor.zone] = cursor.indexFromEnd
    }
    
    private fun getValidLastCursorIndexFromEndForZone(zone: CardZone): Int {
        val lastIndexFromEnd = lastIndexFromEndByZone.getOrDefault(zone, 0)
        
        val cardList = zone.cardStack.cardList
        if (cardList.isEmpty()) {
            return 0
        }
        
        if (lastIndexFromEnd !in cardList.indices) {
            return 0
        }
        
        if (!isZoneSelectionLegal(zone, lastIndexFromEnd)) {
            return 0
        }
        
        return lastIndexFromEnd
    }
    
    //endregion
    
    //region Getters
    
    fun isDragging(): Boolean {
        return dragInfo.getOrCompute() !is DragInfo.Deciding
    }
    
    fun getCurrentDragInfo(): DragInfo = dragInfo.getOrCompute()
    
    fun getDraggingInfo(): DragInfo.Dragging? = getCurrentDragInfo() as? DragInfo.Dragging
    
    fun getDecidingInfo(): DragInfo.Deciding? = getCurrentDragInfo() as? DragInfo.Deciding
    
    fun getCurrentCardCursor(): CardCursor = cardCursor.getOrCompute()
    
    //endregion

}