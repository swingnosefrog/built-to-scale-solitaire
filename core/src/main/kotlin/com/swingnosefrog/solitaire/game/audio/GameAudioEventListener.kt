package com.swingnosefrog.solitaire.game.audio

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Disposable
import com.swingnosefrog.solitaire.game.Card
import com.swingnosefrog.solitaire.game.CardSymbol
import com.swingnosefrog.solitaire.game.assets.GameAssets
import com.swingnosefrog.solitaire.game.logic.CardStack
import com.swingnosefrog.solitaire.game.logic.CardZone
import com.swingnosefrog.solitaire.game.logic.GameEventListener
import com.swingnosefrog.solitaire.game.logic.GameLogic
import com.swingnosefrog.solitaire.soundsystem.SoundSystem
import com.swingnosefrog.solitaire.soundsystem.beads.BeadsSound
import paintbox.util.gdxutils.GdxDelayedRunnable
import paintbox.util.gdxutils.GdxRunnableTransition


class GameAudioEventListener(val gameAudio: GameAudio) : GameEventListener, Disposable {

    companion object {

        private const val MOVEMENT_SOUND_VOLUME = 1f
        private const val FOUNDATION_SOUND_VOLUME = 1f
    }
    
    private val gameLogic: GameLogic = gameAudio.gameLogic
    private val soundSystem: SoundSystem = gameAudio.soundSystem


    private val dealing: BeadsSound get() = GameAssets.get("sfx_game_dealing_loop")
    private var dealingLoopSoundId: Long = -1
    
    private fun stopDealingLoop() {
        soundSystem.getPlayerOrNull(dealingLoopSoundId)?.kill()
        dealingLoopSoundId = -1
    }

    override fun dispose() {
        stopDealingLoop()
    }

    override fun onDealingStart(gameLogic: GameLogic) {
        dealingLoopSoundId = soundSystem.playAudio(dealing) { player ->
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
        soundSystem.playAudio(sound) { player ->
            player.gain = MOVEMENT_SOUND_VOLUME
        }
    }

    private fun playPlaceSound(volumeMultiplier: Float = 1f) {
        soundSystem.playAudio(GameAssets.get<BeadsSound>("sfx_game_place")) { player ->
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
            soundSystem.playAudio(GameAssets.get<BeadsSound>("sfx_game_foundation_scale_${card.symbol.scaleOrder}")) { player ->
                player.gain = FOUNDATION_SOUND_VOLUME
            }
        }
    }

    override fun onWidgetSetCompleted(
        gameLogic: GameLogic,
        freeCellZone: CardZone,
    ) {
        soundSystem.playAudio(GameAssets.get<BeadsSound>("sfx_game_foundation_widget")) { player ->
            player.gain = 0.65f
        }
    }

    override fun onFoundationZoneCompleted(
        gameLogic: GameLogic,
        foundationZone: CardZone,
    ) {
        soundSystem.playAudio(GameAssets.get<BeadsSound>("sfx_game_foundation_scale_7")) { player ->
            player.gain = FOUNDATION_SOUND_VOLUME
        }
    }

    override fun onGameWon(gameLogic: GameLogic) {
        val sound = GameAssets.get<BeadsSound>("sfx_game_won")
        soundSystem.playAudio(sound) { player ->
            player.gain = 0.75f
        }
        
        // Attenuate volume of music
        val multiplier = gameAudio.music.musicGainMultiplier
        val sfxDuration = 3f
        val quietGain = 0.2f
        val pauseTransitionSec = 0.125f
        Gdx.app.postRunnable(GdxRunnableTransition(multiplier.get(), quietGain, pauseTransitionSec) { value, _ ->
            multiplier.set(value)
        })

        val resumeTransitionSec = 1f
        Gdx.app.postRunnable(GdxDelayedRunnable(sfxDuration) {
            Gdx.app.postRunnable(GdxRunnableTransition(quietGain, 1f, resumeTransitionSec) { value, _ ->
                multiplier.set(value)
            })
        })
    }

}