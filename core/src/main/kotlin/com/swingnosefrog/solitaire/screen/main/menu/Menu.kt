package com.swingnosefrog.solitaire.screen.main.menu

import paintbox.binding.ReadOnlyVar


open class Menu(
    val id: String,
    val heading: ReadOnlyVar<String>,
    val options: List<MenuOption>,
)