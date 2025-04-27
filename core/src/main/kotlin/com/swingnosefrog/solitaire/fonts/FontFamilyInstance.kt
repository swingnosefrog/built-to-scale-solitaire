package com.swingnosefrog.solitaire.fonts


data class FontFamilyInstance(
    val family: FontFamily,
    val weight: FontWeight,
    val style: FontStyle,
) {

    fun getFilename(): String {
        return "${family.filenamePrefix}-${weight.filenamePart}${style.filenamePart}.${family.fileExtension}"
    }
}