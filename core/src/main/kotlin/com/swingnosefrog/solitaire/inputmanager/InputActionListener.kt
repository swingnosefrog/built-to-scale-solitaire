package com.swingnosefrog.solitaire.inputmanager


interface InputActionListener {
    
    fun onDigitalActionPressed(actionSource: ActionSource, action: IDigitalInputAction)
    
    fun onDigitalActionReleased(actionSource: ActionSource, action: IDigitalInputAction)
    
    fun onActionSourceChanged(oldSource: ActionSource, newSource: ActionSource)
    
}