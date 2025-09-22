package com.swingnosefrog.solitaire.screen.main.menu

import com.swingnosefrog.solitaire.SolitaireGame
import com.swingnosefrog.solitaire.settings.SolitaireSettings


abstract class AbstractSettingsMenu(id: String) : AbstractMenu(id) {

    protected val settings: SolitaireSettings = SolitaireGame.instance.settings

}