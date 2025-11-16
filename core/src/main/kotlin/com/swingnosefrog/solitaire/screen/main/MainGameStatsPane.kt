package com.swingnosefrog.solitaire.screen.main

import com.badlogic.gdx.graphics.Color
import com.swingnosefrog.solitaire.Localization
import com.swingnosefrog.solitaire.fonts.SolitaireFonts
import paintbox.font.Markup
import paintbox.font.PaintboxFont
import paintbox.ui.Anchor
import paintbox.ui.Pane
import paintbox.ui.RenderAlign
import paintbox.ui.area.Insets
import paintbox.ui.control.TextLabel
import paintbox.ui.element.RoundedRectElement


class MainGameStatsPane(
    private val mainGameUi: MainGameUi,
    private val uiInputHandler: MainGameUi.IUiInputHandler,
) : CloseablePane() {

    private val fonts: SolitaireFonts get() = mainGameUi.mainGameScreen.main.fonts
    private val headingFont: PaintboxFont get() = fonts.uiHeadingFont
    private val mainSerifMarkup: Markup get() = fonts.uiMainSerifMarkup


    init {
        val darker = dark.cpy().apply { a = 0.9f }

        val contentPane = Pane().apply {

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