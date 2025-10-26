package com.swingnosefrog.solitaire.util

import com.badlogic.gdx.Input
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.ICON_QUESTION
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_0_INT
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_ALT_L
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_ALT_R
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_A_INT
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_BACKSLASH
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_BACKSPACE
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_BACKTICK
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_CAPS
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_CLOSE_BRACKET
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_COMMA
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_CONTROL_L
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_CONTROL_R
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_DASH
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_DELETE
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_DOWN
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_END
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_ENTER
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_EQUALS
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_ESCAPE
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_F1_INT
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_HOME
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_INSERT
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_LEFT
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_OPEN_BRACKET
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_PAGE_DOWN
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_PAGE_UP
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_PAUSE
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_PERIOD
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_QUOTE
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_RIGHT
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_SEMICOLON
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_SHIFT_L
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_SHIFT_R
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_SLASH
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_SPACE
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_TAB
import com.swingnosefrog.solitaire.fonts.PromptFontConsts.KEYBOARD_UP


fun attemptMapGdxKeyToPromptFont(gdxKey: Int): String {
    // Note: Not comprehensive
    return when (gdxKey) {
        in Input.Keys.NUM_0..Input.Keys.NUM_9 ->
            Character.toChars(gdxKey - Input.Keys.NUM_0 + KEYBOARD_0_INT).concatToString()

        in Input.Keys.A..Input.Keys.Z ->
            Character.toChars(gdxKey - Input.Keys.A + KEYBOARD_A_INT).concatToString()

        in Input.Keys.F1..Input.Keys.F12 ->
            Character.toChars(gdxKey - Input.Keys.F1 + KEYBOARD_F1_INT).concatToString()

        Input.Keys.UP -> KEYBOARD_UP
        Input.Keys.DOWN -> KEYBOARD_DOWN
        Input.Keys.LEFT -> KEYBOARD_LEFT
        Input.Keys.RIGHT -> KEYBOARD_RIGHT

        Input.Keys.ESCAPE -> KEYBOARD_ESCAPE
        Input.Keys.SPACE -> KEYBOARD_SPACE
        Input.Keys.ENTER -> KEYBOARD_ENTER
        Input.Keys.TAB -> KEYBOARD_TAB
        Input.Keys.CAPS_LOCK -> KEYBOARD_CAPS
        Input.Keys.BACKSPACE -> KEYBOARD_BACKSPACE
        Input.Keys.FORWARD_DEL -> KEYBOARD_DELETE
        Input.Keys.PAGE_UP -> KEYBOARD_PAGE_UP
        Input.Keys.PAGE_DOWN -> KEYBOARD_PAGE_DOWN
        Input.Keys.INSERT -> KEYBOARD_INSERT
        Input.Keys.HOME -> KEYBOARD_HOME
        Input.Keys.END -> KEYBOARD_END
        Input.Keys.PAUSE -> KEYBOARD_PAUSE

        Input.Keys.CONTROL_LEFT -> KEYBOARD_CONTROL_L
        Input.Keys.CONTROL_RIGHT -> KEYBOARD_CONTROL_R
        Input.Keys.SHIFT_LEFT -> KEYBOARD_SHIFT_L
        Input.Keys.SHIFT_RIGHT -> KEYBOARD_SHIFT_R
        Input.Keys.ALT_LEFT -> KEYBOARD_ALT_L
        Input.Keys.ALT_RIGHT -> KEYBOARD_ALT_R

        Input.Keys.MINUS -> KEYBOARD_DASH
        Input.Keys.EQUALS -> KEYBOARD_EQUALS
        Input.Keys.COMMA -> KEYBOARD_COMMA
        Input.Keys.PERIOD -> KEYBOARD_PERIOD
        Input.Keys.SLASH -> KEYBOARD_SLASH
        Input.Keys.SEMICOLON -> KEYBOARD_SEMICOLON
        Input.Keys.APOSTROPHE -> KEYBOARD_QUOTE
        Input.Keys.LEFT_BRACKET -> KEYBOARD_OPEN_BRACKET
        Input.Keys.RIGHT_BRACKET -> KEYBOARD_CLOSE_BRACKET
        Input.Keys.BACKSLASH -> KEYBOARD_BACKSLASH
        Input.Keys.GRAVE -> KEYBOARD_BACKTICK

        else -> ICON_QUESTION // Unknown
    }
}