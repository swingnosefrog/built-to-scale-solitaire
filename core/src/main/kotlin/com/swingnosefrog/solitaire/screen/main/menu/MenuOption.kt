package com.swingnosefrog.solitaire.screen.main.menu

import paintbox.binding.ReadOnlyVar


sealed class MenuOption(
    val text: ReadOnlyVar<String>,
) {

    class Simple(text: ReadOnlyVar<String>, private val onSelectAction: (MenuController) -> Unit) : MenuOption(text) {

        override fun onSelect(controller: MenuController) {
            onSelectAction(controller)
        }
    }

    class SubMenu(text: ReadOnlyVar<String>, private val nextMenuGetter: (MenuController) -> Menu) : MenuOption(text) {

        override fun onSelect(controller: MenuController) {
            controller.goToNextMenu(nextMenuGetter(controller))
        }
    }

    class Back(text: ReadOnlyVar<String>, private val callback: (MenuController) -> Unit = {}) : MenuOption(text) {

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