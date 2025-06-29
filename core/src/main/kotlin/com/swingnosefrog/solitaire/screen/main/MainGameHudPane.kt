package com.swingnosefrog.solitaire.screen.main

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.swingnosefrog.solitaire.fonts.SolitaireFonts
import com.swingnosefrog.solitaire.game.GameContainer
import paintbox.binding.ReadOnlyVar
import paintbox.binding.Var
import paintbox.font.Markup
import paintbox.ui.Pane
import paintbox.ui.RenderAlign
import paintbox.ui.animation.Animation
import paintbox.ui.animation.TransitioningFloatVar
import paintbox.ui.area.Insets
import paintbox.ui.control.TextLabel
import paintbox.util.DecimalFormats


class MainGameHudPane(
    private val mainGameUi: MainGameUi,
) : Pane() {
    
    private val gameContainer: ReadOnlyVar<GameContainer> get() = mainGameUi.mainGameScreen.gameContainer

    private val fonts: SolitaireFonts get() = mainGameUi.mainGameScreen.main.fonts
    private val mainSansSerifMarkup: Markup get() = fonts.uiMainSansSerifMarkup
    
    private val elapsedTimeClock: ReadOnlyVar<String>
    private val movesMadeString: ReadOnlyVar<String>

    init {
        elapsedTimeClock = Var {
            val gameContainer = gameContainer.use()
            val gameStats = gameContainer.gameStats

            val elapsedSec = gameStats.timeElapsedSec.use()
            val elapsedSecondsInt = elapsedSec.toInt()
            val elapsedMinutesPart = elapsedSecondsInt/ 60
            val elapsedSecondsPart = elapsedSecondsInt % 60
            val elapsedMillisPart = (elapsedSec % 1f * 1000).toInt()
            val elapsedCentisecondsPart = elapsedMillisPart / 10
            var clockPortion =
                "${DecimalFormats.format("00", elapsedMinutesPart)}:${DecimalFormats.format("00", elapsedSecondsPart)}[scale=0.75].${DecimalFormats.format("00", elapsedCentisecondsPart)}[]"

            clockPortion
        }
        
        movesMadeString = Var {
            val gameContainer = gameContainer.use()
            val gameStats = gameContainer.gameStats

            val movesMade = gameStats.movesMade.use()
            var movesMadePortion = DecimalFormats.format("#,###", movesMade)

            if (movesMadePortion.length < 3) {
                val extraZeroes = "0".repeat(3 - movesMadePortion.length)
                movesMadePortion = "[opacity=0.4]${extraZeroes}[]" + movesMadePortion
            }
            
            movesMadePortion
        }
        
        val dark = Color(0f, 0f, 0f, 0.5f)
        this += Pane().apply { 
            this += TextLabel(binding = {
                val clockPortion = elapsedTimeClock.use()
                val movesMadePortion = movesMadeString.use()
                
                "Time: $clockPortion | Moves: $movesMadePortion"
            }).apply { 
                this.markup.set(mainSansSerifMarkup)
                this.renderAlign.set(RenderAlign.topRight)
                this.textColor.set(Color.WHITE)
                this.backgroundColor.set(dark)
                this.renderBackground.set(true)
                this.bgPadding.set(Insets(8f))

                this.opacity.bind(
                    TransitioningFloatVar(
                        mainGameUi.animationHandler,
                        {
                            if (gameContainer.use().gameLogic.isStillDealing.use()) 0.6f else 1f
                        }, { currentValue, targetValue ->
                            if (targetValue > currentValue) {
                                // Only animate if increasing in opacity, otherwise instant
                                Animation(Interpolation.exp5, 0.25f, currentValue, targetValue)
                            } else null
                        })
                )
            }
        }
    }


}