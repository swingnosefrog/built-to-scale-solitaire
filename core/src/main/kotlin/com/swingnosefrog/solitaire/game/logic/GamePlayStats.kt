package com.swingnosefrog.solitaire.game.logic

import paintbox.binding.FloatVar
import paintbox.binding.IntVar
import paintbox.binding.ReadOnlyFloatVar
import paintbox.binding.ReadOnlyIntVar


class GamePlayStats(private val logic: GameLogic) {

    val timeElapsedSec: ReadOnlyFloatVar
        field = FloatVar(0f)

    val movesMade: ReadOnlyIntVar
        field = IntVar(0)

    init {
        logic.eventDispatcher.addListener(object : GameEventListener.Adapter() {

            override fun onCardStackPlacedDown(
                gameLogic: GameLogic,
                cardStack: CardStack,
                toZone: CardZone,
            ) {
                movesMade.incrementAndGet()
            }
        })
    }

    fun renderUpdate(deltaSec: Float) {
        if (!logic.isStillDealing.get() && !logic.gameWon.get()) {
            timeElapsedSec.set(timeElapsedSec.get() + deltaSec)
        }
    }
}