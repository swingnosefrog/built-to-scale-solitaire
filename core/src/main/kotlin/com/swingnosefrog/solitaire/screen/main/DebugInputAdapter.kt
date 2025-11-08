package com.swingnosefrog.solitaire.screen.main

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.swingnosefrog.solitaire.game.logic.DeckInitializer
import paintbox.Paintbox
import paintbox.util.gdxutils.isAltDown
import paintbox.util.gdxutils.isControlDown
import paintbox.util.gdxutils.isShiftDown


class DebugInputAdapter(
    private val mainGameScreen: MainGameScreen,
    private val ui: MainGameUi,
) : InputAdapter() {

    override fun keyDown(keycode: Int): Boolean {
        val gameLogic = mainGameScreen.gameContainer.getOrCompute().gameLogic

        when (ui.currentMenuState.getOrCompute()) {
            MainGameUi.MenuState.NONE -> {
                when (keycode) {
                    Input.Keys.G if !Gdx.input.isControlDown() && !Gdx.input.isAltDown() -> {
                        if (Gdx.input.isShiftDown()) {
                            mainGameScreen.startNewGame(gameLogic.deckInitializer)
                        } else {
                            mainGameScreen.startNewGame(DeckInitializer.DebugAutoWin)
                        }

                        return true
                    }

                    Input.Keys.SPACE if !gameLogic.isStillDealing.get() -> {
                        gameLogic.animationContainer.renderUpdate(10f)
                        gameLogic.checkTableauAfterActivity()

                        return true
                    }
                }
            }
            
            MainGameUi.MenuState.PAUSE_MENU -> {
                when (keycode) {
                    Input.Keys.R if ui.currentMenuState.getOrCompute() == MainGameUi.MenuState.PAUSE_MENU && Paintbox.debugMode.get() -> {
                        ui.debugReinitSceneRoot()
                        return true
                    }
                }
            }

            else -> {}
        }

        return false
    }
}