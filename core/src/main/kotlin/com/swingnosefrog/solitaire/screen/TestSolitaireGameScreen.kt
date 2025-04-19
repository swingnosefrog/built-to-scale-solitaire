package com.swingnosefrog.solitaire.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.swingnosefrog.solitaire.AbstractGameScreen
import com.swingnosefrog.solitaire.SolitaireGame
import com.swingnosefrog.solitaire.game.GameContainer
import com.swingnosefrog.solitaire.game.audio.GameMusic
import com.swingnosefrog.solitaire.game.logic.GameLogic
import com.swingnosefrog.solitaire.soundsystem.SoundSystem
import paintbox.util.gdxutils.isShiftDown


class TestSolitaireGameScreen(main: SolitaireGame) : AbstractGameScreen(main) {

    val batch: SpriteBatch = main.batch

    val soundSystem = SoundSystem.createDefaultSoundSystem()

    val gameMusic: GameMusic = GameMusic(soundSystem)
    var gameContainer: GameContainer = GameContainer({ GameLogic() }, batch, soundSystem, gameMusic)
        private set

    init {
        soundSystem.startRealtime()
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        super.render(delta)

        gameContainer.gameRenderer.render(delta)

        main.resetViewportToScreen()
    }

    override fun renderUpdate() {
        super.renderUpdate()

        val gameLogic = gameContainer.gameLogic
        gameLogic.renderUpdate(Gdx.graphics.deltaTime)

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            val oldGameContainer = this.gameContainer
            removeInputProcessor()
            
            val useOldRandomSeed = if (Gdx.input.isShiftDown()) gameLogic.randomSeed else null
            gameContainer = GameContainer({ GameLogic(useOldRandomSeed) }, batch, soundSystem, gameMusic)
            onResize(Gdx.graphics.width, Gdx.graphics.height)
            addInputProcessor()
            
            
            oldGameContainer.dispose()
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            gameLogic.animationContainer.renderUpdate(10f)
            gameLogic.checkTableauAfterActivity()
        }
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        onResize(width, height)
    }
    
    private fun onResize(width: Int, height: Int) {
        gameContainer.gameRenderer.resize(width, height)
    }

    override fun show() {
        super.show()
        addInputProcessor()
    }

    override fun hide() {
        super.hide()
        removeInputProcessor()
    }
    
    private fun addInputProcessor() {
        main.inputMultiplexer.addProcessor(gameContainer.inputProcessor)
    }
    
    private fun removeInputProcessor() {
        main.inputMultiplexer.removeProcessor(gameContainer.inputProcessor)
    }

    override fun dispose() {
        gameContainer.dispose()
        gameMusic.dispose()
        soundSystem.dispose()
    }

}