package com.swingnosefrog.solitaire.screen.main

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.swingnosefrog.solitaire.screen.main.menu.MainGameMenus
import com.swingnosefrog.solitaire.menu.MenuController
import com.swingnosefrog.solitaire.menu.MenuInput
import paintbox.Paintbox
import paintbox.binding.BooleanVar
import paintbox.binding.ReadOnlyBooleanVar
import paintbox.input.ToggleableInputProcessor
import paintbox.ui.SceneRoot


class MainGameUi(val mainGameScreen: MainGameScreen) {

    private val uiCamera: OrthographicCamera = OrthographicCamera().apply {
        setToOrtho(false, 1280f, 720f)
    }
    private val uiViewport: Viewport = FitViewport(uiCamera.viewportWidth, uiCamera.viewportHeight, uiCamera)
    private val uiSceneRoot: SceneRoot = SceneRoot(uiViewport)
    private val sceneRootInputProcessor: ToggleableInputProcessor = ToggleableInputProcessor(uiSceneRoot.inputSystem)

    private val uiInputHandler: UiInputHandler = this.UiInputHandler()
    val inputProcessor: InputProcessor

    private val _isPauseMenuOpen: BooleanVar = BooleanVar(false)
    val isPauseMenuOpen: ReadOnlyBooleanVar get() = _isPauseMenuOpen

    private val menuController: MenuController = MenuController()

    init {
        inputProcessor = InputMultiplexer()
        inputProcessor.addProcessor(sceneRootInputProcessor)
        inputProcessor.addProcessor(uiInputHandler)

        sceneRootInputProcessor.enabled.bind { isPauseMenuOpen.use() }
        uiSceneRoot.visible.bind { isPauseMenuOpen.use() }
    }

    init {
        initSceneRoot()
    }

    private fun initSceneRoot() {
        uiSceneRoot += MainGameUiPane(this, menuController)
    }

    fun render(batch: SpriteBatch) {
        val camera = uiCamera
        batch.projectionMatrix = camera.combined
        batch.begin()

        uiSceneRoot.renderAsRoot(batch)

        batch.end()
    }

    fun resize(width: Int, height: Int) {
        uiViewport.update(width, height)
    }

    private inner class UiInputHandler : InputAdapter() {

        private fun openPauseMenu() {
            _isPauseMenuOpen.set(true)
            
            val gameContainer = mainGameScreen.gameContainer
            if (!isPauseMenuOpen.get() && gameContainer.gameInput.isDragging()) {
                gameContainer.gameInput.cancelDrag()
            }

            val menus = MainGameMenus(requestCloseMenu = { closePauseMenu() })
            menuController.clearMenuStack()
            menuController.setNewMenu(menus.rootMenu)
        }

        private fun closePauseMenu() {
            _isPauseMenuOpen.set(false)
            menuController.clearMenuStack()
            menuController.setNewMenu(null)
        }

        private fun debugReinitSceneRoot() {
            uiSceneRoot.removeAllChildren()
            initSceneRoot()
        }

        override fun keyDown(keycode: Int): Boolean {
            // TODO don't hardcode keycodes
            if (isPauseMenuOpen.get()) {
                when (keycode) {
                    Input.Keys.W, Input.Keys.UP -> {
                        menuController.onMenuInput(MenuInput.UP)
                        return true
                    }
                    Input.Keys.S, Input.Keys.DOWN -> {
                        menuController.onMenuInput(MenuInput.DOWN)
                        return true
                    }
                    Input.Keys.A, Input.Keys.LEFT -> {
                        menuController.onMenuInput(MenuInput.LEFT)
                        return true
                    }
                    Input.Keys.D, Input.Keys.RIGHT -> {
                        menuController.onMenuInput(MenuInput.RIGHT)
                        return true
                    }
                    Input.Keys.Z, Input.Keys.SPACE, Input.Keys.ENTER -> {
                        menuController.onMenuInput(MenuInput.SELECT)
                        return true
                    }
                    Input.Keys.X, Input.Keys.ESCAPE, Input.Keys.BACKSPACE -> {
                        if (menuController.isAtRootMenu()) {
                            closePauseMenu()
                        } else {
                            menuController.onMenuInput(MenuInput.BACK)
                        }

                        return true
                    }
                }
            } else {
                when (keycode) {
                    Input.Keys.X, Input.Keys.ESCAPE, Input.Keys.BACKSPACE -> {
                        openPauseMenu()
                        return true
                    }
                }
            }

            if (isPauseMenuOpen.get() && Paintbox.debugMode.get() && keycode == Input.Keys.R) {
                debugReinitSceneRoot()
                return true
            }

            return false
        }
    }
}