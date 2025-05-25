package com.swingnosefrog.solitaire.menu

import com.swingnosefrog.solitaire.Localization
import com.swingnosefrog.solitaire.screen.main.menu.AbstractMenu
import paintbox.binding.ReadOnlyVar


sealed class MenuOption(
    val text: ReadOnlyVar<String>,
) {
    
    companion object {
        
        fun createNoOp(text: ReadOnlyVar<String>): MenuOption {
            return Simple(text) {}
        }
    }

    class Simple(
        text: ReadOnlyVar<String>,
        private val onSelectAction: (MenuController) -> Unit,
    ) : MenuOption(text) {

        override fun onSelect(controller: MenuController, menuInput: MenuInput) {
            onSelectAction(controller)
        }
    }

    class SubMenu(
        text: ReadOnlyVar<String>,
        private val nextMenuGetter: (MenuController) -> AbstractMenu,
    ) : MenuOption(text) {

        override fun onSelect(controller: MenuController, menuInput: MenuInput) {
            val useDefaultNextMenuOption = menuInput.source == MenuInputSource.KEYBOARD
            controller.goToNextMenu(nextMenuGetter(controller), useDefaultNextMenuOption)
        }
    }

    class Back(
        text: ReadOnlyVar<String> = Localization["common.backOutOfMenu"],
        private val callback: (MenuController) -> Unit = {},
    ) : MenuOption(text) {

        override fun onSelect(controller: MenuController, menuInput: MenuInput) {
            controller.backOutOfMenu()
            callback(controller)
        }
    }

    sealed class OptionWidget(text: ReadOnlyVar<String>) : MenuOption(text) {
        // TODO
    }


    abstract fun onSelect(controller: MenuController, menuInput: MenuInput)

    open fun onBack(controller: MenuController, menuInput: MenuInput) {}
    open fun onLeft(controller: MenuController, menuInput: MenuInput) {}
    open fun onRight(controller: MenuController, menuInput: MenuInput) {}

}