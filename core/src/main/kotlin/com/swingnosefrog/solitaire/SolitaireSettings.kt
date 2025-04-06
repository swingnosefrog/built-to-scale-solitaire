package com.swingnosefrog.solitaire

import com.badlogic.gdx.Preferences
import com.swingnosefrog.solitaire.PreferenceKeys.SETTINGS_FULLSCREEN
import com.swingnosefrog.solitaire.PreferenceKeys.SETTINGS_FULLSCREEN_MONITOR
import com.swingnosefrog.solitaire.PreferenceKeys.SETTINGS_MAX_FPS
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

    init {
        val initScope = InitScope()
        with(initScope) {
            vsyncEnabled = KeyValue.Bool(SETTINGS_VSYNC, false).add().value
            maxFramerate =
                KeyValue.Int(SETTINGS_MAX_FPS, determineMaxRefreshRate(), min = 0, max = Int.MAX_VALUE).add().value
            windowedResolution = KeyValue.WindowSize(SETTINGS_WINDOWED_RESOLUTION, Solitaire.DEFAULT_SIZE).add().value
            fullscreen = KeyValue.Bool(SETTINGS_FULLSCREEN, true).add().value
            fullscreenMonitor = KeyValue.MonitorInfo(SETTINGS_FULLSCREEN_MONITOR, null).add().value
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