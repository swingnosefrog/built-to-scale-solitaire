package com.swingnosefrog.solitaire.screen.main.menu

import com.swingnosefrog.solitaire.Localization
import com.swingnosefrog.solitaire.menu.MenuOption
import paintbox.binding.BooleanVar
import paintbox.binding.ReadOnlyVar
import paintbox.binding.Var
import paintbox.binding.toConstVar


class VideoSettingsMenu(
    id: String
) : AbstractMenu(id) {
    
    override val headingText: ReadOnlyVar<String> = Localization["game.menu.videoSettings.heading"]

    override val options: List<MenuOption> = listOf(
        MenuOption.OptionWidget.Cycle<String>(
            Localization["game.menu.videoSettings.option.windowedResolution"],
            listOf("Test 1", "Test 2", "Test 3").toConstVar(),
            Var("Test 1"),
        ),
        MenuOption.OptionWidget.Checkbox(Localization["game.menu.videoSettings.option.fullscreen"], BooleanVar(false)),
        MenuOption.OptionWidget.Checkbox(Localization["game.menu.videoSettings.option.vsync"], BooleanVar(false)),
        MenuOption.createNoOp(Localization["game.menu.videoSettings.option.maxFps"]),
        MenuOption.Back(),
    )
    
    init {
        this.menuSizeAdjustment.set(4)
    }
}