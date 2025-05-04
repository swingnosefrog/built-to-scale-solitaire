package com.swingnosefrog.solitaire.screen.main.menu

import com.swingnosefrog.solitaire.menu.MenuController
import com.swingnosefrog.solitaire.menu.MenuInput
import com.swingnosefrog.solitaire.menu.MenuOption
import paintbox.binding.ReadOnlyVar


abstract class AbstractMenu(val id: String) {
    
    abstract val headingText: ReadOnlyVar<String>
    abstract val options: List<MenuOption>

    
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