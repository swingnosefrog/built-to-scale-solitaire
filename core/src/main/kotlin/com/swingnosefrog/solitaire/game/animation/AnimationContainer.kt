package com.swingnosefrog.solitaire.game.animation


class AnimationContainer {
    
    private val queue: MutableList<GameAnimation> = mutableListOf()
    private var blockQueueForSec: Float = 0f
    
    private val playingAnimations: MutableList<PlayingAnimation> = mutableListOf()
    
    private var isReadingQueue: Boolean = false
    private val concurrentQueue: MutableList<GameAnimation> = mutableListOf()
    
    fun getPlayingAnimations(): List<PlayingAnimation> = playingAnimations
    
    fun anyAnimationsQueuedOrPlaying(): Boolean = playingAnimations.isNotEmpty() || queue.isNotEmpty() || concurrentQueue.isNotEmpty()
    
    fun enqueueAnimation(animation: GameAnimation) {
        (if (isReadingQueue) concurrentQueue else queue).add(animation)
    }

    /**
     * Returns true if any animations were played
     */
    fun renderUpdate(deltaSec: Float): Boolean {
        blockQueueForSec -= deltaSec

        if (queue.isNotEmpty() && blockQueueForSec <= 0) {
            isReadingQueue = true

            while (blockQueueForSec <= 0 && queue.isNotEmpty()) {
                val nextAnimation = queue.removeAt(0)
                val playingAnimation = nextAnimation.toPlayingAnimation()

                playingAnimations.add(playingAnimation)

                blockQueueForSec += nextAnimation.blockNextAnimationForSec
            }

            isReadingQueue = false
            queue.addAll(concurrentQueue)
        }

        if (blockQueueForSec <= 0f) {
            blockQueueForSec = 0f
        }
        
        var anyAnimationsPlayed = false
        if (playingAnimations.isNotEmpty()) {
            anyAnimationsPlayed = true
            playingAnimations.forEach { ani ->
                ani.renderUpdate(deltaSec)
            }
            playingAnimations.removeIf { it.isComplete() }
        }
        
        return anyAnimationsPlayed
    }
}