package com.swingnosefrog.solitaire.screen.main

import com.swingnosefrog.solitaire.fonts.SolitaireFonts
import paintbox.font.Markup
import paintbox.ui.Anchor
import paintbox.ui.area.Insets
import paintbox.ui.element.RoundedRectElement


class MainGameStatsPane(
    private val mainGameUi: MainGameUi,
    private val uiInputHandler: MainGameUi.IUiInputHandler,
) : CloseablePane() {

    private val fonts: SolitaireFonts get() = mainGameUi.mainGameScreen.main.fonts
    private val mainSerifMarkup: Markup get() = fonts.uiMainSerifMarkup


    init {
        containingPane.apply {
            this.bindHeightToSelfWidth(multiplier = 648f / 1152f)
            Anchor.Centre.configure(this)

            this += RoundedRectElement(dark).apply {
                this.roundedRadius.set(16)
                this.padding.set(Insets(16f))
            }
        }
    }

    override fun onClosePressed() {
        uiInputHandler.closeStatsMenu()
    }
}