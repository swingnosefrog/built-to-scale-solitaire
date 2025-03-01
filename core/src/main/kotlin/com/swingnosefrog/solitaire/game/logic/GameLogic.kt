package com.swingnosefrog.solitaire.game.logic


import com.swingnosefrog.solitaire.game.Card
import com.swingnosefrog.solitaire.game.CardSymbol
import com.swingnosefrog.solitaire.game.animation.AnimationContainer
import com.swingnosefrog.solitaire.game.animation.CardAnimation
import paintbox.Paintbox
import kotlin.math.floor
import kotlin.random.Random

class GameLogic(val randomSeed: Long = System.currentTimeMillis()) {
    
    companion object {

        const val CARD_WIDTH: Float = 1f
        const val CARD_HEIGHT: Float = 1.25f
    }

    val viewportWidth: Float = 20f
    val viewportHeight: Float = 11.25f
    
    private val deck: List<Card> = Card.createStandardDeck().shuffled(Random(randomSeed))
    
    val zones: CardZones = CardZones(this)
    val gameInput: GameInput by lazy { GameInput(this) }
    val animationContainer: AnimationContainer = AnimationContainer()
    
    init {
        Paintbox.LOGGER.debug("GameLogic: Random seed: $randomSeed")
    }
    
    init {
        zones.dealZone.cardStack.cardList.addAll(deck)
        
        val numPlayerZones = zones.playerZones.size
        var playerZoneIndex = 0
        for ((cardIndex, card) in zones.dealZone.cardStack.cardList.withIndex()) {
            val newZone = if (card.symbol == CardSymbol.SPARE) {
                zones.spareZone
            } else {
                zones.playerZones[playerZoneIndex++ % numPlayerZones]
            }
            
            val delay = if (cardIndex == 0) 0.75f else 0f
            animationContainer.enqueueAnimation(CardAnimation(card, zones.dealZone, newZone, 0.1f, delay))
        }
    }
    
    fun renderUpdate(deltaSec: Float) {
        val didAnimate = animationContainer.renderUpdate(deltaSec)
        
        if (didAnimate && !animationContainer.anyAnimationsQueuedOrPlaying()) {
            checkTableauAfterActivity()
        }

        gameInput.inputsDisabled.set(animationContainer.getPlayingAnimations().isNotEmpty())
    }

    fun checkTableauAfterActivity() {
//        if (gameWon) { // TODO
//            return
//        }

        // Flip over completed widgets in free cells
        for (freeCell in zones.freeCellZones) {
            if (!freeCell.isFlippedOver && freeCell.cardStack.isWidgetSet()) {
                freeCell.isFlippedOver = true
            }
        }
        for (foundation in zones.foundationZones) {
            if (!foundation.isFlippedOver && foundation.cardStack.cardList.size >= foundation.maxStackSize) {
                foundation.isFlippedOver = true
            }
        }

        // Game complete check
//        if ((freeCells + foundationZones).all { z -> z.stack.flippedOver.get() }) {
//            gameWon = true
//            inputsEnabled.set(false)
//            playSound("sfx_win", vol = 0.75f)
//            gameListeners.forEach { it.onWin() }
////            GlobalStats.solitaireGamesWon.increment()
//            // Falldown animation
//            maxConcurrentAnimations = Int.MAX_VALUE
//            val affectedZones = freeCells + foundationZones + playerZones
//            affectedZones.forEach { zone ->
//                val invisibleZone =
//                    CardZone(zone.x.get(), zone.y.get() + 6f * cardHeight, 999, false, showOutline = false)
//                val delayPer = 0.175f
//                zone.stack.cardList.asReversed().forEachIndexed { index, card ->
//                    enqueueAnimation(
//                        card,
//                        zone,
//                        invisibleZone,
//                        duration = 0.75f,
//                        delay = delayPer * index,
//                        isUnder = true
//                    )
//                }
//            }
//            return
//        }

        // Possible animations for auto-placing into the foundation pile
        val autoPlaceZones = zones.playerZones + zones.freeCellZones
        for (zone in autoPlaceZones) {
            // Check if last item in the zone can be put in the foundation pile
            // Other cards cannot be played on top of it, and if its value is 3 or greater, all the cards with one less value must ALREADY be in the foundation
            if (zone.canDragFrom && zone.cardStack.cardList.isNotEmpty()) {
                val tail = zone.cardStack.cardList.last()
                if (tail.symbol == CardSymbol.SPARE) {
                    // If it is the spare card, move it immediately to the spare zone
                    gameInput.cancelDrag()
                    enqueueDefaultCardMoveAnimation(tail, zone, zones.spareZone)
                    return
                } else if (!tail.symbol.isNumeric()) {
                    continue
                }


                val targetFoundation = zones.foundationZones.firstOrNull { fz ->
                    if (tail.symbol.scaleOrder == 0) { // 1
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
                    enqueueDefaultCardMoveAnimation(tail, zone, targetFoundation)
                    return
                }
            }
        }

        // Check if a spare card can be placed to cap a foundation
        val spareZone = this.zones.spareZone
        if (spareZone.cardStack.cardList.isNotEmpty()) {
            val tail = spareZone.cardStack.cardList.last()
            val tailSuit = tail.suit
            val targetFoundation = this.zones.foundationZones.firstOrNull {
                val top = it.cardStack.cardList.lastOrNull()
                top != null && top.suit == tailSuit && top.symbol == CardSymbol.SCALE_CARDS.first()
            }
            if (targetFoundation != null) {
                gameInput.cancelDrag()
                enqueueDefaultCardMoveAnimation(tail, spareZone, targetFoundation, durationSec = 0.333f)
                return
            }
        }
    }
    
    private fun enqueueDefaultCardMoveAnimation(card: Card, fromZone: CardZone, toZone: CardZone, durationSec: Float? = null) {
        animationContainer.enqueueAnimation(CardAnimation(card, fromZone, toZone, durationSec ?: 0.25f, 0f))
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
    
}