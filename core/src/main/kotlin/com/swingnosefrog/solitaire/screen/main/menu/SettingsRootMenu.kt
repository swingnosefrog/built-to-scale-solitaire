package com.swingnosefrog.solitaire.screen.main.menu

import com.swingnosefrog.solitaire.Localization
import com.swingnosefrog.solitaire.menu.MenuOption
import paintbox.binding.ReadOnlyVar


class SettingsRootMenu(
    id: String
) : AbstractMenu(id) {
    
    override val headingText: ReadOnlyVar<String> = Localization["game.menu.settings.heading"]

    override val options: List<MenuOption> = listOf(
        MenuOption.Simple(Localization["game.menu.settings.option.gameplay"]) {},
        MenuOption.Simple(Localization["game.menu.settings.option.audio"]) {},
        MenuOption.Simple(Localization["game.menu.settings.option.video"]) {},
        MenuOption.Back(),
    )
}