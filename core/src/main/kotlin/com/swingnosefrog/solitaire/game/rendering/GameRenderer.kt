package com.swingnosefrog.solitaire.game.rendering

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.swingnosefrog.solitaire.SolitaireGame
import com.swingnosefrog.solitaire.game.Card
import com.swingnosefrog.solitaire.game.animation.AnimationContainer
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
import com.swingnosefrog.solitaire.game.rendering.vfx.SlamVfxAnimation
import com.swingnosefrog.solitaire.game.rendering.vfx.SlamVfxPlayingAnimation
import paintbox.binding.BooleanVar
import paintbox.binding.LongVar
import paintbox.binding.ReadOnlyBooleanVar
import paintbox.binding.ReadOnlyVar
import paintbox.binding.Var
import paintbox.util.gdxutils.GdxRunnableTransition
import paintbox.util.gdxutils.fillRect
import paintbox.util.gdxutils.setColor
import paintbox.util.wave.SineWave


class GameRenderer(
    private val logic: GameLogic,
    private val batch: SpriteBatch,
) : IGameRenderer {
    
    companion object {
        
        val DEFAULT_CAMERA_POSITION: CameraPosition = CameraPosition(0f, 0.25f, 0.7875f)
        val ZOOMED_OUT_CAMERA_POSITION: CameraPosition = CameraPosition(0f, 0f, 0.825f)
        
        private const val SHADOW_OFFSET_Y: Float = 0.1f
    }

    private val cursorSelectionColor: Color = Color.valueOf("FF8400")
    private val tableauColor: Color = Color.valueOf("125942")

    val viewportWidth: Float = 18f // 20f
    val viewportHeight: Float = 11.25f

    val camera: OrthographicCamera = OrthographicCamera().apply {
        setToOrtho(false, this@GameRenderer.viewportWidth, this@GameRenderer.viewportHeight)
        this.position.set(0f, 0f, 0f)

        DEFAULT_CAMERA_POSITION.applyToCamera(this)
    }
    override val viewport: Viewport = ExtendViewport(18f, 11.25f, 20f, 11.25f, camera)
    override val shouldApplyViewport: BooleanVar = BooleanVar(true)
    
    val currentCardSkin: ReadOnlyVar<CardSkin> = Var {
        SolitaireGame.instance.settings.gameplayCardSkin.use()
    }
    
    private val rendererEventListener: RendererEventListener = RendererEventListener()
    
    private val showCardCursorInMouseModeSetting: ReadOnlyBooleanVar = BooleanVar {
        SolitaireGame.instance.settings.gameplayShowCardCursorInMouseMode.use()
    }
    private val wasCardCursorRenderedLastFrame: BooleanVar = BooleanVar(false)
    private val cardCursorBlinkOffsetMs: LongVar = LongVar(0L)
    
    private val vfxAnimationContainer: AnimationContainer = AnimationContainer()
    
    init {
        logic.eventDispatcher.addListener(rendererEventListener)
        wasCardCursorRenderedLastFrame.addListener { 
            cardCursorBlinkOffsetMs.set(System.currentTimeMillis())
        }
    }

    override fun render(deltaSec: Float) {
        vfxAnimationContainer.renderUpdate(deltaSec)
        
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

        for (playingAnimation in this.vfxAnimationContainer.getPlayingAnimations()) {
            if (playingAnimation.secondsElapsed < 0f) continue

            when (playingAnimation) {
                is SlamVfxPlayingAnimation -> {
                    renderSlamVfx(playingAnimation)
                }
            }
        }
        batch.setColor(1f, 1f, 1f, 1f)
        
        for (playingAnimation in logic.animationContainer.getPlayingAnimations()) {
            if (playingAnimation.secondsElapsed < 0f) continue

            when (playingAnimation) {
                is CardPlayingAnimation -> {
                    playingAnimation.cardAnimation.card.render(
                        playingAnimation.currentX,
                        playingAnimation.currentY,
                        flippedOver = playingAnimation.cardAnimation.isFlippedOver,
                        renderShadow = true
                    )
                }
            }
        }
        batch.setColor(1f, 1f, 1f, 1f)

        logic.gameInput.getDraggingInfo()?.also { dragging ->
            dragging.cardStack.render(dragging.x, dragging.y - 0.075f, isFlippedOver = false, renderShadow = true)
        }

        renderCardCursor()

        batch.color = Color.WHITE
        batch.end()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    private fun CardStack.render(x: Float, y: Float, isFlippedOver: Boolean, renderShadow: Boolean, shadowOffsetY: Float = SHADOW_OFFSET_Y) {
        val stackOffset = this.stackDirection.yOffset
        this.cardList.forEachIndexed { index, card ->
            card.render(x, y + index * stackOffset, isFlippedOver, renderShadow, shadowOffsetY)
        }
    }

    private fun Card.render(x: Float, y: Float, flippedOver: Boolean, renderShadow: Boolean, shadowOffsetY: Float = SHADOW_OFFSET_Y) {
        if (renderShadow) {
            batch.setColor(0f, 0f, 0f, 0.35f)
            renderCardTex(CardAssetKey.Silhouette, x, y + shadowOffsetY)
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

    private fun renderCardCursor() {
        val gameInput = logic.gameInput
        val cardCursor = gameInput.getCurrentCardCursor()

        if (gameInput.inputsDisabled.get() || logic.gameWon.get() || logic.isStillDealing.get() ||
            (cardCursor.isMouseBased && 
                    (!showCardCursorInMouseModeSetting.get() || cardCursor.lastMouseZoneCoordinates == null))) {
            wasCardCursorRenderedLastFrame.set(false)
            return
        }
        wasCardCursorRenderedLastFrame.set(true)

        val zone = cardCursor.zone
        val cardStack = zone.cardStack

        val currentDragInfo = gameInput.getCurrentDragInfo()
        var indexFromEnd = cardCursor.indexFromEnd
        if (currentDragInfo is DragInfo.Dragging) {
            indexFromEnd -= 1 // Show future position instead of "index 0" position
        }

        val x = zone.x.get()
        val y = zone.y.get() + cardStack.stackDirection.yOffset * (cardStack.cardList.size - indexFromEnd - 1).coerceAtLeast(0)

        val blinkPeriodSec = 1.2f
        val blinkProgress = SineWave.getWaveValue(blinkPeriodSec, offsetMs = -(cardCursorBlinkOffsetMs.get()))
        batch.setColor(cursorSelectionColor, alpha = MathUtils.lerp(0.3f, 0.6f, blinkProgress))
        renderCardTex(CardAssetKey.Hover, x, y)

        val cursorTexAsset = if (currentDragInfo is DragInfo.Dragging)
            CardAssetKey.CardCursorArrowPressed 
        else CardAssetKey.CardCursorArrow
        val cursorTex = GameAssets.get<Texture>(cursorTexAsset.getAssetKey(currentCardSkin.getOrCompute()))
        val cursorWidth = CARD_WIDTH * 0.4f
        val cursorHeight = cursorWidth * (cursorTex.height.toFloat() / cursorTex.width)
        batch.setColor(1f, 1f, 1f, 1f)
        batch.draw(cursorTex, x + CARD_WIDTH * 1.025f, -(y + CARD_HEIGHT * 0.0125f), cursorWidth, cursorHeight)
    }
    
    private fun renderSlamVfx(animation: SlamVfxPlayingAnimation) {
        val slamVfx = animation.slamVfx
        val tex = GameAssets.get<Texture>(if (slamVfx.isLarge) "vfx_slam_large" else "vfx_slam_small")

        val zoneX = slamVfx.cardZone.x.get()
        val zoneY = slamVfx.cardZone.y.get()
        val zoneWidth = CARD_WIDTH
        val zoneHeight = CARD_HEIGHT

        val batch = batch
        val vfxWidth = (475f / 720f) * zoneWidth
        val vfxHeight = (1504f / 1080f) * zoneHeight
        val alphaInterpolation = Interpolation.linear
        batch.setColor(1f, 1f, 1f, alphaInterpolation.apply(1f, 0f, animation.getProgress()))
        batch.draw(tex, zoneX - vfxWidth, -(zoneY + (zoneHeight + vfxHeight) / 2), vfxWidth, vfxHeight)
        batch.draw(tex, zoneX + zoneWidth + vfxWidth, -(zoneY + (zoneHeight + vfxHeight) / 2), -vfxWidth, vfxHeight)
        
        batch.setColor(1f, 1f, 1f, 1f)
    }

    private inner class RendererEventListener : GameEventListener.Adapter() {

        private var didZoomOut: Boolean = false
        
        private fun checkZoomOut(gameLogic: GameLogic, toZone: CardZone) {
            if (!didZoomOut && gameLogic.isPlayerZoneAndTallStack(toZone)) {
                didZoomOut = true
                Gdx.app.postRunnable(
                    GdxRunnableTransition(
                        startValue = 0f,
                        endValue = 1f,
                        durationSec = 1.5f,
                        interpolation = Interpolation.pow3In,
                    ) { currentValue, _ ->
                        DEFAULT_CAMERA_POSITION.lerp(ZOOMED_OUT_CAMERA_POSITION, currentValue).applyToCamera(camera)
                    }.toRunnable()
                )
            }
        }
        
        private fun playSlamVfx(large: Boolean, cardZone: CardZone) {
            vfxAnimationContainer.enqueueAnimation(
                SlamVfxAnimation(cardZone, large, durationSec = 0.2f, delaySec = 0f)
            )
        }

        override fun onCardStackPlacedDown(gameLogic: GameLogic, cardStack: CardStack, toZone: CardZone) {
            checkZoomOut(gameLogic, toZone)
        }

        override fun onWidgetSetCompleted(gameLogic: GameLogic, freeCellZone: CardZone) {
            playSlamVfx(true, freeCellZone)
        }

        override fun onCardPlacedInFoundation(gameLogic: GameLogic, card: Card, foundationZone: CardZone) {
            if (card.symbol.isNumeric()) {
                playSlamVfx(false, foundationZone)
            }
        }

        override fun onFoundationZoneCompleted(gameLogic: GameLogic, foundationZone: CardZone) {
            playSlamVfx(true, foundationZone)
        }
    }
}