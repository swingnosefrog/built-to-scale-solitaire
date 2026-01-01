package com.swingnosefrog.solitaire.steamworks

import com.codedisaster.steamworks.SteamApps
import com.codedisaster.steamworks.SteamControllerOverride
import com.codedisaster.steamworks.SteamUserStats
import com.codedisaster.steamworks.SteamUtils


class SteamInterfaces(
    val utils: SteamUtils,
    val input: SteamControllerOverride,
    val apps: SteamApps,
    val stats: SteamUserStats,
)
