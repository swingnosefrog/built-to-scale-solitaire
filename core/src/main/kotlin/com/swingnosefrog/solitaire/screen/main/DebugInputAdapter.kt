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
                    Input.Keys.G if !Gdx.input.isShiftDown() && !Gdx.input.isAltDown() -> {
                        mainGameScreen.startNewGame(
                            DeckInitializer.DebugAutoWin(startFromWonState = Gdx.input.isControlDown()),
                            breakWinStreakIfNotWon = false
                        )
                        return true
                    }

                    Input.Keys.G if !Gdx.input.isControlDown() && Gdx.input.isShiftDown() && !Gdx.input.isAltDown() -> {
                        mainGameScreen.startNewGame(gameLogic.deckInitializer, breakWinStreakIfNotWon = false)
                        return true
                    }

                    Input.Keys.COMMA if !gameLogic.isStillDealing.get() && !gameLogic.gameInput.isDragging() -> {
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