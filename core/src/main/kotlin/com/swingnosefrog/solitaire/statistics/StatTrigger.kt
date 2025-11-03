package com.swingnosefrog.solitaire.statistics


fun interface StatTrigger {

    fun onIncremented(stat: Stat, oldValue: Int, newValue: Int)

}