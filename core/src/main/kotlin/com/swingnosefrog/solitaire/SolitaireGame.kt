package com.swingnosefrog.solitaire

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics
import com.swingnosefrog.solitaire.init.AssetRegistryLoadingScreen
import com.swingnosefrog.solitaire.game.assets.GameAssetLoader
import com.swingnosefrog.solitaire.game.assets.GameAssets
import com.swingnosefrog.solitaire.init.InitialAssetLoader
import com.swingnosefrog.solitaire.screen.TestSolitaireGameScreen
import com.swingnosefrog.solitaire.steamworks.Steamworks
import paintbox.PaintboxGame
import paintbox.PaintboxSettings
import paintbox.ResizeAction
import paintbox.debug.IDebugKeysInputProcessor
import paintbox.debug.ToggleableDebugKeysInputProcessor
import paintbox.logging.Logger
import paintbox.registry.AssetRegistry
import java.io.File


class SolitaireGame(paintboxSettings: PaintboxSettings) : PaintboxGame(paintboxSettings) {

    companion object {

        lateinit var instance: SolitaireGame
            private set


        fun createPaintboxSettings(launchArguments: List<String>, logger: Logger, logToFile: File?): PaintboxSettings =
            PaintboxSettings(
                launchArguments, logger, logToFile, Solitaire.VERSION, Solitaire.DEFAULT_SIZE,
                ResizeAction.ANY_SIZE, Solitaire.MINIMUM_SIZE
            )
    }

    private var toggleableDebugKeysInputProcessor: ToggleableDebugKeysInputProcessor? = null

    override fun createDebugKeysInputProcessor(): IDebugKeysInputProcessor {
        val currentProcessor = toggleableDebugKeysInputProcessor
        if (currentProcessor != null) return currentProcessor

        val processor = ToggleableDebugKeysInputProcessor()
        toggleableDebugKeysInputProcessor = processor
        return processor
    }

    override fun getWindowTitle(): String = "${Solitaire.TITLE} ${Solitaire.VERSION}"

    override fun create() {
        super.create()
        instance = this

        (Gdx.graphics as Lwjgl3Graphics).window.setVisible(true)

        AssetRegistry.addAssetLoader(InitialAssetLoader())
        GameAssets.addAssetLoader(GameAssetLoader(GameAssets))

        Steamworks.init()

        fun initializeScreens() {
        }
        setScreen(AssetRegistryLoadingScreen(this, AssetRegistry, GameAssets).apply {
            onStart = {}
            onAssetLoadingComplete = {
                initializeScreens()
            }
            nextScreenProducer = {
                TestSolitaireGameScreen(this@SolitaireGame)
            }
        })

    }

    override fun postRender() {
        super.postRender()

        Steamworks.frameUpdate(Gdx.graphics.deltaTime)
    }

    override fun dispose() {
        super.dispose()
        Steamworks.shutdown()
    }
}