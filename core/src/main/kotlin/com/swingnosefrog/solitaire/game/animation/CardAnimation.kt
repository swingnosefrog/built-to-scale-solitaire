package com.swingnosefrog.solitaire.game.animation

import com.badlogic.gdx.math.MathUtils
import com.swingnosefrog.solitaire.game.Card
import com.swingnosefrog.solitaire.game.logic.CardZone


class CardAnimation(
    val card: Card,
    val fromZone: CardZone,
    val toZone: CardZone,
    durationSec: Float,
    delaySec: Float,
    blockAnimationForSec: Float = delaySec + durationSec,
    val onStartAction: () -> Unit = {},
    val onCompleteAction: () -> Unit = {},
) : GameAnimation(durationSec, delaySec, blockAnimationForSec) {

    override fun toPlayingAnimation(): CardPlayingAnimation {
        return CardPlayingAnimation(this)
    }

    override fun onStart() {
        super.onStart()

        val fromList = fromZone.cardStack.cardList
        val index = fromList.lastIndexOf(card)
        if (index >= 0) {
            fromList.removeAt(index)
        }
        
        onStartAction()
    }

    override fun onComplete() {
        super.onComplete()
        
        toZone.cardStack.cardList.add(card)
        
        onCompleteAction()
    }
}

class CardPlayingAnimation(val cardAnimation: CardAnimation) : PlayingAnimation(cardAnimation) {
    
    var fromX: Float = 0f
        private set
    var fromY: Float = 0f
        private set
    var toX: Float = 0f
        private set
    var toY: Float = 0f
        private set
    
    var currentX: Float = 0f
        private set
    var currentY: Float = 0f
        private set
    
    init {
        setUpCoordinates()
    }

    override fun onUpdate(progress: Float) {
        super.onUpdate(progress)
        
        currentX = MathUtils.lerp(fromX, toX, progress)
        currentY = MathUtils.lerp(fromY, toY, progress)
    }

    override fun onStart() {
        super.onStart()
        setUpCoordinates()
    }

    private fun setUpCoordinates() {
        val fromZone = cardAnimation.fromZone
        val fromStack = fromZone.cardStack
        val fromZoneList = fromStack.cardList
        var currentIndex = fromZoneList.indexOf(cardAnimation.card)
        if (currentIndex < 0) {
            currentIndex = fromZoneList.size - 1
        }

        fromX = fromZone.x.get()
        fromY = fromZone.y.get() + currentIndex * fromStack.stackDirection.yOffset

        val toZone = cardAnimation.toZone
        val toStack = toZone.cardStack
        toX = toZone.x.get()
        toY = toZone.y.get() + toStack.cardList.size * toStack.stackDirection.yOffset

        currentX = fromX
        currentY = fromY
    }
}