package com.swingnosefrog.solitaire.game.rendering

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Align
import com.swingnosefrog.solitaire.SolitaireGame
import com.swingnosefrog.solitaire.game.Card
import com.swingnosefrog.solitaire.game.CardSuit
import com.swingnosefrog.solitaire.game.CardSymbol
import com.swingnosefrog.solitaire.game.animation.CardPlayingAnimation
import com.swingnosefrog.solitaire.game.logic.CardStack
import com.swingnosefrog.solitaire.game.logic.GameLogic
import com.swingnosefrog.solitaire.game.logic.GameLogic.Companion.CARD_WIDTH
import com.swingnosefrog.solitaire.game.logic.GameLogic.Companion.CARD_HEIGHT
import paintbox.util.gdxutils.drawRect
import paintbox.util.gdxutils.fillRect
import paintbox.util.gdxutils.scaleMul


open class GameRenderer(
    protected val logic: GameLogic,
    protected val batch: SpriteBatch,
) {
    
    private val tableauColor: Color = Color.valueOf("125942")
    
    val camera: OrthographicCamera = OrthographicCamera().apply { 
        setToOrtho(false, logic.viewportWidth, logic.viewportHeight)
    }
    
    open fun render(deltaSec: Float) {
        camera.update()
        batch.projectionMatrix = camera.combined
        batch.begin()
        
        batch.color = tableauColor
        batch.fillRect(0f, 0f, camera.viewportWidth, camera.viewportHeight)
        
        batch.setColor(1f, 1f, 1f, 0.25f)
        logic.zones.allCardZones.forEach { zone ->
            batch.fillRect(zone.x.get(), camera.viewportHeight - (zone.y.get() + CARD_HEIGHT), CARD_WIDTH, CARD_HEIGHT)
        }
        batch.setColor(1f, 1f, 1f, 1f)
        
        logic.zones.allCardZones.forEach { zone ->
            zone.cardStack.render(zone.x.get(), zone.y.get(), zone.isFlippedOver)
        }
        batch.setColor(1f, 1f, 1f, 1f)

        for (playingAnimation in logic.animationContainer.getPlayingAnimations()) {
            if (playingAnimation.secondsElapsed < 0f) continue
            
            if (playingAnimation is CardPlayingAnimation) {
                playingAnimation.cardAnimation.card.render(playingAnimation.currentX, playingAnimation.currentY, false)
            }
        }
        batch.setColor(1f, 1f, 1f, 1f)
        
        logic.gameInput.getDraggingInfo()?.also { dragging ->
            dragging.cardStack.render(dragging.x, dragging.y, false)
        }
        
        batch.color = Color.WHITE
        batch.end()
    }
    
    protected open fun CardStack.render(x: Float, y: Float, isFlippedOver: Boolean) {
        val stackOffset = this.stackDirection.yOffset
        this.cardList.forEachIndexed { index, card ->
            card.render(x, y + index * stackOffset, isFlippedOver)
        }
    }
    
    protected open fun Card.render(x: Float, y: Float, flippedOver: Boolean) {
        if (flippedOver) {
            batch.setColor(229f / 255f, 110f / 255f, 110f / 255f, 1f)
            batch.fillRect(x, camera.viewportHeight - (y + CARD_HEIGHT), CARD_WIDTH, CARD_HEIGHT)
        } else {
            batch.setColor(1f, 1f, 1f, 1f)
            batch.fillRect(x, camera.viewportHeight - (y + CARD_HEIGHT), CARD_WIDTH, CARD_HEIGHT)
            
            val paintboxFont = SolitaireGame.instance.defaultFonts.debugFontBold
            paintboxFont.useFont(camera) { font ->
                font.color = when (this.suit) {
                    CardSuit.A -> Color.RED
                    CardSuit.B -> Color.FOREST
                    CardSuit.C -> Color.BLUE
                }
                font.scaleMul( (0.25f / CARD_HEIGHT) / font.capHeight)
                val text = when (this.symbol) {
                    CardSymbol.NUM_7 -> "7"
                    CardSymbol.NUM_6 -> "6"
                    CardSymbol.NUM_5 -> "5"
                    CardSymbol.NUM_4 -> "4"
                    CardSymbol.NUM_3 -> "3"
                    CardSymbol.NUM_2 -> "2"
                    CardSymbol.NUM_1 -> "1"
                    CardSymbol.WIDGET_HALF -> "W"
                    CardSymbol.WIDGET_ROD -> "R"
                    CardSymbol.SPARE -> "SP"
                }
                font.draw(batch, text, x + (3 / 32f / CARD_WIDTH), camera.viewportHeight - (y + CARD_HEIGHT * 0.05f))
                font.draw(batch, text, x + CARD_WIDTH * 0.5f, camera.viewportHeight - (y + CARD_HEIGHT * 0.5f), 0f,
                    Align.center, false)
                font.setColor(1f, 1f, 1f, 1f)
            }
        }
        
        batch.setColor(0f, 0f, 0f, 1f)
        val outline = 2 / 32f
        batch.drawRect(x, camera.viewportHeight - (y + CARD_HEIGHT), CARD_WIDTH, CARD_HEIGHT, outline / CARD_WIDTH, outline / CARD_HEIGHT)
        batch.setColor(1f, 1f, 1f, 1f)
    }
}