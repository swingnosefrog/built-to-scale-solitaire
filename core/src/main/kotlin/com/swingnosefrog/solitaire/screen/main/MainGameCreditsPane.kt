package com.swingnosefrog.solitaire.screen.main

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.swingnosefrog.solitaire.Localization
import com.swingnosefrog.solitaire.credits.CreditsInfo
import com.swingnosefrog.solitaire.fonts.SolitaireFonts
import com.swingnosefrog.solitaire.inputmanager.impl.InputActions
import paintbox.binding.ReadOnlyVar
import paintbox.binding.VarContext
import paintbox.font.Markup
import paintbox.font.PaintboxFont
import paintbox.font.TextAlign
import paintbox.registry.AssetRegistry
import paintbox.ui.Anchor
import paintbox.ui.ImageIcon
import paintbox.ui.Pane
import paintbox.ui.RenderAlign
import paintbox.ui.area.Insets
import paintbox.ui.control.ScrollPane
import paintbox.ui.control.TextLabel
import paintbox.ui.element.RoundedRectElement
import paintbox.ui.layout.VBox


class MainGameCreditsPane(
    private val mainGameUi: MainGameUi,
    private val uiInputHandler: MainGameUi.IUiInputHandler,
) : CloseablePane() {

    private val fonts: SolitaireFonts get() = mainGameUi.mainGameScreen.main.fonts

    private val headingFont: PaintboxFont get() = fonts.uiHeadingFont
    private val mainSansSerifMarkup: Markup get() = fonts.uiMainSansSerifMarkup
    
    private val creditsInfo: CreditsInfo = CreditsInfo()
    
    private val scrollPane: ScrollPane
    
    init {
        val darker = dark.cpy().apply { a = 0.9f }

        val creditsHeadingTextColor: Color = Color.valueOf("FFE97F")
        val nameTextColor: Color = Color.valueOf("D8D8D8")
        val attributionTextColor: Color = Color.valueOf("B7B7B7")

        val contentPane = Pane().apply {
            fun createTextLabel(creditsList: List<Pair<ReadOnlyVar<String>, List<ReadOnlyVar<String>>>>): TextLabel {
                fun VarContext.addPair(pair: Pair<ReadOnlyVar<String>, List<ReadOnlyVar<String>>>): String {
                    return "[color=#${creditsHeadingTextColor} lineheight=0.8]${Markup.escape(pair.first.use())}\n[][scale=0.8]${
                        pair.second.joinToString(separator = "\n") { Markup.escape(it.use()) }
                    }\n[]"
                }
                return TextLabel("").apply {
                    this.markup.set(mainSansSerifMarkup)
                    this.renderAlign.set(RenderAlign.top)
                    this.textAlign.set(TextAlign.CENTRE)
                    this.textColor.set(nameTextColor)
                    this.text.bind {
                        creditsList.joinToString(separator = "\n") { addPair(it) }
                    }
                }
            }
            fun TextLabel.setAutoResize() {
                this@MainGameCreditsPane.visible.addListenerAndFire { v ->
                    if (v.getOrCompute()) {
                        this.resizeBoundsToContent(affectWidth = false)
                    }
                }
            }

            val credits = creditsInfo.credits.toList()
            scrollPane = ScrollPane().apply {
                this.setContent(VBox().apply {
                    this.temporarilyDisableLayouts {
                        this += ImageIcon(binding = { TextureRegion(AssetRegistry.get<Texture>("ui_credits_logo")) }).apply {
                            this.bounds.height.set(175f)
                        }
                        this += createTextLabel(credits).apply {
                            this.margin.set(Insets(16f, 8f, 0f, 0f))
                            this.setAutoResize()
                        }
                        creditsInfo.otherAttributions.forEach { attr ->
                            this += TextLabel("").apply {
                                this.setScaleXY(0.6f)
                                this.margin.set(Insets(8f))
                                this.markup.set(mainSansSerifMarkup)
                                this.renderAlign.set(RenderAlign.topLeft)
                                this.textAlign.set(TextAlign.LEFT)
                                this.textColor.set(attributionTextColor)
                                this.doLineWrapping.set(true)
                                this.text.bind {
                                    attr.use()
                                }
                                this.setAutoResize()
                            }
                        }
                    }
                    this.autoSizeToChildren.set(true)
                })
                this.vBarPolicy.set(ScrollPane.ScrollBarPolicy.ALWAYS)
                this.hBarPolicy.set(ScrollPane.ScrollBarPolicy.NEVER)
            }
            this += scrollPane
        }
        
        containingPane.apply {
            this += RoundedRectElement(darker).apply {
                Anchor.Centre.configure(this)
                this.roundedRadius.set(16)
                this.padding.set(Insets(16f * 2))

                this += Pane().apply {
                    this += TextLabel(Localization["credits.title"], font = headingFont).apply {
                        this.margin.set(Insets(0f, 8f))
                        this.textColor.set(Color.WHITE)
                        this.renderAlign.set(RenderAlign.left)
                        this.bounds.height.set(64f)
                    }
                    this += RoundedRectElement(Color.WHITE).apply {
                        this.margin.set(Insets(6f, 8f))
                        this.bounds.y.set(64f)
                        this.bounds.height.set(14f)
                    }
                    this += contentPane.apply {
                        this.bounds.y.set(64f + 14f)
                        this.bindHeightToParent(adjustBinding = { -bounds.y.use() })
                        this.margin.set(Insets(16f, 8f))
                    }
                }
            }
        }
        
        this.visible.addListener { v ->
            if (!v.getOrCompute()) {
                scrollPane.vBar.setValue(0f)
            }
        }
    }

    override fun renderSelf(
        originX: Float,
        originY: Float,
        batch: SpriteBatch,
    ) {
        // HACK -- this is the only pane with a clickable scroll bar. We need to support keyboard/controllers though
        if (this.opacity.get() == 1f) {
            val input = mainGameUi.mainGameScreen.inputManager
            val deltaTime = Gdx.graphics.deltaTime
            val speed = 400f
            val vBar = scrollPane.vBar

            if (input.isDigitalActionPressed(InputActions.DirectionUp)) {
                vBar.setValue(vBar.value.get() - speed * deltaTime)
            }

            if (input.isDigitalActionPressed(InputActions.DirectionDown)) {
                vBar.setValue(vBar.value.get() + speed * deltaTime)
            }
        }
        
        super.renderSelf(originX, originY, batch)
    }

    override fun onClosePressed() {
        uiInputHandler.closeCreditsMenu()
    }
}