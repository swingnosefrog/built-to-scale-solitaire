package com.swingnosefrog.solitaire.menu

import com.swingnosefrog.solitaire.screen.main.menu.AbstractMenu
import paintbox.binding.ReadOnlyVar
import paintbox.binding.Var
import paintbox.util.MathHelper


open class MenuController {

    private val previousMenuStack: ArrayDeque<MenuHistory> = ArrayDeque()

    private val _currentMenu: Var<AbstractMenu?> = Var(null)
    val currentMenu: ReadOnlyVar<AbstractMenu?> get() = _currentMenu

    private val _currentHighlightedMenuOption: Var<MenuOption?> = Var(null)
    val currentHighlightedMenuOption: ReadOnlyVar<MenuOption?> get() = _currentHighlightedMenuOption

    open fun setNewMenu(menu: AbstractMenu?, menuOption: MenuOption? = null) {
        if (_currentMenu.getOrCompute() != menu) {
            _currentMenu.set(menu)

            val selectedOption: MenuOption? = menuOption ?: if (menu != null) {
                val autoHighlightedIndex = menu.getAutoHighlightedOptionIndex(this)
                menu.options.getOrNull(autoHighlightedIndex)
            } else {
                null
            }

            _currentHighlightedMenuOption.set(selectedOption)
        }
    }

    open fun setHighlightedMenuOption(menuOption: MenuOption?) {
        if (_currentHighlightedMenuOption.getOrCompute() != menuOption) {
            _currentHighlightedMenuOption.set(menuOption)
        }
    }

    open fun goToNextMenu(nextMenu: AbstractMenu) {
        val current = currentMenu.getOrCompute()
        if (current != null) {
            previousMenuStack.addFirst(MenuHistory(current, currentHighlightedMenuOption.getOrCompute()))
        }

        setNewMenu(nextMenu)
    }

    open fun backOutOfMenu(): AbstractMenu? {
        val prevMenu = previousMenuStack.removeFirstOrNull()

        setNewMenu(prevMenu?.menu)
        setHighlightedMenuOption(prevMenu?.selectedOption)

        return prevMenu?.menu
    }

    fun clearMenuStack() {
        previousMenuStack.clear()
    }

    fun isAtRootMenu(): Boolean {
        return previousMenuStack.isEmpty
    }

    fun onMenuInput(menuInput: MenuInput) {
        val current = currentMenu.getOrCompute()
        if (current?.onMenuInput(this, menuInput) == true) {
            return
        }

        val currentOption = currentHighlightedMenuOption.getOrCompute()

        when (menuInput) {
            MenuInput.UP -> navigateMenuOption(-1)
            MenuInput.DOWN -> navigateMenuOption(+1)
            MenuInput.LEFT -> currentOption?.onLeft(this)
            MenuInput.RIGHT -> currentOption?.onRight(this)
            MenuInput.SELECT -> currentOption?.onSelect(this)
            MenuInput.BACK -> currentOption?.onBack(this)
        }
    }

    private fun navigateMenuOption(indexChange: Int) {
        val currentMenu = currentMenu.getOrCompute() ?: return
        val currentOption = currentHighlightedMenuOption.getOrCompute()

        val optionsList = currentMenu.options
        val indexOfCurrentOption = optionsList.indexOf(currentOption)

        val newIndex = if (indexOfCurrentOption == -1) 0 else MathHelper.indexWraparound(
            indexOfCurrentOption,
            indexChange,
            optionsList.size
        )

        setHighlightedMenuOption(optionsList[newIndex])
    }

    private data class MenuHistory(val menu: AbstractMenu, val selectedOption: MenuOption?)

}