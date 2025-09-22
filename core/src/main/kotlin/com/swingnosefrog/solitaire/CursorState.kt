package com.swingnosefrog.solitaire

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Buttons
import paintbox.registry.AssetRegistry


object CursorState {

    private var lastCursorAssetKey: String? = null
    
    private fun getCurrentCursorAssetKey(): String {
        val isClickDown = Gdx.input.isButtonPressed(Buttons.LEFT)
        val height = Gdx.graphics.height
        
        return if (height < 1080) {
            if (isClickDown) "cursor_normal_pressed" else "cursor_normal"
        } else if (height < 1440) {
            if (isClickDown) "cursor_normal_pressed_2x" else "cursor_normal_2x"
        } else {
            if (isClickDown) "cursor_normal_pressed_3x" else "cursor_normal_3x"
        }
    }
    
    fun preRender() {
        val current = getCurrentCursorAssetKey()
        if (current != lastCursorAssetKey) {
            lastCursorAssetKey = current
            
            Gdx.graphics.setCursor(AssetRegistry[current])
        }
    }
}