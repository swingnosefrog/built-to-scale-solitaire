package com.swingnosefrog.solitaire.screen.main.menu

import com.badlogic.gdx.Gdx
import com.swingnosefrog.solitaire.Localization


@Suppress("JoinDeclarationAndAssignment")
class MainGameMenus(
    private val requestCloseMenu: () -> Unit,
) {

    val rootMenu: RootMenu
    val quitConfirmationMenu: QuitConfirmationMenu


    init {
        rootMenu = RootMenu(
            "root",
            Localization["game.menu.root.heading"],
            listOf(
                MenuOption.Simple(Localization["game.menu.root.option.resume"]) {
                    requestCloseMenu()
                },
                MenuOption.Simple(Localization["game.menu.root.option.howToPlay"]) {},
                MenuOption.Simple(Localization["game.menu.root.option.settings"]) {},
                MenuOption.SubMenu(Localization["game.menu.root.option.quitGame"]) { quitConfirmationMenu },
            )
        )
        quitConfirmationMenu = QuitConfirmationMenu(
            "quitConfirmation",
            Localization["game.menu.quitConfirmation.heading"],
            listOf(
                MenuOption.Simple(Localization["game.menu.quitConfirmation.option.confirm"]) {
                    Gdx.app.exit()
                },
                MenuOption.Back(Localization["common.cancel"]),
            )
        )
    }

}