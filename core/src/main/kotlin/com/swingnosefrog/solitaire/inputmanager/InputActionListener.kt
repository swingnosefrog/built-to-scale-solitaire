package com.swingnosefrog.solitaire.inputmanager


interface InputActionListener {
    
    fun handleDigitalActionPressed(actionSource: ActionSource, action: IDigitalInputAction): Boolean
    
    fun handleDigitalActionReleased(actionSource: ActionSource, action: IDigitalInputAction): Boolean
    
    fun handleActionSourceChanged(oldSource: ActionSource, newSource: ActionSource): Boolean
    
    open class Adapter : InputActionListener {

        override fun handleDigitalActionPressed(actionSource: ActionSource, action: IDigitalInputAction): Boolean {
            return false
        }

        override fun handleDigitalActionReleased(actionSource: ActionSource, action: IDigitalInputAction): Boolean {
            return false
        }

        override fun handleActionSourceChanged(oldSource: ActionSource, newSource: ActionSource): Boolean {
            return false
        }
    }
}