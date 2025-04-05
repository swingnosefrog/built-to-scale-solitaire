package com.swingnosefrog.solitaire.game.logic

import com.swingnosefrog.solitaire.game.Card
import com.swingnosefrog.solitaire.game.CardSymbol


class CardStack(
    val cardList: MutableList<Card>,
    var stackDirection: StackDirection = StackDirection.UP,
) {

    fun isWidgetSet(): Boolean {
        val list = this.cardList
        return list.size == 3 &&
                list[0].symbol == CardSymbol.WIDGET_HALF &&
                list[1].symbol == CardSymbol.WIDGET_ROD &&
                list[2].symbol == CardSymbol.WIDGET_HALF &&
                list.all { c -> c.suit == list[0].suit }
    }
}

enum class StackDirection(val yOffset: Float) {
    UP(-1 / 160f),
    DOWN(0.5f)
}
