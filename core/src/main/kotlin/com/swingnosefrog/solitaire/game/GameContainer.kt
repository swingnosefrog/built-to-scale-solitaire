package com.swingnosefrog.solitaire.game

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.Viewport
import com.swingnosefrog.solitaire.game.audio.GameAudio
import com.swingnosefrog.solitaire.game.audio.music.GameMusic
import com.swingnosefrog.solitaire.game.input.GameInput
import com.swingnosefrog.solitaire.game.input.GameGdxInputProcessor
import com.swingnosefrog.solitaire.game.input.GameInputActionListener
import com.swingnosefrog.solitaire.game.logic.GameLogic
import com.swingnosefrog.solitaire.game.logic.GamePlayStats
import com.swingnosefrog.solitaire.game.rendering.GameRenderer
import com.swingnosefrog.solitaire.game.rendering.IGameRenderer
import com.swingnosefrog.solitaire.soundsystem.SoundSystem


class GameContainer(
    gameLogicFactory: () -> GameLogic,
    batch: SpriteBatch,
    soundSystem: SoundSystem,
    private val gameMusic: GameMusic?,
) : Disposable {

    val gameLogic: GameLogic = gameLogicFactory()
    val gameInput: GameInput get() = gameLogic.gameInput
    val gamePlayStats: GamePlayStats get() = gameLogic.gamePlayStats
    
    val gameRenderer: IGameRenderer = GameRenderer(gameLogic, batch)
    val viewport: Viewport get() = gameRenderer.viewport
    
    val gdxInputProcessor: InputProcessor = GameGdxInputProcessor(gameInput, viewport)
    val inputActionListener: GameInputActionListener = GameInputActionListener(gameInput)
    
    val gameAudio: GameAudio = GameAudio(gameLogic, soundSystem)
    
    init {
        val gameMusicEventListener = gameMusic?.gameEventListener
        if (gameMusicEventListener != null) {
            gameLogic.eventDispatcher.addListener(gameMusicEventListener)
        }
    }

    fun resize(width: Int, height: Int) {
        gameRenderer.resize(width, height)
    }
    
    override fun dispose() {
        gameAudio.dispose()
        
        val gameMusicEventListener = gameMusic?.gameEventListener
        if (gameMusicEventListener != null) {
            gameLogic.eventDispatcher.removeListener(gameMusicEventListener)
        }
    }
}