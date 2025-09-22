package com.swingnosefrog.solitaire.settings

import paintbox.binding.FloatVar
import paintbox.binding.ReadOnlyFloatVar


class VolumeGain(settings: SolitaireSettings) {
    
    private val masterVolumeGain: ReadOnlyFloatVar = FloatVar { settings.masterVolume.use() / 100f }
    
    val musicVolumeGain: ReadOnlyFloatVar = FloatVar { (settings.musicVolume.use() / 100f) * masterVolumeGain.use() }
    val sfxVolumeGain: ReadOnlyFloatVar = FloatVar { (settings.sfxVolume.use() / 100f) * masterVolumeGain.use() }
    
}