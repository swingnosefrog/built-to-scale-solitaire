package com.swingnosefrog.solitaire.menu

import com.swingnosefrog.solitaire.Localization
import com.swingnosefrog.solitaire.screen.main.menu.AbstractMenu
import paintbox.binding.ReadOnlyVar


sealed class MenuOption(
    val text: ReadOnlyVar<String>,
) {

    class Simple(
        text: ReadOnlyVar<String>,
        private val onSelectAction: (MenuController) -> Unit,
    ) : MenuOption(text) {

        override fun onSelect(controller: MenuController) {
            onSelectAction(controller)
        }
    }

    class SubMenu(
        text: ReadOnlyVar<String>,
        private val nextMenuGetter: (MenuController) -> AbstractMenu,
    ) : MenuOption(text) {

        override fun onSelect(controller: MenuController) {
            controller.goToNextMenu(nextMenuGetter(controller), true /* FIXME */)
        }
    }

    class Back(
        text: ReadOnlyVar<String> = Localization["common.backOutOfMenu"],
        private val callback: (MenuController) -> Unit = {},
    ) : MenuOption(text) {

        override fun onSelect(controller: MenuController) {
            controller.backOutOfMenu()
            callback(controller)
        }
    }

    sealed class OptionWidget(text: ReadOnlyVar<String>) : MenuOption(text) {
        // TODO
    }


    abstract fun onSelect(controller: MenuController)

    open fun onBack(controller: MenuController) {}
    open fun onLeft(controller: MenuController) {}
    open fun onRight(controller: MenuController) {}

}