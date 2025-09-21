package com.swingnosefrog.solitaire.screen.main.menu

import com.swingnosefrog.solitaire.Localization
import com.swingnosefrog.solitaire.menu.MenuOption
import paintbox.binding.ReadOnlyVar


class GameplaySettingsMenu(
    id: String
) : AbstractSettingsMenu(id) {
    
    override val headingText: ReadOnlyVar<String> = Localization["game.menu.gameplaySettings.heading"]

    override val options: List<MenuOption> = listOf(
        MenuOption.OptionWidget.Checkbox(
            Localization["game.menu.gameplaySettings.option.showMoveCounter"],
            settings.gameplayShowMoveCounter
        ),
        MenuOption.OptionWidget.Checkbox(
            Localization["game.menu.gameplaySettings.option.showTimer"],
            settings.gameplayShowTimer
        ),
        MenuOption.OptionWidget.Checkbox(
            Localization["game.menu.gameplaySettings.option.useClassicCardSkin"],
            settings.gameplayUseClassicCardSkin
        ),
        MenuOption.Back(),
    )

    init {
        this.menuSizeAdjustment.set(1)
    }
}