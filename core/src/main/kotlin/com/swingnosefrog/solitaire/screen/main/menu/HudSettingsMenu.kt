package com.swingnosefrog.solitaire.screen.main.menu

import com.swingnosefrog.solitaire.Localization
import com.swingnosefrog.solitaire.menu.MenuOption
import paintbox.binding.ReadOnlyVar


class HudSettingsMenu(
    id: String
) : AbstractSettingsMenu(id) {
    
    override val headingText: ReadOnlyVar<String> = Localization["game.menu.hudSettings.heading"]
    
    override val options: List<MenuOption> = listOf(
        MenuOption.OptionWidget.Checkbox(
            Localization["game.menu.hudSettings.option.showMoveCounter"],
            settings.gameplayShowMoveCounter
        ),
        MenuOption.OptionWidget.Checkbox(
            Localization["game.menu.hudSettings.option.showTimer"],
            settings.gameplayShowTimer
        ),
        MenuOption.OptionWidget.Checkbox(
            Localization["game.menu.hudSettings.option.showHowToPlayButton"],
            settings.gameplayShowHowToPlayButton
        ),
        MenuOption.Back(),
    )

    init {
        this.menuSizeAdjustment.set(2)
    }
}