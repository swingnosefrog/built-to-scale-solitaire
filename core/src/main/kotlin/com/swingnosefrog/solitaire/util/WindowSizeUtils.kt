package com.swingnosefrog.solitaire.util

import paintbox.util.WindowSize
import kotlin.math.abs


object WindowSizeUtils {
    
    val DEFAULT_WINDOWED_SIZE: WindowSize = WindowSize(1280, 720)
    val DEFAULT_WINDOWED_SIZE_STEAM_DECK: WindowSize = WindowSize(1280, 800)
    var DEFAULT_COMPUTED_WINDOWED_SIZE: WindowSize = DEFAULT_WINDOWED_SIZE
        private set
    val MINIMUM_WINDOWED_SIZE: WindowSize = WindowSize(1152, 648)

    val commonAspectRatios: List<WindowSize> = listOf(
        WindowSize(16, 9),
        WindowSize(16, 10),
        WindowSize(3, 2),
        WindowSize(4, 3),
        WindowSize(5, 4),
    ).sortedBy { it.width.toFloat() / it.height }
    
    val commonResolutions: List<WindowSize> = listOf(
        WindowSize(1152, 648),
        WindowSize(1280, 720),
        WindowSize(1280, 800),
        WindowSize(1366, 768),
        WindowSize(1600, 900),
        WindowSize(1760, 990),
        WindowSize(1920, 1080),
        WindowSize(2240, 1260),
        WindowSize(2560, 1440),
        WindowSize(3200, 1800),
        WindowSize(3840, 2160),
    ).sortedWith(Comparator.comparingInt<WindowSize> { it.width }.thenComparingInt { it.height })

    fun getAspectRatio(windowSize: WindowSize): WindowSize {
        val targetRatio = windowSize.width.toFloat() / windowSize.height
        val tolerance = 0.01f

        for (aspect in commonAspectRatios) {
            val aspectRatio = aspect.width.toFloat() / aspect.height
            if (abs(aspectRatio - targetRatio) < tolerance) {
                return aspect
            }
        }

        return windowSize
    }
    
    fun setDefaultComputedWindowedSizeToSteamDeck() {
        DEFAULT_COMPUTED_WINDOWED_SIZE = DEFAULT_WINDOWED_SIZE_STEAM_DECK
    }
}