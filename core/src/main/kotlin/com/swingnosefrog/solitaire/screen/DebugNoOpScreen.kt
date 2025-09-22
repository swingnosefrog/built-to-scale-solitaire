package com.swingnosefrog.solitaire.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import paintbox.ui.Pane
import paintbox.ui.SceneRoot
import paintbox.ui.area.Insets
import paintbox.ui.control.TextLabel
import com.swingnosefrog.solitaire.SolitaireGame
import com.swingnosefrog.solitaire.screen.AbstractGameScreen


class DebugNoOpScreen(main: SolitaireGame) : AbstractGameScreen(main) {

    val batch: SpriteBatch = main.batch
    val uiCamera: OrthographicCamera = OrthographicCamera().apply {
        this.setToOrtho(false, 1280f, 720f)
        this.update()
    }
    val uiViewport: Viewport = FitViewport(uiCamera.viewportWidth, uiCamera.viewportHeight, uiCamera)
    val sceneRoot: SceneRoot = SceneRoot(uiViewport)

    val textLabelLoading: TextLabel

    init {
        val labelFont = main.defaultFonts.debugFontBoldItalic
        textLabelLoading = TextLabel("This screen is blank", font = labelFont).apply {
            this.renderAlign.set(Align.center)
            this.textColor.set(Color.WHITE)
        }

        val pane = Pane().apply {
            this.margin.set(Insets(48f))

            this += textLabelLoading
        }
        sceneRoot += pane
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        super.render(delta)

        val camera = uiCamera
        batch.projectionMatrix = camera.combined
        batch.begin()

        sceneRoot.renderAsRoot(batch)

        batch.end()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)

        uiViewport.update(width, height)
    }

    override fun dispose() {
    }
}