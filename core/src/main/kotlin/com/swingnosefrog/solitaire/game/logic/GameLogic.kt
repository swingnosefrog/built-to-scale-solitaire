package com.swingnosefrog.solitaire.game.logic


import com.swingnosefrog.solitaire.game.Card
import com.swingnosefrog.solitaire.game.CardSymbol
import com.swingnosefrog.solitaire.game.animation.AnimationContainer
import com.swingnosefrog.solitaire.game.animation.CardAnimation
import com.swingnosefrog.solitaire.game.animation.GameAnimation
import com.swingnosefrog.solitaire.game.input.GameInput
import paintbox.Paintbox
import paintbox.binding.BooleanVar
import paintbox.binding.ReadOnlyBooleanVar
import kotlin.math.floor

class GameLogic(val deckInitializer: DeckInitializer) {

    companion object {

        const val CARD_WIDTH: Float = 1f
        const val CARD_HEIGHT: Float = 1.5f
    }

    private val deck: List<Card> = deckInitializer.initializeDeck(Card.createStandardDeck())

    val zones: CardZones = CardZones(this)
    val gameInput: GameInput by lazy { GameInput(this) }
    val gameStats: GameStats by lazy { GameStats(this) }
    val animationContainer: AnimationContainer = AnimationContainer()

    val eventDispatcher: GameEventDispatcher = DispatcherImpl()
    
    val isStillDealing: ReadOnlyBooleanVar = BooleanVar(true)
    val gameWon: ReadOnlyBooleanVar = BooleanVar(false)

    init {
        Paintbox.LOGGER.debug("DeckInitializer: $deckInitializer", tag = "GameLogic")
    }

    init {
        val dealZoneCardList = zones.dealZone.cardStack.cardList
        dealZoneCardList.addAll(deck)

        val numPlayerZones = zones.playerZones.size
        var playerZoneIndex = 0
        for ((cardIndex, card) in dealZoneCardList.withIndex()) {
            val newZone = if (card.symbol == CardSymbol.SPARE) {
                zones.spareZone
            } else {
                zones.playerZones[playerZoneIndex++ % numPlayerZones]
            }

            val isFirst = cardIndex == 0
            val isLast = cardIndex == dealZoneCardList.size - 1

            var onStart: (() -> Unit)? = null
            var onComplete: (() -> Unit)? = null

            if (isFirst) {
                onStart = { eventDispatcher.onDealingStart(this) }
            }
            if (isLast) {
                onComplete = { eventDispatcher.onDealingEnd(this) }
            }

            val delay = if (isFirst) 0.75f else 0f
            animationContainer.enqueueAnimation(
                CardAnimation(
                    card, zones.dealZone, newZone, 0.1f, delay,
                    onStartAction = onStart ?: {}, onCompleteAction = onComplete ?: {}
                )
            )
        }
        
        enqueueSlightDelayAnimation()
    }

    fun renderUpdate(deltaSec: Float) {
        val didAnimate = animationContainer.renderUpdate(deltaSec)

        if (didAnimate && !animationContainer.anyAnimationsQueuedOrPlaying()) {
            checkTableauAfterActivity()
        }

        val shouldInputsBeDisabled = gameWon.get() || animationContainer.getPlayingAnimations().isNotEmpty()
        gameInput.inputsDisabled.set(shouldInputsBeDisabled)
        
        gameStats.renderUpdate(deltaSec)
    }

    fun checkTableauAfterActivity() {
        if (gameWon.get()) {
            return
        }

        // Flip over completed widgets in free cells
        for (freeCellZone in zones.freeCellZones) {
            if (!freeCellZone.isFlippedOver && freeCellZone.cardStack.isWidgetSet()) {
                freeCellZone.isFlippedOver = true
                eventDispatcher.onWidgetSetCompleted(this, freeCellZone)
            }
        }
        for (foundationZone in zones.foundationZones) {
            if (!foundationZone.isFlippedOver && foundationZone.cardStack.cardList.size >= foundationZone.maxStackSize) {
                foundationZone.isFlippedOver = true
                eventDispatcher.onFoundationZoneCompleted(this, foundationZone)
            }
        }

        // Game complete check
        if ((zones.freeCellZones + zones.foundationZones).all { z -> z.isFlippedOver }) {
            (gameWon as BooleanVar).set(true)
            eventDispatcher.onGameWon(this)
            
            // TODO animation on win
            return
        }

        // Possible animations for auto-placing into the foundation pile
        val autoPlaceZones = zones.playerZones + zones.freeCellZones
        val spareZone = zones.spareZone
        for (zone in autoPlaceZones) {
            // Check if last item in the zone can be put in the foundation pile
            // Other cards cannot be played on top of it, and if its value is 3 or greater, all the cards with one less value must ALREADY be in the foundation
            if (zone.canDragFrom && zone.cardStack.cardList.isNotEmpty()) {
                val tail = zone.cardStack.cardList.last()
                if (tail.symbol == CardSymbol.SPARE) {
                    // If it is the spare card, move it immediately to the spare zone
                    gameInput.cancelDrag()
                    eventDispatcher.onCardAutoMoved(this, tail, spareZone)
                    enqueueDefaultCardMoveAnimation(tail, zone, spareZone)
                    return
                } else if (!tail.symbol.isNumeric()) {
                    continue
                }


                val targetFoundation = zones.foundationZones.firstOrNull { fz ->
                    if (tail.symbol == CardSymbol.NUM_1) {
                        fz.cardStack.cardList.isEmpty()
                    } else {
                        val lastInFoundation = fz.cardStack.cardList.lastOrNull()
                        lastInFoundation != null && lastInFoundation.suit == tail.suit && lastInFoundation.symbol.scaleOrder == tail.symbol.scaleOrder - 1
                    }
                }

                val canMoveToFoundation: Boolean = when (tail.symbol) {
                    CardSymbol.NUM_1 -> true
                    CardSymbol.NUM_2 -> true
                    else -> {
                        // All other cards with value one less than tail should ALREADY be in a foundation
                        // AKA: no cards with value one less than tail will be in the free zone/player zones
                        autoPlaceZones.all { z ->
                            z.cardStack.cardList.none { c ->
                                c.symbol.scaleOrder == tail.symbol.scaleOrder - 1
                            }
                        }
                    }
                }

                if (targetFoundation != null && canMoveToFoundation) {
                    gameInput.cancelDrag()
                    eventDispatcher.onCardAutoMoved(this, tail, targetFoundation)
                    enqueueDefaultCardMoveAnimation(tail, zone, targetFoundation)
                    return
                }
            }
        }

        // Check if a spare card can be placed to cap a foundation
        if (spareZone.cardStack.cardList.isNotEmpty()) {
            val tail = spareZone.cardStack.cardList.last()
            val tailSuit = tail.suit
            val targetFoundation = this.zones.foundationZones.firstOrNull {
                val top = it.cardStack.cardList.lastOrNull()
                top != null && top.suit == tailSuit && top.symbol == CardSymbol.SCALE_CARDS.first()
            }
            if (targetFoundation != null) {
                gameInput.cancelDrag()
                eventDispatcher.onCardAutoMoved(this, tail, targetFoundation)
                enqueueDefaultCardMoveAnimation(tail, spareZone, targetFoundation, durationSec = 0.333f)
                return
            }
        }
    }

    private fun enqueueDefaultCardMoveAnimation(
        card: Card,
        fromZone: CardZone,
        toZone: CardZone,
        durationSec: Float? = null,
    ) {
        val onCompleteAction: () -> Unit = {
            if (toZone in zones.foundationZones) {
                eventDispatcher.onCardPlacedInFoundation(this, card, toZone)
            }
        }
        animationContainer.enqueueAnimation(
            CardAnimation(
                card,
                fromZone,
                toZone,
                durationSec ?: 0.25f,
                0f,
                onCompleteAction = onCompleteAction
            )
        )
    }

    private fun enqueueSlightDelayAnimation(durationSec: Float? = null) {
        animationContainer.enqueueAnimation(object : GameAnimation(durationSec = durationSec ?: 0.15f, 0f) {
            override fun onComplete() {
                (isStillDealing as BooleanVar).set(false)
            }
        })
    }

    fun getSelectedZoneCoordinates(worldX: Float, worldY: Float): ZoneCoordinates? {
        for (zone in zones.allCardZones) {
            val stack = zone.cardStack
            val cardList = stack.cardList
            if (cardList.isEmpty()) continue

            // Height of the zone is cardHeight + (n - 1) * cardStackOffset 
            val cardStackOffset = zone.cardStack.stackDirection.yOffset
            val cardHeightMinusOffset = CARD_HEIGHT - cardStackOffset
            val zoneX = zone.x.get()
            val zoneY = zone.y.get()
            if (worldX in zoneX..(zoneX + CARD_WIDTH) &&
                worldY in zoneY..(zoneY + (cardList.size * cardStackOffset) + cardHeightMinusOffset)
            ) {
                val cardIndex = floor((worldY - zoneY) / cardStackOffset).toInt().coerceIn(0, cardList.size - 1)
                return ZoneCoordinates(
                    zone,
                    cardIndex,
                    worldX - zoneX,
                    worldY - (zoneY + cardIndex * cardStackOffset)
                )
            }
        }

        return null
    }

    fun isStackValidToMove(stack: List<Card>): Boolean {
        for ((index, card) in stack.withIndex()) {
            if (index > 0) {
                val prevCard = stack[index - 1]

                if (prevCard.symbol.isWidgetLike()) {
                    // Only alternating-symbol widgets can be here, with same suit
                    if (!(card.symbol.isWidgetLike() && card.symbol != prevCard.symbol && prevCard.suit == card.suit)) {
                        return false
                    }
                } else {
                    // Non-WIDGET cards: must be alternating suit and directly one up in DESCENDING scale order
                    if (!(prevCard.suit != card.suit && !card.symbol.isWidgetLike() && prevCard.symbol.scaleOrder - 1 == card.symbol.scaleOrder)) {
                        return false
                    }
                }
            }
        }
        return true
    }

    fun canPlaceStackOnZone(stack: CardStack, targetZone: CardZone): Boolean {
        // Can't place if flipped over
        if (targetZone.isFlippedOver) {
            return false
        }

        val dragStackList = stack.cardList

        // Special exception when dragging a widget set to a free cell
        if (stack.isWidgetSet() && targetZone in zones.freeCellZones && targetZone.cardStack.cardList.isEmpty()) {
            return true
        }

        // Dragging straight to foundation pile
        if (targetZone in zones.foundationZones) {
            if (dragStackList.size != 1) {
                return false
            }
            val dragItem = dragStackList.first()
            if (targetZone.cardStack.cardList.isEmpty()) {
                // Can only drag to an empty one if the dragItem has scaleOrder = 0 and if that suit isn't already in another foundation
                if (!(dragItem.symbol.scaleOrder == 0 &&
                            zones.foundationZones.none { z -> z.cardStack.cardList.firstOrNull()?.suit == dragItem.suit })
                ) {
                    return false
                }
            } else {
                val lastInTargetZone = targetZone.cardStack.cardList.last()
                return dragItem.suit == lastInTargetZone.suit && lastInTargetZone.symbol.scaleOrder == dragItem.symbol.scaleOrder - 1
            }
        }

        // Can't place if exceeds capacity
        if (dragStackList.size + targetZone.cardStack.cardList.size > targetZone.maxStackSize) {
            return false
        }

        return isStackValidToMove(listOfNotNull(targetZone.cardStack.cardList.lastOrNull()) + dragStackList)
    }


    private class DispatcherImpl : GameEventDispatcher {

        private var listeners: List<GameEventListener> = emptyList()

        override fun addListener(listener: GameEventListener) {
            listeners += listener
        }

        override fun removeListener(listener: GameEventListener) {
            listeners -= listener
        }


        //#region GameEventListener delegates

        override fun onDealingStart(gameLogic: GameLogic) {
            listeners.forEach { it.onDealingStart(gameLogic) }
        }

        override fun onDealingEnd(gameLogic: GameLogic) {
            listeners.forEach { it.onDealingEnd(gameLogic) }
        }

        override fun onCardStackPickedUp(
            gameLogic: GameLogic,
            cardStack: CardStack,
            fromZone: CardZone,
        ) {
            listeners.forEach { it.onCardStackPickedUp(gameLogic, cardStack, fromZone) }
        }

        override fun onCardStackPickupCancelled(
            gameLogic: GameLogic,
            cardStack: CardStack,
            originalZone: CardZone,
        ) {
            listeners.forEach { it.onCardStackPickupCancelled(gameLogic, cardStack, originalZone) }
        }

        override fun onCardStackPlacedDown(
            gameLogic: GameLogic,
            cardStack: CardStack,
            toZone: CardZone,
        ) {
            listeners.forEach { it.onCardStackPlacedDown(gameLogic, cardStack, toZone) }
        }

        override fun onCardAutoMoved(
            gameLogic: GameLogic,
            card: Card,
            targetZone: CardZone,
        ) {
            listeners.forEach { it.onCardAutoMoved(gameLogic, card, targetZone) }
        }

        override fun onCardPlacedInFoundation(
            gameLogic: GameLogic,
            card: Card,
            foundationZone: CardZone,
        ) {
            listeners.forEach { it.onCardPlacedInFoundation(gameLogic, card, foundationZone) }
        }

        override fun onWidgetSetCompleted(
            gameLogic: GameLogic,
            freeCellZone: CardZone,
        ) {
            listeners.forEach { it.onWidgetSetCompleted(gameLogic, freeCellZone) }
        }

        override fun onFoundationZoneCompleted(
            gameLogic: GameLogic,
            foundationZone: CardZone,
        ) {
            listeners.forEach { it.onFoundationZoneCompleted(gameLogic, foundationZone) }
        }

        override fun onGameWon(gameLogic: GameLogic) {
            listeners.forEach { it.onGameWon(gameLogic) }
        }

        //#endregion
    }

}