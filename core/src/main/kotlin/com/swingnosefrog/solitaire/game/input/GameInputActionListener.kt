package com.swingnosefrog.solitaire.game.input

import com.swingnosefrog.solitaire.game.logic.DragInfo
import com.swingnosefrog.solitaire.inputmanager.ActionSource
import com.swingnosefrog.solitaire.inputmanager.IDigitalInputAction
import com.swingnosefrog.solitaire.inputmanager.InputActionListener
import com.swingnosefrog.solitaire.inputmanager.impl.InputActions


class GameInputActionListener(private val input: GameInput) : InputActionListener.Adapter() {

    override fun handleDigitalActionPressed(actionSource: ActionSource, action: IDigitalInputAction): Boolean {
        if (action !is InputActions) return false
        if (input.inputsDisabled.get()) return false

        when (action) {
            InputActions.DirectionUp -> {
                return input.updateFromDirectionPress(Direction.UP)
            }

            InputActions.DirectionDown -> {
                return input.updateFromDirectionPress(Direction.DOWN)
            }

            InputActions.DirectionLeft -> {
                return input.updateFromDirectionPress(Direction.LEFT)
            }

            InputActions.DirectionRight -> {
                return input.updateFromDirectionPress(Direction.RIGHT)
            }

            InputActions.Select -> {
                val currentCardCursor = input.getCurrentCardCursor()
                
                // Attempt to "steal input focus" first unless already hovering over card
                if (currentCardCursor.isMouseBased && currentCardCursor.lastMouseZoneCoordinates == null) {
                    input.switchToButtonsFocusAndSnapToNearestZoneIfNotAlready()
                    return true
                }
                
                input.switchToButtonsFocus()
                
                return when (input.getCurrentDragInfo()) {
                    is DragInfo.Deciding -> {
                        input.attemptStartDrag(initialMouseMode = null)
                    }

                    is DragInfo.Dragging -> {
                        input.endDrag(isFromButtonInput = true)
                    }
                }
            }

            InputActions.Back -> {
                input.switchToButtonsFocus()
                return input.cancelDrag()
            }

            else -> {}
        }

        return false
    }
}