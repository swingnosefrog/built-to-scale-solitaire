package com.swingnosefrog.solitaire.screen.main

import com.badlogic.gdx.graphics.Color
import com.swingnosefrog.solitaire.fonts.SolitaireFonts
import paintbox.font.Markup
import paintbox.ui.Pane
import paintbox.ui.RenderAlign
import paintbox.ui.area.Insets
import paintbox.ui.control.TextLabel


class MainGameHudPane(
    private val mainGameUi: MainGameUi,
) : Pane() {
    
    companion object {
        
        private const val MENU_SIZE_ADJUSTMENT_MULTIPLIER: Float = 0.05f
    }

    private val fonts: SolitaireFonts get() = mainGameUi.mainGameScreen.main.fonts
    private val mainSansSerifMarkup: Markup get() = fonts.uiMainSansSerifMarkup

    init {
        val dark = Color(0f, 0f, 0f, 0.5f)
        this += Pane().apply { 
//            this.margin.set(Insets(32f))
            
            this += TextLabel(binding = {
                "Time: 00:00 | Moves: ##"
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