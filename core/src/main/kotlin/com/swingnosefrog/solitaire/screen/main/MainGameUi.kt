package com.swingnosefrog.solitaire.screen.main

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import paintbox.ui.SceneRoot


class MainGameUi(private val mainGameScreen: MainGameScreen) {

    private val uiCamera: OrthographicCamera = OrthographicCamera().apply {
        setToOrtho(false, 1280f, 720f)
    }
    private val uiViewport: Viewport = FitViewport(uiCamera.viewportWidth, uiCamera.viewportHeight, uiCamera)
    private val uiSceneRoot: SceneRoot = SceneRoot(uiViewport)

    private val uiInput: UiInput = this.UiInput()
    val inputProcessor: InputProcessor

    init {
        inputProcessor = InputMultiplexer()
        inputProcessor.addProcessor(uiInput)
        inputProcessor.addProcessor(uiSceneRoot.inputSystem)
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

        override fun keyDown(keycode: Int): Boolean {
            return false
        }
    }
}