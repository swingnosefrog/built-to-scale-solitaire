package com.swingnosefrog.solitaire.game.logic

import com.swingnosefrog.solitaire.game.CardSymbol


class CardZones(
    logic: GameLogic,
) {

    val freeCellZones: List<CardZone>
    val playerZones: List<CardZone>
    val dealZone: CardZone
    val spareZone: CardZone
    val foundationZones: List<CardZone>

    val placeableCardZones: List<CardZone>
    val allCardZones: List<CardZone>

    init {
        val zoneSpacingX = 0.5f
        val zoneSpacingY = (1f / 3f) * (GameLogic.CARD_HEIGHT * 1.5f)

        freeCellZones = listOf(
            CardZone("free 0", (1 + zoneSpacingX) * 0, 0f, 1, true),
            CardZone("free 1", (1 + zoneSpacingX) * 1, 0f, 1, true),
            CardZone("free 2", (1 + zoneSpacingX) * 2, 0f, 1, true),
        ).onEach { zone ->
            zone.cardStack.stackDirection = StackDirection.UP
        }
        playerZones = listOf(
            CardZone("player 0", (1 + zoneSpacingX) * 0, (1 + zoneSpacingY), 999, true),
            CardZone("player 1", (1 + zoneSpacingX) * 1, (1 + zoneSpacingY), 999, true),
            CardZone("player 2", (1 + zoneSpacingX) * 2, (1 + zoneSpacingY), 999, true),
            CardZone("player 3", (1 + zoneSpacingX) * 3, (1 + zoneSpacingY), 999, true),
            CardZone("player 4", (1 + zoneSpacingX) * 4, (1 + zoneSpacingY), 999, true),
        ).onEach { zone ->
            zone.cardStack.stackDirection = StackDirection.DOWN
        }
        dealZone = CardZone("deal", (1 + zoneSpacingX) * 4.5f, 0f, 2, false, isFlippedOver = true, isOutlineVisible = false)
        spareZone = CardZone("spare", (1 + zoneSpacingX) * 3.5f, 0f, 3, false, isFlippedOver = false).apply {
            this.cardStack.stackDirection = StackDirection.UP
        }
        val numOfScaleCards = CardSymbol.SCALE_CARDS.size + 1 /* +1 for spare card */
        foundationZones = mutableListOf(
            CardZone("foundation 0", (1 + zoneSpacingX) * (playerZones.size + 0.5f), 0f, numOfScaleCards, false),
            CardZone("foundation 1", (1 + zoneSpacingX) * (playerZones.size + 0.5f), 0f, numOfScaleCards, false),
            CardZone("foundation 2", (1 + zoneSpacingX) * (playerZones.size + 0.5f), 0f, numOfScaleCards, false),
        ).apply {
            val totalHeight = this.size * 1 + (this.size - 1) * zoneSpacingY
            this.forEachIndexed { index, zone ->
                zone.y.set((5f - totalHeight) / 2 + index * (1 + zoneSpacingY))
            }
        }

        placeableCardZones = freeCellZones + playerZones + foundationZones
        allCardZones = freeCellZones + playerZones + spareZone + dealZone + foundationZones

        // Horizontal center
        val totalWidth = (allCardZones.maxOf { it.x.get() } + 1) - allCardZones.minOf { it.x.get() }
        val hcOffset = (logic.viewportWidth - totalWidth) / 2
        allCardZones.forEach {
            it.x.set(it.x.get() + hcOffset)
            it.y.set(it.y.get() + 1.5f)
        }
    }
}