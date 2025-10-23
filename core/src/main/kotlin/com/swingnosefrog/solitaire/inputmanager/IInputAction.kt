package com.swingnosefrog.solitaire.inputmanager


sealed interface IInputAction {

    val actionName: String
    val actionId: String

}

interface IDigitalInputAction : IInputAction

/**
 * NB: Not implemented, but intentionally in the sealed interface hierarchy.
 */
@Suppress("unused")
interface IAnalogInputAction : IInputAction
