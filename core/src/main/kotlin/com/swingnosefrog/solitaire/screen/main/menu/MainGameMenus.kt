package com.swingnosefrog.solitaire.screen.main.menu

import com.swingnosefrog.solitaire.Localization
import com.swingnosefrog.solitaire.menu.MenuOption
import com.swingnosefrog.solitaire.screen.main.MainGameUi

class MainGameMenus(
    private val ui: MainGameUi,
    private val requestCloseMenu: () -> Unit,
    private val requestOpenHowToPlayMenu: () -> Unit,
    private val requestOpenCreditsMenu: () -> Unit,
    private val requestOpenStatsMenu: () -> Unit,
) {

    val rootMenu: RootMenu
    val settingsMenu: SettingsRootMenu
    val quitConfirmationMenu: QuitConfirmationMenu


    init {
        rootMenu = RootMenu(
            "root",
            Localization["game.menu.root.heading"],
            listOf(
                MenuOption.Simple(Localization["game.menu.root.option.resume"]) {
                    requestCloseMenu()
                },
                MenuOption.Simple(Localization["game.menu.root.option.howToPlay"]) {
                    requestOpenHowToPlayMenu()
                },
                MenuOption.Simple(Localization["game.menu.root.option.stats"]) {
                    requestOpenStatsMenu()
                },
                MenuOption.SubMenu(Localization["game.menu.root.option.settings"]) { settingsMenu },
                MenuOption.Simple(Localization["game.menu.root.option.credits"]) {
                    requestOpenCreditsMenu()
                },
                MenuOption.SubMenu(Localization["game.menu.root.option.quitGame"]) { quitConfirmationMenu },
            )
        )
        settingsMenu = SettingsRootMenu("settingsRoot")
        quitConfirmationMenu = QuitConfirmationMenu("quitConfirmation")
    }
}