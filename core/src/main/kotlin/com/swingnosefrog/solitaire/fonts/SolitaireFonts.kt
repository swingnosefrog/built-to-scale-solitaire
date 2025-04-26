package com.swingnosefrog.solitaire.fonts

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.swingnosefrog.solitaire.SolitaireGame
import paintbox.font.FontCache
import paintbox.font.FreeTypeFontAfterLoad
import paintbox.font.PaintboxFont
import paintbox.font.PaintboxFontFreeType
import paintbox.font.PaintboxFontParams
import paintbox.util.WindowSize
import kotlin.reflect.KProperty

class SolitaireFonts(private val game: SolitaireGame) {

    private val fontCache: FontCache get() = game.fontCache


    var headingFont: PaintboxFont by bind(FontKeys.OUTFIT_BOLD)
        private set

    fun registerFonts() {
        val cache = this.fontCache

        val defaultReferenceWindowSize = WindowSize(1280, 720)
        fun createDefaultFontParams(fileHandle: FileHandle): PaintboxFontParams {
            return PaintboxFontParams(
                file = fileHandle,
                scaleToReferenceSize = true,
                referenceSize = defaultReferenceWindowSize,
            )
        }
        
        fun createIncrementalFtfParam() = FreeTypeFontGenerator.FreeTypeFontParameter().apply {
            minFilter = Texture.TextureFilter.Linear
            magFilter = Texture.TextureFilter.Linear
            genMipMaps = false
            incremental = true
            mono = false
            color = Color(1f, 1f, 1f, 1f)
            borderColor = Color(0f, 0f, 0f, 1f)
            characters = " a" // Needed to at least put one glyph in each font to prevent errors
            hinting = FreeTypeFontGenerator.Hinting.Medium
        }

        val defaultAfterLoad: FreeTypeFontAfterLoad = { font ->
            font.setUseIntegerPositions(true) // Filtering doesn't kick in so badly, solves "wiggly" glyphs
            font.setFixedWidthGlyphs("0123456789")
        }
        val defaultScaledFontAfterLoad: FreeTypeFontAfterLoad = { font ->
            font.setUseIntegerPositions(false) // Stops glyphs from being offset due to rounding
        }
        val defaultFontSize = 20

        fun addFontFamily(
            familyName: String, fontIDPrefix: String = familyName, fileExt: String = "ttf",
            fontFamilies: List<FontFamily> = FontFamily.regularBoldWithItalic,
            fontSize: Int = defaultFontSize, borderWidth: Float = 1.5f,
            folderName: String = familyName,
            hinting: FreeTypeFontGenerator.Hinting? = null, generateBordered: Boolean = true,
            scaleToReferenceSize: Boolean = false, referenceSize: WindowSize = defaultReferenceWindowSize,
            afterLoadFunc: FreeTypeFontAfterLoad = defaultAfterLoad,
        ) {
            fontFamilies.forEach { family ->
                val fileHandle = Gdx.files.internal("fonts/${folderName}/${family.toFullFilename(familyName, fileExt)}")
                cache[family.toID(fontIDPrefix, false)] = PaintboxFontFreeType(
                    PaintboxFontParams(fileHandle, scaleToReferenceSize, referenceSize),
                    createIncrementalFtfParam().apply {
                        if (hinting != null) {
                            this.hinting = hinting
                        }
                        this.size = fontSize
                        this.borderWidth = 0f
                    }).setAfterLoad(afterLoadFunc)
                if (generateBordered) {
                    cache[family.toID(fontIDPrefix, true)] = PaintboxFontFreeType(
                        PaintboxFontParams(fileHandle, scaleToReferenceSize, referenceSize),
                        createIncrementalFtfParam().apply {
                            if (hinting != null) {
                                this.hinting = hinting
                            }
                            this.size = fontSize
                            this.borderWidth = borderWidth
                        }).setAfterLoad(afterLoadFunc)
                }
            }
        }
        
        headingFont = PaintboxFontFreeType(
            createDefaultFontParams(Gdx.files.internal("fonts/Outfit/Outfit-SemiBold.ttf")),
            createIncrementalFtfParam().apply {
                hinting = FreeTypeFontGenerator.Hinting.Slight
                size = 20
                borderWidth = 0f
            }
        ).setAfterLoad(defaultAfterLoad)
    }

    private fun bind(key: FontKeys): FontBindingDelegate = FontBindingDelegate(key)
    
    private inner class FontBindingDelegate(private val key: FontKeys) {

        operator fun getValue(thisRef: SolitaireFonts, property: KProperty<*>): PaintboxFont {
            return fontCache[key]
        }

        operator fun setValue(thisRef: SolitaireFonts, property: KProperty<*>, value: PaintboxFont) {
            fontCache[key] = value
        }
    }


    private enum class FontKeys {

        OUTFIT_BOLD,

    }
}