package com.swingnosefrog.solitaire.game.audio.music

import com.swingnosefrog.solitaire.SolitaireGame
import com.swingnosefrog.solitaire.game.audio.FoundationNoteProvider
import com.swingnosefrog.solitaire.progress.Progress
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

    protected val defaultTrack: Track = Track.Default
    protected val practiceTrack: Track = Track.Practice

    override val allTracks: List<Track> = listOf(defaultTrack, practiceTrack)

}

class ProgressBasedTrackManager(private val game: SolitaireGame) : TrackManagerBase() {
    
    private val randomStartingTrack: Track by lazy {
        if (!hasUnlockedAllTracks()) practiceTrack else this.allTracks.random()
    }

    private val progress: Progress = game.progress
    private val musicTrackSetting: ReadOnlyVar<MusicTrackSetting> = Var { game.settings.audioMusicTrackSetting.use() }

    private val _currentTrack: Var<Track> = Var(getInitialTrack())
    override val currentTrack: ReadOnlyVar<Track> get() = _currentTrack

    init {
        musicTrackSetting.addListener { l ->
            if (hasUnlockedAllTracks()) {
                when (l.getOrCompute()) {
                    MusicTrackSetting.SHUFFLE_AFTER_WIN -> {}
                    MusicTrackSetting.BGM_DEFAULT -> changeTrack(defaultTrack)
                    MusicTrackSetting.BGM_PRACTICE -> changeTrack(practiceTrack)
                }
            }
        }
    }

    override fun changeTrack(track: Track) {
        if (!hasUnlockedAllTracks()) {
            _currentTrack.set(practiceTrack)
        } else {
            _currentTrack.set(track)
        }
    }

    private fun hasUnlockedAllTracks(): Boolean = progress.unlockedMusicTrackChanging.get()

    private fun getInitialTrack(): Track {
        if (!hasUnlockedAllTracks()) {
            return practiceTrack
        }

        return when (musicTrackSetting.getOrCompute()) {
            MusicTrackSetting.SHUFFLE_AFTER_WIN -> randomStartingTrack
            MusicTrackSetting.BGM_DEFAULT -> defaultTrack
            MusicTrackSetting.BGM_PRACTICE -> practiceTrack
        }
    }
}