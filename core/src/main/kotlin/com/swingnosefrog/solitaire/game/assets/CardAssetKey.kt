package com.swingnosefrog.solitaire.game.assets

import com.swingnosefrog.solitaire.game.CardSuit
import com.swingnosefrog.solitaire.game.CardSymbol


sealed class CardAssetKey {

    data class Front(val suit: CardSuit, val symbol: CardSymbol) : CardAssetKey() {
        
        override fun getAssetKey(): String = "${getSymbolKey()}_${getSuitKey()}_card"
        
        private fun getSuitKey(): String = when (suit) {
            CardSuit.A -> "red"
            CardSuit.B -> "green"
            CardSuit.C -> "blue"
        }
        
        private fun getSymbolKey(): String = when (symbol) {
            CardSymbol.NUM_7 -> "7"
            CardSymbol.NUM_6 -> "6"
            CardSymbol.NUM_5 -> "5"
            CardSymbol.NUM_4 -> "4"
            CardSymbol.NUM_3 -> "3"
            CardSymbol.NUM_2 -> "2"
            CardSymbol.NUM_1 -> "1"
            CardSymbol.WIDGET_HALF -> "nut"
            CardSymbol.WIDGET_ROD -> "rod"
            CardSymbol.SPARE -> "cap"
        }
    }
    
    data object Back : CardAssetKey() {

        override fun getAssetKey(): String = "back_card"
    }
    
    data object Slot : CardAssetKey() {
        
        override fun getAssetKey(): String = "slot_card"
    }
    
    abstract fun getAssetKey(): String
}

