package com.swingnosefrog.solitaire.screen.main.menu

import com.swingnosefrog.solitaire.menu.MenuController
import com.swingnosefrog.solitaire.menu.MenuInput
import com.swingnosefrog.solitaire.menu.MenuInputType
import com.swingnosefrog.solitaire.menu.MenuOption
import paintbox.binding.IntVar
import paintbox.binding.ReadOnlyVar


abstract class AbstractMenu(val id: String) {
    
    abstract val headingText: ReadOnlyVar<String>
    abstract val options: List<MenuOption>

    val menuSizeAdjustment: IntVar = IntVar(0)
    
    /**
     * Returns the [MenuOption] index in [options] to highlight when this menu is entered with the keyboard.
     * A value outside of the valid index range (such as -1) will not select anything.
     * Defaults to first non-disabled item (usually index 0).
     */
    open fun getAutoHighlightedOptionIndex(controller: MenuController): Int {
        return options.indexOfFirst { !it.disabled.get() }
    }
    
    fun getAutoHighlightedOption(controller: MenuController): MenuOption? = options.getOrNull(getAutoHighlightedOptionIndex(controller))

    /**
     * Return true to override handling.
     *
     * By default, if the last option is of type [MenuOption.Back], then it triggers that.
     */
    open fun onMenuInput(menuController: MenuController, menuInput: MenuInput): Boolean {
        if (menuInput.type == MenuInputType.BACK) {
            if (defaultBackInputBackOptionHandling(menuController, menuInput)) return true
        }

        return false
    }

    protected fun defaultBackInputBackOptionHandling(menuController: MenuController, menuInput: MenuInput): Boolean {
        val lastOption = options.lastOrNull() ?: return false
        if (lastOption is MenuOption.Back) {
            lastOption.onSelect(menuController, menuInput)
            return true
        }

        return false
    }
    
    open fun onEnter(menuController: MenuController) {}
    
    open fun onExit(menuController: MenuController) {}
}