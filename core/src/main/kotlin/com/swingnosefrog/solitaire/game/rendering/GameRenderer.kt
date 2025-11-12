package com.swingnosefrog.solitaire.game.rendering

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.swingnosefrog.solitaire.SolitaireGame
import com.swingnosefrog.solitaire.game.Card
import com.swingnosefrog.solitaire.game.animation.CardPlayingAnimation
import com.swingnosefrog.solitaire.game.assets.CardAssetKey
import com.swingnosefrog.solitaire.game.assets.CardSkin
import com.swingnosefrog.solitaire.game.assets.GameAssets
import com.swingnosefrog.solitaire.game.logic.CardStack
import com.swingnosefrog.solitaire.game.logic.CardZone
import com.swingnosefrog.solitaire.game.logic.DragInfo
import com.swingnosefrog.solitaire.game.logic.GameEventListener
import com.swingnosefrog.solitaire.game.logic.GameLogic
import com.swingnosefrog.solitaire.game.logic.GameLogic.Companion.CARD_HEIGHT
import com.swingnosefrog.solitaire.game.logic.GameLogic.Companion.CARD_WIDTH
import com.swingnosefrog.solitaire.game.logic.StackDirection
import paintbox.binding.BooleanVar
import paintbox.binding.ReadOnlyVar
import paintbox.binding.Var
import paintbox.registry.AssetRegistry
import paintbox.util.gdxutils.GdxRunnableTransition
import paintbox.util.gdxutils.fillRect


open class GameRenderer(
    protected val logic: GameLogic,
    protected val batch: SpriteBatch,
) {
    
    companion object {
        
        val DEFAULT_CAMERA_POSITION: CameraPosition = CameraPosition(0f, 0.25f, 0.7875f)
        val ZOOMED_OUT_CAMERA_POSITION: CameraPosition = CameraPosition(0f, 0f, 0.825f)
    }

    protected val tableauColor: Color = Color.valueOf("125942")

    val viewportWidth: Float = 18f // 20f
    val viewportHeight: Float = 11.25f

    val camera: OrthographicCamera = OrthographicCamera().apply {
        setToOrtho(false, this@GameRenderer.viewportWidth, this@GameRenderer.viewportHeight)
        this.position.set(0f, 0f, 0f)

        DEFAULT_CAMERA_POSITION.applyToCamera(this)
    }
    val viewport: Viewport = ExtendViewport(18f, 11.25f, 20f, 11.25f, camera)
    
    val shouldApplyViewport: BooleanVar = BooleanVar(true)
    
    val currentCardSkin: ReadOnlyVar<CardSkin> = Var { 
        if (SolitaireGame.instance.settings.gameplayUseClassicCardSkin.use()) CardSkin.CLASSIC else CardSkin.MODERN
    }
    
    protected val tallStackEventListener: TallStackEventListener = TallStackEventListener()
    
    init {
        logic.eventDispatcher.addListener(tallStackEventListener)
    }

    open fun render(deltaSec: Float) {
        val cam = this.camera
        val camWidth = cam.viewportWidth
        val camHeight = cam.viewportHeight
        
        cam.update()
        if (shouldApplyViewport.get()) {
            viewport.apply()
        }
        
        batch.projectionMatrix = cam.combined
        batch.begin()

        batch.color = tableauColor
        batch.fillRect(-camWidth / 2f, -camHeight / 2f, camWidth, camHeight)

        batch.setColor(1f, 1f, 1f, 0.25f)
        val slotCardTex = GameAssets.get<Texture>(CardAssetKey.Slot.getAssetKey(currentCardSkin.getOrCompute()))
        for (zone in logic.zones.allCardZones) {
            if (!zone.isOutlineVisible) continue
            batch.draw(slotCardTex, zone.x.get(), - (zone.y.get() + CARD_HEIGHT), CARD_WIDTH, CARD_HEIGHT)
        }
        batch.setColor(1f, 1f, 1f, 1f)

        logic.zones.allCardZones.forEach { zone ->
            zone.cardStack.render(zone.x.get(), zone.y.get(), zone.isFlippedOver, zone.cardStack.stackDirection == StackDirection.DOWN)
        }
        batch.setColor(1f, 1f, 1f, 1f)

        for (playingAnimation in logic.animationContainer.getPlayingAnimations()) {
            if (playingAnimation.secondsElapsed < 0f) continue

            if (playingAnimation is CardPlayingAnimation) {
                playingAnimation.cardAnimation.card.render(
                    playingAnimation.currentX,
                    playingAnimation.currentY,
                    flippedOver = false,
                    renderShadow = true
                )
            }
        }
        batch.setColor(1f, 1f, 1f, 1f)

        logic.gameInput.getDraggingInfo()?.also { dragging ->
            dragging.cardStack.render(dragging.x, dragging.y - 0.05f, isFlippedOver = false, renderShadow = true)
        }

        // TODO remove me
        run {
            val cardCursor = logic.gameInput.getCurrentCardCursor()
            val zone = cardCursor.zone
            val cardStack = zone.cardStack

            val currentDragInfo = logic.gameInput.getCurrentDragInfo()
            var indexFromEnd = cardCursor.indexFromEnd
            if (currentDragInfo is DragInfo.Dragging) {
                indexFromEnd -= 1
            }
            
            val x = zone.x.get()
            val y = zone.y.get() + cardStack.stackDirection.yOffset * (cardStack.cardList.size - indexFromEnd - 1).coerceAtLeast(0)
            
            val alpha = if (cardCursor.isMouseBased && cardCursor.lastMouseZoneCoordinates == null) 0.2f else 1f
            
            batch.setColor(0f, 0f, 1f, 0.35f * alpha)
            renderCardTex(CardAssetKey.Silhouette, x, y)
            
            batch.setColor(1f, 1f, 1f, 1f * alpha)
            val cursorTex = AssetRegistry.get<Texture>("ui_cursor_invert")
            val cursorWidth = CARD_WIDTH * 0.5f
            val cursorHeight = cursorWidth * (cursorTex.height.toFloat() / cursorTex.width)
            batch.draw(cursorTex, x + CARD_WIDTH, -(y + CARD_HEIGHT * 0.0625f), cursorWidth, cursorHeight)
        }

        batch.color = Color.WHITE
        batch.end()
    }

    protected open fun CardStack.render(x: Float, y: Float, isFlippedOver: Boolean, renderShadow: Boolean) {
        val stackOffset = this.stackDirection.yOffset
        this.cardList.forEachIndexed { index, card ->
            card.render(x, y + index * stackOffset, isFlippedOver, renderShadow)
        }
    }

    protected open fun Card.render(x: Float, y: Float, flippedOver: Boolean, renderShadow: Boolean) {
        if (renderShadow) {
            batch.setColor(0f, 0f, 0f, 0.35f)
            renderCardTex(CardAssetKey.Silhouette, x, y + 0.1f)
        }   
        
        val cardAssetKey: CardAssetKey = if (flippedOver) CardAssetKey.Back else this.cardAssetKey
        batch.setColor(1f, 1f, 1f, 1f)
        renderCardTex(cardAssetKey, x, y)
        
        batch.setColor(1f, 1f, 1f, 1f)
    }
    
    private fun renderCardTex(cardAssetKey: CardAssetKey, x: Float, y: Float) {
        val tex = GameAssets.get<Texture>(cardAssetKey.getAssetKey(currentCardSkin.getOrCompute()))
        batch.draw(tex, x, -(y + CARD_HEIGHT), CARD_WIDTH, CARD_HEIGHT)
    }
    
    fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    protected inner class TallStackEventListener : GameEventListener.Adapter() {

        private var didZoomOut: Boolean = false

        override fun onCardStackPlacedDown(
            gameLogic: GameLogic,
            cardStack: CardStack,
            toZone: CardZone,
        ) {
            if (!didZoomOut && gameLogic.isPlayerZoneAndTallStack(toZone)) {
                didZoomOut = true
                Gdx.app.postRunnable(
                    GdxRunnableTransition(
                        startValue = 0f,
                        endValue = 1f,
                        durationSec = 1.5f,
                        interpolation = Interpolation.pow3Out,
                    ) { currentValue, _ ->
                        DEFAULT_CAMERA_POSITION.lerp(ZOOMED_OUT_CAMERA_POSITION, currentValue).applyToCamera(camera)
                    })
            }
        }
    }
}