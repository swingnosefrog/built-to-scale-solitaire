package com.swingnosefrog.solitaire.screen.main.menu

import com.badlogic.gdx.Gdx
import com.swingnosefrog.solitaire.Localization
import com.swingnosefrog.solitaire.menu.MenuController
import com.swingnosefrog.solitaire.menu.MenuOption
import paintbox.binding.ReadOnlyVar


class QuitConfirmationMenu(
    id: String,
) : AbstractMenu(id) {

    override val headingText: ReadOnlyVar<String> = Localization["game.menu.quitConfirmation.heading"]

    override val options: List<MenuOption> = listOf(
        MenuOption.Simple(Localization["game.menu.quitConfirmation.option.confirm"]) {
            Gdx.app.exit()
        },
        MenuOption.Back(Localization["common.cancel"]),
    )

    override fun getAutoHighlightedOptionIndex(controller: MenuController): Int {
        return options.size - 1
    }
}