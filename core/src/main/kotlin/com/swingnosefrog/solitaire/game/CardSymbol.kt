package com.swingnosefrog.solitaire.game


enum class CardSymbol(val scaleOrder: Int) {

    NUM_7(6),
    NUM_6(5),
    NUM_5(4),
    NUM_4(3),
    NUM_3(2),
    NUM_2(1),
    NUM_1(0),

    WIDGET_HALF(999),
    WIDGET_ROD(999),

    SPARE(9999),
    ;

    companion object {

        val SCALE_CARDS: List<CardSymbol> = listOf(NUM_7, NUM_6, NUM_5, NUM_4, NUM_3, NUM_2, NUM_1)
    }
    
    fun isWidgetLike(): Boolean = this == WIDGET_HALF || this == WIDGET_ROD
    
    fun isNumeric(): Boolean = this == NUM_7 || this == NUM_6 || this == NUM_5 || this == NUM_4 || this == NUM_3 || this == NUM_2 || this == NUM_1

}