package com.swingnosefrog.solitaire.steamworks

import com.codedisaster.steamworks.*
import com.swingnosefrog.solitaire.statistics.StatsImpl
import paintbox.Paintbox

object SteamStats : SteamUserStatsCallback {

    private const val LOGGING_TAG: String = "SteamStats-SteamUserStatsCallback"

    private const val STEAMWORKS_STAT_GAMES_WON: String = "games_won"
    private const val STEAMWORKS_STAT_GAMES_DEALT: String = "games_dealt"
    private const val STEAMWORKS_STAT_MOVES_MADE: String = "moves_made"

    fun updateStatCache(stats: StatsImpl): Boolean {
        val steamUserStats = getSteamUserStats() ?: return false

        var allSuccessful = false

        allSuccessful = steamUserStats.setStatI(STEAMWORKS_STAT_GAMES_WON, stats.gamesWon.value.get()) || allSuccessful
        allSuccessful =
            steamUserStats.setStatI(STEAMWORKS_STAT_GAMES_DEALT, stats.gamesDealt.value.get()) || allSuccessful
        allSuccessful =
            steamUserStats.setStatI(STEAMWORKS_STAT_MOVES_MADE, stats.movesMade.value.get()) || allSuccessful

        return allSuccessful
    }

    fun triggerAchievementProgress(stats: StatsImpl) {
        val steamUserStats = getSteamUserStats() ?: return

        fun triggerIfThreshold(achievementApiName: String, statName: String, threshold: Int, maxProgress: Int, currentStatValue: Int) {
            val oldStat = steamUserStats.getStatI(statName, 0)
            if (oldStat < threshold && currentStatValue >= threshold) {
                steamUserStats.indicateAchievementProgress(achievementApiName, currentStatValue, maxProgress)
            }
        }
        
        triggerIfThreshold(AchievementIds.WIN_GAMES_100, STEAMWORKS_STAT_GAMES_WON, 50, 100, stats.gamesWon.value.get())
    }

    fun markAchievementAsAchieved(achievementApiName: String): Boolean {
        val steamUserStats = getSteamUserStats() ?: return false

        return steamUserStats.setAchievement(achievementApiName)
    }

    fun persistStats(): Boolean {
        val steamUserStats = getSteamUserStats() ?: return false

        return steamUserStats.storeStats()
    }

    private fun getSteamUserStats(): SteamUserStats? = Steamworks.getSteamInterfaces()?.stats

    //region SteamUserStatsCallback functions

    override fun onUserStatsReceived(gameId: Long, steamIDUser: SteamID, result: SteamResult) {
        Paintbox.LOGGER.debug(
            "onUserStatsReceived for ${steamIDUser.accountID} (isValid = ${steamIDUser.isValid}), result $result",
            tag = LOGGING_TAG
        )
    }

    override fun onUserStatsStored(gameId: Long, result: SteamResult) {
        Paintbox.LOGGER.debug("onUserStatsStored, result $result", tag = LOGGING_TAG)
    }

    override fun onUserStatsUnloaded(steamIDUser: SteamID) {
    }

    override fun onUserAchievementStored(
        gameId: Long,
        isGroupAchievement: Boolean,
        achievementName: String,
        curProgress: Int,
        maxProgress: Int,
    ) {
        Paintbox.LOGGER.debug(
            "onUserAchievementStored, $achievementName ($curProgress / $maxProgress)",
            tag = LOGGING_TAG
        )
    }

    override fun onGlobalStatsReceived(gameId: Long, result: SteamResult) {
        Paintbox.LOGGER.debug("onGlobalStatsReceived, result $result", tag = LOGGING_TAG)
    }

    override fun onNumberOfCurrentPlayersReceived(success: Boolean, players: Int) {
        Paintbox.LOGGER.debug("onNumberOfCurrentPlayersReceived, success $success, players $players", tag = LOGGING_TAG)
    }

    override fun onLeaderboardFindResult(leaderboard: SteamLeaderboardHandle, found: Boolean) {
    }

    override fun onLeaderboardScoresDownloaded(
        leaderboard: SteamLeaderboardHandle,
        entries: SteamLeaderboardEntriesHandle,
        numEntries: Int,
    ) {
    }

    override fun onLeaderboardScoreUploaded(
        success: Boolean,
        leaderboard: SteamLeaderboardHandle,
        score: Int,
        scoreChanged: Boolean,
        globalRankNew: Int,
        globalRankPrevious: Int,
    ) {
    }

    //endregion
}