package com.swingnosefrog.solitaire.game

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.Viewport
import com.swingnosefrog.solitaire.game.audio.GameAudio
import com.swingnosefrog.solitaire.game.audio.GameMusic
import com.swingnosefrog.solitaire.game.logic.GameInput
import com.swingnosefrog.solitaire.game.logic.GameInputProcessor
import com.swingnosefrog.solitaire.game.logic.GameLogic
import com.swingnosefrog.solitaire.game.rendering.GameRenderer
import com.swingnosefrog.solitaire.soundsystem.SoundSystem


class GameContainer(
    gameLogicFactory: () -> GameLogic,
    batch: SpriteBatch,
    soundSystem: SoundSystem,
    private val gameMusic: GameMusic?,
) : Disposable {

    val gameLogic: GameLogic = gameLogicFactory()
    val gameInput: GameInput get() = gameLogic.gameInput
    
    val gameRenderer: GameRenderer = GameRenderer(gameLogic, batch)
    val viewport: Viewport get() = gameRenderer.viewport
    
    val inputProcessor: InputProcessor = GameInputProcessor(gameInput, viewport)
    
    val gameAudio: GameAudio = GameAudio(gameLogic, soundSystem)
    
    init {
        val gameMusicListener = gameMusic?.gameEventListener
        if (gameMusicListener != null) {
            gameLogic.eventDispatcher.addListener(gameMusicListener)
        }
    }

    fun resize(width: Int, height: Int) {
        gameRenderer.resize(width, height)
    }
    
    override fun dispose() {
        gameAudio.dispose()
        
        val gameMusicListener = gameMusic?.gameEventListener
        if (gameMusicListener != null) {
            gameLogic.eventDispatcher.removeListener(gameMusicListener)
        }
    }
}