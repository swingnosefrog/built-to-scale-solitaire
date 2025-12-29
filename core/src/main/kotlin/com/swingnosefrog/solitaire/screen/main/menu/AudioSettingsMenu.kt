package com.swingnosefrog.solitaire.screen.main.menu

import com.swingnosefrog.solitaire.Localization
import com.swingnosefrog.solitaire.SolitaireGame
import com.swingnosefrog.solitaire.game.audio.music.MusicTrackSetting
import com.swingnosefrog.solitaire.menu.MenuOption
import paintbox.binding.*
import paintbox.ui.StringVarConverter

class AudioSettingsMenu(
    id: String
) : AbstractSettingsMenu(id) {
    
    companion object {

        private fun convertIntVolumeToFloatVar(volume: IntVar): FloatVar {
            // Note: intentionally not a Compute-type binding, as this value can be `set` by the UI and override the binding to be Const
            return FloatVar(volume.get().toFloat()).apply {
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
    
    private val unlockedMusicTrackChanging: ReadOnlyBooleanVar = BooleanVar {
        SolitaireGame.instance.progress.unlockedMusicTrackChanging.use()
    }

    override val options: List<MenuOption>

    init {
        fun createSliderOption(localization: ReadOnlyVar<String>, intVolume: IntVar): Pair<MenuOption.OptionWidget.Slider, () -> Unit> {
            val volumePercentageString =
                Localization["game.menu.audioSettings.option.volumePercentage", Var { listOf(intVolume.use()) }]
            val adjustedText: ReadOnlyVar<String> = Var {
                "${localization.use()}  [font=ui_main_sansserif scale=0.75]\\[${volumePercentageString.use()}\\][]"
            }
            val floatVolume = convertIntVolumeToFloatVar(intVolume)
            val syncFunc = fun() {
                floatVolume.set(intVolume.get().toFloat())
            }
            return MenuOption.OptionWidget.Slider(
                adjustedText,
                minVolumeValue, maxVolumeValue, tickVolumeValue,
                floatVolume
            ) to syncFunc
        }


        val (masterVolSlider, masterVolSync) = createSliderOption(
            Localization["game.menu.audioSettings.option.masterVolume"],
            settings.masterVolume
        )
        val (musicVolSlider, musicVolSync) = createSliderOption(
            Localization["game.menu.audioSettings.option.musicVolume"],
            settings.musicVolume
        )
        val (sfxVolSlider, sfxVolSync) = createSliderOption(
            Localization["game.menu.audioSettings.option.sfxVolume"],
            settings.sfxVolume
        )

        options = listOf(
            masterVolSlider,
            musicVolSlider,
            sfxVolSlider,
            MenuOption.Simple(Localization["game.menu.audioSettings.option.resetVolumesToDefault"], fun(_) {
                settings.resetVolumeSettingsToDefault()
                masterVolSync()
                musicVolSync()
                sfxVolSync()
            }),

            MenuOption.Separator(),

            MenuOption.OptionWidget.Cycle(
                Localization["game.menu.audioSettings.option.musicTrackSetting"],
                ReadOnlyVar.const(MusicTrackSetting.entries.toList()),
                settings.audioMusicTrackSetting,
                StringVarConverter { setting: MusicTrackSetting ->
                    Var<String> {
                        if (!unlockedMusicTrackChanging.use()) {
                            Localization["game.menu.optionUnlockedAfterFirstWin"]
                        } else {
                            when (setting) {
                                MusicTrackSetting.SHUFFLE_AFTER_WIN -> Localization["game.menu.audioSettings.option.musicTrackSetting.shuffleAfterWin"]
                                MusicTrackSetting.BGM_PRACTICE -> Localization["game.menu.audioSettings.option.musicTrackSetting.practice"]
                                MusicTrackSetting.BGM_CLASSIC -> Localization["game.menu.audioSettings.option.musicTrackSetting.classic"]
                            }
                        }.use()
                    }
                }
            ).apply { 
                this.disabled.bind { !unlockedMusicTrackChanging.use() }
            },

            MenuOption.Back(),
        )

        this.menuSizeAdjustment.set(4)
    }
}