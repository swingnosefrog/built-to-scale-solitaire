package com.swingnosefrog.solitaire.game.input

import com.swingnosefrog.solitaire.game.logic.DragInfo
import com.swingnosefrog.solitaire.inputmanager.ActionSource
import com.swingnosefrog.solitaire.inputmanager.IDigitalInputAction
import com.swingnosefrog.solitaire.inputmanager.InputActionListener
import com.swingnosefrog.solitaire.inputmanager.impl.InputActions


class GameInputActionListener(private val input: GameInput) : InputActionListener.Adapter() {

    override fun handleDigitalActionPressed(actionSource: ActionSource, action: IDigitalInputAction): Boolean {
        if (action !is InputActions) return false

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
                when (val dragInfo = input.getCurrentDragInfo()) {
                    is DragInfo.Deciding -> {
                        if (!dragInfo.isHoveringOverSelection) {
                            input.switchToButtonsFocus(true)
                            return true
                        }

                        val mouseOffset = dragInfo.lastKnownMouseOffset
                        val zoneCoords = dragInfo.currentSelection.toZoneCoordinates(mouseOffset.x, mouseOffset.y)
                        return input.attemptStartDrag(zoneCoords, null)
                    }

                    is DragInfo.Dragging -> {
                        if (!dragInfo.isCurrentlyHoveringOverZone) {
                            // Don't actually end drag, steal input focus first
                            input.switchToButtonsFocus(true)
                            return true
                        }

                        return input.endDrag(dragInfo.hoveredZone, true)
                    }
                }
            }

            InputActions.Back -> {
                return input.cancelDrag()
            }

            else -> {}
        }

        return false
    }
}