package com.swingnosefrog.solitaire.statistics

import com.badlogic.gdx.files.FileHandle
import com.swingnosefrog.solitaire.persistence.GameSaveLocationHelper
import com.swingnosefrog.solitaire.statistics.formatter.LocalizedStatFormatter
import paintbox.Paintbox
import java.util.concurrent.atomic.AtomicBoolean


class StatsImpl : AbstractStats() {
    
    companion object {
        
        private val loggingTag: String = StatsImpl::class.java.simpleName
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
    
    //endregion
    

    private val successfulLoad: AtomicBoolean = AtomicBoolean(false)

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