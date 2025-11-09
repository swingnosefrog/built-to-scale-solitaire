package com.swingnosefrog.solitaire.screen.main.menu

import com.swingnosefrog.solitaire.Localization
import com.swingnosefrog.solitaire.inputmanager.InputManager
import com.swingnosefrog.solitaire.menu.MenuOption
import com.swingnosefrog.solitaire.screen.main.MainGameUi

class MainGameMenus(
    private val ui: MainGameUi,
    private val requestCloseMenu: () -> Unit,
    private val requestOpenHowToPlayMenu: () -> Unit,
    private val requestOpenCreditsMenu: () -> Unit,
) {

    val rootMenu: RootMenu
    val settingsMenu: SettingsRootMenu
    val quitConfirmationMenu: QuitConfirmationMenu


    private val inputManager: InputManager get() = ui.mainGameScreen.inputManager
    
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