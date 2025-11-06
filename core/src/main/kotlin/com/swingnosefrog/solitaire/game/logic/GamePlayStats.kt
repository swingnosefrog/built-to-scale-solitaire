package com.swingnosefrog.solitaire.game.logic

import paintbox.binding.FloatVar
import paintbox.binding.IntVar
import paintbox.binding.ReadOnlyFloatVar
import paintbox.binding.ReadOnlyIntVar


class GamePlayStats(private val logic: GameLogic) {

    private val _timeElapsedSec: FloatVar = FloatVar(0f)
    val timeElapsedSec: ReadOnlyFloatVar get() = _timeElapsedSec

    private val _movesMade: IntVar = IntVar(0)
    val movesMade: ReadOnlyIntVar get() = _movesMade

    init {
        logic.eventDispatcher.addListener(object : GameEventListener.Adapter() {

            override fun onCardStackPlacedDown(
                gameLogic: GameLogic,
                cardStack: CardStack,
                toZone: CardZone,
            ) {
                _movesMade.incrementAndGet()
            }
        })
    }

    fun renderUpdate(deltaSec: Float) {
        if (!logic.isStillDealing.get() && !logic.gameWon.get()) {
            _timeElapsedSec.set(_timeElapsedSec.get() + deltaSec)
        }
    }
}