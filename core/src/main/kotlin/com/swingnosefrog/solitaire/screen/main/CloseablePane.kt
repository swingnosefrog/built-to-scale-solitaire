package com.swingnosefrog.solitaire.screen.main

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Align
import paintbox.font.TextAlign
import paintbox.registry.AssetRegistry
import paintbox.ui.Anchor
import paintbox.ui.ImageIcon
import paintbox.ui.Pane
import paintbox.ui.area.Insets
import paintbox.ui.control.Button
import paintbox.ui.control.ButtonSkin


open class CloseablePane() : Pane() {

    protected val dark: Color = Color(0f, 0f, 0f, 0.8f)
    
    protected val containingPane: Pane
    
    init {
        this += Button("").apply {
            Anchor.TopRight.configure(this, offsetX = -8f, offsetY = 8f)
            this.bounds.width.set(48f)
            this.bounds.height.set(48f)
            this.padding.set(Insets(8f))
            this.applyStyle()

            this += ImageIcon(TextureRegion(AssetRegistry.get<Texture>("ui_x_bordered")))

            this.setOnAction {
                onClosePressed()
            }
        }

        containingPane = Pane().apply {
            this.margin.set(Insets(48f, 24f, 64f, 64f))
        }
        this += containingPane
    }
    
    protected open fun onClosePressed() {}

    protected fun Button.applyStyle() {
        this.textAlign.set(TextAlign.CENTRE)
        this.renderAlign.set(Align.center)
        this.setScaleXY(0.75f)
        (this.skin.getOrCompute() as ButtonSkin).apply {
            this.roundedRadius.set(10)
            this.defaultBgColor.set(dark)
            this.defaultTextColor.set(Color.WHITE)
        }
    }
}