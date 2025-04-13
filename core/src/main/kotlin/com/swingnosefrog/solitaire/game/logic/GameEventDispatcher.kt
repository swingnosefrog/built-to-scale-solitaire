package com.swingnosefrog.solitaire.game.logic


interface GameEventDispatcher : GameEventListener {

    fun addListener(listener: GameEventListener)

    fun removeListener(listener: GameEventListener)

}