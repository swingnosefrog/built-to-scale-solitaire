package com.swingnosefrog.solitaire.game.logic

import paintbox.binding.FloatVar
import paintbox.binding.IntVar
import paintbox.binding.ReadOnlyFloatVar


class GameStats(private val logic: GameLogic) {
    
    private val _timeElapsedSec: FloatVar = FloatVar(0f)
    val timeElapsedSec: ReadOnlyFloatVar get() = _timeElapsedSec
    
    val movesMade: IntVar = IntVar(0)
    
    fun renderUpdate(deltaSec: Float) {
        if (!logic.isStillDealing.get()) {
            _timeElapsedSec.set(_timeElapsedSec.get() + deltaSec)
        }
    }
}