package com.swingnosefrog.solitaire.screen.main

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.swingnosefrog.solitaire.screen.main.menu.Menu
import com.swingnosefrog.solitaire.screen.main.menu.MenuController
import com.swingnosefrog.solitaire.screen.main.menu.MenuInput
import com.swingnosefrog.solitaire.screen.main.menu.MenuOption
import paintbox.Paintbox
import paintbox.binding.BooleanVar
import paintbox.binding.ReadOnlyBooleanVar
import paintbox.binding.toConstVar
import paintbox.input.ToggleableInputProcessor
import paintbox.ui.SceneRoot


class MainGameUi(private val mainGameScreen: MainGameScreen) {

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

        sceneRootInputProcessor.enabled.bind { _isPauseMenuOpen.use() }
        uiSceneRoot.visible.bind { _isPauseMenuOpen.use() }
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
            if (!_isPauseMenuOpen.get() && gameContainer.gameInput.isDragging()) {
                gameContainer.gameInput.cancelDrag()
            }
            
            menuController.clearMenuStack()
            menuController.setNewMenu(
                Menu(
                    "testRoot", "Menu".toConstVar(), listOf(
                        MenuOption.Custom("Resume".toConstVar()) {},
                        MenuOption.Custom("How to Play".toConstVar()) {},
                        MenuOption.Custom("Settings".toConstVar()) {},
                        MenuOption.Custom("Quit Game".toConstVar()) {},
                    )
                )
            )
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

            if (_isPauseMenuOpen.get() && Paintbox.debugMode.get() && keycode == Input.Keys.R) {
                debugReinitSceneRoot()
                return true
            }

            return false
        }
    }
}