package com.swingnosefrog.solitaire.game.logic

import com.badlogic.gdx.math.Rectangle
import com.swingnosefrog.solitaire.game.Card
import com.swingnosefrog.solitaire.game.input.CardCursor
import com.swingnosefrog.solitaire.game.input.GameInput
import com.swingnosefrog.solitaire.game.input.MouseMode


sealed class DragInfo {

    class Deciding() : DragInfo()

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


        override fun updateMousePosition(input: GameInput, worldX: Float, worldY: Float) {
            super.updateMousePosition(input, worldX, worldY)
            
            x = worldX - mouseOffsetX
            y = worldY - mouseOffsetY

            if (mouseMode == null) {
                mouseMode = MouseMode.CLICK_THEN_CLICK
            }
        }
        
        fun toOverlapCheckRectangle(): Rectangle = Rectangle(x, y, GameLogic.CARD_WIDTH, GameLogic.CARD_HEIGHT)

    }
    
    open fun updateMousePosition(input: GameInput, worldX: Float, worldY: Float) {
    }
    
}