package com.swingnosefrog.solitaire.steamworks

import com.codedisaster.steamworks.SteamAPI
import com.codedisaster.steamworks.SteamApps
import com.codedisaster.steamworks.SteamController
import com.codedisaster.steamworks.SteamUserStats
import com.codedisaster.steamworks.SteamUtils
import java.util.concurrent.atomic.AtomicBoolean


object Steamworks {

    private const val CALLBACK_UPDATE_DELTA: Float = 1 / 30f

    private val initCalled: AtomicBoolean = AtomicBoolean(false)
    private val inited: AtomicBoolean = AtomicBoolean(false)
    private val failedToInitialize: AtomicBoolean = AtomicBoolean(false)
    private val shutdownCalled: AtomicBoolean = AtomicBoolean(false)

    private var deltaElapsed: Float = 0f

    private var steamInterfaces: SteamInterfaces? = null
    private val isRunningOnSteamDeck: AtomicBoolean = AtomicBoolean(false)

    @Synchronized
    fun init() {
        if (initCalled.get()) return

        initCalled.set(true)
        try {
            SteamAPI.loadLibraries()
            if (!SteamAPI.init()) {
                // Steamworks initialization error
            } else {
                val steamInterfaces = SteamInterfaces(
                    SteamUtils(fun() {}),
                    SteamController().apply { this.init() },
                    SteamApps(),
                    SteamUserStats(SteamStats),
                )

                setInitialSettings(steamInterfaces)

                SteamAPI.runCallbacks()
                isRunningOnSteamDeck.set(steamInterfaces.utils.isSteamRunningOnSteamDeck)

                steamInterfaces.stats.requestCurrentStats()

                this.steamInterfaces = steamInterfaces
                inited.set(true)
            }
        } catch (e: Exception) {
            failedToInitialize.set(true)
            e.printStackTrace()
        }
    }
    
    fun getSteamInterfaces(): SteamInterfaces? {
        if (!inited.get() || shutdownCalled.get()) return null
        return steamInterfaces
    }
    
    fun isRunningOnSteamDeck(): Boolean {
        if (!initCalled.get())
            error("Steamworks.init() was not called")
        
        return isRunningOnSteamDeck.get()
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
    
    private fun setInitialSettings(interfaces: SteamInterfaces) {
        interfaces.utils.setOverlayNotificationPosition(SteamUtils.NotificationPosition.BottomRight)
    }
}