package com.swingnosefrog.solitaire.game.input

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import com.swingnosefrog.solitaire.SolitaireGame
import paintbox.binding.ReadOnlyVar
import paintbox.binding.Var

class GameGdxInputProcessor(private val input: GameInput, private val viewport: Viewport) : InputProcessor {

    private val currentMouseModeSetting: ReadOnlyVar<MouseMode> = Var {
        SolitaireGame.instance.settings.gameplayMouseMode.use()
    }

    private fun convertToWorldCoords(screenX: Int, screenY: Int): Vector2 {
        val unprojected = viewport.unproject(Vector2(screenX.toFloat(), screenY.toFloat()))
        unprojected.y = -unprojected.y
        return unprojected
    }

    private fun updateDrag(screenX: Int, screenY: Int) {
        if (input.isDragging()) {
            val worldCoords = convertToWorldCoords(screenX, screenY)
            input.updateDrag(worldCoords.x, worldCoords.y)
        }
    }

    private fun Int.isFirstPointer(): Boolean = this == 0

    private fun areInputsDisabled(): Boolean = input.inputsDisabled.get()

    private fun attemptPickUpCards(screenX: Int, screenY: Int): Boolean {
        val worldCoords = convertToWorldCoords(screenX, screenY)

        val zoneCoords = input.logic.getSelectedZoneCoordinates(worldCoords.x, worldCoords.y)
        if (zoneCoords != null) {
            input.attemptStartDrag(zoneCoords)
            return true
        }

        return false
    }

    private fun attemptPutDownCards(screenX: Int, screenY: Int) {
        val worldCoords = convertToWorldCoords(screenX, screenY)
        input.updateDrag(worldCoords.x, worldCoords.y)

        val nearestZone = input.getNearestOverlappingDraggingZone()
        if (nearestZone == null) {
            input.cancelDrag()
        } else {
            input.endDrag(nearestZone)
        }
    }


    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (areInputsDisabled()) return false
        if (!pointer.isFirstPointer()) return false

        if (button == Input.Buttons.LEFT) {
            val mouseMode = currentMouseModeSetting.getOrCompute()

            when (mouseMode) {
                MouseMode.CLICK_AND_DRAG -> {
                    return attemptPickUpCards(screenX, screenY)
                }

                MouseMode.CLICK_THEN_CLICK -> {
                    if (input.isDragging()) {
                        attemptPutDownCards(screenX, screenY)
                        return true
                    } else {
                        return attemptPickUpCards(screenX, screenY)
                    }
                }
            }
        } else if (button == Input.Buttons.RIGHT) {
            if (input.isDragging()) {
                input.cancelDrag()
                return true
            }
        }

        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (areInputsDisabled()) return false
        if (!pointer.isFirstPointer()) return false

        if (button == Input.Buttons.LEFT) {
            val mouseMode = currentMouseModeSetting.getOrCompute()
            if (mouseMode == MouseMode.CLICK_AND_DRAG) {
                attemptPutDownCards(screenX, screenY)
            }
        }

        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if (areInputsDisabled()) return false
        if (!pointer.isFirstPointer()) return false

        updateDrag(screenX, screenY)

        return false
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (areInputsDisabled()) return false
        if (!pointer.isFirstPointer()) return false

        if (input.isDragging()) {
            input.cancelDrag()
        }

        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        val mouseMode = currentMouseModeSetting.getOrCompute()
        
        if (mouseMode == MouseMode.CLICK_THEN_CLICK) {
            if (input.isDragging()) {
                updateDrag(screenX, screenY)
            }
        }
        
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean = false

    override fun keyDown(keycode: Int): Boolean = false

    override fun keyUp(keycode: Int): Boolean = false

    override fun keyTyped(character: Char): Boolean = false

}