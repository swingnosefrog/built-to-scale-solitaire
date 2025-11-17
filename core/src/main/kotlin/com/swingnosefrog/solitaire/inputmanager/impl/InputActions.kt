package com.swingnosefrog.solitaire.inputmanager.impl

import com.swingnosefrog.solitaire.inputmanager.IDigitalInputAction


enum class InputActions(override val actionId: String) : IDigitalInputAction {

    DirectionUp("button_General_DirectionUp"),
    DirectionDown("button_General_DirectionDown"),
    DirectionLeft("button_General_DirectionLeft"),
    DirectionRight("button_General_DirectionRight"),

    Select("button_General_Select"),
    Back("button_General_Back"),

    Menu("button_General_Menu"),

    NewGame("button_General_NewGame"),
    HowToPlay("button_General_HowToPlay"),
    
    JumpToTopOfStack("button_General_JumpToTopOfStack"),
    JumpToBottomOfStack("button_General_JumpToBottomOfStack"),
    ;

    override val actionName: String = this.name
}