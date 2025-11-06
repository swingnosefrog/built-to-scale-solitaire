package com.swingnosefrog.solitaire.statistics

import com.swingnosefrog.solitaire.game.logic.GameLogic
import com.swingnosefrog.solitaire.game.logic.GamePlayStats
import com.swingnosefrog.solitaire.steamworks.AchievementIds


object AchievementsLogic {
    
    private const val WIN_GAME_FEW_MOVES_THRESHOLD_MOVES: Int = 25
    private const val WIN_GAME_FAST_THRESHOLD_SECONDS: Float = 75f

    fun getAchievementsGottenOnGameWin(gameLogic: GameLogic, gamePlayStats: GamePlayStats): List<String> {
        return buildList {
            if (gamePlayStats.movesMade.get() <= WIN_GAME_FEW_MOVES_THRESHOLD_MOVES) {
                this += AchievementIds.WIN_GAME_FEW_MOVES
            }

            if (gamePlayStats.timeElapsedSec.get() <= WIN_GAME_FAST_THRESHOLD_SECONDS) {
                this += AchievementIds.WIN_GAME_FAST
            }
        }
    }
}