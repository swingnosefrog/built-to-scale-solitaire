package com.swingnosefrog.solitaire.screen.main

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import paintbox.Paintbox
import paintbox.binding.BooleanVar
import paintbox.binding.ReadOnlyBooleanVar
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
        uiSceneRoot += MainGameUiPane(this)
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
        
        private fun onEscapePressed(): Boolean {
            val gameContainer = mainGameScreen.gameContainer
            if (!_isPauseMenuOpen.get() && gameContainer.gameInput.isDragging()) {
                gameContainer.gameInput.cancelDrag()
            }

            _isPauseMenuOpen.invert()
            
            return true
        }   
        
        private fun debugReinitSceneRoot() {
            uiSceneRoot.removeAllChildren()
            initSceneRoot()
        }

        override fun keyDown(keycode: Int): Boolean {
            if (keycode == Input.Keys.ESCAPE) {
                if (onEscapePressed()) {
                    return true
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