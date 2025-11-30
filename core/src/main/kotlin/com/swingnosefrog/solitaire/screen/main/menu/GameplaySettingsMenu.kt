package com.swingnosefrog.solitaire.screen.main.menu

import com.swingnosefrog.solitaire.Localization
import com.swingnosefrog.solitaire.SolitaireGame
import com.swingnosefrog.solitaire.game.assets.CardSkin
import com.swingnosefrog.solitaire.game.input.MouseMode
import com.swingnosefrog.solitaire.menu.MenuOption
import paintbox.binding.BooleanVar
import paintbox.binding.ReadOnlyBooleanVar
import paintbox.binding.ReadOnlyVar
import paintbox.binding.Var
import paintbox.ui.StringVarConverter


class GameplaySettingsMenu(
    id: String
) : AbstractSettingsMenu(id) {
    
    override val headingText: ReadOnlyVar<String> = Localization["game.menu.gameplaySettings.heading"]
    
    private val unlockedCardSkinChanging: ReadOnlyBooleanVar = BooleanVar {
        SolitaireGame.instance.progress.unlockedCardSkinChanging.use()
    }
    
    override val options: List<MenuOption> = listOf(
        MenuOption.OptionWidget.Cycle(
            Localization["game.menu.gameplaySettings.option.cardSkin"],
            ReadOnlyVar.const(CardSkin.entries.toList()),
            settings.gameplayCardSkin,
            StringVarConverter { cardSkin: CardSkin ->
                Var<String> {
                    if (!unlockedCardSkinChanging.use()) {
                        Localization["game.menu.optionUnlockedAfterFirstWin"]
                    } else {
                        Localization["game.menu.gameplaySettings.option.cardSkin.${cardSkin.localizationKeySuffix}"]
                    }.use()
                }
            }
        ).apply {
            this.disabled.bind { !unlockedCardSkinChanging.use() }
        },
        MenuOption.OptionWidget.Cycle(
            Localization["game.menu.gameplaySettings.option.mouseMode"],
            ReadOnlyVar.const(MouseMode.entries.toList()),
            settings.gameplayMouseMode,
            StringVarConverter { mouseMode: MouseMode ->
                when (mouseMode) {
                    MouseMode.CLICK_AND_DRAG -> Localization["game.menu.gameplaySettings.option.mouseMode.drag"]
                    MouseMode.CLICK_THEN_CLICK -> Localization["game.menu.gameplaySettings.option.mouseMode.toggle"]
                }
            }
        ),
        MenuOption.OptionWidget.Checkbox(
            Localization["game.menu.gameplaySettings.option.showCardCursorInMouseMode"],
            settings.gameplayShowCardCursorInMouseMode
        ),
        MenuOption.OptionWidget.Checkbox(
            Localization["game.menu.gameplaySettings.option.showMoveCounter"],
            settings.gameplayShowMoveCounter
        ),
        MenuOption.OptionWidget.Checkbox(
            Localization["game.menu.gameplaySettings.option.showTimer"],
            settings.gameplayShowTimer
        ),
        MenuOption.OptionWidget.Checkbox(
            Localization["game.menu.gameplaySettings.option.showHowToPlayButton"],
            settings.gameplayShowHowToPlayButton
        ),
        MenuOption.Back(),
    )

    init {
        this.menuSizeAdjustment.set(2)
    }
}