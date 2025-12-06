package com.swingnosefrog.solitaire.screen.main.menu

import com.badlogic.gdx.Gdx
import com.swingnosefrog.solitaire.Localization
import com.swingnosefrog.solitaire.SolitaireGame
import com.swingnosefrog.solitaire.menu.MenuController
import com.swingnosefrog.solitaire.menu.MenuOption
import com.swingnosefrog.solitaire.util.WindowSizeUtils
import paintbox.binding.BooleanVar
import paintbox.binding.ReadOnlyBooleanVar
import paintbox.binding.ReadOnlyVar
import paintbox.binding.Var
import paintbox.binding.toConstVar
import paintbox.prefs.PaintboxPreferences
import paintbox.util.WindowSize


class VideoSettingsMenu(
    id: String
) : AbstractSettingsMenu(id) {
    
    override val headingText: ReadOnlyVar<String> = Localization["game.menu.videoSettings.heading"]
    
    private val windowedResolution: Var<WindowSize> = Var(settings.windowedResolution.getOrCompute())
    private val fullscreen: BooleanVar = BooleanVar(settings.fullscreen.get())
    
    private val hasPendingResolutionChanges: ReadOnlyBooleanVar = BooleanVar {
        windowedResolution.use() != settings.windowedResolution.use() || fullscreen.use() != settings.fullscreen.use()
    }

    override val options: List<MenuOption> = listOf(
        MenuOption.OptionWidget.Cycle(
            Localization["game.menu.videoSettings.option.windowedResolution"],
            WindowSizeUtils.commonResolutions.toConstVar(),
            windowedResolution,
            { resolution ->
                var resolutionMarkupText = "${resolution.width}×${resolution.height}"
                val aspectRatio = WindowSizeUtils.getAspectRatio(resolution)
                if (aspectRatio != resolution) {
                    resolutionMarkupText += "  [scale=0.65 font=ui_main_sansserif]\\[${aspectRatio.width}:${aspectRatio.height}\\][]"
                }
                resolutionMarkupText.toConstVar()
            }
        ),
        MenuOption.OptionWidget.Checkbox(
            Localization["game.menu.videoSettings.option.fullscreen"],
            fullscreen
        ),
        MenuOption.Simple(Var {
            val locKey = if (!hasPendingResolutionChanges.use())
                "game.menu.videoSettings.option.windowedResolution.apply"
            else "game.menu.videoSettings.option.windowedResolution.applyChanges"

            "       " + Localization[locKey].use()
        }, fun(_) {
            applyResolution()
            copyResolutionVarsToSettings()
        }),
        MenuOption.Separator(),
        MenuOption.OptionWidget.Checkbox(Localization["game.menu.videoSettings.option.vsyncEnabled"], settings.vsyncEnabled).apply { 
            this.selectedState.addListener { v ->
                applyMaxFpsAndVsync()
            }
        },
        MenuOption.OptionWidget.Cycle(
            Localization["game.menu.videoSettings.option.maxFps"],
            listOf(30, 60, 90, 120, 144, 240, PaintboxPreferences.UNLIMITED_FPS).toConstVar(),
            settings.maxFramerate,
            { fps -> 
                if (fps == PaintboxPreferences.UNLIMITED_FPS)
                    Localization["game.menu.videoSettings.option.maxFps.unlimited"]
                else fps.toString().toConstVar()
            }
        ).apply {
            this.selectedOption.addListener { v ->
                applyMaxFpsAndVsync()
            }
        },
        MenuOption.Back(),
    )
    
    init {
        this.menuSizeAdjustment.set(4)
    }

    override fun onEnter(menuController: MenuController) {
        resetResolutionVarsToSettings()
    }

    private fun applyResolution() {
        val fullscreenProcessor = SolitaireGame.instance.fullscreenWindowedInputProcessor
        
        val windowedResolution = this.windowedResolution
        val isFullscreen = this.fullscreen
        
        if (isFullscreen.get()) {
            fullscreenProcessor.attemptFullscreen()
        } else {
            fullscreenProcessor.attemptSetWindowed(windowedResolution.getOrCompute())
        }
    }
    
    private fun applyMaxFpsAndVsync() {
        val graphics = Gdx.graphics
        graphics.setForegroundFPS(settings.maxFramerate.get())
        graphics.setVSync(settings.vsyncEnabled.get())
    }
    
    private fun resetResolutionVarsToSettings() {
        this.windowedResolution.set(settings.windowedResolution.getOrCompute())
        this.fullscreen.set(settings.fullscreen.get())
    }
    
    private fun copyResolutionVarsToSettings() {
        settings.windowedResolution.set(this.windowedResolution.getOrCompute())
        settings.fullscreen.set(this.fullscreen.get())
    }
}