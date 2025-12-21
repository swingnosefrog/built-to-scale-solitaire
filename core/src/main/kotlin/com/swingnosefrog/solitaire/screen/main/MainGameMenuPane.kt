package com.swingnosefrog.solitaire.screen.main

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Align
import com.swingnosefrog.solitaire.Solitaire
import com.swingnosefrog.solitaire.fonts.PromptFontConsts
import com.swingnosefrog.solitaire.fonts.SolitaireFonts
import com.swingnosefrog.solitaire.menu.*
import com.swingnosefrog.solitaire.screen.main.menu.AbstractMenu
import com.swingnosefrog.solitaire.screen.main.menu.RootMenu
import com.swingnosefrog.solitaire.steamworks.Steamworks
import paintbox.binding.*
import paintbox.font.Markup
import paintbox.font.PaintboxFont
import paintbox.font.TextAlign
import paintbox.registry.AssetRegistry
import paintbox.ui.*
import paintbox.ui.animation.TransitioningFloatVar
import paintbox.ui.area.Insets
import paintbox.ui.control.Slider
import paintbox.ui.control.TextLabel
import paintbox.ui.element.QuadElement
import paintbox.ui.element.RectElement
import paintbox.ui.layout.HBox
import paintbox.ui.layout.VBox
import paintbox.util.Version


class MainGameMenuPane(
    private val mainGameUi: MainGameUi,
    private val menuController: MenuController,
) : Pane() {
    
    companion object {
        
        private const val MENU_SIZE_ADJUSTMENT_MULTIPLIER: Float = 0.05f
    }

    private val fonts: SolitaireFonts get() = mainGameUi.mainGameScreen.main.fonts
    private val headingFont: PaintboxFont get() = fonts.uiHeadingFont
    private val mainSerifMarkup: Markup get() = fonts.uiMainSerifMarkup
    private val mainSerifBoldMarkup: Markup get() = fonts.uiMainSerifBoldMarkup
    private val mainSansSerifMarkup: Markup get() = fonts.uiMainSansSerifMarkup
    private val mainSansSerifBoldMarkup: Markup get() = fonts.uiMainSansSerifBoldMarkup

    private val currentMenu: ReadOnlyVar<AbstractMenu?> = Var { menuController.currentMenu.use() }
    private val currentHighlightedMenuOption: ReadOnlyVar<MenuOption?> =
        Var { menuController.currentHighlightedMenuOption.use() }

    private val currentMenuSizeAdjustment: ReadOnlyIntVar =
        IntVar { currentMenu.use()?.menuSizeAdjustment?.use()?.coerceAtLeast(0) ?: 0 }

    init {
        val dark = Color(0f, 0f, 0f, 0.85f)
        this += NoInputPane().apply {
            this += VBox().apply {
                Anchor.BottomRight.configure(this)
                this.bindWidthToParent(multiplier = 0.3f)
                
                this.spacing.set(0f)
                this.bottomToTop.set(true)
                this.align.set(VBox.Align.BOTTOM)

                this.opacity.bind(TransitioningFloatVar(mainGameUi.animationHandler, {
                    val menu = currentMenu.use()
                    if (menu == null || menu is RootMenu) 1f else 0.5f
                }, { currentValue, targetValue ->
                    mainGameUi.createOpacityAnimation(currentValue, targetValue)
                }))

                this.temporarilyDisableLayouts {
                    val bgColor = Color(0f, 0f, 0f, 0.65f)
                    
                    fun createTextLabel(): TextLabel {
                        return TextLabel("").apply {
                            this.bounds.height.set(32f)
                            this.markup.set(mainSansSerifMarkup)
                            this.textColor.set(Color.WHITE)
                            this.textAlign.set(TextAlign.RIGHT)
                            this.renderAlign.set(Align.bottomRight)
                            this.backgroundColor.set(bgColor)
                            this.bgPadding.set(Insets(6f))
                            this.renderBackground.set(true)
                        }
                    }

                    val versionLabel = createTextLabel().apply {
                        this.text.set(Solitaire.VERSION.toMarkupString())
                        this.setScaleXY(0.8f)
                    }
                    this += versionLabel

                    if (Steamworks.getSteamInterfaces() != null) {
                        this += createTextLabel().apply {
                            var textValue = "[font=promptfont]${PromptFontConsts.ICON_STEAM}[]"

                            if (Steamworks.isRunningOnSteamDeck()) {
                                textValue += " [scale=0.75]Deck[]"
                            }

                            this.text.set(textValue)
                        }
                    }
                }
            }
        }
        this += HBox().apply {
            this.temporarilyDisableLayouts {
                this += RectElement(dark).apply {
                    this.bindWidthToParent(
                        multiplierBinding = { 0.4f + currentMenuSizeAdjustment.use() * MENU_SIZE_ADJUSTMENT_MULTIPLIER },
                        adjustBinding = { 0f }
                    )

                    this += VBox().apply {
                        this.margin.set(Insets(48f).copy(right = 24f))

                        val headingPane = NoInputPane().apply {
                            this.bindHeightToParent(multiplier = 0.35f)
                            this.margin.set(Insets(48f, 0f))

                            this += TextLabel({ currentMenu.use()?.headingText?.use() ?: "" }, headingFont).apply {
                                this.visible.bind { currentMenu.use() !is RootMenu }
                                this.textColor.set(Color.WHITE)
                                this.textAlign.set(TextAlign.LEFT)
                                this.renderAlign.set(Align.bottomLeft)
                            }
                            this += ImageIcon({ TextureRegion(AssetRegistry.get<Texture>("ui_logo_menu")) }).apply {
                                this.visible.bind { currentMenu.use() is RootMenu }
                                this.bounds.y.set(20f)
                                this.renderAlign.set(RenderAlign.bottomLeft)
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
        if (option is MenuOption.Separator) {
            val element = RectElement(Color.WHITE).apply {
                this.bounds.height.set(14f)
                this.margin.set(Insets(6f, 4f))

                this.opacity.set(0.5f)
            }
            return element
        }
        
        val inputListener = InputEventListener { evt ->
            if (option.disabled.get()) return@InputEventListener false

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
        val pane = Pane().apply {
            this.bounds.height.set(54f)
            
            this.opacity.bind { if (option.disabled.use()) 0.5f else 1f }
            this.addInputEventListener(inputListener)
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
                        val xSymbol = "X"
                        val opacity = if (option.selectedState.use()) 1f else 0f
                        "\\[[opacity=${opacity}] $xSymbol []\\]"
                    }).apply {
                        this.markup.set(mainSansSerifBoldMarkup)
                        this.disabled.bind(option.disabled)
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
                            @Suppress("UNCHECKED_CAST")
                            (option.stringVarConverter as StringVarConverter<Any?>).toVar(selectedOption).use()
                        }).apply {
                            this.markup.set(mainSerifBoldMarkup)
                            this.disabled.bind(option.disabled)
                            this.textColor.bind { textColor.use() }
                            this.textAlign.set(TextAlign.CENTRE)
                            this.renderAlign.set(Align.center)
                            
                            fun createCycleZone(indexChange: Int): ActionablePane {
                                return ActionablePane().apply {
                                    this.bindWidthToParent(multiplier = 0.5f)
                                    this.opacity.set(0f)
                                    this.visible.bind(option.isSelected) // Prevents being able to start a click when not yet selected

                                    this.onLeftClick = {
                                        if (!option.disabled.get() && option.isSelected.get()) {
                                            option.selectNext(indexChange)
                                            true
                                        } else false
                                    }
                                }
                            }
                            this += createCycleZone(-1).apply { 
                                Anchor.TopLeft.configure(this)
                            }
                            this += createCycleZone(+1).apply { 
                                Anchor.TopRight.configure(this)
                            }
                        }
                        this += NoInputPane().apply {
                            this.visible.bind { isSelected.use() }
                            val font = fonts.uiMainSansSerifFontBold
                            this += TextLabel("<", font = font).apply {
                                this.disabled.bind(option.disabled)
                                this.textColor.set(Color.WHITE)
                                this.renderAlign.set(Align.left)
                            }
                            this += TextLabel(">", font = font).apply {
                                this.disabled.bind(option.disabled)
                                this.textColor.set(Color.WHITE)
                                this.renderAlign.set(Align.right)
                            }
                        }
                    }
                }

                is MenuOption.OptionWidget.Slider -> {
                    textLabel.bindWidthToParent(multiplier = 0.5f)
                    pane += Pane().apply {
                        this.bindWidthToParent(multiplier = 0.5f)
                        this.bindVarToSelfWidth(this.bounds.x)

                        this += Pane().apply {
                            this += Slider().apply {
                                Anchor.Centre.configure(this)
                                this.bindHeightToParent(multiplier = 0.75f)
                                this.disabled.bind {
                                    option.disabled.use() || !option.isSelected.use()
                                }

                                (this.skin.getOrCompute() as Slider.SliderSkin).let { skin ->
                                    val sizeMultiplier = FloatVar { if (option.isSelected.use()) 1f else 0.675f }
                                    skin.circleSizeMultiplier.bind(sizeMultiplier)
                                    skin.barHeightMultiplier.bind(sizeMultiplier)
                                }

                                this.minimum.bind(option.minimum)
                                this.maximum.bind(option.maximum)
                                this.tickUnit.bind(option.tickUnit)
                                this.setValue(option.value.get())

                                this.value.addListener { v ->
                                    if (!this.apparentDisabledState.get()) {
                                        val newSliderValue = v.getOrCompute()
                                        if (option.value.get() != newSliderValue) {
                                            option.setValue(newSliderValue)
                                        }
                                    }
                                }
                                option.value.addListener(WeakVarChangedListener { v ->
                                    val newOptionValue = v.getOrCompute()
                                    if (this.value.get() != newOptionValue) {
                                        this.setValue(newOptionValue)
                                    }
                                })
                            }
                        }
                    }
                }
            }
        }
        
        return pane
    }

    private fun Version.toMarkupString(): String {
        val ver = this

        val onlyDateRegex = """(\d{8}(?:.+)?)""".toRegex()
        val dateMatch = onlyDateRegex.matchEntire(ver.suffix)

        val versionString = if (dateMatch != null) {
            "v${ver.major}.${ver.minor}.${ver.patch}${if (ver.suffix.isNotEmpty()) "[scale=0.75]-${dateMatch.value}[]" else ""}"
        } else {
            val verSuffixRegex = """(.+)(_\d{8}(?:.+)?)""".toRegex()
            val suffixMatch = verSuffixRegex.matchEntire(ver.suffix)
            val verSuffixNoDate = suffixMatch?.groupValues?.get(1)
                ?: ver.suffix // Use entire suffix if cannot match _date
            val verSuffixDate = suffixMatch?.groupValues?.get(2) ?: ""
            "v${ver.major}.${ver.minor}.${ver.patch}${if (ver.suffix.isNotEmpty()) "-${verSuffixNoDate}[scale=0.75]${verSuffixDate}[]" else ""}"
        }

        return versionString
    }
}