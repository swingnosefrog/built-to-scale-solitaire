package com.swingnosefrog.solitaire.screen.main

import com.badlogic.gdx.graphics.Color
import com.swingnosefrog.solitaire.fonts.SolitaireFonts
import com.swingnosefrog.solitaire.game.GameContainer
import paintbox.binding.ReadOnlyVar
import paintbox.font.Markup
import paintbox.ui.Pane
import paintbox.ui.RenderAlign
import paintbox.ui.area.Insets
import paintbox.ui.control.TextLabel
import paintbox.util.DecimalFormats


class MainGameHudPane(
    private val mainGameUi: MainGameUi,
) : Pane() {
    
    private val gameContainer: ReadOnlyVar<GameContainer> get() = mainGameUi.mainGameScreen.gameContainer

    private val fonts: SolitaireFonts get() = mainGameUi.mainGameScreen.main.fonts
    private val mainSansSerifMarkup: Markup get() = fonts.uiMainSansSerifMarkup

    init {
        val dark = Color(0f, 0f, 0f, 0.5f)
        this += Pane().apply { 
//            this.margin.set(Insets(32f))
            
            this += TextLabel(binding = {
                val gameContainer = gameContainer.use()
                val gameStats = gameContainer.gameStats
                
                val movesMade = gameStats.movesMade.use()
                val movesMadePortion = DecimalFormats.format("#,###", movesMade)
                
                val elapsedSeconds = gameStats.timeElapsedSec.use().toInt()
                val elapsedMinutesPart = elapsedSeconds / 60
                val elapsedSecondsPart = elapsedSeconds % 60
                var clockPortion = "${DecimalFormats.format("00", elapsedMinutesPart)}:${DecimalFormats.format("00", elapsedSecondsPart)}"
                if (gameContainer.gameLogic.isStillDealing.use()) {
                    clockPortion = "[color=#FFFFFF99]${clockPortion}[]"
                }
                
                "Time: $clockPortion | Moves: $movesMadePortion"
            }).apply { 
                this.markup.set(mainSansSerifMarkup)
                this.renderAlign.set(RenderAlign.topRight)
                this.textColor.set(Color.WHITE)
                this.backgroundColor.set(dark)
                this.renderBackground.set(true)
                this.bgPadding.set(Insets(8f))
            }
        }
    }


}