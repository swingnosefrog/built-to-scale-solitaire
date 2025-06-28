package com.swingnosefrog.solitaire.screen.main

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Align
import com.swingnosefrog.solitaire.fonts.SolitaireFonts
import com.swingnosefrog.solitaire.menu.MenuController
import com.swingnosefrog.solitaire.menu.MenuInput
import com.swingnosefrog.solitaire.menu.MenuInputSource
import com.swingnosefrog.solitaire.menu.MenuInputType
import com.swingnosefrog.solitaire.menu.MenuOption
import com.swingnosefrog.solitaire.screen.main.menu.AbstractMenu
import paintbox.binding.BooleanVar
import paintbox.binding.IntVar
import paintbox.binding.ReadOnlyBooleanVar
import paintbox.binding.ReadOnlyIntVar
import paintbox.binding.ReadOnlyVar
import paintbox.binding.Var
import paintbox.font.Markup
import paintbox.font.PaintboxFont
import paintbox.font.TextAlign
import paintbox.registry.AssetRegistry
import paintbox.ui.ClickPressed
import paintbox.ui.ImageIcon
import paintbox.ui.MouseEntered
import paintbox.ui.Pane
import paintbox.ui.RenderAlign
import paintbox.ui.StringVarConverter
import paintbox.ui.UIElement
import paintbox.ui.area.Insets
import paintbox.ui.control.TextLabel
import paintbox.ui.element.QuadElement
import paintbox.ui.element.RectElement
import paintbox.ui.layout.HBox
import paintbox.ui.layout.VBox


class MainGameUiPane(
    private val mainGameUi: MainGameUi,
    private val menuController: MenuController,
) : Pane() {
    
    companion object {
        
        private const val MENU_SIZE_ADJUSTMENT_MULTIPLIER: Float = 0.05f
    }

    private val fonts: SolitaireFonts get() = mainGameUi.mainGameScreen.main.fonts
    private val headingFont: PaintboxFont get() = fonts.uiHeadingFont
    private val mainSerifMarkup: Markup get() = fonts.uiMainSerifMarkup
    private val mainSansSerifMarkup: Markup get() = fonts.uiMainSansSerifMarkup

    private val currentMenu: ReadOnlyVar<AbstractMenu?> = Var { menuController.currentMenu.use() }
    private val currentHighlightedMenuOption: ReadOnlyVar<MenuOption?> =
        Var { menuController.currentHighlightedMenuOption.use() }

    private val currentMenuSizeAdjustment: ReadOnlyIntVar =
        IntVar { currentMenu.use()?.menuSizeAdjustment?.use()?.coerceAtLeast(0) ?: 0 }

    init {
        val dark = Color(0f, 0f, 0f, 0.85f)
        this += HBox().apply {
            this.temporarilyDisableLayouts {
                this += RectElement(dark).apply {
                    this.bindWidthToParent(
                        multiplierBinding = { 0.4f + currentMenuSizeAdjustment.use() * MENU_SIZE_ADJUSTMENT_MULTIPLIER },
                        adjustBinding = { 0f }
                    )

                    this += VBox().apply {
                        this.margin.set(Insets(48f).copy(right = 24f))

                        val headingPane = Pane().apply {
                            this.bindHeightToParent(multiplier = 0.35f)
                            this.margin.set(Insets(48f, 0f))

                            this += TextLabel({ currentMenu.use()?.headingText?.use() ?: "" }, headingFont).apply {
                                this.textColor.set(Color.WHITE)
                                this.textAlign.set(TextAlign.LEFT)
                                this.renderAlign.set(Align.bottomLeft)
                            }
                        }
                        this += headingPane

                        val bodyPane = Pane().apply {
                            this.bindHeightToParent(adjustBinding = { -headingPane.bounds.height.use() })

                            this += VBox().apply {
                                this.margin.set(Insets(0f, 0f, 24f, 0f))
                                currentMenu.addListenerAndFire { menuVar ->
                                    val newMenu = menuVar.getOrCompute()

                                    this.temporarilyDisableLayouts {
                                        this.removeAllChildren()
                                        for (option in newMenu?.options ?: emptyList()) {
                                            this += createUIElementFromMenuOption(option)
                                        }
                                    }
                                }
                            }
                        }
                        this += bodyPane
                    }
                }
                this += QuadElement().apply {
                    this.leftRightGradient(dark, Color.CLEAR)
                    this.bounds.width.set(100f)
                }
            }
        }

//        this += Pane().apply {
////            this.margin.set(Insets(32f))
//
//            this += TextLabel(binding = {
//                "R - Restart"
//            }).apply {
//                this.markup.set(mainSansSerifMarkup)
//                this.renderAlign.set(RenderAlign.bottomLeft)
//                this.textColor.set(Color.WHITE)
//                this.backgroundColor.set(dark)
//                this.renderBackground.set(true)
//                this.bgPadding.set(Insets(8f))
//                this.setScaleXY(0.75f)
//            }
//        }
    }

    private fun createUIElementFromMenuOption(option: MenuOption): UIElement {
        val pane = Pane().apply {
            this.bounds.height.set(54f)

            this.addInputEventListener { evt ->
                when (evt) {
                    is MouseEntered -> {
                        menuController.setHighlightedMenuOption(option)
                        true
                    }

                    is ClickPressed -> {
                        menuController.setHighlightedMenuOption(option)

                        if (menuController.currentHighlightedMenuOption.getOrCompute() == option) {
                            // If the currently highlighted option isn't this one, it means someone else has focus
                            if (evt.button == Input.Buttons.LEFT) {
                                menuController.onMenuInput(MenuInput(MenuInputType.SELECT, MenuInputSource.MOUSE))
                                true
                            } else if (evt.button == Input.Buttons.RIGHT) {
                                menuController.onMenuInput(MenuInput(MenuInputType.BACK, MenuInputSource.MOUSE))
                                true
                            } else false
                        } else {
                            if (evt.button == Input.Buttons.LEFT || evt.button == Input.Buttons.RIGHT) {
                                // Attempt to unfocus the selected item
                                menuController.onMenuInput(MenuInput(MenuInputType.BACK, MenuInputSource.MOUSE))
                                true
                            } else false
                        }
                    }

                    else -> false
                }
            }
        }
        val selectedIcon = ImageIcon(TextureRegion(AssetRegistry.get<Texture>("ui_nut_icon"))).apply {
            this.bounds.width.set(40f)
            this.bindVarToSelfWidth(this.bounds.x, multiplier = -1f)
            this.visible.bind { currentHighlightedMenuOption.use() == option }
            this.margin.set(Insets(0f, 0f, 0f, 10f))
            this.renderAlign.set(Align.left)
        }
        pane += selectedIcon
        
        val isSelected: ReadOnlyBooleanVar = BooleanVar { option.isSelected.use() }
        val textColor: ReadOnlyVar<Color> = Var { if (isSelected.use()) Color.CYAN else Color.WHITE }
        
        val textLabel = TextLabel(option.text).apply {
            this.markup.set(mainSerifMarkup)
            this.textColor.bind { textColor.use() }
            this.textAlign.set(TextAlign.LEFT)
            this.padding.set(Insets(0f, 0f, 0f, 8f))
        }
        pane += textLabel
        
        if (option is MenuOption.OptionWidget) {
            when (option) {
                is MenuOption.OptionWidget.Checkbox -> {
                    pane += TextLabel(binding = {
                        if (option.selectedState.use()) "[X]" else "[   ]"
                    }, font = fonts.uiMainSerifFontBold).apply {
                        this.textColor.set(Color.WHITE)
                        this.textAlign.set(TextAlign.RIGHT)
                        this.renderAlign.set(Align.right)
                    }
                }

                is MenuOption.OptionWidget.Cycle<*> -> {
                    textLabel.bindWidthToParent(multiplier = 0.5f)
                    pane += Pane().apply {
                        this.bindWidthToParent(multiplier = 0.5f)
                        this.bindVarToSelfWidth(this.bounds.x)

                        this += TextLabel(binding = {
                            val selectedOption = option.selectedOption.use()
                            (option.stringVarConverter as StringVarConverter<Any?>).toVar(selectedOption).use()
                        }, font = fonts.uiMainSerifFontBold).apply {
                            this.textColor.bind { textColor.use() }
                            this.textAlign.set(TextAlign.CENTRE)
                            this.renderAlign.set(Align.center)
                        }
                        this += Pane().apply {
                            this.visible.bind { isSelected.use() }
                            this += TextLabel("<", font = fonts.uiMainSerifFontBold).apply {
                                this.textColor.set(Color.WHITE)
                                this.renderAlign.set(Align.left)
                            }
                            this += TextLabel(">", font = fonts.uiMainSerifFontBold).apply {
                                this.textColor.set(Color.WHITE)
                                this.renderAlign.set(Align.right)
                            }
                        }
                    }
                }
            }
        }
        
        return pane
    }

}