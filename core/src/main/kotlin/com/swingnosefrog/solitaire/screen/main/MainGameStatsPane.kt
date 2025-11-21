package com.swingnosefrog.solitaire.screen.main

import com.badlogic.gdx.graphics.Color
import com.swingnosefrog.solitaire.Localization
import com.swingnosefrog.solitaire.SolitaireGame
import com.swingnosefrog.solitaire.fonts.SolitaireFonts
import com.swingnosefrog.solitaire.statistics.Stat
import com.swingnosefrog.solitaire.statistics.StatsImpl
import paintbox.font.Markup
import paintbox.font.PaintboxFont
import paintbox.ui.Anchor
import paintbox.ui.Pane
import paintbox.ui.RenderAlign
import paintbox.ui.area.Insets
import paintbox.ui.control.TextLabel
import paintbox.ui.element.RoundedRectElement
import paintbox.ui.layout.ColumnarPane
import paintbox.ui.layout.VBox


class MainGameStatsPane(
    private val mainGameUi: MainGameUi,
    private val uiInputHandler: MainGameUi.IUiInputHandler,
) : CloseablePane() {

    private val fonts: SolitaireFonts get() = mainGameUi.mainGameScreen.main.fonts
    private val headingFont: PaintboxFont get() = fonts.uiHeadingFont
    private val mainSerifMarkup: Markup get() = fonts.uiMainSerifMarkup
    private val mainSansSerifMarkup: Markup get() = fonts.uiMainSansSerifBoldMarkup

    private val stats: StatsImpl = SolitaireGame.instance.stats

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
                    this += TextLabel(Localization["statistics.title"], font = headingFont).apply {
                        this.margin.set(Insets(0f, 8f))
                        this.textColor.set(Color.WHITE)
                        this.renderAlign.set(RenderAlign.left)
                        this.bounds.height.set(48f)
                        this.setScaleXY(0.75f)
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
    }

    override fun onClosePressed() {
        uiInputHandler.closeStatsMenu()
    }
}