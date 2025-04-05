package com.swingnosefrog.solitaire.game

import com.swingnosefrog.solitaire.game.assets.CardAssetKey


data class Card(
    val suit: CardSuit,
    val symbol: CardSymbol,
) {

    companion object {

        fun createStandardDeck(): List<Card> = mutableListOf<Card>().apply {
            this += listOf(CardSuit.A, CardSuit.B, CardSuit.C).flatMap { suit ->
                CardSymbol.SCALE_CARDS.map { sym ->
                    Card(suit, sym)
                } + mutableListOf<Card>().apply {
                    repeat(2) {
                        this += Card(suit, CardSymbol.WIDGET_HALF)
                    }
                    this += Card(suit, CardSymbol.WIDGET_ROD)
                } + listOf(Card(suit, CardSymbol.SPARE))
            }
        }
    }
    
    val cardAssetKey: CardAssetKey = CardAssetKey.Front(suit, symbol)

    override fun toString(): String {
        return "${suit} ${symbol}"
    }
}
