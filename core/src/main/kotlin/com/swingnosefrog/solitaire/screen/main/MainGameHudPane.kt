package com.swingnosefrog.solitaire.screen.main

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.swingnosefrog.solitaire.SolitaireGame
import com.swingnosefrog.solitaire.fonts.SolitaireFonts
import com.swingnosefrog.solitaire.game.GameContainer
import com.swingnosefrog.solitaire.settings.SolitaireSettings
import paintbox.binding.ReadOnlyVar
import paintbox.binding.Var
import paintbox.font.Markup
import paintbox.font.TextAlign
import paintbox.ui.Anchor
import paintbox.ui.Corner
import paintbox.ui.NoInputPane
import paintbox.ui.RenderAlign
import paintbox.ui.animation.Animation
import paintbox.ui.animation.TransitioningFloatVar
import paintbox.ui.area.Insets
import paintbox.ui.control.TextLabel
import paintbox.ui.element.RoundedRectElement
import paintbox.ui.layout.HBox
import paintbox.util.DecimalFormats
import java.util.*


class MainGameHudPane(
    private val mainGameUi: MainGameUi,
) : NoInputPane() {
    
    private val gameContainer: ReadOnlyVar<GameContainer> get() = mainGameUi.mainGameScreen.gameContainer

    private val fonts: SolitaireFonts get() = mainGameUi.mainGameScreen.main.fonts
    private val mainSansSerifMarkup: Markup get() = fonts.uiMainSansSerifMarkup
    
    private val settings: SolitaireSettings get() = SolitaireGame.instance.settings
    private val onAnyHudSettingsChanged: ReadOnlyVar<Unit>
    
    private val elapsedTimeClock: ReadOnlyVar<String>
    private val movesMadeString: ReadOnlyVar<String>

    init {
        onAnyHudSettingsChanged = Var.eagerBind {
            settings.gameplayShowTimer.use()
            settings.gameplayShowMoveCounter.use()
        }
        
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
        
        val dark = Color(0f, 0f, 0f, 0.4f)
        this += HBox().apply {
            Anchor.TopRight.configure(this)
            this.bindWidthToParent(adjust = -128f)
            this.bounds.height.set(48f)
            this.margin.set(Insets(0f, 16f))
            this.spacing.set(16f)
            
            this.rightToLeft.set(true)
            this.align.set(HBox.Align.RIGHT)

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

            fun createRoundedRect(): RoundedRectElement {
                return RoundedRectElement(dark).apply {
                    this.padding.set(Insets(8f))

                    this.roundedRadius.set(8)
                    this.roundedCorners.set(EnumSet.of(Corner.BOTTOM_LEFT, Corner.BOTTOM_RIGHT))
                }
            }

            fun createTextLabel(): TextLabel {
                return TextLabel("").apply {
                    this.markup.set(mainSansSerifMarkup)
                    this.textColor.set(Color.WHITE)
                    this.textAlign.set(TextAlign.CENTRE)
                    this.renderAlign.set(RenderAlign.center)
                }
            }

            onAnyHudSettingsChanged.addListenerAndFire { v ->
                v.getOrCompute() // Force it to be un-invalided
                this.temporarilyDisableLayouts {
                    this.removeAllChildren()

                    if (settings.gameplayShowMoveCounter.get()) {
                        this += createRoundedRect().apply {
                            this.bounds.width.set(250f)

                            this += createTextLabel().apply {
                                this.text.bind {
                                    val movesMadePortion = movesMadeString.use()
                                    "Moves: $movesMadePortion"
                                }
                            }
                        }
                    }

                    if (settings.gameplayShowTimer.get()) {
                        this += createRoundedRect().apply {
                            this.bounds.width.set(300f)

                            this += createTextLabel().apply {
                                this.text.bind {
                                    val clockPortion = elapsedTimeClock.use()
                                    "Time: $clockPortion"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}