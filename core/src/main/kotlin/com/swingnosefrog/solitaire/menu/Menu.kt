package com.swingnosefrog.solitaire.menu

import paintbox.binding.ReadOnlyVar


open class Menu(
    val id: String,
    val heading: ReadOnlyVar<String>,
    val options: List<MenuOption>,
) {

    /**
     * Returns the [MenuOption] index in [options] to highlight when this menu is entered.
     * A value outside of the valid index range (such as -1) will not select anything.
     * Defaults to first item (index 0).
     */
    open fun getAutoHighlightedOptionIndex(controller: MenuController): Int {
        return 0
    }

    /**
     * Return true to override handling.
     *
     * By default, if the last option is of type [MenuOption.Back], then it triggers that.
     */
    open fun onMenuInput(menuController: MenuController, menuInput: MenuInput): Boolean {
        if (menuInput == MenuInput.BACK) {
            if (defaultBackInputBackOptionHandling(menuController)) return true
        }

        return false
    }

    protected fun defaultBackInputBackOptionHandling(menuController: MenuController): Boolean {
        val lastOption = options.lastOrNull() ?: return false
        if (lastOption is MenuOption.Back) {
            lastOption.onSelect(menuController)
            return true
        }
        
        return false
    }
}