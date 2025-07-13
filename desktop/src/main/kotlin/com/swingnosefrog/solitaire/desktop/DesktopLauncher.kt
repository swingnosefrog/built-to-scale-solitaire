package com.swingnosefrog.solitaire.desktop

import com.badlogic.gdx.Files
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.HdpiMode
import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import paintbox.desktop.PaintboxDesktopLauncher
import paintbox.logging.Logger
import com.swingnosefrog.solitaire.Solitaire
import com.swingnosefrog.solitaire.SolitaireGame
import com.swingnosefrog.solitaire.soundsystem.AudioDeviceSettings
import com.swingnosefrog.solitaire.util.WindowSizeUtils
import paintbox.IPaintboxSettings

object DesktopLauncher {

    private fun printHelp(jCommander: JCommander) {
        println("${Solitaire.TITLE} ${Solitaire.VERSION}\n\n${StringBuilder().apply { jCommander.usageFormatter.usage(this) }}")
    }

    @JvmStatic
    fun main(args: Array<String>) {
        try {
            // Check for bad arguments but don't cause a full crash
            JCommander.newBuilder().acceptUnknownOptions(false).addObject(LaunchArguments()).build().parse(*args)
        } catch (e: ParameterException) {
            println("WARNING: Failed to parse arguments. Check below for details and help documentation. You may have unexpected results from ignoring unknown options.\n")
            e.printStackTrace()
            println("\n\n")
            printHelp(JCommander(LaunchArguments()))
            println("\n\n")
        }

        val arguments = LaunchArguments()
        val jcommander = JCommander.newBuilder().acceptUnknownOptions(true).addObject(arguments).build()
        jcommander.parse(*args)

        if (arguments.printHelp) {
            printHelp(jcommander)
            return
        }

        val app = SolitaireGame(
            SolitaireGame.createPaintboxSettings(
                args.toList(),
                IPaintboxSettings.ILoggerSettings.Impl(
                    Logger(),
                    Solitaire.DOT_DIRECTORY.resolve("logs/")
                )
            )
        )

        Solitaire.CommandLineArguments.logMissingLocalizations = arguments.logMissingLocalizations
        if (arguments.audioDeviceBufferSize != null || arguments.audioDeviceBufferCount != null) {
            val audioDeviceSettings = AudioDeviceSettings(
                arguments.audioDeviceBufferSize ?: AudioDeviceSettings.getDefaultBufferSize(),
                arguments.audioDeviceBufferCount ?: AudioDeviceSettings.getDefaultBufferCount()
            )
            Solitaire.CommandLineArguments.audioDeviceSettings = audioDeviceSettings
        }

        PaintboxDesktopLauncher(app, arguments).editConfig {
            this.setAutoIconify(true)
            val emulatedSize = app.paintboxSettings.emulatedSize
            this.setWindowedMode(emulatedSize.width, emulatedSize.height)
            val minimumSize = WindowSizeUtils.MINIMUM_WINDOWED_SIZE
            this.setWindowSizeLimits(minimumSize.width, minimumSize.height, -1, -1)
            this.setTitle(app.getWindowTitle())
            this.setResizable(true)
            this.setInitialVisible(false)
            this.setInitialBackgroundColor(Color(0f, 0f, 0f, 1f))
            // Note: the audio buffer size and count here are largely ignored since we don't use Gdx.audio.newAudioDevice
            val audioDeviceSettings = Solitaire.CommandLineArguments.audioDeviceSettings
            this.setAudioConfig(
                100,
                audioDeviceSettings?.bufferSize ?: AudioDeviceSettings.getDefaultBufferSize(),
                audioDeviceSettings?.bufferCount ?: AudioDeviceSettings.getDefaultBufferCount()
            )
            this.setHdpiMode(HdpiMode.Logical)
            this.setBackBufferConfig(8, 8, 8, 8, 16, 0, /* samples = */ 2)
            this.setPreferencesConfig(
                "${Solitaire.DOT_DIRECTORY_NAME}/prefs/",
                Files.FileType.External
            )

            val sizes: List<Int> = listOf(32, 24, 16)
            this.setWindowIcon(Files.FileType.Internal, *sizes.map { "icon/$it.png" }.toTypedArray())
        }.launch()
    }

}
