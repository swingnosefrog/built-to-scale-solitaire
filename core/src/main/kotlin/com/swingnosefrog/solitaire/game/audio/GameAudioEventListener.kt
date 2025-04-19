package com.swingnosefrog.solitaire.game.audio

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Disposable
import com.swingnosefrog.solitaire.game.Card
import com.swingnosefrog.solitaire.game.CardSymbol
import com.swingnosefrog.solitaire.game.assets.GameAssets
import com.swingnosefrog.solitaire.game.logic.CardStack
import com.swingnosefrog.solitaire.game.logic.CardZone
import com.swingnosefrog.solitaire.game.logic.GameEventListener
import com.swingnosefrog.solitaire.game.logic.GameLogic
import com.swingnosefrog.solitaire.soundsystem.beads.BeadsSound


class GameAudioEventListener(val gameAudio: GameAudio) : GameEventListener, Disposable {

    companion object {

        private const val MOVEMENT_SOUND_VOLUME = 1f
        private const val FOUNDATION_SOUND_VOLUME = 1f
    }
    
    private val dealing: BeadsSound get() = GameAssets.get<BeadsSound>("sfx_game_dealing_loop")
    private var dealingLoopSoundId: Long = -1
    
    private fun stopDealingLoop() {
        gameAudio.getPlayerOrNull(dealingLoopSoundId)?.kill()
        dealingLoopSoundId = -1
    }
    
    fun stopAllSounds() {
        stopDealingLoop()
    }

    override fun dispose() {
        stopAllSounds()
    }

    override fun onDealingStart(gameLogic: GameLogic) {
        dealingLoopSoundId = gameAudio.playSfx(dealing) { player ->
            player.gain = MOVEMENT_SOUND_VOLUME
        }
    }

    override fun onDealingEnd(gameLogic: GameLogic) {
        stopDealingLoop()
    }

    override fun onCardStackPickedUp(
        gameLogic: GameLogic,
        cardStack: CardStack,
        fromZone: CardZone,
    ) {
        val sound = if (cardStack.cardList.size >= 3) {
            GameAssets.get<BeadsSound>("sfx_game_pickup_stack")
        } else {
            GameAssets.get<BeadsSound>("sfx_game_pickup${MathUtils.random(1, 3)}")
        }
        gameAudio.playSfx(sound) { player ->
            player.gain = MOVEMENT_SOUND_VOLUME
        }
    }

    private fun playPlaceSound(volumeMultiplier: Float = 1f) {
        gameAudio.playSfx(GameAssets.get<BeadsSound>("sfx_game_place")) { player ->
            player.gain = MOVEMENT_SOUND_VOLUME * volumeMultiplier
        }
    }

    override fun onCardStackPickupCancelled(
        gameLogic: GameLogic,
        cardStack: CardStack,
        originalZone: CardZone,
    ) {
        playPlaceSound(volumeMultiplier = 0.5f)
    }

    override fun onCardStackPlacedDown(
        gameLogic: GameLogic,
        cardStack: CardStack,
        toZone: CardZone,
    ) {
        if (cardStack.isWidgetSet() && toZone in gameLogic.zones.freeCellZones) {
            return
        }

        playPlaceSound()
    }

    override fun onCardPlacedInFoundation(
        gameLogic: GameLogic,
        card: Card,
        foundationZone: CardZone,
    ) {
        if (card.symbol in CardSymbol.SCALE_CARDS) {
            gameAudio.playSfx(GameAssets.get<BeadsSound>("sfx_game_foundation_scale_${card.symbol.scaleOrder}")) { player ->
                player.gain = FOUNDATION_SOUND_VOLUME
            }
        }
    }

    override fun onWidgetSetCompleted(
        gameLogic: GameLogic,
        freeCellZone: CardZone,
    ) {
        gameAudio.playSfx(GameAssets.get<BeadsSound>("sfx_game_foundation_widget")) { player ->
            player.gain = 0.65f
        }
    }

    override fun onFoundationZoneCompleted(
        gameLogic: GameLogic,
        foundationZone: CardZone,
    ) {
        gameAudio.playSfx(GameAssets.get<BeadsSound>("sfx_game_foundation_scale_7")) { player ->
            player.gain = FOUNDATION_SOUND_VOLUME
        }
    }

    override fun onGameWon(gameLogic: GameLogic) {
        val sound = GameAssets.get<BeadsSound>("sfx_game_won")
        gameAudio.playSfx(sound) { player ->
            player.gain = 0.75f
        }

        val gameMusic = gameAudio.music
        gameMusic?.attenuateMusicForGameWinSfx()
        gameMusic?.transitionToStemMix(GameMusic.StemMixes.AFTER_WIN, 0.5f)
    }

}