package com.swingnosefrog.solitaire.screen.main

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Align
import com.swingnosefrog.solitaire.fonts.SolitaireFonts
import com.swingnosefrog.solitaire.menu.MenuController
import com.swingnosefrog.solitaire.menu.MenuInputType
import com.swingnosefrog.solitaire.menu.MenuOption
import com.swingnosefrog.solitaire.screen.main.menu.AbstractMenu
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

    private val fonts: SolitaireFonts get() = mainGameUi.mainGameScreen.main.fonts
    private val headingFont: PaintboxFont get() = fonts.uiHeadingFont
    private val mainSerifMarkup: Markup get() = fonts.uiMainSerifMarkup

    private val currentMenu: ReadOnlyVar<AbstractMenu?> = Var { menuController.currentMenu.use() }
    private val currentHighlightedMenuOption: ReadOnlyVar<MenuOption?> =
        Var { menuController.currentHighlightedMenuOption.use() }

    init {
        val dark = Color(0f, 0f, 0f, 0.85f)
        this += HBox().apply {
            this.temporarilyDisableLayouts {
                this += RectElement(dark).apply {
                    this.bindWidthToParent(multiplier = 0.4f)

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
    }

    private fun createUIElementFromMenuOption(option: MenuOption): UIElement {
        return when (option) {
            is MenuOption.Simple, is MenuOption.SubMenu, is MenuOption.Back -> {
                HBox().apply {
                    this.bounds.height.set(54f)

                    val selectedIcon = ImageIcon(TextureRegion(AssetRegistry.get<Texture>("ui_nut_icon"))).apply {
                        this.bounds.width.bind { 
                            if (currentHighlightedMenuOption.use() == option) {
                                40f
                            } else 0f
                        }
                        this.visible.bind { bounds.width.use() > 0f }
                        this.margin.set(Insets(0f, 0f, 0f, 10f))
                        this.renderAlign.set(Align.left)
                    }
                    val label = TextLabel(option.text).apply {
                        this.bindWidthToParent(adjustBinding = { -selectedIcon.bounds.width.use() })
                        this.markup.set(mainSerifMarkup)
                        this.textColor.set(Color.WHITE)
                        this.textAlign.set(TextAlign.LEFT)
                    }

                    this.temporarilyDisableLayouts {
                        this += selectedIcon
                        this += label
                    }
                    
                    this.addInputEventListener { evt ->
                        when (evt) {
                            is MouseEntered -> {
                                menuController.setHighlightedMenuOption(option)
                                true
                            }
                            
                            is ClickPressed -> {
                                menuController.setHighlightedMenuOption(option)
                                
                                if (evt.button == Input.Buttons.LEFT) {
                                    menuController.onMenuInput(MenuInputType.SELECT)
                                    true
                                } else if (evt.button == Input.Buttons.RIGHT) {
                                    menuController.onMenuInput(MenuInputType.BACK)
                                    true
                                } else false
                            }

                            else -> false
                        }
                    }
                }
            }
        }
    }

}