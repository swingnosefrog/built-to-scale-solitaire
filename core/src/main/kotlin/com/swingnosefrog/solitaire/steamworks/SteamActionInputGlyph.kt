package com.swingnosefrog.solitaire.steamworks

import com.codedisaster.steamworks.SteamController
import com.swingnosefrog.solitaire.inputmanager.IActionInputGlyph


data class SteamActionInputGlyph(
    val actionOrigin: SteamController.ActionOrigin,
    override val promptFontText: String,
    override val glyphName: String = actionOrigin.name,
) : IActionInputGlyph 
