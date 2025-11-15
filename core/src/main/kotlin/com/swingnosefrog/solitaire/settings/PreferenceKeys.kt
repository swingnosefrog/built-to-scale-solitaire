package com.swingnosefrog.solitaire.settings


@Suppress("MayBeConstant")
object PreferenceKeys {

    val LAST_VERSION: String = "lastVersion"

    val SETTINGS_VSYNC = "settings_vsync"
    val SETTINGS_MAX_FPS = "settings_max_framerate"
    val SETTINGS_WINDOWED_RESOLUTION = "settings_windowedResolution"
    val SETTINGS_FULLSCREEN = "settings_fullscreen"
    val SETTINGS_FULLSCREEN_MONITOR = "settings_fullscreen_monitor"
    
    val SETTINGS_MASTER_VOLUME = "settings_volume_master"
    val SETTINGS_MUSIC_VOLUME = "settings_volume_music"
    val SETTINGS_SFX_VOLUME = "settings_volume_sfx"
    
    val SETTINGS_GAMEPLAY_MOUSE_MODE = "settings_gameplay_mouseMode"
    val SETTINGS_GAMEPLAY_SHOW_CARD_CURSOR_IN_MOUSE_MODE = "settings_gameplay_showCardCursorInMouseMode"
    val SETTINGS_GAMEPLAY_CARD_SKIN = "settings_gameplay_cardSkin"
    val SETTINGS_GAMEPLAY_SHOW_MOVE_COUNTER = "settings_gameplay_showMoveCounter"
    val SETTINGS_GAMEPLAY_SHOW_TIMER = "settings_gameplay_showTimer"
    val SETTINGS_GAMEPLAY_SHOW_HOW_TO_PLAY_BUTTON = "settings_gameplay_showHowToPlayButton"

}