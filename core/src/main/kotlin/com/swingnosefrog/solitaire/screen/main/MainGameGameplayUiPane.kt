package com.swingnosefrog.solitaire.screen.main

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.swingnosefrog.solitaire.Localization
import com.swingnosefrog.solitaire.fonts.SolitaireFonts
import com.swingnosefrog.solitaire.game.GameContainer
import com.swingnosefrog.solitaire.inputmanager.IActionInputGlyph
import com.swingnosefrog.solitaire.inputmanager.InputManager
import com.swingnosefrog.solitaire.inputmanager.impl.InputActions
import paintbox.binding.ReadOnlyVar
import paintbox.binding.Var
import paintbox.font.Markup
import paintbox.font.TextAlign
import paintbox.ui.Corner
import paintbox.ui.Pane
import paintbox.ui.control.Button
import paintbox.ui.control.ButtonSkin
import paintbox.ui.layout.VBox


class MainGameGameplayUiPane(
    private val mainGameUi: MainGameUi,
    private val uiInputHandler: MainGameUi.IUiInputHandler
) : Pane() {

    private val gameContainer: ReadOnlyVar<GameContainer> get() = mainGameUi.mainGameScreen.gameContainer

    private val fonts: SolitaireFonts get() = mainGameUi.mainGameScreen.main.fonts
    private val mainSerifMarkup: Markup get() = fonts.uiMainSerifMarkup
    
    private val inputManager: InputManager get() = mainGameUi.mainGameScreen.inputManager
    
    private val menuGlyph: ReadOnlyVar<List<IActionInputGlyph>> = inputManager.getGlyphsVarForAction(InputActions.Menu)
    private val newGameGlyph: ReadOnlyVar<List<IActionInputGlyph>> = inputManager.getGlyphsVarForAction(InputActions.NewGame)

    init {
        val dark = Color(0f, 0f, 0f, 0.85f)
        this += Pane().apply {
            this += VBox().apply { 
                this.align.set(VBox.Align.CENTRE)
                this.bounds.width.set(64f)
                
                this.temporarilyDisableLayouts { 
                    fun Button.applyStyle(vararg corners: Corner) {
                        this.textAlign.set(TextAlign.CENTRE)
                        this.renderAlign.set(Align.center)
                        this.markup.set(mainSerifMarkup)
                        this.setScaleXY(0.75f)
                        (this.skin.getOrCompute() as ButtonSkin).apply {
                            this.roundedRadius.set(10)
                            this.roundedCorners.clear()
                            this.roundedCorners.addAll(corners)
                            this.defaultBgColor.set(dark)
                            this.defaultTextColor.set(Color.WHITE)
                        }
                    }
                    
                    this += Button(Localization["game.gameplay.button.menu", Var { listOf(menuGlyph.use().firstOrNull()?.promptFontText ?: "") }]).apply {
                        this.bindHeightToSelfWidth(multiplier = 1.25f)
                        this.applyStyle(Corner.TOP_RIGHT)
                        
                        this.setOnAction { 
                            uiInputHandler.openPauseMenu()
                        }
                    }
                    this += Button(Localization["game.gameplay.button.newGame", Var { listOf(newGameGlyph.use().firstOrNull()?.promptFontText ?: "") }]).apply {
                        this.bindHeightToSelfWidth(multiplier = 1.25f)
                        this.applyStyle(Corner.BOTTOM_RIGHT)
                        
                        this.setOnAction {
                            uiInputHandler.startNewGame()
                        }
                    }
                }
            }
        }
    }
}