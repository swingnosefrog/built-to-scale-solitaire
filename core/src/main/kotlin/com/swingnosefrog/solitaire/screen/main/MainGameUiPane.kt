package com.swingnosefrog.solitaire.screen.main

import com.badlogic.gdx.graphics.Color
import com.swingnosefrog.solitaire.SolitaireGame
import com.swingnosefrog.solitaire.fonts.SolitaireFonts
import paintbox.font.TextAlign
import paintbox.ui.Pane
import paintbox.ui.area.Insets
import paintbox.ui.control.TextLabel
import paintbox.ui.element.QuadElement
import paintbox.ui.element.RectElement
import paintbox.ui.layout.HBox
import paintbox.ui.layout.VBox


class MainGameUiPane(mainGameUi: MainGameUi) : Pane() {

    private val fonts: SolitaireFonts = SolitaireGame.instance.fonts

    init {
        val dark = Color(0f, 0f, 0f, 0.85f)
        this += HBox().apply {
            this.temporarilyDisableLayouts {
                this += RectElement(dark).apply {
                    this.bindWidthToParent(multiplier = 0.4f)

                    this += Pane().apply {
                        this.margin.set(Insets(48f).copy(right = 24f))
                        
                        this += Pane().apply {
                            this.bounds.height.set(100f)
                            this.bindYToParentHeight(
                                multiplierBinding = { 0.25f },
                                adjustBinding = { -bounds.height.use() })

                            this += TextLabel("Pause", fonts.uiHeadingFont).apply {
                                this.textColor.set(Color.WHITE)
                                this.textAlign.set(TextAlign.LEFT)
                            }
                        }
                        
                        this += Pane().apply {
                            this.bindYToParentHeight(multiplier = 0.25f)
                            this.bindHeightToParent(adjust = -100f)

                            this += VBox().apply { 
                                this.margin.set(Insets(0f, 0f, 24f, 0f))
                                this.temporarilyDisableLayouts {
                                    this += TextLabel("Resume").apply {
                                        this.markup.set(fonts.uiMainSerifMarkup)
                                        this.bounds.height.set(48f)
                                        this.textColor.set(Color.WHITE)
                                        this.textAlign.set(TextAlign.LEFT)
                                    }
                                    this += TextLabel("How to Play").apply {
                                        this.markup.set(fonts.uiMainSerifMarkup)
                                        this.bounds.height.set(48f)
                                        this.textColor.set(Color.WHITE)
                                        this.textAlign.set(TextAlign.LEFT)
                                    }
                                    this += TextLabel("Settings").apply {
                                        this.markup.set(fonts.uiMainSerifMarkup)
                                        this.bounds.height.set(48f)
                                        this.textColor.set(Color.WHITE)
                                        this.textAlign.set(TextAlign.LEFT)
                                    }
                                    this += TextLabel("Quit Game").apply {
                                        this.markup.set(fonts.uiMainSerifMarkup)
                                        this.bounds.height.set(48f)
                                        this.textColor.set(Color.WHITE)
                                        this.textAlign.set(TextAlign.LEFT)
                                    }
                                }
                            }
                        }
                    }
                }
                this += QuadElement().apply {
                    this.leftRightGradient(dark, Color.CLEAR)
                    this.bounds.width.set(100f)
                }
            }
        }
    }

}