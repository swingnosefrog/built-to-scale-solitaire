package com.swingnosefrog.solitaire.game.audio.music

import com.swingnosefrog.solitaire.SolitaireGame
import com.swingnosefrog.solitaire.game.audio.FoundationNoteProvider
import com.swingnosefrog.solitaire.progress.Progress
import paintbox.binding.GenericVar
import paintbox.binding.ReadOnlyVar
import paintbox.binding.Var


interface TrackManager : FoundationNoteProvider {

    val allTracks: List<Track>
    val currentTrack: ReadOnlyVar<Track>

    override val notesAssetKeys: List<String>
        get() = currentTrack.getOrCompute().notesAssetKeys

    fun changeTrack(track: Track)

    fun shuffleTrack() {
        val nextTrack = allTracks.filter { it != currentTrack.getOrCompute() }.randomOrNull()
        if (nextTrack != null) {
            changeTrack(nextTrack)
        }
    }
}

abstract class TrackManagerBase : TrackManager {

    protected val defaultTrack: Track = Track.Classic
    protected val practiceTrack: Track = Track.Practice

    override val allTracks: List<Track> = listOf(defaultTrack, practiceTrack)

}

class ProgressBasedTrackManager(private val game: SolitaireGame) : TrackManagerBase() {
    
    private val randomStartingTrack: Track by lazy {
        if (!hasUnlockedAllTracks()) practiceTrack else this.allTracks.random()
    }

    private val progress: Progress = game.progress
    private val musicTrackSetting: ReadOnlyVar<MusicTrackSetting> = GenericVar(eager = true) { game.settings.audioMusicTrackSetting.use() }

    final override val currentTrack: ReadOnlyVar<Track>
        field = Var(getInitialTrack())

    init {
        musicTrackSetting.addListener { l ->
            val newValue = l.getOrCompute()
            if (hasUnlockedAllTracks()) {
                when (newValue) {
                    MusicTrackSetting.SHUFFLE_AFTER_WIN -> {}
                    MusicTrackSetting.BGM_CLASSIC -> changeTrack(defaultTrack)
                    MusicTrackSetting.BGM_PRACTICE -> changeTrack(practiceTrack)
                }
            }
        }
    }

    override fun changeTrack(track: Track) {
        if (!hasUnlockedAllTracks()) {
            currentTrack.set(practiceTrack)
        } else {
            currentTrack.set(track)
        }
    }

    private fun hasUnlockedAllTracks(): Boolean = progress.unlockedMusicTrackChanging.get()

    private fun getInitialTrack(): Track {
        if (!hasUnlockedAllTracks()) {
            return practiceTrack
        }

        return when (musicTrackSetting.getOrCompute()) {
            MusicTrackSetting.SHUFFLE_AFTER_WIN -> randomStartingTrack
            MusicTrackSetting.BGM_CLASSIC -> defaultTrack
            MusicTrackSetting.BGM_PRACTICE -> practiceTrack
        }
    }
}