package com.swingnosefrog.solitaire

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics
import com.badlogic.gdx.graphics.Color
import com.swingnosefrog.solitaire.assets.AssetRegistryLoadingScreen
import com.swingnosefrog.solitaire.game.assets.GameAssetLoader
import com.swingnosefrog.solitaire.game.assets.GameAssets
import com.swingnosefrog.solitaire.assets.AssetRegistryAssetLoader
import com.swingnosefrog.solitaire.fonts.SolitaireFonts
import com.swingnosefrog.solitaire.persistence.GameSaveLocationHelper
import com.swingnosefrog.solitaire.screen.main.MainGameScreen
import com.swingnosefrog.solitaire.settings.SolitaireSettings
import com.swingnosefrog.solitaire.settings.VolumeGain
import com.swingnosefrog.solitaire.steamworks.Steamworks
import com.swingnosefrog.solitaire.util.WindowSizeUtils
import paintbox.IPaintboxSettings
import paintbox.Paintbox
import paintbox.PaintboxGame
import paintbox.ResizeAction
import paintbox.debug.IDebugKeysInputProcessor
import paintbox.debug.ToggleableDebugKeysInputProcessor
import paintbox.input.DefaultFullscreenWindowedInputProcessor
import paintbox.input.IFullscreenWindowedInputProcessor
import paintbox.registry.AssetRegistry
import paintbox.transition.FadeToOpaque
import paintbox.transition.FadeToTransparent
import paintbox.transition.TransitionScreen
import java.time.LocalDateTime

typealias GdxPreferences = com.badlogic.gdx.Preferences


class SolitaireGame(paintboxSettings: IPaintboxSettings) : PaintboxGame(paintboxSettings) {

    companion object {

        lateinit var instance: SolitaireGame
            private set

        val globalVolumeGain: VolumeGain get() = instance.volumeGain

        fun createPaintboxSettings(
            launchArguments: List<String>, 
            loggerSettings: IPaintboxSettings.ILoggerSettings
        ): IPaintboxSettings =
            IPaintboxSettings.Impl(
                launchArguments, loggerSettings, Solitaire.VERSION, WindowSizeUtils.DEFAULT_WINDOWED_SIZE,
                ResizeAction.ANY_SIZE, WindowSizeUtils.MINIMUM_WINDOWED_SIZE
            )
    }
    
    private lateinit var gdxPrefs: GdxPreferences
    lateinit var settings: SolitaireSettings
        private set
    val volumeGain: VolumeGain by lazy { VolumeGain(settings) }

    val fonts: SolitaireFonts = SolitaireFonts(this)
    
    lateinit var fullscreenWindowedInputProcessor: IFullscreenWindowedInputProcessor
        private set
    private var toggleableDebugKeysInputProcessor: ToggleableDebugKeysInputProcessor? = null

    override fun create() {
        super.create()
        instance = this
        
        AssetRegistry.addAssetLoader(AssetRegistryAssetLoader())
        GameAssets.addAssetLoader(GameAssetLoader())

        Steamworks.init()
        if (Steamworks.isRunningOnSteamDeck()) {
            Paintbox.LOGGER.info("Detected running on Steam Deck/SteamOS")
            WindowSizeUtils.DEFAULT_COMPUTED_WINDOWED_SIZE = WindowSizeUtils.DEFAULT_WINDOWED_SIZE_STEAM_DECK
        }
        
        gdxPrefs = Gdx.app.getPreferences("com.swingnosefrog.solitaire")
        settings = SolitaireSettings(this, gdxPrefs).apply {
            load()
            setStartupSettings()
        }
        
        fonts.registerFonts()

        fullscreenWindowedInputProcessor =
            DefaultFullscreenWindowedInputProcessor(
                WindowSizeUtils.DEFAULT_COMPUTED_WINDOWED_SIZE,
                { this.settings },
                { it.fullscreenMonitor },
                { it.windowedResolution }
            )
        this.inputMultiplexer.addProcessor(fullscreenWindowedInputProcessor)

        GameSaveLocationHelper.saveDirectory.resolve("lastUpdated.sav").writeText("${LocalDateTime.now()}")
        
        (Gdx.graphics as Lwjgl3Graphics).window.setVisible(true)

        setScreen(AssetRegistryLoadingScreen(this, AssetRegistry, GameAssets).apply {
            onStart = {}
            onAssetLoadingComplete = {
            }
            nextScreenProducer = {
                val nextScreen = MainGameScreen(this@SolitaireGame)
                val black = Color(0f, 0f, 0f, 1f)
                TransitionScreen(
                    this@SolitaireGame,
                    this@SolitaireGame.getScreen(),
                    nextScreen,
                    FadeToOpaque(0.125f, black),
                    FadeToTransparent(0.175f, black)
                )
            }
        })
    }

    override fun createDebugKeysInputProcessor(): IDebugKeysInputProcessor {
        val currentProcessor = toggleableDebugKeysInputProcessor
        if (currentProcessor != null) return currentProcessor

        val processor = ToggleableDebugKeysInputProcessor().apply { 
            this.reloadableLocalizationInstances = listOf(Localization)
        }
        toggleableDebugKeysInputProcessor = processor
        return processor
    }

    override fun getWindowTitle(): String {
        return buildString { 
            append(Solitaire.TITLE)
            if (Solitaire.isNonProductionVersion) {
                append(' ')
                append(Solitaire.VERSION.toString())
            }
        }
    }
    
    override fun preRender() {
        super.preRender()

        CursorState.preRender()
    }

    override fun postRender() {
        super.postRender()

        val deltaSec = Gdx.graphics.deltaTime
        Steamworks.frameUpdate(deltaSec)
    }

    override fun dispose() {
        super.dispose()
        settings.persist()
        Steamworks.shutdown()
    }
}