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

    open fun setNewMenu(menu: AbstractMenu?, selectedMenuOption: MenuOption?) {
        if (_currentMenu.getOrCompute() != menu) {
            _currentMenu.set(menu)
            _currentHighlightedMenuOption.set(selectedMenuOption)
        }
    }

    open fun setHighlightedMenuOption(menuOption: MenuOption?) {
        if (_currentHighlightedMenuOption.getOrCompute() != menuOption) {
            _currentHighlightedMenuOption.set(menuOption)
        }
    }

    open fun goToNextMenu(nextMenu: AbstractMenu, useDefaultNextMenuOption: Boolean) {
        val current = currentMenu.getOrCompute()
        if (current != null) {
            previousMenuStack.addFirst(MenuHistory(current, currentHighlightedMenuOption.getOrCompute()))
        }

        setNewMenu(nextMenu, if (useDefaultNextMenuOption) nextMenu.getAutoHighlightedOption(this) else null)
    }

    open fun backOutOfMenu(): AbstractMenu? {
        val prevMenu = previousMenuStack.removeFirstOrNull()

        setNewMenu(prevMenu?.menu, prevMenu?.selectedOption)

        return prevMenu?.menu
    }

    fun clearMenuStack() {
        previousMenuStack.clear()
    }

    fun isAtRootMenu(): Boolean {
        return previousMenuStack.isEmpty()
    }

    fun onMenuInput(menuInput: MenuInputType) {
        val current = currentMenu.getOrCompute()
        if (current?.onMenuInput(this, menuInput) == true) {
            return
        }

        val currentOption = currentHighlightedMenuOption.getOrCompute()

        when (menuInput) {
            MenuInputType.UP -> navigateMenuOption(-1)
            MenuInputType.DOWN -> navigateMenuOption(+1)
            MenuInputType.LEFT -> currentOption?.onLeft(this)
            MenuInputType.RIGHT -> currentOption?.onRight(this)
            MenuInputType.SELECT -> currentOption?.onSelect(this)
            MenuInputType.BACK -> currentOption?.onBack(this)
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