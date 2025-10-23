package com.swingnosefrog.solitaire.inputmanager


abstract class ActionSource(protected val actions: List<IInputAction>) {
    
    abstract val sourceName: String
    
    abstract fun getGlyphsForAction(action: IInputAction): List<IActionInputGlyph>

    abstract fun frameUpdate()
    
    abstract fun isDigitalActionPressed(action: IDigitalInputAction): Boolean
    
    abstract fun isAnyInputActive(): Boolean
}