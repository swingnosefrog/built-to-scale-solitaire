package com.swingnosefrog.solitaire.screen.main

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.swingnosefrog.solitaire.game.logic.DeckInitializer
import com.swingnosefrog.solitaire.inputmanager.ActionSource
import com.swingnosefrog.solitaire.inputmanager.IDigitalInputAction
import com.swingnosefrog.solitaire.inputmanager.InputActionListener
import com.swingnosefrog.solitaire.inputmanager.impl.InputActions
import com.swingnosefrog.solitaire.menu.MenuController
import com.swingnosefrog.solitaire.menu.MenuInput
import com.swingnosefrog.solitaire.menu.MenuInputSource
import com.swingnosefrog.solitaire.menu.MenuInputType
import com.swingnosefrog.solitaire.screen.main.menu.GameplaySettingsMenu
import com.swingnosefrog.solitaire.screen.main.menu.MainGameMenus
import paintbox.Paintbox
import paintbox.binding.ReadOnlyVar
import paintbox.binding.Var
import paintbox.input.ToggleableInputProcessor
import paintbox.ui.Pane
import paintbox.ui.SceneRoot
import paintbox.ui.UIElement
import paintbox.ui.animation.Animation
import paintbox.ui.animation.AnimationHandler
import paintbox.ui.animation.TransitioningFloatVar


class MainGameUi(val mainGameScreen: MainGameScreen) {

    enum class MenuState {

        NONE,
        PAUSE_MENU,
        HOW_TO_PLAY,
        CREDITS,
    }

    private val uiCamera: OrthographicCamera = OrthographicCamera().apply {
        setToOrtho(false, 1f, 1f) // width and height controlled by uiViewport
    }
    private val uiViewport: Viewport = ExtendViewport(1280f, 720f, 1280f, 800f, uiCamera)
    private val uiSceneRoot: SceneRoot = SceneRoot(uiViewport)
    private val sceneRootInputProcessor: ToggleableInputProcessor = ToggleableInputProcessor(uiSceneRoot.inputSystem)

    val uiInputHandler: IUiInputHandler
    val inputProcessor: InputProcessor
    val inputActionListener: InputActionListener

    private val _currentMenuState: Var<MenuState> = Var(MenuState.NONE)
    val currentMenuState: ReadOnlyVar<MenuState> get() = _currentMenuState.asReadOnlyVar()

    private val menuController: MenuController = MenuController()

    val animationHandler: AnimationHandler = AnimationHandler()

    init {
        uiInputHandler = this.UiInputHandler()

        inputProcessor = InputMultiplexer()
        inputProcessor.addProcessor(sceneRootInputProcessor)

        inputActionListener = uiInputHandler
    }

    init {
        initSceneRoot()
    }

    private fun initSceneRoot() {
        animationHandler.cancelAllAnimations()

        val parentPane = Pane()
        parentPane += MainGameHudPane(this).apply {
            this.opacity.bind(TransitioningFloatVar(animationHandler, {
                if (currentMenuState.use() != MenuState.NONE) {
                    if (menuController.currentMenu.use() is GameplaySettingsMenu)
                        1f
                    else 0.25f
                } else 1f
            }, { currentValue, targetValue ->
                createOpacityAnimation(currentValue, targetValue)
            }))
        }

        // Clickable panes
        parentPane += Pane().apply {
            fun UIElement.bindVisibleIfNotZeroOpacity() {
                this.visible.bind { opacity.use() > 0f }
            }
            this += MainGameMenuPane(this@MainGameUi, menuController).apply {
                this.opacity.bind(TransitioningFloatVar(animationHandler, {
                    if (currentMenuState.use() == MenuState.PAUSE_MENU) 1f else 0f
                }, { currentValue, targetValue ->
                    createOpacityAnimation(currentValue, targetValue)
                }))
                this.bindVisibleIfNotZeroOpacity()
            }
            this += MainGameGameplayUiPane(this@MainGameUi, uiInputHandler).apply {
                this.opacity.bind(TransitioningFloatVar(animationHandler, {
                    if (currentMenuState.use() != MenuState.NONE) 0f else 1f
                }, { currentValue, targetValue ->
                    if (targetValue < currentValue) null
                    else createOpacityAnimation(currentValue, targetValue)
                }))
                this.bindVisibleIfNotZeroOpacity()
            }
            this += MainGameHowToPlayPane(this@MainGameUi, uiInputHandler).apply {
                this.opacity.bind(TransitioningFloatVar(animationHandler, {
                    if (currentMenuState.use() != MenuState.HOW_TO_PLAY) 0f else 1f
                }, { currentValue, targetValue ->
                    createOpacityAnimation(currentValue, targetValue)
                }))
                this.bindVisibleIfNotZeroOpacity()
            }
            this += MainGameCreditsPane(this@MainGameUi, uiInputHandler).apply {
                this.opacity.bind(TransitioningFloatVar(animationHandler, {
                    if (currentMenuState.use() != MenuState.CREDITS) 0f else 1f
                }, { currentValue, targetValue ->
                    createOpacityAnimation(currentValue, targetValue)
                }))
                this.bindVisibleIfNotZeroOpacity()
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

    fun getDebugString(): String {
        return """MenuState: ${currentMenuState.getOrCompute()}
"""
    }

    interface IUiInputHandler {

        fun openPauseMenu()

        fun closePauseMenu()

        fun openHowToPlayMenu()

        fun closeHowToPlayMenu()

        fun openCreditsMenu()

        fun closeCreditsMenu()

        fun startNewGame()

        fun skipDealingAnimation(): Boolean

        fun debugReinitSceneRoot()
    }

    private inner class UiInputHandler : InputActionListener, IUiInputHandler {

        private fun cancelDragOnMenuOpen() {
            val gameContainer = mainGameScreen.gameContainer.getOrCompute()
            if (gameContainer.gameInput.isDragging()) {
                gameContainer.gameInput.cancelDrag()
            }
        }

        private fun MenuInputType.toKeyOrButtonInput(): MenuInput = MenuInput(this, MenuInputSource.KEYBOARD_OR_BUTTON)

        //region IUiInputHandler

        override fun openPauseMenu() {
            _currentMenuState.set(MenuState.PAUSE_MENU)

            cancelDragOnMenuOpen()

            val menus = MainGameMenus(
                this@MainGameUi,
                requestCloseMenu = { closePauseMenu() },
                requestOpenHowToPlayMenu = { openHowToPlayMenu() },
                requestOpenCreditsMenu = { openCreditsMenu() },
            )
            menuController.clearMenuStack()

            val rootMenu = menus.rootMenu
            menuController.setNewMenu(rootMenu, rootMenu.getAutoHighlightedOption(menuController))
        }

        override fun closePauseMenu() {
            _currentMenuState.set(MenuState.NONE)
            menuController.clearMenuStack()
            menuController.setNewMenu(null, null)
        }

        override fun openHowToPlayMenu() {
            closePauseMenu()
            _currentMenuState.set(MenuState.HOW_TO_PLAY)
            cancelDragOnMenuOpen()
        }

        override fun closeHowToPlayMenu() {
            _currentMenuState.set(MenuState.NONE)
        }

        override fun openCreditsMenu() {
            closePauseMenu()
            _currentMenuState.set(MenuState.CREDITS)
            cancelDragOnMenuOpen()
        }

        override fun closeCreditsMenu() {
            _currentMenuState.set(MenuState.NONE)
        }

        override fun startNewGame() {
            mainGameScreen.startNewGame(DeckInitializer.RandomSeed())
        }

        override fun skipDealingAnimation(): Boolean {
            val gameLogic = mainGameScreen.gameContainer.getOrCompute().gameLogic
            if (gameLogic.isStillDealing.get()) {
                val secondsToAdvance = 10f
                gameLogic.animationContainer.renderUpdate(secondsToAdvance)
                gameLogic.checkTableauAfterActivity()
                return true
            }

            return false
        }

        override fun debugReinitSceneRoot() {
            uiSceneRoot.removeAllChildren()
            initSceneRoot()
            Paintbox.LOGGER.debug("Reinitialized UI scene root")
        }

        //endregion

        //region InputActionListener

        override fun handleDigitalActionPressed(actionSource: ActionSource, action: IDigitalInputAction): Boolean {
            if (action !is InputActions) return false

            when (currentMenuState.getOrCompute()) {
                MenuState.PAUSE_MENU -> {
                    when (action) {
                        InputActions.DirectionUp -> {
                            menuController.onMenuInput(MenuInputType.UP.toKeyOrButtonInput())
                            return true
                        }

                        InputActions.DirectionDown -> {
                            menuController.onMenuInput(MenuInputType.DOWN.toKeyOrButtonInput())
                            return true
                        }

                        InputActions.DirectionLeft -> {
                            menuController.onMenuInput(MenuInputType.LEFT.toKeyOrButtonInput())
                            return true
                        }

                        InputActions.DirectionRight -> {
                            menuController.onMenuInput(MenuInputType.RIGHT.toKeyOrButtonInput())
                            return true
                        }

                        InputActions.Select -> {
                            menuController.onMenuInput(MenuInputType.SELECT.toKeyOrButtonInput())
                            return true
                        }

                        InputActions.Back, InputActions.Menu -> {
                            if (menuController.isAtRootMenu()) {
                                closePauseMenu()
                            } else {
                                menuController.onMenuInput(MenuInputType.BACK.toKeyOrButtonInput())
                            }

                            return true
                        }

                        else -> {}
                    }
                }

                MenuState.HOW_TO_PLAY -> {
                    when (action) {
                        InputActions.HowToPlay, InputActions.Back, InputActions.Menu -> {
                            closeHowToPlayMenu()
                            return true
                        }

                        else -> {}
                    }
                }

                MenuState.CREDITS -> {
                    when (action) {
                        InputActions.Back, InputActions.Menu -> {
                            closeCreditsMenu()
                            return true
                        }

                        else -> {}
                    }
                }

                MenuState.NONE -> {
                    when (action) {
                        InputActions.HowToPlay -> {
                            openHowToPlayMenu()
                            return true
                        }

                        InputActions.Back, InputActions.Menu -> {
                            openPauseMenu()
                            return true
                        }

                        InputActions.NewGame -> {
                            startNewGame()
                            return true
                        }

                        InputActions.Select -> {
                            return skipDealingAnimation()
                        }

                        else -> {}
                    }
                }
            }

            return false
        }

        override fun handleDigitalActionReleased(actionSource: ActionSource, action: IDigitalInputAction): Boolean {
            return false
        }

        override fun handleActionSourceChanged(oldSource: ActionSource, newSource: ActionSource): Boolean {
            return false
        }

        //endregion
    }
}