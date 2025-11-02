package com.swingnosefrog.solitaire.ui

import com.badlogic.gdx.graphics.Color
import paintbox.ui.Anchor
import paintbox.ui.Corner
import paintbox.ui.Pane
import paintbox.ui.element.RectElement


open class FourPane(
    borderColor: Color = Color(1f, 1f, 1f, 1f),
    borderWidth: Float = 2f,
) : Pane() {

    val corners: Map<Corner, Pane>

    init {
        this += RectElement(borderColor).apply {
            this.bounds.width.set(borderWidth)
            Anchor.TopCentre.configure(this)
        }
        this += RectElement(borderColor).apply {
            this.bounds.height.set(borderWidth)
            Anchor.CentreLeft.configure(this)
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