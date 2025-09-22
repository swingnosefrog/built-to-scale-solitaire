package com.swingnosefrog.solitaire.screen.main

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Align
import com.swingnosefrog.solitaire.fonts.SolitaireFonts
import com.swingnosefrog.solitaire.game.GameContainer
import paintbox.binding.ReadOnlyVar
import paintbox.font.Markup
import paintbox.font.TextAlign
import paintbox.registry.AssetRegistry
import paintbox.ui.Anchor
import paintbox.ui.Corner
import paintbox.ui.ImageIcon
import paintbox.ui.Pane
import paintbox.ui.area.Insets
import paintbox.ui.control.Button
import paintbox.ui.control.ButtonSkin
import paintbox.ui.element.RectElement
import paintbox.ui.element.RoundedRectElement


class MainGameHowToPlayPane(
    private val mainGameUi: MainGameUi,
    private val uiInputHandler: MainGameUi.IUiInputHandler,
) : Pane() {
    
    private val gameContainer: ReadOnlyVar<GameContainer> get() = mainGameUi.mainGameScreen.gameContainer

    private val fonts: SolitaireFonts get() = mainGameUi.mainGameScreen.main.fonts
    private val mainSansSerifMarkup: Markup get() = fonts.uiMainSansSerifMarkup
    

    init {
        val dark = Color(0f, 0f, 0f, 0.8f)

        fun Button.applyStyle() {
            this.textAlign.set(TextAlign.CENTRE)
            this.renderAlign.set(Align.center)
            this.setScaleXY(0.75f)
            (this.skin.getOrCompute() as ButtonSkin).apply {
                this.roundedRadius.set(10)
                this.defaultBgColor.set(dark)
                this.defaultTextColor.set(Color.WHITE)
            }
        }

        this += Button("").apply {
            Anchor.TopRight.configure(this, offsetX = -8f, offsetY = 8f)
            this.bounds.width.set(48f)
            this.bounds.height.set(48f)
            this.padding.set(Insets(8f))
            this.applyStyle()
            
            this += ImageIcon(TextureRegion(AssetRegistry.get<Texture>("ui_x_bordered")))

            this.setOnAction {
                uiInputHandler.closeHowToPlayMenu()
            }
        }
        
        this += Pane().apply {
            this.margin.set(Insets(48f, 24f, 64f, 64f))
            
            this += RoundedRectElement(dark).apply {
                this.roundedRadius.set(16)
                this.padding.set(Insets(16f))
                this.bindHeightToParent(adjust = -(48f + 24f))

                this += FourPane().apply {
//                this[Corner.TOP_LEFT] += RectElement(Color.RED)
//                this[Corner.TOP_RIGHT] += RectElement(Color.GREEN)
//                this[Corner.BOTTOM_LEFT] += RectElement(Color.BLUE)
//                this[Corner.BOTTOM_RIGHT] += RectElement(Color.YELLOW)
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
    
    private class FourPane : Pane() {
        
        val corners: Map<Corner, Pane>
        
        init {
            val borderColor = Color(1f, 1f, 1f, 1f)
            val borderWidth = 2f
            this += RectElement(borderColor).apply { 
                this.bounds.width.set(borderWidth)
                Anchor.TopCentre.configure(this, offsetX = -(borderWidth / 2))
            }
            this += RectElement(borderColor).apply { 
                this.bounds.height.set(borderWidth)
                Anchor.CentreLeft.configure(this, offsetY = -(borderWidth / 2))
            }
            
            fun createCornerPane(): Pane {
                return Pane().apply { 
                    this.bindWidthToParent(multiplier = 0.5f, adjust = -(borderWidth * 4))
                    this.bindHeightToParent(multiplier = 0.5f, adjust = -(borderWidth * 4))
                }
            }
            
            corners = mapOf(
                Corner.TOP_LEFT to createCornerPane().apply { 
                    Anchor.TopLeft.configure(this)
                },
                Corner.TOP_RIGHT to createCornerPane().apply { 
                    Anchor.TopRight.configure(this)
                },
                Corner.BOTTOM_LEFT to createCornerPane().apply { 
                    Anchor.BottomLeft.configure(this)
                },
                Corner.BOTTOM_RIGHT to createCornerPane().apply { 
                    Anchor.BottomRight.configure(this)
                },
            )
            
            corners.values.forEach { p -> this.addChild(p) }
        }
        
        operator fun get(corner: Corner): Pane = this.corners.getValue(corner)
    }
}