package com.swingnosefrog.solitaire.game.assets

import com.swingnosefrog.solitaire.game.CardSuit
import com.swingnosefrog.solitaire.game.CardSymbol


sealed class CardAssetKey {

    data class Front(val suit: CardSuit, val symbol: CardSymbol) : CardAssetKey() {

        override val skinlessAssetKey: String = "${getSymbolKey()}_${getSuitKey()}_card"
        
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

        override val skinlessAssetKey: String = "back_card"
    }
    
    data object Slot : CardAssetKey() {

        override val skinlessAssetKey: String = "slot_card"
    }
    
    data object Silhouette : CardAssetKey() {
        
        override val skinlessAssetKey: String = "card_silhouette"
    }
    
    data object Hover : CardAssetKey() {
        
        override val skinlessAssetKey: String = "card_hover"
    }
    
    data object CardCursorArrow : CardAssetKey() {
        
        override val skinlessAssetKey: String = "card_cursor_arrow"
    }
    
    data object CardCursorArrowPressed : CardAssetKey() {
        
        override val skinlessAssetKey: String = "card_cursor_arrow_pressed"
    }
    
    abstract val skinlessAssetKey: String
    
    private val assetKeysBySkin: MutableMap<CardSkin, String> = mutableMapOf()
    
    fun getAssetKey(skin: CardSkin): String = assetKeysBySkin.getOrPut(skin) { "${skin.assetKeyPrefix}_${skinlessAssetKey}"}
}

