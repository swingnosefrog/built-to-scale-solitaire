package com.swingnosefrog.solitaire.game.logic

import com.swingnosefrog.solitaire.game.Card
import com.swingnosefrog.solitaire.game.CardSuit
import com.swingnosefrog.solitaire.game.CardSymbol
import java.util.*


sealed class DeckInitializer {
    
    data class RandomSeed(
        val randomSeed: Long,
    ) : DeckInitializer() {
        
        constructor(randomSeed: Long? = null) : this(randomSeed = randomSeed ?: System.currentTimeMillis())

        override fun initializeDeck(startingDeck: List<Card>): List<Card> {
            return startingDeck.shuffled(Random(this.randomSeed))
        }
    }
    
    data object DebugAutoWin : DeckInitializer() {

        override fun initializeDeck(startingDeck: List<Card>): List<Card> {
            val col1 = listOf(
                Card(CardSuit.A, CardSymbol.WIDGET_HALF),
                Card(CardSuit.A, CardSymbol.WIDGET_ROD),
                Card(CardSuit.A, CardSymbol.WIDGET_HALF),
                
                Card(CardSuit.B, CardSymbol.WIDGET_HALF),
                Card(CardSuit.B, CardSymbol.WIDGET_ROD),
                Card(CardSuit.B, CardSymbol.WIDGET_HALF),
            )
            val col2 = listOf(
                Card(CardSuit.C, CardSymbol.WIDGET_HALF),
                Card(CardSuit.C, CardSymbol.WIDGET_ROD),
                Card(CardSuit.C, CardSymbol.WIDGET_HALF),

                Card(CardSuit.A, CardSymbol.NUM_1),
                Card(CardSuit.B, CardSymbol.NUM_1),
                Card(CardSuit.C, CardSymbol.NUM_1),
            )
            fun generate7to2Col(suit: CardSuit) = CardSymbol.SCALE_CARDS.dropLast(1).map { sym ->
                Card(suit, sym)
            }
            val col3 = generate7to2Col(CardSuit.A)
            val col4 = generate7to2Col(CardSuit.B)
            val col5 = generate7to2Col(CardSuit.C)
            val allColumns = listOf(col1, col2, col3, col4, col5)
            
            val interleaved = mutableListOf<Card>()
            for (row in col1.indices) {
                for (col in allColumns.indices) {
                    interleaved += allColumns[col][row]
                }
            }
            
            return CardSuit.entries.map { suit ->
                Card(suit, CardSymbol.SPARE)
            } + interleaved
        }
    }
    
    abstract fun initializeDeck(startingDeck: List<Card>): List<Card>
    
}