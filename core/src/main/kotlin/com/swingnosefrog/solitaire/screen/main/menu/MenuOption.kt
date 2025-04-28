package com.swingnosefrog.solitaire.screen.main.menu

import paintbox.binding.ReadOnlyVar
import java.awt.SystemColor.text


sealed class MenuOption(
    val text: ReadOnlyVar<String>,
) {

    class Custom(text: ReadOnlyVar<String>, private val onSelectAction: (MenuController) -> Unit) : MenuOption(text) {

        override fun onSelect(controller: MenuController) {
            onSelectAction(controller)
        }
    }

    class SubMenu(text: ReadOnlyVar<String>, private val nextMenuGetter: () -> Menu) : MenuOption(text) {

        override fun onSelect(controller: MenuController) {
            controller.goToNextMenu(nextMenuGetter())
        }
    }

    sealed class Option(text: ReadOnlyVar<String>) : MenuOption(text) {
        // TODO
    }


    abstract fun onSelect(controller: MenuController)

}