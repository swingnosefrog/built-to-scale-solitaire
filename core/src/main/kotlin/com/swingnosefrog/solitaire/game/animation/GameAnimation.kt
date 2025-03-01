package com.swingnosefrog.solitaire.game.animation


open class GameAnimation(
    val durationSec: Float,
    val delaySec: Float,
    val blockNextAnimationForSec: Float = delaySec + durationSec
) {
    
    open fun toPlayingAnimation(): PlayingAnimation {
        return PlayingAnimation(this)
    }
    
    open fun onStart() {
    }
    
    open fun update(progress: Float) {
    }
    
    open fun onComplete() {
    }
}