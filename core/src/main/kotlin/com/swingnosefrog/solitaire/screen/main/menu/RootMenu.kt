package com.swingnosefrog.solitaire.screen.main.menu

import paintbox.binding.ReadOnlyVar


class RootMenu(id: String, heading: ReadOnlyVar<String>, options: List<MenuOption>) : Menu(id, heading, options) {
    
    override fun onMenuInput(
        menuController: MenuController,
        menuInput: MenuInput,
    ): Boolean {
        if (menuInput == MenuInput.BACK) {
            options.first().onSelect(menuController)
        }
        
        return super.onMenuInput(menuController, menuInput)
    }
}