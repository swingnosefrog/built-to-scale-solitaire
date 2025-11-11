package com.swingnosefrog.solitaire.screen.main

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.ScalingViewport
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.swingnosefrog.solitaire.Solitaire
import com.swingnosefrog.solitaire.SolitaireGame
import com.swingnosefrog.solitaire.game.GameContainer
import com.swingnosefrog.solitaire.game.audio.GameMusic
import com.swingnosefrog.solitaire.game.logic.DeckInitializer
import com.swingnosefrog.solitaire.game.logic.GameLogic
import com.swingnosefrog.solitaire.inputmanager.InputManager
import com.swingnosefrog.solitaire.screen.AbstractGameScreen
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
    private val toggleableGameInputProcessor: ToggleableInputProcessor
    val inputManager: InputManager = main.inputManagerFactory.create()

    private val soundSystem: SoundSystem = SoundSystem.createDefaultSoundSystem()
    private val gameMusic: GameMusic = GameMusic(soundSystem)

    private val backingGameContainer: BackingGameContainer = BackingGameContainer()
    val gameContainer: ReadOnlyVar<GameContainer> get() = backingGameContainer.currentContainer

    private val ui: MainGameUi = MainGameUi(this)

    private val gameFrameBuffer: GameFrameBuffer = GameFrameBuffer()

    init {
        toggleableGameInputProcessor = ToggleableInputProcessor(backingGameContainer)
        toggleableGameInputProcessor.enabled.bind { ui.currentMenuState.use() == MainGameUi.MenuState.NONE }
        
        this.screenInputMultiplexer.addProcessor(ui.inputProcessor)
        if (Solitaire.isNonProductionVersion) {
            this.screenInputMultiplexer.addProcessor(DebugInputAdapter(this, this.ui))
        }
        this.screenInputMultiplexer.addProcessor(toggleableGameInputProcessor)
        
        inputManager.addInputActionListener(ui.inputActionListener, index = 0)

        startNewGame(DeckInitializer.RandomSeed())
        soundSystem.startRealtime()
    }
    
    fun startNewGame(deckInitializer: DeckInitializer) {
        val newContainer =
            GameContainer({ GameLogic(deckInitializer, initiallyMouseBased = true) }, batch, soundSystem, gameMusic)
        backingGameContainer.setNewGameContainer(newContainer)
        
        gameMusic.transitionToStemMix(GameMusic.StemMixes.ALL, 1f)
    }
    

    override fun render(delta: Float) {
        inputManager.frameUpdate()
        
        super.render(delta)

        gameFrameBuffer.fbManager.frameUpdate()

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        renderGameToBuffer(delta)
        renderGameBufferToBackBuffer()

        main.resetViewportToScreen()
        
        ui.render(batch, delta)

        main.resetViewportToScreen()
    }

    private fun renderGameToBuffer(delta: Float) {
        gameFrameBuffer.fbManager.getFramebuffer(GAME_BUFFER_INDEX)?.also { gameFb ->
            gameFb.begin()

            Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

            val gameRenderer = gameContainer.getOrCompute().gameRenderer
            gameRenderer.render(delta)

            gameFb.end()
        }
    }

    private fun renderGameBufferToBackBuffer() {
        val batch = batch
        val cam = gameFrameBuffer.camera
        gameFrameBuffer.viewport.apply()

        batch.projectionMatrix = cam.combined
        batch.begin()

        val gameFbRegion = gameFrameBuffer.fbManager.getFramebufferRegion(GAME_BUFFER_INDEX)
        batch.setColor(1f, 1f, 1f, 1f)
        batch.draw(gameFbRegion, 0f, 0f, cam.viewportWidth, cam.viewportHeight)

        batch.end()
    }

    override fun renderUpdate() {
        super.renderUpdate()

        val gameLogic = gameContainer.getOrCompute().gameLogic
        gameLogic.renderUpdate(Gdx.graphics.deltaTime)
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        gameFrameBuffer.viewport.update(width, height)
        ui.resize(width, height)
        gameContainer.getOrCompute().resize(width, height)
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

InputManager:
src: ${inputManager.mostRecentActionSource.getOrCompute().sourceName}
"""
    }

    override fun dispose() {
        backingGameContainer.dispose()
        
        main.inputMultiplexer.removeProcessor(this.screenInputMultiplexer)
        
        gameMusic.dispose()
        soundSystem.dispose()
        
        gameFrameBuffer.dispose()
    }
    
    private inner class BackingGameContainer private constructor(
        private val gameGdxInputMultiplexer: InputMultiplexer,
    ) : Disposable, InputProcessor by gameGdxInputMultiplexer {
        
        private var statsAndAchievementsGameListener: StatsAndAchievementsGameListener? = null
        
        private var _container: GameContainer? = null
        private var current: GameContainer
            set(newValue) {
                val stats = main.stats
                
                val oldValue = _container
                if (oldValue != null) {
                    inputManager.removeInputActionListener(oldValue.inputActionListener)
                    this.gameGdxInputMultiplexer.removeProcessor(oldValue.gdxInputProcessor)
                    statsAndAchievementsGameListener?.let { oldValue.gameLogic.eventDispatcher.removeListener(it) }
                    stats.persist()
                }

                newValue.resize(Gdx.graphics.width, Gdx.graphics.height)
                newValue.gameRenderer.shouldApplyViewport.set(true)

                inputManager.addInputActionListener(newValue.inputActionListener)
                this.gameGdxInputMultiplexer.addProcessor(newValue.gdxInputProcessor)
                val newStatsListener = StatsAndAchievementsGameListener(stats, newValue)
                newValue.gameLogic.eventDispatcher.addListener(newStatsListener)
                statsAndAchievementsGameListener = newStatsListener
                
                _container = newValue
                (currentContainer as Var).set(newValue)

                oldValue?.dispose()
            }
            get() = _container ?: error("backingGameContainer is not initialized yet")
        
        val currentContainer: ReadOnlyVar<GameContainer> by lazy { Var(current) }

        constructor() : this(InputMultiplexer())
        
        fun setNewGameContainer(newValue: GameContainer) {
            this.current = newValue
        }
        
        override fun dispose() {
            current.dispose()
        }
    }
    
    private class GameFrameBuffer : Disposable {

        // Note: Game framebuffer is set to stretch scaling, so initial width/height doesn't matter and can be 1
        val camera: OrthographicCamera = OrthographicCamera().apply {
            setToOrtho(false, 1f, 1f)
        }
        val viewport: ScalingViewport = StretchViewport(1f, 1f, camera)
        val fbManager: FrameBufferManager = FrameBufferManager(
            1, FrameBufferManager.BufferSettings(Pixmap.Format.RGB888), scaling = viewport.scaling,
        )

        override fun dispose() {
            fbManager.dispose()
        }
    }
}