package com.swingnosefrog.solitaire

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics
import com.swingnosefrog.solitaire.assets.AssetRegistryLoadingScreen
import com.swingnosefrog.solitaire.game.assets.GameAssetLoader
import com.swingnosefrog.solitaire.game.assets.GameAssets
import com.swingnosefrog.solitaire.assets.AssetRegistryAssetLoader
import com.swingnosefrog.solitaire.fonts.SolitaireFonts
import com.swingnosefrog.solitaire.screen.main.MainGameScreen
import com.swingnosefrog.solitaire.steamworks.Steamworks
import paintbox.IPaintboxSettings
import paintbox.Paintbox
import paintbox.PaintboxGame
import paintbox.ResizeAction
import paintbox.debug.IDebugKeysInputProcessor
import paintbox.debug.ToggleableDebugKeysInputProcessor
import paintbox.input.DefaultFullscreenWindowedInputProcessor
import paintbox.input.IFullscreenWindowedInputProcessor
import paintbox.registry.AssetRegistry

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
                launchArguments, loggerSettings, Solitaire.VERSION, Solitaire.DEFAULT_SIZE,
                ResizeAction.ANY_SIZE, Solitaire.MINIMUM_SIZE
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
        
        gdxPrefs = Gdx.app.getPreferences("com.swingnosefrog.solitaire")
        settings = SolitaireSettings(this, gdxPrefs).apply {
            load()
            setStartupSettings()
        }
        
        fonts.registerFonts()

        fullscreenWindowedInputProcessor =
            DefaultFullscreenWindowedInputProcessor(
                Solitaire.DEFAULT_SIZE,
                { this.settings },
                { it.fullscreenMonitor },
                { it.windowedResolution }
            )
        this.inputMultiplexer.addProcessor(fullscreenWindowedInputProcessor)
        
        Steamworks.init()
        if (Steamworks.getSteamInterfaces()?.isRunningOnSteamDeck == true) {
            Paintbox.LOGGER.info("Detected running on Steam Deck/SteamOS")
        }
        
        (Gdx.graphics as Lwjgl3Graphics).window.setVisible(true)

        setScreen(AssetRegistryLoadingScreen(this, AssetRegistry, GameAssets).apply {
            onStart = {}
            onAssetLoadingComplete = {
            }
            nextScreenProducer = {
                MainGameScreen(this@SolitaireGame)
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

    override fun getWindowTitle(): String = "${Solitaire.TITLE} ${Solitaire.VERSION}"

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