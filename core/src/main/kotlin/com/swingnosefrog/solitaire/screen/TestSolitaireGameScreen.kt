package com.swingnosefrog.solitaire.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.swingnosefrog.solitaire.AbstractGameScreen
import com.swingnosefrog.solitaire.SolitaireGame
import com.swingnosefrog.solitaire.game.audio.GameAudio
import com.swingnosefrog.solitaire.game.audio.GameMusic
import com.swingnosefrog.solitaire.game.logic.GameInput
import com.swingnosefrog.solitaire.game.logic.GameInputProcessor
import com.swingnosefrog.solitaire.game.logic.GameLogic
import com.swingnosefrog.solitaire.game.rendering.GameRenderer
import com.swingnosefrog.solitaire.soundsystem.SoundSystem
import paintbox.util.gdxutils.isShiftDown


class TestSolitaireGameScreen(main: SolitaireGame, randomSeed: Long? = null) : AbstractGameScreen(main) {

    val batch: SpriteBatch = main.batch
    
    val gameLogic: GameLogic = GameLogic(randomSeed)
    val gameRenderer: GameRenderer = GameRenderer(gameLogic, batch)
    val gameInput: GameInput get() = gameLogic.gameInput
    val inputProcessor: InputProcessor = GameInputProcessor(gameInput, gameRenderer.viewport)
    val soundSystem = SoundSystem.createDefaultSoundSystem()
    val gameMusic: GameMusic = GameMusic(soundSystem)
    val gameAudio: GameAudio = GameAudio(gameLogic, soundSystem, music = gameMusic)
    
    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        super.render(delta)

        gameRenderer.render(delta)
        
        main.resetViewportToScreen()
    }

    override fun renderUpdate() {
        super.renderUpdate()
        
        gameLogic.renderUpdate(Gdx.graphics.deltaTime)
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            val useOldRandomSeed = if (Gdx.input.isShiftDown()) this.gameLogic.randomSeed else null
            main.screen = TestSolitaireGameScreen(main, useOldRandomSeed)
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
        gameRenderer.resize(width, height)
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
        gameAudio.dispose()
        soundSystem.dispose()
    }
    
}