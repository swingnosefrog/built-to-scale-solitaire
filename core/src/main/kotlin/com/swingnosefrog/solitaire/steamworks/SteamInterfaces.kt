package com.swingnosefrog.solitaire.steamworks

import com.codedisaster.steamworks.SteamController
import com.codedisaster.steamworks.SteamUtils


class SteamInterfaces(
    val utils: SteamUtils,
    val input: SteamController,
) {

    val isRunningOnSteamDeck: Boolean get() = utils.isSteamRunningOnSteamDeck
}