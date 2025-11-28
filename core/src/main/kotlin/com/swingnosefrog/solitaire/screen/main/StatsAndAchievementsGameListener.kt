package com.swingnosefrog.solitaire.screen.main

import com.swingnosefrog.solitaire.game.Card
import com.swingnosefrog.solitaire.game.GameContainer
import com.swingnosefrog.solitaire.game.logic.CardStack
import com.swingnosefrog.solitaire.game.logic.CardZone
import com.swingnosefrog.solitaire.game.logic.GameEventListener
import com.swingnosefrog.solitaire.game.logic.GameLogic
import com.swingnosefrog.solitaire.game.logic.GamePlayStats
import com.swingnosefrog.solitaire.statistics.StatsImpl
import com.swingnosefrog.solitaire.steamworks.AchievementIds
import com.swingnosefrog.solitaire.steamworks.SteamStats


class StatsAndAchievementsGameListener(
    private val stats: StatsImpl,
    private val gameContainer: GameContainer,
) : GameEventListener {

    companion object {

        private const val WIN_GAME_FEW_MOVES_THRESHOLD_MOVES: Int = 25
        private const val WIN_GAME_FEW_MOVES_2_THRESHOLD_MOVES: Int = 20
        private const val WIN_GAME_FAST_THRESHOLD_SECONDS: Float = 75f

    }


    private fun getAchievementsGottenOnGameWin(gamePlayStats: GamePlayStats): List<String> {
        val movesMade = gamePlayStats.movesMade.get()
        val timeElapsedSec = gamePlayStats.timeElapsedSec.get()

        return buildList {
            if (movesMade <= WIN_GAME_FEW_MOVES_THRESHOLD_MOVES) {
                this += AchievementIds.WIN_GAME_FEW_MOVES
            }
            if (movesMade <= WIN_GAME_FEW_MOVES_2_THRESHOLD_MOVES) {
                this += AchievementIds.WIN_GAME_FEW_MOVES_2
            }

            if (timeElapsedSec <= WIN_GAME_FAST_THRESHOLD_SECONDS) {
                this += AchievementIds.WIN_GAME_FAST
            }
        }
    }

    private fun handleStatsOnGameWon(gameLogic: GameLogic) {
        stats.gamesWon.increment()
        stats.currentWinStreak.increment()
        val gamePlayStats = gameLogic.gamePlayStats
        stats.pushGameWinRollingStat(gamePlayStats.movesMade.get(), gamePlayStats.timeElapsedSec.get())
        stats.persist()

        val steamStats = SteamStats
        steamStats.triggerAchievementProgress(stats)
        steamStats.updateStatCache(stats)

        val achievementsWon = getAchievementsGottenOnGameWin(gameContainer.gamePlayStats)
        if (achievementsWon.isNotEmpty()) {
            achievementsWon.forEach { steamStats.markAchievementAsAchieved(it) }
        }

        steamStats.persistStats()
    }
    
    override fun onGameWon(gameLogic: GameLogic) {
        handleStatsOnGameWon(gameLogic)
    }

    override fun onDealingEnd(gameLogic: GameLogic) {
        stats.gamesDealt.increment()
    }

    override fun onDealingStart(gameLogic: GameLogic) {
    }

    override fun onCardStackPickedUp(gameLogic: GameLogic, cardStack: CardStack, fromZone: CardZone) {
    }

    override fun onCardStackPickupCancelled(gameLogic: GameLogic, cardStack: CardStack, originalZone: CardZone) {
    }

    override fun onCardStackPlacedDown(gameLogic: GameLogic, cardStack: CardStack, toZone: CardZone) {
        stats.movesMade.increment()

        if (gameLogic.isPlayerZoneAndTallStack(toZone)) {
            val steamStats = SteamStats
            val id = AchievementIds.TALL_STACK
            if (!steamStats.isAchievementUnlocked(id)) {
                steamStats.markAchievementAsAchieved(id)
                steamStats.persistStats()
            }
        }
    }

    override fun onCardAutoMoved(gameLogic: GameLogic, card: Card, targetZone: CardZone) {
    }

    override fun onCardPlacedInFoundation(gameLogic: GameLogic, card: Card, foundationZone: CardZone) {
    }

    override fun onWidgetSetCompleted(gameLogic: GameLogic, freeCellZone: CardZone) {
    }

    override fun onFoundationZoneCompleted(gameLogic: GameLogic, foundationZone: CardZone) {
    }

    override fun onCardsRecollected(gameLogic: GameLogic) {
    }
}