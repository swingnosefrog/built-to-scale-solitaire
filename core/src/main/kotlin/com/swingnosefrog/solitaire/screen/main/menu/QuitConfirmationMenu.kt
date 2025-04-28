package com.swingnosefrog.solitaire.menu

import paintbox.binding.ReadOnlyVar


class QuitConfirmationMenu(
    id: String, heading: ReadOnlyVar<String>, options: List<MenuOption>,
) : Menu(id, heading, options) {

    override fun getAutoHighlightedOptionIndex(controller: MenuController): Int {
        return options.size - 1
    }
}