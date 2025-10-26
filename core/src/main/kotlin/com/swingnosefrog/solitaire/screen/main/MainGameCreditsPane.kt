package com.swingnosefrog.solitaire.screen.main

import com.badlogic.gdx.graphics.Color
import com.swingnosefrog.solitaire.Localization
import com.swingnosefrog.solitaire.credits.CreditsInfo
import com.swingnosefrog.solitaire.fonts.SolitaireFonts
import paintbox.binding.ReadOnlyVar
import paintbox.binding.VarContext
import paintbox.font.Markup
import paintbox.font.PaintboxFont
import paintbox.font.TextAlign
import paintbox.ui.Anchor
import paintbox.ui.Pane
import paintbox.ui.RenderAlign
import paintbox.ui.area.Insets
import paintbox.ui.control.ScrollPane
import paintbox.ui.control.TextLabel
import paintbox.ui.element.RoundedRectElement


class MainGameCreditsPane(
    private val mainGameUi: MainGameUi,
    private val uiInputHandler: MainGameUi.IUiInputHandler,
) : CloseablePane() {

    private val fonts: SolitaireFonts get() = mainGameUi.mainGameScreen.main.fonts

    private val headingFont: PaintboxFont get() = fonts.uiHeadingFont
    private val mainSansSerifMarkup: Markup get() = fonts.uiMainSansSerifMarkup
    private val mainSerifMarkup: Markup get() = fonts.uiMainSerifMarkup
    
    private val creditsInfo: CreditsInfo = CreditsInfo()
    
    init {
        val darker = dark.cpy().apply { a = 0.9f }

        val creditsHeadingTextColor: Color = Color.valueOf("FFE97F")
        val nameTextColor: Color = Color.valueOf("D8D8D8")

        val contentPane = Pane().apply {

            fun createTextLabel(creditsList: List<Pair<ReadOnlyVar<String>, List<ReadOnlyVar<String>>>>): TextLabel {
                fun VarContext.addPair(pair: Pair<ReadOnlyVar<String>, List<ReadOnlyVar<String>>>): String {
                    return "[color=#${creditsHeadingTextColor} lineheight=0.8]${Markup.escape(pair.first.use())}\n[][scale=0.8]${
                        pair.second.joinToString(
                            separator = "\n"
                        ) { Markup.escape(it.use()) }
                    }\n[]"
                }
                return TextLabel("").apply {
                    this.markup.set(mainSansSerifMarkup)
                    this.renderAlign.set(RenderAlign.top)
                    this.textAlign.set(TextAlign.LEFT)
                    this.textColor.set(nameTextColor)
                    this.text.bind {
                        creditsList.joinToString(separator = "\n") { addPair(it) }
                    }
                }
            }

            val credits = creditsInfo.credits.toList()
            
            this += ScrollPane().apply { 
                this.setContent(createTextLabel(credits).apply { 
                    this@MainGameCreditsPane.visible.addListenerAndFire { v -> 
                        if (v.getOrCompute()) {
                            this.resizeBoundsToContent(affectWidth = false)
                        }
                    }
                })
                this.vBarPolicy.set(ScrollPane.ScrollBarPolicy.ALWAYS)
                this.hBarPolicy.set(ScrollPane.ScrollBarPolicy.NEVER)
            }
        }
        
        containingPane.apply {
            this += RoundedRectElement(darker).apply {
                this.bindHeightToSelfWidth(multiplier = 648f / 1152f)
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
    }

    override fun onClosePressed() {
        uiInputHandler.closeCreditsMenu()
    }
}