package com.swingnosefrog.solitaire.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Disposable
import com.swingnosefrog.solitaire.AbstractGameScreen
import com.swingnosefrog.solitaire.SolitaireGame
import com.swingnosefrog.solitaire.game.Card
import com.swingnosefrog.solitaire.game.assets.GameAssets
import com.swingnosefrog.solitaire.game.logic.CardStack
import com.swingnosefrog.solitaire.game.logic.CardZone
import com.swingnosefrog.solitaire.game.logic.GameEventListener
import com.swingnosefrog.solitaire.game.logic.GameInput
import com.swingnosefrog.solitaire.game.logic.GameInputProcessor
import com.swingnosefrog.solitaire.game.logic.GameLogic
import com.swingnosefrog.solitaire.game.rendering.GameRenderer
import paintbox.util.viewport.NoOpViewport


class TestSolitaireGameScreen(main: SolitaireGame) : AbstractGameScreen(main) {

    val batch: SpriteBatch = main.batch
    
    val gameLogic: GameLogic = GameLogic()
    val gameInput: GameInput get() = gameLogic.gameInput
    val gameRenderer: GameRenderer = GameRenderer(gameLogic, batch)
    val inputProcessor: InputProcessor = GameInputProcessor(gameInput, NoOpViewport(gameRenderer.camera))
    private val soundGameListener: TestSoundGameEventListener = TestSoundGameEventListener()
    
    init {
        gameLogic.eventDispatcher.addListener(soundGameListener)
    }
    
    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        super.render(delta)

        gameRenderer.render(delta)
    }

    override fun renderUpdate() {
        super.renderUpdate()
        
        gameLogic.renderUpdate(Gdx.graphics.deltaTime)
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            main.screen = TestSolitaireGameScreen(main)
            Gdx.app.postRunnable { 
                this.dispose()
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            gameLogic.animationContainer.renderUpdate(10f)
            gameLogic.checkTableauAfterActivity()
        }
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
    }

    override fun show() {
        super.show()
        main.inputMultiplexer.addProcessor(inputProcessor)
    }

    override fun hide() {
        super.hide()
        main.inputMultiplexer.removeProcessor(inputProcessor)
    }

    override fun dispose() {
        this.soundGameListener.dispose()
    }
    
    private class TestSoundGameEventListener : GameEventListener, Disposable {
        
        private val dealing: Sound get() = GameAssets.get<Sound>("sfx_game_dealing_loop")
        
        private var dealingLoopSoundId: Long = -1
        
        override fun dispose() {
            dealing.stop(dealingLoopSoundId)
        }

        override fun onDealingStart(gameLogic: GameLogic) {
            dealingLoopSoundId = dealing.play()
        }

        override fun onDealingEnd(gameLogic: GameLogic) {
            dealing.stop(dealingLoopSoundId)
            dealingLoopSoundId = -1
        }

        override fun onCardStackPickedUp(
            gameLogic: GameLogic,
            cardStack: CardStack,
            fromZone: CardZone,
        ) {
            if (cardStack.cardList.size >= 3) {
                GameAssets.get<Sound>("sfx_game_pickup_stack").play()
            } else {
                GameAssets.get<Sound>("sfx_game_pickup${MathUtils.random(1, 3)}").play()
            }
        }
        
        private fun playPlaceSound() {
            GameAssets.get<Sound>("sfx_game_place").play()
        }

        override fun onCardStackPickupCancelled(
            gameLogic: GameLogic,
            cardStack: CardStack,
            originalZone: CardZone,
        ) {
            playPlaceSound()
        }

        override fun onCardStackPlacedDown(
            gameLogic: GameLogic,
            cardStack: CardStack,
            toZone: CardZone,
        ) {
            playPlaceSound()
        }

        override fun onCardPlacedInFoundation(
            gameLogic: GameLogic,
            card: Card,
            foundationZone: CardZone,
        ) {
        }

        override fun onWidgetSetCompleted(
            gameLogic: GameLogic,
            freeCellZone: CardZone,
        ) {
        }

        override fun onFoundationZoneCompleted(
            gameLogic: GameLogic,
            foundationZone: CardZone,
        ) {
        }

        override fun onGameWon(gameLogic: GameLogic) {
        }

    }
}