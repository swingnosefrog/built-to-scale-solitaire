package com.swingnosefrog.solitaire.screen.main

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.ScalingViewport
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.swingnosefrog.solitaire.screen.AbstractGameScreen
import com.swingnosefrog.solitaire.SolitaireGame
import com.swingnosefrog.solitaire.game.GameContainer
import com.swingnosefrog.solitaire.game.audio.GameMusic
import com.swingnosefrog.solitaire.game.logic.DeckInitializer
import com.swingnosefrog.solitaire.game.logic.GameLogic
import com.swingnosefrog.solitaire.soundsystem.SoundSystem
import paintbox.binding.ReadOnlyVar
import paintbox.binding.Var
import paintbox.framebuffer.FrameBufferManager
import paintbox.input.ToggleableInputProcessor

class MainGameScreen(
    main: SolitaireGame,
) : AbstractGameScreen(main) {
    
    companion object {
        
        private const val GAME_BUFFER_INDEX: Int = 0
    }

    private val batch: SpriteBatch = main.batch
    private val screenInputMultiplexer: InputMultiplexer = InputMultiplexer()

    private val soundSystem: SoundSystem = SoundSystem.createDefaultSoundSystem()
    private val gameMusic: GameMusic = GameMusic(soundSystem)

    private val gameInputMultiplexer: InputMultiplexer = InputMultiplexer()
    private var _backingGameContainer: GameContainer? = null
    private var backingGameContainer: GameContainer
        set(newValue) {
            val oldValue = _backingGameContainer
            if (oldValue != null) {
                this.gameInputMultiplexer.removeProcessor(oldValue.inputProcessor)
            }

            newValue.resize(Gdx.graphics.width, Gdx.graphics.height)
            newValue.gameRenderer.shouldApplyViewport.set(true)
            this.gameInputMultiplexer.addProcessor(newValue.inputProcessor)
            _backingGameContainer = newValue
            (gameContainer as Var).set(newValue)

            oldValue?.dispose()
        }
        get() = _backingGameContainer ?: error("backingGameContainer is not initialized yet")
    val gameContainer: ReadOnlyVar<GameContainer> by lazy { Var(backingGameContainer) }

    // Note: Game framebuffer is set to stretch scaling, so initial width/height doesn't matter and can be 1
    private val gameFrameBufferCamera: OrthographicCamera = OrthographicCamera().apply { 
        setToOrtho(false, 1f, 1f)
    }
    private val gameFrameBufferViewport: ScalingViewport = StretchViewport(1f, 1f, gameFrameBufferCamera)
    private val gameFrameBufferMgr: FrameBufferManager = FrameBufferManager(
        1, FrameBufferManager.BufferSettings(Pixmap.Format.RGB888), scaling = gameFrameBufferViewport.scaling,
    )

    private val ui: MainGameUi = MainGameUi(this)
    private val toggleableGameInputProcessor: ToggleableInputProcessor = ToggleableInputProcessor(gameInputMultiplexer)
            
    init {
        toggleableGameInputProcessor.enabled.bind { ui.currentMenuState.use() == MainGameUi.MenuState.NONE }
        
        this.screenInputMultiplexer.addProcessor(ui.inputProcessor)
        
        startNewGame(DeckInitializer.RandomSeed())
        
        soundSystem.startRealtime()
        
        this.screenInputMultiplexer.addProcessor(DebugInputAdapter(this, this.ui))
        this.screenInputMultiplexer.addProcessor(toggleableGameInputProcessor)
    }
    
    fun startNewGame(deckInitializer: DeckInitializer) {
        val newContainer = GameContainer({ GameLogic(deckInitializer) }, batch, soundSystem, gameMusic)
        backingGameContainer = newContainer
        
        gameMusic.transitionToStemMix(GameMusic.StemMixes.ALL, 1f)
    }

    override fun render(delta: Float) {
        super.render(delta)
        
        gameFrameBufferMgr.frameUpdate()

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        renderGameToBuffer(delta)
        renderGameBufferToBackBuffer()

        main.resetViewportToScreen()
        
        ui.render(batch, delta)

        main.resetViewportToScreen()
    }

    private fun renderGameToBuffer(delta: Float) {
        gameFrameBufferMgr.getFramebuffer(GAME_BUFFER_INDEX)?.also { gameFb ->
            gameFb.begin()

            Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

            val gameRenderer = backingGameContainer.gameRenderer
            gameRenderer.render(delta)

            gameFb.end()
        }
    }

    private fun renderGameBufferToBackBuffer() {
        val batch = batch
        val cam = gameFrameBufferCamera
        gameFrameBufferViewport.apply()

        batch.projectionMatrix = cam.combined
        batch.begin()

        val gameFbRegion = gameFrameBufferMgr.getFramebufferRegion(GAME_BUFFER_INDEX)
        batch.setColor(1f, 1f, 1f, 1f)
        batch.draw(gameFbRegion, 0f, 0f, cam.viewportWidth, cam.viewportHeight)

        batch.end()
    }

    override fun renderUpdate() {
        super.renderUpdate()

        val gameLogic = backingGameContainer.gameLogic
        gameLogic.renderUpdate(Gdx.graphics.deltaTime)
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        gameFrameBufferViewport.update(width, height)
        ui.resize(width, height)
        backingGameContainer.resize(width, height)
    }
    
    override fun show() {
        super.show()
        main.inputMultiplexer.addProcessor(this.screenInputMultiplexer)
    }

    override fun hide() {
        super.hide()
        main.inputMultiplexer.removeProcessor(this.screenInputMultiplexer)
    }

    override fun getDebugString(): String {
        return """UI:
${ui.getDebugString()}
"""
    }

    override fun dispose() {
        backingGameContainer.dispose()
        
        main.inputMultiplexer.removeProcessor(this.screenInputMultiplexer)
        
        gameMusic.dispose()
        soundSystem.dispose()
        
        gameFrameBufferMgr.dispose()
    }
}