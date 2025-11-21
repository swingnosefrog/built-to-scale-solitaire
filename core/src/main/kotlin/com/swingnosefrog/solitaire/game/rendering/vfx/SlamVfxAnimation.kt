package com.swingnosefrog.solitaire.game.rendering.vfx

import com.swingnosefrog.solitaire.game.animation.GameAnimation
import com.swingnosefrog.solitaire.game.animation.PlayingAnimation
import com.swingnosefrog.solitaire.game.logic.CardZone


class SlamVfxAnimation(
    val cardZone: CardZone,
    val isLarge: Boolean,
    durationSec: Float,
    delaySec: Float,
    blockNextAnimationForSec: Float = 0f,
) : GameAnimation(durationSec, delaySec, blockNextAnimationForSec) {

    override fun toPlayingAnimation(): SlamVfxPlayingAnimation {
        return SlamVfxPlayingAnimation(this)
    }
}

class SlamVfxPlayingAnimation(val slamVfx: SlamVfxAnimation) : PlayingAnimation(slamVfx)
