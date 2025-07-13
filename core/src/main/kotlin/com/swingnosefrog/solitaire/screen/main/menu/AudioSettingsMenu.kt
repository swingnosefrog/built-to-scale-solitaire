package com.swingnosefrog.solitaire.screen.main.menu

import com.swingnosefrog.solitaire.Localization
import com.swingnosefrog.solitaire.SolitaireGame
import com.swingnosefrog.solitaire.menu.MenuOption
import paintbox.binding.FloatVar
import paintbox.binding.IntVar
import paintbox.binding.ReadOnlyFloatVar
import paintbox.binding.ReadOnlyVar
import paintbox.binding.Var

class AudioSettingsMenu(
    id: String
) : AbstractMenu(id) {
    
    companion object {

        private fun convertIntVolumeToFloatVar(volume: IntVar): FloatVar {
            return FloatVar(eager = true, computation = { volume.use().toFloat() }).apply {
                this.addListener { v ->
                    val converted = v.getOrCompute().toInt()
                    if (volume.get() != converted) {
                        volume.set(converted)
                    }
                }
            }
        }
    }
    
    override val headingText: ReadOnlyVar<String> = Localization["game.menu.audioSettings.heading"]

    val minVolumeValue: ReadOnlyFloatVar = ReadOnlyFloatVar.const(0f)
    val maxVolumeValue: ReadOnlyFloatVar = ReadOnlyFloatVar.const(100f)
    val tickVolumeValue: ReadOnlyFloatVar = ReadOnlyFloatVar.const(5f)

    override val options: List<MenuOption>

    init {
        fun createSliderOption(localization: ReadOnlyVar<String>, intVolume: IntVar): MenuOption.OptionWidget.Slider {
            val volumePercentageString =
                Localization["game.menu.audioSettings.option.volumePercentage", Var { listOf(intVolume.use()) }]
            val adjustedText: ReadOnlyVar<String> = Var {
                "${localization.use()}   [scale=0.75]\\[${volumePercentageString.use()}\\][]"
            }
            return MenuOption.OptionWidget.Slider(
                adjustedText,
                minVolumeValue, maxVolumeValue, tickVolumeValue,
                convertIntVolumeToFloatVar(intVolume)
            )
        }
        
        val settings = SolitaireGame.instance.settings
        options = listOf(
            createSliderOption(Localization["game.menu.audioSettings.option.masterVolume"], settings.masterVolume),
            createSliderOption(Localization["game.menu.audioSettings.option.musicVolume"], settings.musicVolume),
            createSliderOption(Localization["game.menu.audioSettings.option.sfxVolume"], settings.sfxVolume),
            MenuOption.Back(),
        )
        
        this.menuSizeAdjustment.set(3)
    }
}