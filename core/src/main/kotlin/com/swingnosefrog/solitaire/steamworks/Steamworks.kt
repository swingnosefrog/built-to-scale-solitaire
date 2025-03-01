package com.swingnosefrog.solitaire.steamworks

import com.codedisaster.steamworks.SteamAPI
import com.codedisaster.steamworks.SteamUtils
import com.codedisaster.steamworks.SteamUtilsCallback
import java.util.concurrent.atomic.AtomicBoolean


object Steamworks {

    private const val CALLBACK_UPDATE_DELTA: Float = 1 / 30f

    private data class SteamInterfaces(
        val utils: SteamUtils,
    )

    private val initCalled: AtomicBoolean = AtomicBoolean(false)
    private val inited: AtomicBoolean = AtomicBoolean(false)
    private val shutdownCalled: AtomicBoolean = AtomicBoolean(false)

    private var deltaElapsed: Float = CALLBACK_UPDATE_DELTA

    private var steamInterfaces: SteamInterfaces? = null

    @Synchronized
    fun init() {
        if (initCalled.get()) return

        initCalled.set(true)
        try {
            SteamAPI.loadLibraries()
            if (!SteamAPI.init()) {
                // Steamworks initialization error
            } else {
                inited.set(true)
                steamInterfaces = SteamInterfaces(
                    SteamUtils(object : SteamUtilsCallback {
                        override fun onSteamShutdown() {
                        }
                    }),
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun frameUpdate(deltaSec: Float) {
        if (!inited.get()) return

        val callbackUpdateDelta = CALLBACK_UPDATE_DELTA
        deltaElapsed += deltaSec
        if (deltaElapsed >= callbackUpdateDelta) {
            deltaElapsed %= callbackUpdateDelta

            if (SteamAPI.isSteamRunning()) {
                SteamAPI.runCallbacks()
            }
        }
    }

    @Synchronized
    fun shutdown() {
        if (shutdownCalled.get() || !inited.get()) return

        shutdownCalled.set(true)
        try {
            SteamAPI.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}