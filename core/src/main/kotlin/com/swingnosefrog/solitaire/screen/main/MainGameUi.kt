package com.swingnosefrog.solitaire.screen.main

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import paintbox.Paintbox
import paintbox.binding.BooleanVar
import paintbox.font.TextAlign
import paintbox.input.ToggleableInputProcessor
import paintbox.ui.Pane
import paintbox.ui.SceneRoot
import paintbox.ui.area.Insets
import paintbox.ui.control.TextLabel
import paintbox.ui.element.QuadElement


class MainGameUi(private val mainGameScreen: MainGameScreen) {

    private val uiCamera: OrthographicCamera = OrthographicCamera().apply {
        setToOrtho(false, 1280f, 720f)
    }
    private val uiViewport: Viewport = FitViewport(uiCamera.viewportWidth, uiCamera.viewportHeight, uiCamera)
    private val uiSceneRoot: SceneRoot = SceneRoot(uiViewport)
    private val sceneRootInputProcessor: ToggleableInputProcessor = ToggleableInputProcessor(uiSceneRoot.inputSystem)

    private val uiInput: UiInput = this.UiInput()
    val inputProcessor: InputProcessor
    
    private val isPauseMenuOpen: BooleanVar = BooleanVar(false)

    init {
        inputProcessor = InputMultiplexer()
        inputProcessor.addProcessor(sceneRootInputProcessor)
        inputProcessor.addProcessor(uiInput)
        
        sceneRootInputProcessor.enabled.bind { isPauseMenuOpen.use() }
        uiSceneRoot.visible.bind { isPauseMenuOpen.use() }
    }
    
    init {
        initSceneRoot()
    }
    
    private fun initSceneRoot() {
        uiSceneRoot += Pane().apply {
            val dark = Color(0f, 0f, 0f, 0.75f)
            this += QuadElement(dark, Color.CLEAR, dark, Color.CLEAR).apply {
                this.bindWidthToParent(multiplier = 0.5f)
                this += Pane().apply {
                    this.margin.set(Insets(48f))
                    this += TextLabel("PAUSE").apply {
                        this.textColor.set(Color.WHITE)
                        this.bindYToParentHeight(multiplier = 0.25f)
                        this.bounds.height.set(100f)
                        this.textAlign.set(TextAlign.LEFT)
                    }
                }
            }
        }
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

    private inner class UiInput : InputAdapter() {
        
        private fun onEscapePressed(): Boolean {
            val gameContainer = mainGameScreen.gameContainer
            if (!isPauseMenuOpen.get() && gameContainer.gameInput.isDragging()) {
                gameContainer.gameInput.cancelDrag()
            }

            isPauseMenuOpen.invert()
            
            return true
        }   

        override fun keyDown(keycode: Int): Boolean {
            if (keycode == Input.Keys.ESCAPE) {
                if (onEscapePressed()) {
                    return true
                }
            }
            
            if (isPauseMenuOpen.get() && Paintbox.debugMode.get() && keycode == Input.Keys.R) {
                uiSceneRoot.removeChild(0)
                initSceneRoot()
                return true
            }
            
            return false
        }
    }
}