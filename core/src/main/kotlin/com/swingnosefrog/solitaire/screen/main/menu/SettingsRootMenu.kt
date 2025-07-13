package com.swingnosefrog.solitaire.screen.main.menu

import com.swingnosefrog.solitaire.Localization
import com.swingnosefrog.solitaire.SolitaireGame
import com.swingnosefrog.solitaire.menu.MenuOption
import paintbox.binding.ReadOnlyVar


class SettingsRootMenu(
    id: String,
) : AbstractMenu(id) {

    private val gameplaySettingsMenu: GameplaySettingsMenu = GameplaySettingsMenu("gameplaySettings")
    private val audioSettingsMenu: AudioSettingsMenu = AudioSettingsMenu("audioSettings")
    private val videoSettingsMenu: VideoSettingsMenu = VideoSettingsMenu("videoSettings")

    override val headingText: ReadOnlyVar<String> = Localization["game.menu.settings.heading"]

    override val options: List<MenuOption> = listOf(
        MenuOption.SubMenu(Localization["game.menu.settings.option.gameplay"]) { gameplaySettingsMenu },
        MenuOption.SubMenu(Localization["game.menu.settings.option.audio"]) { audioSettingsMenu },
        MenuOption.SubMenu(Localization["game.menu.settings.option.video"]) { videoSettingsMenu },
        MenuOption.Back(callback = { menuController -> 
            SolitaireGame.instance.settings.persist()
        }),
    )
}