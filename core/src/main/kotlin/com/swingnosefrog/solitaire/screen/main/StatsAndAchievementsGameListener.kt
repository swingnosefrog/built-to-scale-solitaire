package com.swingnosefrog.solitaire.screen.main

import com.swingnosefrog.solitaire.game.Card
import com.swingnosefrog.solitaire.game.CardSymbol
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
        private const val WIN_GAME_FAST_2_THRESHOLD_SECONDS: Float = 60f

    }

    private var satisfiesNoNumericalCardsInFreeSlots: Boolean = true

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
            if (timeElapsedSec <= WIN_GAME_FAST_2_THRESHOLD_SECONDS) {
                this += AchievementIds.WIN_GAME_FAST_2
            }
            
            if (satisfiesNoNumericalCardsInFreeSlots) {
                this += AchievementIds.NO_NUMERICAL_CARDS_IN_FREE_SLOTS
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
    
    private fun tryAwardSingleAchievement(id: String) {
        val steamStats = SteamStats
        if (!steamStats.isAchievementUnlocked(id)) {
            steamStats.markAchievementAsAchieved(id)
            steamStats.persistStats()
        }
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

        if (toZone in gameLogic.zones.freeCellZones) {
            if (satisfiesNoNumericalCardsInFreeSlots &&
                cardStack.cardList.firstOrNull()?.symbol?.isNumeric() == true) {
                satisfiesNoNumericalCardsInFreeSlots = false
            }

            val allFreeZones = gameLogic.zones.freeCellZones // Assumed to be in left-to-right order!
            if (allFreeZones.size == 3) {
                val openCardsInFreeZones: List<Card?> = allFreeZones.map { it.cardStack.cardList.singleOrNull() }
                
                if (openCardsInFreeZones.all { it?.symbol == CardSymbol.NUM_7 }) {
                    tryAwardSingleAchievement(AchievementIds.TRIPLE_SEVENS_FREE_CELL)
                }
                
                if (openCardsInFreeZones.all { it != null && it.suit == openCardsInFreeZones.first()?.suit } && 
                    openCardsInFreeZones[0]?.symbol == CardSymbol.WIDGET_HALF &&
                    openCardsInFreeZones[1]?.symbol == CardSymbol.WIDGET_ROD &&
                    openCardsInFreeZones[2]?.symbol == CardSymbol.WIDGET_HALF) {
                    tryAwardSingleAchievement(AchievementIds.ASSEMBLE_WIDGET_HORIZONTAL)
                }
            }
        }

        if (gameLogic.isPlayerZoneAndTallStack(toZone)) {
            tryAwardSingleAchievement(AchievementIds.TALL_STACK)
        }
    }

    override fun onCardAutoMoved(gameLogic: GameLogic, card: Card, targetZone: CardZone) {
    }

    override fun onCardPlacedInFoundation(gameLogic: GameLogic, card: Card, foundationZone: CardZone) {
    }

    override fun onWidgetSetCompleted(gameLogic: GameLogic, freeCellZone: CardZone) {
    }

    override fun onFoundationZoneCompleted(gameLogic: GameLogic, foundationZone: CardZone) {
        if (gameLogic.zones.foundationZones.filterNot { it == foundationZone }.all { it.cardStack.cardList.isEmpty() }) {
            tryAwardSingleAchievement(AchievementIds.BUILD_FULL_SUIT_MANUALLY)
        }
    }

    override fun onCardsRecollected(gameLogic: GameLogic) {
    }
}