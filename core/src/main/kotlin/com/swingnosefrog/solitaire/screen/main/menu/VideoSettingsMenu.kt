package com.swingnosefrog.solitaire.screen.main.menu

import com.swingnosefrog.solitaire.Localization
import com.swingnosefrog.solitaire.menu.MenuOption
import paintbox.binding.ReadOnlyVar


class VideoSettingsMenu(
    id: String
) : AbstractMenu(id) {
    
    override val headingText: ReadOnlyVar<String> = Localization["game.menu.videoSettings.heading"]

    override val options: List<MenuOption> = listOf(
        MenuOption.createNoOp(Localization["game.menu.videoSettings.option.windowedResolution"]),
        MenuOption.createNoOp(Localization["game.menu.videoSettings.option.fullscreen"]),
        MenuOption.createNoOp(Localization["game.menu.videoSettings.option.vsync"]),
        MenuOption.createNoOp(Localization["game.menu.videoSettings.option.maxFps"]),
        MenuOption.Back(),
    )
}