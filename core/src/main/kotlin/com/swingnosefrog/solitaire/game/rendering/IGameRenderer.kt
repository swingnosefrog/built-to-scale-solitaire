package com.swingnosefrog.solitaire.game.rendering

import com.badlogic.gdx.utils.viewport.Viewport
import paintbox.binding.BooleanVar


interface IGameRenderer {
    
    val viewport: Viewport
    val shouldApplyViewport: BooleanVar
    
    fun render(deltaSec: Float)
    
    fun resize(width: Int, height: Int)
}