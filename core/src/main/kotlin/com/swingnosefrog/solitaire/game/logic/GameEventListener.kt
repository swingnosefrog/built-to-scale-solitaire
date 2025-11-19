package com.swingnosefrog.solitaire.game.logic

import com.swingnosefrog.solitaire.game.Card


interface GameEventListener {

    fun onDealingStart(gameLogic: GameLogic)

    fun onDealingEnd(gameLogic: GameLogic)

    fun onCardStackPickedUp(gameLogic: GameLogic, cardStack: CardStack, fromZone: CardZone)
    
    fun onCardStackPickupCancelled(gameLogic: GameLogic, cardStack: CardStack, originalZone: CardZone)

    fun onCardStackPlacedDown(gameLogic: GameLogic, cardStack: CardStack, toZone: CardZone)

    fun onCardAutoMoved(gameLogic: GameLogic, card: Card, targetZone: CardZone)
    
    fun onCardPlacedInFoundation(gameLogic: GameLogic, card: Card, foundationZone: CardZone)

    fun onWidgetSetCompleted(gameLogic: GameLogic, freeCellZone: CardZone)

    fun onFoundationZoneCompleted(gameLogic: GameLogic, foundationZone: CardZone)

    fun onGameWon(gameLogic: GameLogic)

    fun onCardsRecollected(gameLogic: GameLogic)
    

    open class Adapter : GameEventListener {

        override fun onDealingStart(gameLogic: GameLogic) {
        }

        override fun onDealingEnd(gameLogic: GameLogic) {
        }

        override fun onCardStackPickedUp(
            gameLogic: GameLogic,
            cardStack: CardStack,
            fromZone: CardZone,
        ) {
        }

        override fun onCardStackPickupCancelled(
            gameLogic: GameLogic,
            cardStack: CardStack,
            originalZone: CardZone,
        ) {
        }

        override fun onCardStackPlacedDown(
            gameLogic: GameLogic,
            cardStack: CardStack,
            toZone: CardZone,
        ) {
        }

        override fun onCardAutoMoved(
            gameLogic: GameLogic,
            card: Card,
            targetZone: CardZone,
        ) {
        }

        override fun onCardPlacedInFoundation(
            gameLogic: GameLogic,
            card: Card,
            foundationZone: CardZone,
        ) {
        }

        override fun onWidgetSetCompleted(
            gameLogic: GameLogic,
            freeCellZone: CardZone,
        ) {
        }

        override fun onFoundationZoneCompleted(
            gameLogic: GameLogic,
            foundationZone: CardZone,
        ) {
        }

        override fun onGameWon(gameLogic: GameLogic) {
        }

        override fun onCardsRecollected(gameLogic: GameLogic) {
        }
    }
}