package com.swingnosefrog.solitaire.screen.main.menu

import com.swingnosefrog.solitaire.Localization
import com.swingnosefrog.solitaire.menu.MenuOption
import paintbox.binding.ReadOnlyVar

class AudioSettingsMenu(
    id: String
) : AbstractMenu(id) {
    
    override val headingText: ReadOnlyVar<String> = Localization["game.menu.audioSettings.heading"]

    override val options: List<MenuOption> = listOf(
        MenuOption.createNoOp(Localization["game.menu.audioSettings.option.masterVolume"]),
        MenuOption.createNoOp(Localization["game.menu.audioSettings.option.musicVolume"]),
        MenuOption.createNoOp(Localization["game.menu.audioSettings.option.sfxVolume"]),
        MenuOption.Back(),
    )

    init {
        this.menuSizeAdjustment.set(3)
    }
}