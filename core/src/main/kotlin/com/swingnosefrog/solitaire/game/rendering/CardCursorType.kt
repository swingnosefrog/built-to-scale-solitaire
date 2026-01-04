package com.swingnosefrog.solitaire.game.rendering


enum class CardCursorType(
    val renderHighlight: Boolean,
    val renderArrow: Boolean,
) {
    FULL(renderHighlight = true, renderArrow = true),
    HIGHLIGHT_ONLY(renderHighlight = true, renderArrow = false),
}