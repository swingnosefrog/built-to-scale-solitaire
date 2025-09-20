package com.swingnosefrog.solitaire.screen.main.menu

import com.swingnosefrog.solitaire.Localization
import com.swingnosefrog.solitaire.menu.MenuOption

class MainGameMenus(
    private val requestCloseMenu: () -> Unit,
    private val requestOpenHowToPlayMenu: () -> Unit,
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
                MenuOption.SubMenu(Localization["game.menu.root.option.settings"]) { settingsMenu },
                MenuOption.createNoOp(Localization["game.menu.root.option.credits"]).apply { 
                    this.disabled.set(true)
                },
                MenuOption.SubMenu(Localization["game.menu.root.option.quitGame"]) { quitConfirmationMenu },
            )
        )
        settingsMenu = SettingsRootMenu("settingsRoot")
        quitConfirmationMenu = QuitConfirmationMenu("quitConfirmation")
    }

}