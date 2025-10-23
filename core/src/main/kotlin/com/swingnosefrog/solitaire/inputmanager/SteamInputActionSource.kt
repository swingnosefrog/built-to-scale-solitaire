package com.swingnosefrog.solitaire.inputmanager

class SteamInputActionSource(actions: List<IInputAction>) : ActionSource(actions) {

    override val sourceName: String = "SteamInput"

    override fun getGlyphsForAction(action: IInputAction): List<IActionInputGlyph> {
        // TODO
        return emptyList()
    }

    override fun frameUpdate() {
    }

    override fun isDigitalActionPressed(action: IDigitalInputAction): Boolean {
        // TODO
        return false
    }

    override fun isAnyInputActive(): Boolean {
        // TODO
        return false
    }
}