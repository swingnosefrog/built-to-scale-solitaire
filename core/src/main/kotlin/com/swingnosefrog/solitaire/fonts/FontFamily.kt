package com.swingnosefrog.solitaire.fonts


data class FontFamily(val filenameSuffix: String, val idSuffix: String) {
    
    companion object {

        val REGULAR = FontFamily("Regular", "")
        val ITALIC = FontFamily("Italic", "ITALIC")
        val BOLD = FontFamily("Bold", "BOLD")
        val BOLD_ITALIC = FontFamily("BoldItalic", "BOLD_ITALIC")
        val LIGHT = FontFamily("Light", "LIGHT")
        val LIGHT_ITALIC = FontFamily("LightItalic", "LIGHT_ITALIC")

        val regularBoldWithItalic: List<FontFamily> = listOf(REGULAR, ITALIC, BOLD, BOLD_ITALIC)
        val regularBoldLightWithItalic: List<FontFamily> =
            listOf(REGULAR, ITALIC, BOLD, BOLD_ITALIC, LIGHT, LIGHT_ITALIC)
    }

    fun toFullFilename(familyName: String, fileExt: String): String {
        return "$familyName-$filenameSuffix.$fileExt"
    }

    fun toID(fontIDPrefix: String, isBordered: Boolean): String {
        var id = fontIDPrefix
        if (idSuffix.isNotEmpty()) id += "_$idSuffix"
        if (isBordered) id += "_BORDERED"
        return id
    }
}
