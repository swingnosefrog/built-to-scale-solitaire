package com.swingnosefrog.solitaire.steamworks

import com.codedisaster.steamworks.SteamAPI
import com.codedisaster.steamworks.SteamController
import com.codedisaster.steamworks.SteamUtils
import com.codedisaster.steamworks.SteamUtilsCallback
import java.util.concurrent.atomic.AtomicBoolean


object Steamworks {

    private const val CALLBACK_UPDATE_DELTA: Float = 1 / 30f

    private val initCalled: AtomicBoolean = AtomicBoolean(false)
    private val inited: AtomicBoolean = AtomicBoolean(false)
    private val shutdownCalled: AtomicBoolean = AtomicBoolean(false)

    private var deltaElapsed: Float = 0f

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
                steamInterfaces = SteamInterfaces(
                    SteamUtils(object : SteamUtilsCallback {
                        override fun onSteamShutdown() {
                        }
                    }),
                    SteamController()
                )
                inited.set(true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun getSteamInterfaces(): SteamInterfaces? {
        if (!inited.get() || shutdownCalled.get()) return null
        return steamInterfaces
    }

    fun frameUpdate(deltaSec: Float) {
        if (!inited.get() || shutdownCalled.get()) return

        val callbackUpdateDelta = CALLBACK_UPDATE_DELTA
        deltaElapsed += deltaSec
        if (deltaElapsed >= callbackUpdateDelta) {
            deltaElapsed %= callbackUpdateDelta

            if (SteamAPI.isSteamRunning()) {
                SteamAPI.runCallbacks()
            }
        }

        if (SteamAPI.isSteamRunning()) {
            steamInterfaces!!.input.runFrame()
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