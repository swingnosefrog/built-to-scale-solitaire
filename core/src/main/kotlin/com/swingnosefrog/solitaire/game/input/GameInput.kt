package com.swingnosefrog.solitaire.game.input

import com.badlogic.gdx.math.Rectangle
import com.swingnosefrog.solitaire.game.logic.CardZone
import com.swingnosefrog.solitaire.game.logic.DragInfo
import com.swingnosefrog.solitaire.game.logic.GameLogic
import com.swingnosefrog.solitaire.game.logic.ZoneCoordinates
import paintbox.binding.BooleanVar
import paintbox.binding.Var
import paintbox.util.gdxutils.maxX
import paintbox.util.gdxutils.maxY
import kotlin.math.max
import kotlin.math.min

class GameInput(val logic: GameLogic) {

    val inputsDisabled: BooleanVar = BooleanVar(false)
    
    private val dragInfo: Var<DragInfo> = Var(DragInfo.Nothing)
    
    fun isDragging(): Boolean {
        return dragInfo.getOrCompute() !is DragInfo.Nothing
    }

    fun cancelDrag() {
        val dragging = getDraggingInfo() ?: return
        
        val myList = dragging.cardStack.cardList.toList()
        dragging.originalZone.cardStack.cardList.addAll(myList)

        logic.eventDispatcher.onCardStackPickupCancelled(logic, dragging.cardStack, dragging.originalZone)
        dragInfo.set(DragInfo.Nothing)
        logic.checkTableauAfterActivity()
    }
    
    fun endDrag(newZone: CardZone): Boolean {
        val dragging = getDraggingInfo() ?: return false
        
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
        dragInfo.set(DragInfo.Nothing)
        logic.checkTableauAfterActivity()

        return true
    }

    fun attemptStartDrag(zoneCoords: ZoneCoordinates) {
        if (isDragging()) return

        if (!zoneCoords.zone.canDragFrom || zoneCoords.zone.isFlippedOver) {
            return
        }
        
        val zoneCardList = zoneCoords.zone.cardStack.cardList
        val newSet = zoneCoords.getCardsToDrag()

        if (!logic.isStackValidToMove(newSet)) {
            return
        }

        val newDragging = DragInfo.Dragging(zoneCoords, newSet)
        repeat(newSet.size) {
            zoneCardList.removeAt(zoneCoords.index)
        }
        dragInfo.set(newDragging)
        
        logic.eventDispatcher.onCardStackPickedUp(logic, newDragging.cardStack, zoneCoords.zone)
    }

    fun updateDrag(worldX: Float, worldY: Float) {
        val dragging = getDraggingInfo() ?: return
        
        dragging.updatePosition(worldX, worldY)
    }
    
    fun getDraggingInfo(): DragInfo.Dragging? = dragInfo.getOrCompute() as? DragInfo.Dragging

    fun getNearestOverlappingDraggingZone(): CardZone? {
        val dragging = getDraggingInfo() ?: return null
        
        var nearest: CardZone? = null
        var mostArea = 0f

        val dragX = dragging.x
        val dragY = dragging.y
        val dragW = GameLogic.CARD_WIDTH
        val dragH = GameLogic.CARD_HEIGHT // Only the topmost card of the stack counts for area checking
        val dragRect = Rectangle(dragX, dragY, dragW, dragH)

        for (zone in logic.zones.placeableCardZones) {
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
}