package com.swingnosefrog.solitaire.screen.main

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.swingnosefrog.solitaire.game.logic.DeckInitializer
import com.swingnosefrog.solitaire.screen.main.menu.MainGameMenus
import com.swingnosefrog.solitaire.menu.MenuController
import com.swingnosefrog.solitaire.menu.MenuInput
import com.swingnosefrog.solitaire.menu.MenuInputSource
import com.swingnosefrog.solitaire.menu.MenuInputType
import com.swingnosefrog.solitaire.screen.main.menu.GameplaySettingsMenu
import paintbox.Paintbox
import paintbox.binding.BooleanVar
import paintbox.binding.ReadOnlyBooleanVar
import paintbox.input.ToggleableInputProcessor
import paintbox.ui.Pane
import paintbox.ui.SceneRoot
import paintbox.ui.animation.Animation
import paintbox.ui.animation.AnimationHandler
import paintbox.ui.animation.TransitioningFloatVar


class MainGameUi(val mainGameScreen: MainGameScreen) {

    private val uiCamera: OrthographicCamera = OrthographicCamera().apply {
        setToOrtho(false, 1f, 1f) // width and height controlled by uiViewport
    }
    private val uiViewport: Viewport = ExtendViewport(1280f, 720f, 1280f, 800f, uiCamera)
    private val uiSceneRoot: SceneRoot = SceneRoot(uiViewport)
    private val sceneRootInputProcessor: ToggleableInputProcessor = ToggleableInputProcessor(uiSceneRoot.inputSystem)

    private val uiInputHandler: UiInputHandler = this.UiInputHandler()
    val inputProcessor: InputProcessor

    private val _isPauseMenuOpen: BooleanVar = BooleanVar(false)
    val isPauseMenuOpen: ReadOnlyBooleanVar get() = _isPauseMenuOpen

    private val menuController: MenuController = MenuController()
    
    val animationHandler: AnimationHandler = AnimationHandler()

    init {
        inputProcessor = InputMultiplexer()
        inputProcessor.addProcessor(sceneRootInputProcessor)
        inputProcessor.addProcessor(uiInputHandler)
    }

    init {
        initSceneRoot()
    }

    private fun initSceneRoot() {
        animationHandler.cancelAllAnimations()
        
        val parentPane = Pane()
        parentPane += MainGameHudPane(this).apply {
            this.opacity.bind(TransitioningFloatVar(animationHandler, {
                if (isPauseMenuOpen.use()) {
                    if (menuController.currentMenu.use() is GameplaySettingsMenu)
                        1f
                    else 0.25f
                } else 1f
            }, { currentValue, targetValue ->
                createOpacityAnimation(currentValue, targetValue)
            }))
        }
        parentPane += Pane().apply {
            this += MainGameMenuPane(this@MainGameUi, menuController).apply {
                this.opacity.bind(TransitioningFloatVar(animationHandler, {
                    if (isPauseMenuOpen.use()) 1f else 0f
                }, { currentValue, targetValue ->
                    createOpacityAnimation(currentValue, targetValue)
                }))
                this.visible.bind { opacity.use() > 0f }
            }
            this += MainGameGameplayUiPane(this@MainGameUi, uiInputHandler).apply {
                this.opacity.bind(TransitioningFloatVar(animationHandler, {
                    if (isPauseMenuOpen.use()) 0f else 1f
                }, { currentValue, targetValue ->
                    if (targetValue < currentValue) null
                    else createOpacityAnimation(currentValue, targetValue)
                }))
                this.visible.bind { opacity.use() > 0f }
            }
        }
        
        uiSceneRoot += parentPane
    }

    fun render(batch: SpriteBatch, deltaSec: Float) {
        animationHandler.frameUpdate(deltaSec)

        val camera = uiCamera
        batch.projectionMatrix = camera.combined
        batch.begin()

        uiSceneRoot.renderAsRoot(batch)

        batch.end()
        
        mainGameScreen.main.resetViewportToScreen()
    }

    fun resize(width: Int, height: Int) {
        uiViewport.update(width, height, true)
        uiSceneRoot.resize(uiViewport.worldWidth, uiViewport.worldHeight)
    }
    
    fun createOpacityAnimation(currentValue: Float, targetValue: Float, duration: Float = 0.2f): Animation {
        return Animation(Interpolation.exp5, duration, currentValue, targetValue)
    }

    interface IUiInputHandler {

        fun openPauseMenu()

        fun closePauseMenu()
        
        fun startNewGame()
    }

    private inner class UiInputHandler : InputAdapter(), IUiInputHandler {

        override fun openPauseMenu() {
            _isPauseMenuOpen.set(true)
            
            val gameContainer = mainGameScreen.gameContainer.getOrCompute()
            if (!isPauseMenuOpen.get() && gameContainer.gameInput.isDragging()) {
                gameContainer.gameInput.cancelDrag()
            }

            val menus = MainGameMenus(requestCloseMenu = { closePauseMenu() })
            menuController.clearMenuStack()
            val rootMenu = menus.rootMenu
            menuController.setNewMenu(rootMenu, rootMenu.getAutoHighlightedOption(menuController))
        }

        override fun closePauseMenu() {
            _isPauseMenuOpen.set(false)
            menuController.clearMenuStack()
            menuController.setNewMenu(null, null)
        }

        override fun startNewGame() {
            // TODO This currently only exists for new game button
            mainGameScreen.startNewGame(DeckInitializer.RandomSeed())
        }

        private fun debugReinitSceneRoot() {
            uiSceneRoot.removeAllChildren()
            initSceneRoot()
        }
        
        private fun MenuInputType.toKeyboardInput(): MenuInput = MenuInput(this, MenuInputSource.KEYBOARD)

        override fun keyDown(keycode: Int): Boolean {
            // TODO don't hardcode keycodes
            if (isPauseMenuOpen.get()) {
                when (keycode) {
                    Input.Keys.W, Input.Keys.UP -> {
                        menuController.onMenuInput(MenuInputType.UP.toKeyboardInput())
                        return true
                    }
                    Input.Keys.S, Input.Keys.DOWN -> {
                        menuController.onMenuInput(MenuInputType.DOWN.toKeyboardInput())
                        return true
                    }
                    Input.Keys.A, Input.Keys.LEFT -> {
                        menuController.onMenuInput(MenuInputType.LEFT.toKeyboardInput())
                        return true
                    }
                    Input.Keys.D, Input.Keys.RIGHT -> {
                        menuController.onMenuInput(MenuInputType.RIGHT.toKeyboardInput())
                        return true
                    }
                    Input.Keys.Z, Input.Keys.SPACE, Input.Keys.ENTER -> {
                        menuController.onMenuInput(MenuInputType.SELECT.toKeyboardInput())
                        return true
                    }
                    Input.Keys.X, Input.Keys.ESCAPE, Input.Keys.BACKSPACE -> {
                        if (menuController.isAtRootMenu()) {
                            closePauseMenu()
                        } else {
                            menuController.onMenuInput(MenuInputType.BACK.toKeyboardInput())
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