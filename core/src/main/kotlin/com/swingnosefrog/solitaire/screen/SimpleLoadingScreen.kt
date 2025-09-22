package com.swingnosefrog.solitaire.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import paintbox.ui.Anchor
import paintbox.ui.Pane
import paintbox.ui.SceneRoot
import paintbox.ui.area.Insets
import paintbox.ui.control.TextLabel
import paintbox.util.gdxutils.grey
import com.swingnosefrog.solitaire.Localization
import com.swingnosefrog.solitaire.SolitaireGame
import com.swingnosefrog.solitaire.screen.AbstractGameScreen


/**
 * A simple black screen with "Loading..." at the bottom right.
 */
open class SimpleLoadingScreen(main: SolitaireGame) : AbstractGameScreen(main) {

    val batch: SpriteBatch = main.batch
    val uiCamera: OrthographicCamera = OrthographicCamera().apply {
        this.setToOrtho(false, 1280f, 720f)
        this.update()
    }
    val uiViewport: Viewport = FitViewport(uiCamera.viewportWidth, uiCamera.viewportHeight, uiCamera)
    val sceneRoot: SceneRoot = SceneRoot(uiViewport)

    val textLabelLoading: TextLabel

    init {
        val labelFont = main.defaultFonts.debugFontBold // FIXME main.fontMainMenuHeading
        textLabelLoading = TextLabel(Localization.getVar("loadingScreen.loading"), font = labelFont).apply {
            Anchor.BottomRight.configure(this)
            this.renderAlign.set(Align.bottomRight)
            this.bounds.height.set(64f)
            this.bounds.width.set(300f)
            this.textColor.set(Color().grey(0.9f))
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