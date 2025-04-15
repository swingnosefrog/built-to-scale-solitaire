package com.swingnosefrog.solitaire.game.animation

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.swingnosefrog.solitaire.game.Card
import com.swingnosefrog.solitaire.game.logic.CardZone
import kotlin.math.sqrt


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
    
    private var distance: Float = 0f
    
    var currentX: Float = 0f
        private set
    var currentY: Float = 0f
        private set
    
    init {
        setUpCoordinates()
    }
    
    private fun arcFunction(progress: Float): Float {
        // y = -(2x - 1)^2 + 1
        val x = (2 * progress - 1)
        return -(x * x) + 1
    }

    override fun onUpdate(progress: Float) {
        super.onUpdate(progress)
        
        val interpolationX = Interpolation.linear
        val interpolationY = Interpolation.linear
        
        val interpolationArc = Interpolation.linear
        val normalizedDistance = MathUtils.norm(0.5f, 3f, distance).coerceIn(0f, 1f)
        val arcStrength = interpolationArc.apply(0.1f, 1f, normalizedDistance)
        
        currentX = interpolationX.apply(fromX, toX, progress)
        currentY = interpolationY.apply(fromY, toY, progress) + arcFunction(progress) * (-0.75f * arcStrength)
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
        
        val deltaX = toX - fromX
        val deltaY = toY - fromY
        distance = sqrt(deltaX * deltaX + deltaY * deltaY)
    }
}