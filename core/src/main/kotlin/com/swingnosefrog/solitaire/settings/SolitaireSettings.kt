package com.swingnosefrog.solitaire.settings

import com.badlogic.gdx.Preferences
import com.swingnosefrog.solitaire.SolitaireGame
import com.swingnosefrog.solitaire.game.assets.CardSkin
import com.swingnosefrog.solitaire.game.audio.music.MusicTrackSetting
import com.swingnosefrog.solitaire.game.input.MouseMode
import com.swingnosefrog.solitaire.settings.PreferenceKeys.SETTINGS_AUDIO_MUSIC_TRACK
import com.swingnosefrog.solitaire.settings.PreferenceKeys.SETTINGS_FULLSCREEN
import com.swingnosefrog.solitaire.settings.PreferenceKeys.SETTINGS_FULLSCREEN_MONITOR
import com.swingnosefrog.solitaire.settings.PreferenceKeys.SETTINGS_GAMEPLAY_CARD_SKIN
import com.swingnosefrog.solitaire.settings.PreferenceKeys.SETTINGS_GAMEPLAY_MOUSE_MODE
import com.swingnosefrog.solitaire.settings.PreferenceKeys.SETTINGS_GAMEPLAY_SHOW_CARD_CURSOR_IN_MOUSE_MODE
import com.swingnosefrog.solitaire.settings.PreferenceKeys.SETTINGS_GAMEPLAY_SHOW_HOW_TO_PLAY_BUTTON
import com.swingnosefrog.solitaire.settings.PreferenceKeys.SETTINGS_GAMEPLAY_SHOW_MOVE_COUNTER
import com.swingnosefrog.solitaire.settings.PreferenceKeys.SETTINGS_GAMEPLAY_SHOW_TIMER
import com.swingnosefrog.solitaire.settings.PreferenceKeys.SETTINGS_MASTER_VOLUME
import com.swingnosefrog.solitaire.settings.PreferenceKeys.SETTINGS_MAX_FPS
import com.swingnosefrog.solitaire.settings.PreferenceKeys.SETTINGS_MUSIC_VOLUME
import com.swingnosefrog.solitaire.settings.PreferenceKeys.SETTINGS_SFX_VOLUME
import com.swingnosefrog.solitaire.settings.PreferenceKeys.SETTINGS_VSYNC
import com.swingnosefrog.solitaire.settings.PreferenceKeys.SETTINGS_WINDOWED_RESOLUTION
import com.swingnosefrog.solitaire.util.WindowSizeUtils
import paintbox.binding.BooleanVar
import paintbox.binding.IntVar
import paintbox.binding.Var
import paintbox.prefs.KeyValue
import paintbox.prefs.NewIndicator
import paintbox.prefs.PaintboxPreferences
import paintbox.util.MonitorInfo
import paintbox.util.WindowSize


class SolitaireSettings(
    main: SolitaireGame,
    prefs: Preferences,
    isRunningOnSteamDeckHint: Boolean?,
) : PaintboxPreferences<SolitaireGame>(main, prefs) {

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
    
    val gameplayMouseMode: Var<MouseMode>
    val gameplayShowCardCursorInMouseMode: BooleanVar
    val gameplayCardSkin: Var<CardSkin>
    val gameplayShowMoveCounter: BooleanVar
    val gameplayShowTimer: BooleanVar
    val gameplayShowHowToPlayButton: BooleanVar
    
    val audioMusicTrackSetting: Var<MusicTrackSetting>

    init {
        val definitelyOnSteamDeck = isRunningOnSteamDeckHint == true
        
        val initScope = InitScope()
        with(initScope) {
            vsyncEnabled = KeyValue.Bool(SETTINGS_VSYNC, false).add().value
            maxFramerate =
                KeyValue.Int(SETTINGS_MAX_FPS, determineMaxRefreshRate(), min = 0, max = Int.MAX_VALUE).add().value
            windowedResolution =
                KeyValue.WindowSize(SETTINGS_WINDOWED_RESOLUTION, WindowSizeUtils.DEFAULT_COMPUTED_WINDOWED_SIZE)
                    .add().value
            fullscreen = KeyValue.Bool(SETTINGS_FULLSCREEN, true).add().value
            fullscreenMonitor = KeyValue.MonitorInfo(SETTINGS_FULLSCREEN_MONITOR, null).add().value

            masterVolume = KeyValue.Int(SETTINGS_MASTER_VOLUME, 50, min = 0, max = 100).add().value
            musicVolume = KeyValue.Int(SETTINGS_MUSIC_VOLUME, 100, min = 0, max = 100).add().value
            sfxVolume = KeyValue.Int(SETTINGS_SFX_VOLUME, 100, min = 0, max = 100).add().value
            
            gameplayMouseMode = KeyValue.Enum<MouseMode>(SETTINGS_GAMEPLAY_MOUSE_MODE, MouseMode.CLICK_AND_DRAG).add().value
            gameplayShowCardCursorInMouseMode = KeyValue.Bool(SETTINGS_GAMEPLAY_SHOW_CARD_CURSOR_IN_MOUSE_MODE, true).add().value
            gameplayCardSkin = KeyValue.Enum<CardSkin>(SETTINGS_GAMEPLAY_CARD_SKIN, CardSkin.MODERN).add().value
            gameplayShowMoveCounter = KeyValue.Bool(SETTINGS_GAMEPLAY_SHOW_MOVE_COUNTER, true).add().value
            gameplayShowTimer = KeyValue.Bool(SETTINGS_GAMEPLAY_SHOW_TIMER, true).add().value
            gameplayShowHowToPlayButton = KeyValue.Bool(SETTINGS_GAMEPLAY_SHOW_HOW_TO_PLAY_BUTTON, true).add().value
            
            audioMusicTrackSetting = KeyValue.Enum<MusicTrackSetting>(SETTINGS_AUDIO_MUSIC_TRACK, MusicTrackSetting.BGM_PRACTICE).add().value
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