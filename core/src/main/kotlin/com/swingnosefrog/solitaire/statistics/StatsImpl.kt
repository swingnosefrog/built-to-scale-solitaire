package com.swingnosefrog.solitaire.statistics

import com.badlogic.gdx.files.FileHandle
import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonObject
import com.swingnosefrog.solitaire.persistence.GameSaveLocationHelper
import com.swingnosefrog.solitaire.statistics.formatter.*
import paintbox.Paintbox
import paintbox.binding.Var
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.roundToInt


class StatsImpl : AbstractStats() {

    companion object {

        private val loggingTag: String = StatsImpl::class.java.simpleName

        const val ROLLING_AVERAGE_GAME_COUNT: Int = 20
    }

    private data class RollingGameStat(val movesMade: Int, val timeElapsedMs: Int)

    private val storageLoc: FileHandle by lazy {
        val currentStatsFile =
            FileHandle(GameSaveLocationHelper.saveDirectory.resolve("stats.${GameSaveLocationHelper.SAVE_FILE_EXTENSION}"))
        val oldStatsFile = FileHandle(GameSaveLocationHelper.saveDirectory.resolve("statistics.json"))

        if (oldStatsFile.exists() && !oldStatsFile.isDirectory && !currentStatsFile.exists()) {
            try {
                currentStatsFile.file().createNewFile()
                oldStatsFile.copyTo(currentStatsFile)
                oldStatsFile.delete()
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
        
        currentStatsFile
    }

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
     * Shortest game by time. Measured in milliseconds
     */
    val shortestGameTime: Stat = register(Stat("shortestGame.time", DurationMsStatFormatter))

    /**
     * Longest game by moves.
     */
    val longestGameMoves: Stat = register(Stat("longestGame.moves", LocalizedStatFormatter.MOVES))

    /**
     * Longest game by time. Measured in milliseconds
     */
    val longestGameTime: Stat = register(Stat("longestGame.time", DurationMsStatFormatter))

    /**
     * Average game by moves.
     */
    val averageGameMoves: Stat = register(Stat("averageGame.moves", LocalizedStatFormatter.MOVES))

    /**
     * Average game by time. Measured in milliseconds
     */
    val averageGameTime: Stat = register(Stat("averageGame.time", DurationMsStatFormatter))


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
    
    private val lastNGamesData: Var<List<RollingGameStat>> = Var(emptyList())

    init {
        currentWinStreak.value.addListenerAndFire { v ->
            val newValue = v.getOrCompute()
            if (bestWinStreak.value.get() < newValue) {
                bestWinStreak.setValue(newValue)
            }
        }
        lastNGamesData.addListenerAndFire { v ->
            val list = v.getOrCompute().take(ROLLING_AVERAGE_GAME_COUNT)
            if (list.isEmpty()) {
                averageGameMoves.setValue(0)
                averageGameTime.setValue(0)
            } else {
                averageGameMoves.setValue((list.sumOf { it.movesMade } / list.size.toFloat()).roundToInt())
                averageGameTime.setValue((list.sumOf { it.timeElapsedMs } / list.size.toFloat()).roundToInt())
            }
        }
    }

    @Suppress("ConvertTwoComparisonsToRangeCheck")
    fun pushGameWinRollingStat(movesMade: Int, timeSec: Float) {
        val timeMs = (timeSec * 1000).toInt()

        val currentShortestGameMoves = shortestGameMoves.value.get()
        if (movesMade < currentShortestGameMoves || currentShortestGameMoves <= 0) {
            shortestGameMoves.setValue(movesMade)
        }
        val currentShortestGameTime = shortestGameTime.value.get()
        if (timeMs < currentShortestGameTime || currentShortestGameTime <= 0) {
            shortestGameTime.setValue(timeMs)
        }

        val currentLongestGameMoves = longestGameMoves.value.get()
        if (movesMade > currentLongestGameMoves) {
            longestGameMoves.setValue(movesMade)
        }
        val currentLongestGameTime = longestGameTime.value.get()
        if (timeMs > currentLongestGameTime) {
            longestGameTime.setValue(timeMs)
        }

        val newStat = RollingGameStat(movesMade, timeMs)
        lastNGamesData.set((lastNGamesData.getOrCompute() + newStat).takeLast(ROLLING_AVERAGE_GAME_COUNT))
    }

    override fun fromJson(rootObj: JsonObject) {
        super.fromJson(rootObj)

        try {
            val lastNGamesArr = rootObj["lastNGames"]?.asArray()
            if (lastNGamesArr != null) {
                val maxSize = lastNGamesArr.size().coerceAtMost(ROLLING_AVERAGE_GAME_COUNT)
                val rollingStats = (0 until maxSize).map { index ->
                    val obj = lastNGamesArr[index].asObject()
                    RollingGameStat(obj.getInt("moves", -1), obj.getInt("timeMs", -1))
                }.filter { it.movesMade > 0 && it.timeElapsedMs > 0 }

                lastNGamesData.set(rollingStats)
            }
        } catch (_: Exception) {
        }
    }

    override fun toJson(rootObj: JsonObject) {
        super.toJson(rootObj)
        
        val list = lastNGamesData.getOrCompute()
        rootObj.add("lastNGames", Json.array().also { array ->
            list.forEach { s ->
                array.add(Json.`object`().also { obj ->
                    obj.add("moves", s.movesMade)
                    obj.add("timeMs", s.timeElapsedMs)
                })
            }
        })
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