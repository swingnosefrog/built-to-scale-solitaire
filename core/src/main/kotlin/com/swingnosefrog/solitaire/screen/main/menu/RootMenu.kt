package com.swingnosefrog.solitaire.screen.main.menu

import com.swingnosefrog.solitaire.menu.*
import paintbox.binding.ReadOnlyVar


class RootMenu(id: String, heading: ReadOnlyVar<String>, options: List<MenuOption>) : Menu(id, heading, options) {
    
    override fun onMenuInput(
        menuController: MenuController,
        menuInput: MenuInput,
    ): Boolean {
        if (menuInput.type == MenuInputType.BACK) {
            options.first().onSelect(menuController, menuInput)
        }
        
        return super.onMenuInput(menuController, menuInput)
    }
}