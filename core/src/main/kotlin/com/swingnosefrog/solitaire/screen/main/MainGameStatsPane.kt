package com.swingnosefrog.solitaire.screen.main

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.swingnosefrog.solitaire.Localization
import com.swingnosefrog.solitaire.SolitaireGame
import com.swingnosefrog.solitaire.fonts.SolitaireFonts
import com.swingnosefrog.solitaire.inputmanager.IActionInputGlyph
import com.swingnosefrog.solitaire.inputmanager.InputManager
import com.swingnosefrog.solitaire.inputmanager.impl.InputActions
import com.swingnosefrog.solitaire.statistics.Stat
import com.swingnosefrog.solitaire.statistics.StatsImpl
import com.swingnosefrog.solitaire.steamworks.SteamStats
import paintbox.binding.BooleanVar
import paintbox.binding.FloatVar
import paintbox.binding.ReadOnlyBooleanVar
import paintbox.binding.ReadOnlyVar
import paintbox.binding.Var
import paintbox.binding.VarChangedListener
import paintbox.binding.WeakVarChangedListener
import paintbox.font.Markup
import paintbox.font.PaintboxFont
import paintbox.ui.Anchor
import paintbox.ui.Pane
import paintbox.ui.RenderAlign
import paintbox.ui.area.Insets
import paintbox.ui.border.SolidBorder
import paintbox.ui.control.TextLabel
import paintbox.ui.element.RectElement
import paintbox.ui.element.RoundedRectElement
import paintbox.ui.layout.ColumnarPane
import paintbox.ui.layout.VBox


class MainGameStatsPane(
    private val mainGameUi: MainGameUi,
    private val uiInputHandler: MainGameUi.IUiInputHandler,
) : CloseablePane() {

    private val inputManager: InputManager get() = mainGameUi.mainGameScreen.inputManager
    private val fonts: SolitaireFonts get() = mainGameUi.mainGameScreen.main.fonts
    private val headingFont: PaintboxFont get() = fonts.uiHeadingFont
    private val mainSerifMarkup: Markup get() = fonts.uiMainSerifMarkup
    private val mainSansSerifMarkup: Markup get() = fonts.uiMainSansSerifBoldMarkup

    private val resetStatsButton: InputActions = InputActions.NewGame
    private val newGameGlyph: ReadOnlyVar<List<IActionInputGlyph>> =
        inputManager.getGlyphsVarForAction(resetStatsButton)

    private val stats: StatsImpl = SolitaireGame.instance.stats

    private val holdToResetProgress: FloatVar = FloatVar(0f)
    private val isHoldingToResetProgress: ReadOnlyBooleanVar = BooleanVar { holdToResetProgress.use() > 0f }
    private val menuStateListener: VarChangedListener<MainGameUi.MenuState>
    
    init {
        val darker = dark.cpy().apply { a = 0.9f }

        fun Pane.setUpStatBox(stat: Stat, placeholderIfZero: Boolean = true) {
            this += TextLabel(Localization[stat.localizationId]).apply {
                this.bindHeightToParent(multiplier = 0.5f)
                this.margin.set(Insets(0f, 4f))
                this.renderAlign.set(RenderAlign.center)
                this.markup.set(mainSerifMarkup)
                this.textColor.set(Color.WHITE)
            }
            val formattedValue = stat.formatter.format(stat.value)
            this += TextLabel(binding = {
                val regularValue = formattedValue.use()
                if (placeholderIfZero && stat.value.use() == 0) {
                    "---"
                } else regularValue
            }).apply {
                Anchor.BottomLeft.configure(this)
                this.bindHeightToParent(multiplier = 0.5f)
                this.margin.set(Insets(0f, 4f))
                this.renderAlign.set(RenderAlign.center)
                this.markup.set(mainSansSerifMarkup)
                this.textColor.set(Color.WHITE)
            }
        }

        val contentPane = Pane().apply {
            this += VBox().apply {
                this.spacing.set(12f)
                this.align.set(VBox.Align.CENTRE)
                this += ColumnarPane(3, false).apply {
                    this.bounds.height.set(96f)

                    this[0].setUpStatBox(stats.movesMade, placeholderIfZero = false)
                    this[1].setUpStatBox(stats.gamesWon, placeholderIfZero = false)
                    this[2].setUpStatBox(stats.gamesDealt, placeholderIfZero = false)
                }
                this += ColumnarPane(2, false).apply {
                    this.bounds.height.set(96f)

                    this[0].setUpStatBox(stats.currentWinStreak, placeholderIfZero = false)
                    this[1].setUpStatBox(stats.bestWinStreak, placeholderIfZero = false)
                }
                this += ColumnarPane(3, false).apply {
                    this.bounds.height.set(96f)

                    this[0].setUpStatBox(stats.shortestGameMoves)
                    this[1].setUpStatBox(stats.averageGameMoves)
                    this[2].setUpStatBox(stats.longestGameMoves)
                }
                this += ColumnarPane(3, false).apply {
                    this.bounds.height.set(96f)

                    this[0].setUpStatBox(stats.shortestGameTime)
                    this[1].setUpStatBox(stats.averageGameTime)
                    this[2].setUpStatBox(stats.longestGameTime)
                }
            }
            this += TextLabel(Localization["statistics.averageFootnote", listOf(StatsImpl.ROLLING_AVERAGE_GAME_COUNT)]).apply {
                this.bounds.height.set(32f)
                this.bindVarToParentHeight(this.bounds.y)
                this.renderAlign.set(RenderAlign.top)
                this.setScaleXY(0.75f)
                this.markup.set(mainSerifMarkup)
                this.textColor.set(Color.LIGHT_GRAY)
            }
        }

        containingPane.apply {
            this += RoundedRectElement(darker).apply {
                this.bindHeightToSelfWidth(multiplier = 648f / 1152f)
                Anchor.Centre.configure(this)
                this.roundedRadius.set(16)
                this.padding.set(Insets(16f * 2))

                this += Pane().apply {
                    this += Pane().apply {
                        this.bounds.height.set(48f)
                        this.margin.set(Insets(0f, 8f))

                        this += TextLabel(Localization["statistics.title"], font = headingFont).apply {
                            Anchor.TopLeft.configure(this)
                            this.bindWidthToParent(multiplier = 0.5f)
                            this.margin.set(Insets(0f, 0f, 0f, 16f))
                            this.textColor.set(Color.WHITE)
                            this.renderAlign.set(RenderAlign.left)
                            this.setScaleXY(0.75f)
                        }
                        this += Pane().apply {
                            Anchor.TopRight.configure(this)
                            this.bindWidthToParent(multiplier = 0.5f)
                            this.margin.set(Insets(0f, 0f, 16f, 0f))

                            this += Pane().apply {
                                this.visible.bind(isHoldingToResetProgress)
                                this.border.set(Insets(3f))
                                this.borderStyle.set(SolidBorder(Color.WHITE))
                                
                                this += RectElement(Color.RED).apply {
                                    this.bindWidthToParent(multiplierBinding = {
                                        holdToResetProgress.use().coerceIn(0f, 1f)
                                    }, adjustBinding = { 0f })
                                }
                            }
                            this += TextLabel(
                                binding = {
                                    val resetProgress = holdToResetProgress.use()
                                    if (resetProgress >= 0f) {
                                        val key =
                                            if (isHoldingToResetProgress.use()) "statistics.reset.holding" else "statistics.reset.hint"
                                        Localization[key, Var {
                                            listOf(
                                                newGameGlyph.use().firstOrNull()?.promptFontText
                                            )
                                        }]
                                    } else {
                                        Localization["statistics.reset.done"]
                                    }.use()
                                }
                            ).apply {
                                this.markup.set(mainSerifMarkup)
                                this.textColor.set(Color.WHITE)
                                this.renderAlign.set(RenderAlign.center)
                                this.setScaleXY(0.75f)

                                this.backgroundColor.set(Color(0f, 0f, 0f, 0.5f))
                                this.bgPadding.set(Insets(4f))
                                this.renderBackground.bind(isHoldingToResetProgress)
                            }
                        }
                    }
                    this += RoundedRectElement(Color.WHITE).apply {
                        this.margin.set(Insets(6f, 8f))
                        this.bounds.y.set(48f)
                        this.bounds.height.set(14f)
                    }
                    this += contentPane.apply {
                        this.bounds.y.set(48f + 14f)
                        this.bindHeightToParent(adjustBinding = { -bounds.y.use() })
                        this.margin.set(Insets(16f, 8f))
                    }
                }
            }
        }

        menuStateListener = WeakVarChangedListener { newState ->
            if (newState.getOrCompute() == MainGameUi.MenuState.STATS) {
                holdToResetProgress.set(0f)
            }
        }
        mainGameUi.currentMenuState.addListener(menuStateListener)
    }

    private fun resetStats() {
        val gameStats = stats
        gameStats.resetToResetValues()
        gameStats.persist()

        val steamStats = SteamStats
        steamStats.resetAllStats()
        steamStats.persistStats()
    }

    override fun renderSelf(originX: Float, originY: Float, batch: SpriteBatch) {
        if (this.opacity.get() == 1f) {
            val input = inputManager
            val deltaTime = Gdx.graphics.deltaTime

            val currentProgress = holdToResetProgress.get()
            if (input.isDigitalActionPressed(resetStatsButton)) {
                if (currentProgress >= 0f) {
                    val timeToReset = 10.0
                    holdToResetProgress.set((currentProgress + (deltaTime / timeToReset).coerceAtMost(0.1)).toFloat())
                    if (holdToResetProgress.get() >= 1f) {
                        holdToResetProgress.set(-1f)
                        resetStats()
                    }
                }
            } else {
                if (currentProgress > 0f) {
                    holdToResetProgress.set(0f)
                }
            }
        }

        super.renderSelf(originX, originY, batch)
    }

    override fun onClosePressed() {
        uiInputHandler.closeStatsMenu()
    }
}