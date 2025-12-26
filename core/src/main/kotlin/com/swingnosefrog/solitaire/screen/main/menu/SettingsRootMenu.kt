package com.swingnosefrog.solitaire.screen.main.menu

import com.swingnosefrog.solitaire.Localization
import com.swingnosefrog.solitaire.SolitaireGame
import com.swingnosefrog.solitaire.SolitaireLocalePicker
import com.swingnosefrog.solitaire.menu.MenuOption
import paintbox.binding.ReadOnlyVar
import paintbox.binding.Var
import paintbox.i18n.NamedLocale
import paintbox.ui.StringVarConverter


class SettingsRootMenu(
    id: String,
) : AbstractMenu(id) {

    private val gameplaySettingsMenu: GameplaySettingsMenu = GameplaySettingsMenu("gameplaySettings")
    private val audioSettingsMenu: AudioSettingsMenu = AudioSettingsMenu("audioSettings")
    private val videoSettingsMenu: VideoSettingsMenu = VideoSettingsMenu("videoSettings")

    override val headingText: ReadOnlyVar<String> = Localization["game.menu.settings.heading"]

    override val options: List<MenuOption> = listOfNotNull(
        MenuOption.SubMenu(Localization["game.menu.settings.option.gameplay"]) { gameplaySettingsMenu },
        MenuOption.SubMenu(Localization["game.menu.settings.option.audio"]) { audioSettingsMenu },
        MenuOption.SubMenu(Localization["game.menu.settings.option.video"]) { videoSettingsMenu },
        if (SolitaireLocalePicker.namedLocales.size > 1)
            MenuOption.OptionWidget.Cycle<NamedLocale>(
                Localization["game.menu.settings.option.language"],
                Var(SolitaireLocalePicker.namedLocales),
                SolitaireLocalePicker.currentLocale,
                StringVarConverter { ReadOnlyVar.const(it.name) }
            )
        else null,
        MenuOption.Back(callback = { menuController ->
            SolitaireGame.instance.settings.persist()
        }),
    )

    init {
        this.menuSizeAdjustment.set(1)
    }
}