package com.swingnosefrog.solitaire.inputmanager


interface InputActionListener {
    
    fun onDigitalActionPressed(actionSource: ActionSource, action: IDigitalInputAction)
    
    fun onDigitalActionReleased(actionSource: ActionSource, action: IDigitalInputAction)
    
    fun onActionSourceChanged(oldSource: ActionSource, newSource: ActionSource)
    
    open class Adapter : InputActionListener {

        override fun onDigitalActionPressed(actionSource: ActionSource, action: IDigitalInputAction) {
        }

        override fun onDigitalActionReleased(actionSource: ActionSource, action: IDigitalInputAction) {
        }

        override fun onActionSourceChanged(oldSource: ActionSource, newSource: ActionSource) {
        }
    }
}