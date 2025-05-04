package com.swingnosefrog.solitaire.menu

import com.swingnosefrog.solitaire.screen.main.menu.AbstractMenu
import paintbox.binding.ReadOnlyVar


open class Menu(
    id: String,
    override val headingText: ReadOnlyVar<String>,
    override val options: List<MenuOption>,
) : AbstractMenu(id)
