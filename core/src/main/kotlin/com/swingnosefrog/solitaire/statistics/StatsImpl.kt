package com.swingnosefrog.solitaire.statistics

import com.badlogic.gdx.files.FileHandle
import com.swingnosefrog.solitaire.persistence.GameSaveLocationHelper
import com.swingnosefrog.solitaire.statistics.formatter.DurationStatFormatter
import com.swingnosefrog.solitaire.statistics.formatter.LocalizedStatFormatter
import paintbox.Paintbox
import java.util.concurrent.atomic.AtomicBoolean


class StatsImpl : AbstractStats() {
    
    companion object {
        
        private val loggingTag: String = StatsImpl::class.java.simpleName
        
        const val ROLLING_AVERAGE_GAME_COUNT: Int = 20
    }

    private val storageLoc: FileHandle by lazy { FileHandle(GameSaveLocationHelper.saveDirectory.resolve("statistics.json")) }

    //region Stat registration

    /**
     * Total number of times a game was won.
     */
    val gamesWon: Stat = register(Stat("gamesWon", LocalizedStatFormatter.DEFAULT))

    /**
     * Total number of times "new game" was dealt FULLY.
     */
    val gamesDealt: Stat = register(Stat("gamesDealt", LocalizedStatFormatter.DEFAULT))

    /**
     * Total number of times a legal move was made.
     */
    val movesMade: Stat = register(Stat("movesMade", LocalizedStatFormatter.DEFAULT))


    /**
     * Shortest game by moves.
     */
    val shortestGameMoves: Stat = register(Stat("shortestGame.moves", LocalizedStatFormatter.MOVES))
    /**
     * Shortest game by moves.
     */
    val shortestGameTime: Stat = register(Stat("shortestGame.time", DurationStatFormatter.DEFAULT))

    /**
     * Longest game by moves.
     */
    val longestGameMoves: Stat = register(Stat("longestGame.moves", LocalizedStatFormatter.MOVES))
    /**
     * Longest game by moves.
     */
    val longestGameTime: Stat = register(Stat("longestGame.time", DurationStatFormatter.DEFAULT))

    /**
     * Average game by moves.
     */
    val averageGameMoves: Stat = register(Stat("averageGame.moves", LocalizedStatFormatter.MOVES))
    /**
     * Average game by moves.
     */
    val averageGameTime: Stat = register(Stat("averageGame.time", DurationStatFormatter.DEFAULT))

    
    /** 
     * Best (highest) win streak.
     */
    val bestWinStreak: Stat = register(Stat("winStreak.best", LocalizedStatFormatter.DEFAULT))
    /**
     * Current win streak.
     */
    val currentWinStreak: Stat = register(Stat("winStreak.current", LocalizedStatFormatter.DEFAULT))
    
    //endregion
    

    private val successfulLoad: AtomicBoolean = AtomicBoolean(false)
    
    init {
        currentWinStreak.value.addListenerAndFire { v ->
            val newValue = v.getOrCompute()
            if (bestWinStreak.value.get() < newValue) {
                bestWinStreak.setValue(newValue)
            }
        }
    }
    
    fun pushGameWinRollingStat(movesMade: Int, timeSec: Float) {
        
    }

    fun load() {
        if (this.fromJsonFile(storageLoc)) {
            Paintbox.LOGGER.debug("Statistics loaded", loggingTag)
            successfulLoad.set(true)
        } else {
            Paintbox.LOGGER.warn("Statistics NOT loaded successfully!", loggingTag)
        }
    }

    fun persist() {
        if (successfulLoad.get()) {
            this.toJsonFile(storageLoc)
            Paintbox.LOGGER.debug("Statistics saved", loggingTag)
        } else {
            Paintbox.LOGGER.warn("Statistics NOT saved due to no successful load flag!", loggingTag)
        }
    }
}