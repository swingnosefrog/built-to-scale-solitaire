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
import com.swingnosefrog.solitaire.game.assets.CardAssetKey
import com.swingnosefrog.solitaire.game.assets.GameAssets
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
        val slotCardTex = GameAssets.get<Texture>(CardAssetKey.Slot.getAssetKey())
        logic.zones.allCardZones.forEach { zone ->
            batch.draw(slotCardTex, zone.x.get(), camera.viewportHeight - (zone.y.get() + CARD_HEIGHT), CARD_WIDTH, CARD_HEIGHT)
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
        batch.setColor(1f, 1f, 1f, 1f)

        val cardAssetKey: CardAssetKey = if (flippedOver) CardAssetKey.Back else this.cardAssetKey
        val tex = GameAssets.get<Texture>(cardAssetKey.getAssetKey())
        batch.draw(tex, x, camera.viewportHeight - (y + CARD_HEIGHT), CARD_WIDTH, CARD_HEIGHT)
    }
}