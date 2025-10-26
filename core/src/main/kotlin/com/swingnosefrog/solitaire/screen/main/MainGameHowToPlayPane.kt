package com.swingnosefrog.solitaire.screen.main

import com.badlogic.gdx.graphics.Color
import com.swingnosefrog.solitaire.fonts.SolitaireFonts
import com.swingnosefrog.solitaire.game.GameContainer
import com.swingnosefrog.solitaire.ui.FourPane
import paintbox.binding.ReadOnlyVar
import paintbox.font.Markup
import paintbox.ui.Anchor
import paintbox.ui.Pane
import paintbox.ui.area.Insets
import paintbox.ui.element.RoundedRectElement


class MainGameHowToPlayPane(
    private val mainGameUi: MainGameUi,
    private val uiInputHandler: MainGameUi.IUiInputHandler,
) : CloseablePane() {
    
    private val gameContainer: ReadOnlyVar<GameContainer> get() = mainGameUi.mainGameScreen.gameContainer

    private val fonts: SolitaireFonts get() = mainGameUi.mainGameScreen.main.fonts
    private val mainSansSerifMarkup: Markup get() = fonts.uiMainSansSerifMarkup
    

    init {
        containingPane.apply {
            this += RoundedRectElement(dark).apply {
                this.roundedRadius.set(16)
                this.padding.set(Insets(16f))
                this.bindHeightToParent(adjust = -(48f + 24f))

                this += FourPane(Color.WHITE, 2f).apply {
                }
            }

            this += Pane().apply {
                Anchor.BottomLeft.configure(this)
                this.bounds.height.set(56f)

                this += RoundedRectElement(dark).apply {
                    this.roundedRadius.set(12)
                    this.padding.set(Insets(12f))
                }
            }
        }
    }

    override fun onClosePressed() {
        uiInputHandler.closeHowToPlayMenu()
    }
}