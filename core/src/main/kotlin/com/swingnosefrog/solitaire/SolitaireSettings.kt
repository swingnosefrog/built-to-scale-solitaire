package com.swingnosefrog.solitaire

import com.badlogic.gdx.Preferences
import com.swingnosefrog.solitaire.PreferenceKeys.SETTINGS_FULLSCREEN
import com.swingnosefrog.solitaire.PreferenceKeys.SETTINGS_FULLSCREEN_MONITOR
import com.swingnosefrog.solitaire.PreferenceKeys.SETTINGS_GAMEPLAY_SHOW_MOVE_COUNTER
import com.swingnosefrog.solitaire.PreferenceKeys.SETTINGS_GAMEPLAY_SHOW_TIMER
import com.swingnosefrog.solitaire.PreferenceKeys.SETTINGS_MASTER_VOLUME
import com.swingnosefrog.solitaire.PreferenceKeys.SETTINGS_MAX_FPS
import com.swingnosefrog.solitaire.PreferenceKeys.SETTINGS_MUSIC_VOLUME
import com.swingnosefrog.solitaire.PreferenceKeys.SETTINGS_SFX_VOLUME
import com.swingnosefrog.solitaire.PreferenceKeys.SETTINGS_VSYNC
import com.swingnosefrog.solitaire.PreferenceKeys.SETTINGS_WINDOWED_RESOLUTION
import paintbox.binding.BooleanVar
import paintbox.binding.IntVar
import paintbox.binding.Var
import paintbox.prefs.KeyValue
import paintbox.prefs.NewIndicator
import paintbox.prefs.PaintboxPreferences
import paintbox.util.MonitorInfo
import paintbox.util.WindowSize


class SolitaireSettings(main: SolitaireGame, prefs: Preferences) : PaintboxPreferences<SolitaireGame>(main, prefs) {

    override val allKeyValues: List<KeyValue<*>>
    override val allNewIndicators: List<NewIndicator>

    val vsyncEnabled: BooleanVar
    val maxFramerate: IntVar
    val windowedResolution: Var<WindowSize>
    val fullscreen: BooleanVar
    val fullscreenMonitor: Var<MonitorInfo?>

    val masterVolume: IntVar
    val musicVolume: IntVar
    val sfxVolume: IntVar
    
    val gameplayShowMoveCounter: BooleanVar
    val gameplayShowTimer: BooleanVar

    init {
        val initScope = InitScope()
        with(initScope) {
            vsyncEnabled = KeyValue.Bool(SETTINGS_VSYNC, false).add().value
            maxFramerate =
                KeyValue.Int(SETTINGS_MAX_FPS, determineMaxRefreshRate(), min = 0, max = Int.MAX_VALUE).add().value
            windowedResolution = KeyValue.WindowSize(SETTINGS_WINDOWED_RESOLUTION, Solitaire.DEFAULT_SIZE).add().value
            fullscreen = KeyValue.Bool(SETTINGS_FULLSCREEN, true).add().value
            fullscreenMonitor = KeyValue.MonitorInfo(SETTINGS_FULLSCREEN_MONITOR, null).add().value

            masterVolume = KeyValue.Int(SETTINGS_MASTER_VOLUME, 50, min = 0, max = 100).add().value
            musicVolume = KeyValue.Int(SETTINGS_MUSIC_VOLUME, 100, min = 0, max = 100).add().value
            sfxVolume = KeyValue.Int(SETTINGS_SFX_VOLUME, 100, min = 0, max = 100).add().value
            
            gameplayShowMoveCounter = KeyValue.Bool(SETTINGS_GAMEPLAY_SHOW_MOVE_COUNTER, true).add().value
            gameplayShowTimer = KeyValue.Bool(SETTINGS_GAMEPLAY_SHOW_TIMER, true).add().value
        }

        allKeyValues = initScope.allKeyValues.toList()
        allNewIndicators = initScope.allNewIndicators.toList()
    }

    override fun getLastVersionKey(): String = PreferenceKeys.LAST_VERSION

    override fun setStartupSettings() {
        setFpsAndVsync(maxFramerate, vsyncEnabled)
        setFullscreenOrWindowed(fullscreen, fullscreenMonitor, windowedResolution)
    }
}