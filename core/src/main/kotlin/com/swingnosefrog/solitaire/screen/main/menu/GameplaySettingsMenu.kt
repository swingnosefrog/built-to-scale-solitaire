package com.swingnosefrog.solitaire.screen.main.menu

import com.swingnosefrog.solitaire.Localization
import com.swingnosefrog.solitaire.menu.MenuOption
import paintbox.binding.ReadOnlyVar


class GameplaySettingsMenu(
    id: String
) : AbstractSettingsMenu(id) {
    
    override val headingText: ReadOnlyVar<String> = Localization["game.menu.gameplaySettings.heading"]

    override val options: List<MenuOption> = listOf(
        MenuOption.createNoOp(Localization["game.menu.gameplaySettings.option.1"]),
        MenuOption.createNoOp(Localization["game.menu.gameplaySettings.option.2"]),
        MenuOption.Back(),
    )

    init {
        this.menuSizeAdjustment.set(2)
    }
}