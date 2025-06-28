package com.swingnosefrog.solitaire.screen.main

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.swingnosefrog.solitaire.game.logic.DeckInitializer
import paintbox.util.gdxutils.isShiftDown


class DebugInputAdapter(private val mainGameScreen: MainGameScreen) : InputAdapter() {
    
    override fun keyDown(keycode: Int): Boolean {
        val gameLogic = mainGameScreen.gameContainer.getOrCompute().gameLogic

        if (keycode == Input.Keys.R) {
            val usePrevDeckInitializer = if (Gdx.input.isShiftDown()) gameLogic.deckInitializer else null
            mainGameScreen.startNewGame(usePrevDeckInitializer ?: DeckInitializer.RandomSeed())

            return true
        } else if (keycode == Input.Keys.G) {
            mainGameScreen.startNewGame(DeckInitializer.DebugAutoWin)

            return true
        } else if (keycode == Input.Keys.SPACE) {
            gameLogic.animationContainer.renderUpdate(10f)
            gameLogic.checkTableauAfterActivity()
            
            return true
        }
        
        return false
    }
}