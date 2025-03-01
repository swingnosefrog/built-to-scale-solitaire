package com.swingnosefrog.solitaire.game.animation


open class PlayingAnimation(val animation: GameAnimation) {
    
    private var started: Boolean = false
    private var ended: Boolean = false
    
    var secondsElapsed: Float = -animation.delaySec
        protected set
    
    fun renderUpdate(deltaSec: Float) {
        if (ended) return

        secondsElapsed += deltaSec.coerceAtLeast(0f)
        
        if (secondsElapsed >= 0f) {
            if (!started) {
                started = true
                animation.onStart()
                animation.update(0f)
                this.onStart()
                this.onUpdate(0f)
            }

            val progress = getProgress()
            if (!ended) {
                animation.update(progress)
                onUpdate(progress)
            }
            
            if (progress >= 1f) {
                if (!ended) {
                    ended = true
                    animation.onComplete()
                    animation.update(1f)
                    this.onComplete()
                    this.onUpdate(1f)
                }
            }
        }
    }
    
    protected open fun onStart() {
    }
    
    protected open fun onUpdate(progress: Float) {
    }
    
    protected open fun onComplete() {
    }
    
    protected fun getProgress(): Float {
        if (animation.durationSec <= 0f) return 1f
        
        return (secondsElapsed / animation.durationSec).coerceIn(0f, 1f)
    }
    
    fun isComplete(): Boolean = ended
}
