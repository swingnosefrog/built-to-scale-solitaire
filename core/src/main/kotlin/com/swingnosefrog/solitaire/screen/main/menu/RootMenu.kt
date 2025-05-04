package com.swingnosefrog.solitaire.screen.main.menu

import com.swingnosefrog.solitaire.menu.Menu
import com.swingnosefrog.solitaire.menu.MenuController
import com.swingnosefrog.solitaire.menu.MenuInputType
import com.swingnosefrog.solitaire.menu.MenuOption
import paintbox.binding.ReadOnlyVar


class RootMenu(id: String, heading: ReadOnlyVar<String>, options: List<MenuOption>) : Menu(id, heading, options) {
    
    override fun onMenuInput(
        menuController: MenuController,
        menuInputType: MenuInputType,
    ): Boolean {
        if (menuInputType == MenuInputType.BACK) {
            options.first().onSelect(menuController)
        }
        
        return super.onMenuInput(menuController, menuInputType)
    }
}