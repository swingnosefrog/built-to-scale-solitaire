package com.swingnosefrog.solitaire.screen.main

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.swingnosefrog.solitaire.Localization
import com.swingnosefrog.solitaire.fonts.PromptFontConsts
import com.swingnosefrog.solitaire.fonts.SolitaireFonts
import com.swingnosefrog.solitaire.inputmanager.IActionInputGlyph
import com.swingnosefrog.solitaire.inputmanager.InputManager
import com.swingnosefrog.solitaire.inputmanager.impl.InputActions
import com.swingnosefrog.solitaire.ui.FourPane
import paintbox.binding.ReadOnlyVar
import paintbox.binding.Var
import paintbox.font.Markup
import paintbox.font.PaintboxFont
import paintbox.font.TextAlign
import paintbox.registry.AssetRegistry
import paintbox.ui.Anchor
import paintbox.ui.Corner
import paintbox.ui.ImageIcon
import paintbox.ui.ImageRenderingMode
import paintbox.ui.Pane
import paintbox.ui.RenderAlign
import paintbox.ui.area.Insets
import paintbox.ui.control.TextLabel
import paintbox.ui.element.RoundedRectElement
import paintbox.util.gdxutils.grey
import kotlin.math.min


class MainGameHowToPlayPane(
    private val mainGameUi: MainGameUi,
    private val uiInputHandler: MainGameUi.IUiInputHandler,
) : CloseablePane() {

    private val inputManager: InputManager get() = mainGameUi.mainGameScreen.inputManager

    private val moveUpGlyph: ReadOnlyVar<List<IActionInputGlyph>> = inputManager.getGlyphsVarForAction(InputActions.DirectionUp)
    private val moveDownGlyph: ReadOnlyVar<List<IActionInputGlyph>> = inputManager.getGlyphsVarForAction(InputActions.DirectionDown)
    private val moveLeftGlyph: ReadOnlyVar<List<IActionInputGlyph>> = inputManager.getGlyphsVarForAction(InputActions.DirectionLeft)
    private val moveRightGlyph: ReadOnlyVar<List<IActionInputGlyph>> = inputManager.getGlyphsVarForAction(InputActions.DirectionRight)
    private val moveGlyphs: ReadOnlyVar<List<IActionInputGlyph>> = Var {
        listOfNotNull(
            moveUpGlyph.use().firstOrNull(),
            moveDownGlyph.use().firstOrNull(),
            moveLeftGlyph.use().firstOrNull(),
            moveRightGlyph.use().firstOrNull(),
        )
    }
    private val jumpToTopOfStackGlyph: ReadOnlyVar<List<IActionInputGlyph>> = inputManager.getGlyphsVarForAction(InputActions.JumpToTopOfStack)
    private val jumpToBottomOfStackGlyph: ReadOnlyVar<List<IActionInputGlyph>> = inputManager.getGlyphsVarForAction(InputActions.JumpToBottomOfStack)
    private val jumpInStackGlyphs: ReadOnlyVar<List<IActionInputGlyph>> = Var {
        listOfNotNull(
            jumpToTopOfStackGlyph.use().firstOrNull(),
            jumpToBottomOfStackGlyph.use().firstOrNull(),
        )
    }
    private val selectGlyph: ReadOnlyVar<List<IActionInputGlyph>> = inputManager.getGlyphsVarForAction(InputActions.Select)
    private val cancelGlyph: ReadOnlyVar<List<IActionInputGlyph>> = inputManager.getGlyphsVarForAction(InputActions.Back)

    private val fonts: SolitaireFonts get() = mainGameUi.mainGameScreen.main.fonts
    private val numberFont: PaintboxFont get() = fonts.uiHeadingFontBordered
    private val mainSerifMarkup: Markup get() = fonts.uiMainSerifMarkup


    init {
        this += Pane().apply {
            val spacing = 8f
            this.bounds.x.bind { closeButton.bounds.x.use() - bounds.width.use() - spacing }
            this.bounds.y.bind(closeButton.bounds.y)
            this.bounds.width.bind(containingPane.bounds.width)
            this.bounds.height.bind { 
                min(48f, (containingPane.bounds.y.use() + 48f) - (bounds.y.use()) - spacing)
            }
            
            this += RoundedRectElement(dark).apply {
                Anchor.TopRight.configure(this)
                this.bounds.width.set(1100f)

                this.roundedRadius.set(12)
                this.padding.set(Insets(12f))

                this += TextLabel(Localization["game.howToPlay.keybindHint", Var {
                    val unkIcon = PromptFontConsts.ICON_QUESTION_INT
                    listOf(
                        moveGlyphs.use().joinToString(separator = "") { it.promptFontText }.takeIf { it.isNotEmpty() } ?: unkIcon,
                        jumpInStackGlyphs.use().joinToString(separator = "") { it.promptFontText }.takeIf { it.isNotEmpty() } ?: unkIcon,
                        selectGlyph.use().firstOrNull()?.promptFontText ?: unkIcon,
                        cancelGlyph.use().firstOrNull()?.promptFontText ?: unkIcon,
                    )
                }]).apply {
                    this.markup.set(mainSerifMarkup)
                    this.setScaleXY(0.8125f)
                    this.textColor.set(Color().grey(0.9f))
                    this.renderAlign.set(RenderAlign.center)
                }
            }
        }
        
        containingPane.apply {
            this.bindHeightToSelfWidth(multiplier = 648f / 1152f)
            Anchor.Centre.configure(this)
            
            fun createStepPane(stepNum: Int, instructionText: ReadOnlyVar<String>): Pane {
                return Pane().apply {
                    this += Pane().apply { 
                        Anchor.TopLeft.configure(this)
                        this.bindHeightToParent(multiplier = 0.6f)
                        this.margin.set(Insets(0f, 4f, 0f, 0f))
                        
                        this += ImageIcon(
                            binding = { TextureRegion(AssetRegistry.get<Texture>("how_to_play_$stepNum")) },
                            renderingMode = ImageRenderingMode.MAINTAIN_ASPECT_RATIO
                        )
                        
                        this += TextLabel("$stepNum", font = numberFont).apply {
                            this.textColor.set(Color.WHITE)
                            this.renderAlign.set(RenderAlign.topLeft)
                            this.textAlign.set(TextAlign.LEFT)
                            this.bounds.width.set(72f)
                            this.bounds.height.set(72f)
                            this.margin.set(Insets(13f, 0f, 9f, 0f))
                        }
                    }
                    this += Pane().apply {
                        Anchor.BottomLeft.configure(this)
                        this.bindHeightToParent(multiplier = 0.4f)
                        this += TextLabel(bindable = instructionText).apply {
                            this.textColor.set(Color.WHITE)
                            this.setScaleXY(0.75f)
                            this.markup.set(mainSerifMarkup)
                            this.renderAlign.set(RenderAlign.center)
                            this.textAlign.set(TextAlign.CENTRE)
                        }
                    }
                }
            }

            this += RoundedRectElement(dark).apply {
                this.roundedRadius.set(16)
                this.padding.set(Insets(16f))
                this.bindHeightToParent(adjust = -(48f + 24f))

                this += FourPane(Color.WHITE, 2f).apply {
                    this[Corner.TOP_LEFT].addChild(createStepPane(1, Localization["game.howToPlay.instructions.1"]))
                    this[Corner.TOP_RIGHT].addChild(createStepPane(2, Localization["game.howToPlay.instructions.2"]))
                    this[Corner.BOTTOM_LEFT].addChild(createStepPane(3, Localization["game.howToPlay.instructions.3"]))
                    this[Corner.BOTTOM_RIGHT].addChild(createStepPane(4, Localization["game.howToPlay.instructions.4"]))
                }
            }

            this += Pane().apply {
                Anchor.BottomLeft.configure(this)
                this.bounds.height.set(56f)

                this += RoundedRectElement(dark).apply {
                    this.roundedRadius.set(12)
                    this.padding.set(Insets(12f))

                    this += TextLabel(Localization["game.howToPlay.objective"]).apply {
                        this.markup.set(mainSerifMarkup)
                        this.textColor.set(Color.WHITE)
                        this.renderAlign.set(RenderAlign.center)
                    }
                }
            }
        }
    }

    override fun onClosePressed() {
        uiInputHandler.closeHowToPlayMenu()
    }
}