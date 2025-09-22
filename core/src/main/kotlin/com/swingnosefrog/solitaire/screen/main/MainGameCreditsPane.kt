package com.swingnosefrog.solitaire.screen.main

import com.badlogic.gdx.graphics.Color
import com.swingnosefrog.solitaire.Localization
import com.swingnosefrog.solitaire.fonts.SolitaireFonts
import com.swingnosefrog.solitaire.game.GameContainer
import paintbox.binding.ReadOnlyVar
import paintbox.font.Markup
import paintbox.font.PaintboxFont
import paintbox.ui.Pane
import paintbox.ui.RenderAlign
import paintbox.ui.area.Insets
import paintbox.ui.control.TextLabel
import paintbox.ui.element.RectElement
import paintbox.ui.element.RoundedRectElement


class MainGameCreditsPane(
    private val mainGameUi: MainGameUi,
    private val uiInputHandler: MainGameUi.IUiInputHandler,
) : CloseablePane() {

    private val fonts: SolitaireFonts get() = mainGameUi.mainGameScreen.main.fonts

    private val headingFont: PaintboxFont get() = fonts.uiHeadingFont
    private val mainSansSerifMarkup: Markup get() = fonts.uiMainSansSerifMarkup
    private val mainSerifMarkup: Markup get() = fonts.uiMainSerifMarkup
    

    init {
        val darker = dark.cpy().apply { a = 0.9f }
        containingPane.apply {
            this += RoundedRectElement(darker).apply {
                this.roundedRadius.set(16)
                this.padding.set(Insets(16f * 2))

                this += Pane().apply {
                    this += TextLabel(Localization["credits.title"], font = headingFont).apply {
                        this.margin.set(Insets(0f, 8f))
                        this.textColor.set(Color.WHITE)
                        this.renderAlign.set(RenderAlign.left)
                        this.bounds.height.set(64f)
                    }
                    this += RoundedRectElement(Color.WHITE).apply {
                        this.margin.set(Insets(4f, 8f))
                        this.bounds.y.set(64f)
                        this.bounds.height.set(10f)
                    }
                    this += Pane().apply {
                        this.bounds.y.set(64f + 10f)
                        this.bindHeightToParent(adjustBinding = { -bounds.y.use() })
                        this.margin.set(Insets(16f, 8f))
                    }
                }
            }
        }
    }

    override fun onClosePressed() {
        uiInputHandler.closeCreditsMenu()
    }
}