package com.swingnosefrog.solitaire.fonts

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.Hinting
import com.swingnosefrog.solitaire.SolitaireGame
import paintbox.font.*
import paintbox.util.WindowSize
import kotlin.reflect.KProperty

class SolitaireFonts(private val game: SolitaireGame) {

    private val fontCache: FontCache get() = game.fontCache

    private val uiMainSerifFamily: FontFamily = FontFamily(
        "Crimson Pro",
        setOf(FontWeight.MEDIUM, FontWeight.BOLD),
        setOf(FontStyle.REGULAR, FontStyle.ITALIC)
    )
    private val uiMainSansSerifFamily: FontFamily = FontFamily(
        "Radio Canada Big",
        setOf(FontWeight.MEDIUM, FontWeight.BOLD),
        setOf(FontStyle.REGULAR, FontStyle.ITALIC)
    )
    
    
    var uiPromptFont: PaintboxFont by bind(FontKeys.UI_PROMPTFONT)
        private set

    var uiHeadingFont: PaintboxFont by bind(FontKeys.UI_HEADING)
        private set
    
    var uiMainSerifFont: PaintboxFont by bind(uiMainSerifFamily[FontWeight.MEDIUM, FontStyle.REGULAR])
        private set
    var uiMainSerifFontBold: PaintboxFont by bind(uiMainSerifFamily[FontWeight.BOLD, FontStyle.REGULAR])
        private set
    var uiMainSerifFontItalic: PaintboxFont by bind(uiMainSerifFamily[FontWeight.MEDIUM, FontStyle.ITALIC])
        private set
    var uiMainSerifFontBoldItalic: PaintboxFont by bind(uiMainSerifFamily[FontWeight.BOLD, FontStyle.ITALIC])
        private set
    
    var uiMainSansSerifFont: PaintboxFont by bind(uiMainSansSerifFamily[FontWeight.MEDIUM, FontStyle.REGULAR])
        private set
    var uiMainSansSerifFontBold: PaintboxFont by bind(uiMainSansSerifFamily[FontWeight.BOLD, FontStyle.REGULAR])
        private set
    var uiMainSansSerifFontItalic: PaintboxFont by bind(uiMainSansSerifFamily[FontWeight.MEDIUM, FontStyle.ITALIC])
        private set
    var uiMainSansSerifFontBoldItalic: PaintboxFont by bind(uiMainSansSerifFamily[FontWeight.BOLD, FontStyle.ITALIC])
        private set

    
    val uiMainSerifMarkup: Markup by lazy {
        Markup.createWithBoldItalic(
            uiMainSerifFont,
            uiMainSerifFontBold,
            uiMainSerifFontItalic,
            uiMainSerifFontBoldItalic,
            additionalMappings = getAdditionalMarkupFontMappings(),
        )
    }
    val uiMainSerifBoldMarkup: Markup by lazy {
        Markup.createWithBoldItalic(
            uiMainSerifFontBold,
            uiMainSerifFont,
            uiMainSerifFontBoldItalic,
            uiMainSerifFontItalic,
            additionalMappings = getAdditionalMarkupFontMappings(),
        )
    }
    val uiMainSansSerifMarkup: Markup by lazy {
        Markup.createWithBoldItalic(
            uiMainSansSerifFont,
            uiMainSansSerifFontBold,
            uiMainSansSerifFontItalic,
            uiMainSansSerifFontBoldItalic,
            additionalMappings = getAdditionalMarkupFontMappings(),
        )
    }
    val uiMainSansSerifBoldMarkup: Markup by lazy {
        Markup.createWithBoldItalic(
            uiMainSansSerifFontBold,
            uiMainSansSerifFont,
            uiMainSansSerifFontBoldItalic,
            uiMainSansSerifFontItalic,
            additionalMappings = getAdditionalMarkupFontMappings(),
        )
    }

    fun getAdditionalMarkupFontMappings(): Map<String, PaintboxFont> = mapOf(
        "promptfont" to uiPromptFont,
        
        "ui_heading" to uiHeadingFont,
        "ui_main_serif" to uiMainSerifFont,
        "ui_main_serif_bold" to uiMainSerifFontBold,
        "ui_main_serif_italic" to uiMainSerifFontItalic,
        "ui_main_serif_bold_italic" to uiMainSerifFontBoldItalic,
        "ui_main_sansserif" to uiMainSansSerifFont,
        "ui_main_sansserif_bold" to uiMainSansSerifFontBold,
        "ui_main_sansserif_italic" to uiMainSansSerifFontItalic,
        "ui_main_sansserif_bold_italic" to uiMainSansSerifFontBoldItalic,
    )

    fun registerFonts() {
        val cache = this.fontCache
        
        val fontsFolder = Gdx.files.internal("fonts/")

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
            hinting = Hinting.Medium
        }

        val defaultAfterLoad: FreeTypeFontAfterLoad = { font ->
            font.setUseIntegerPositions(true) // Filtering doesn't kick in so badly, solves "wiggly" glyphs
            font.setFixedWidthGlyphs("0123456789")
        }
        val defaultScaledFontAfterLoad: FreeTypeFontAfterLoad = { font ->
            font.setUseIntegerPositions(false) // Stops glyphs from being offset due to rounding
            font.setFixedWidthGlyphs("0123456789")
        }

        fun addFontFamily(
            family: FontFamily,
            fontSize: Int,
            scaleToReferenceSize: Boolean = true, referenceSize: WindowSize = defaultReferenceWindowSize,
            hinting: Hinting? = null,
            afterLoadFunc: FreeTypeFontAfterLoad = if (scaleToReferenceSize) defaultScaledFontAfterLoad else defaultAfterLoad,
        ) {
            family.createAllInstances().forEach { instance ->
                val fileHandle = fontsFolder.child("${family.familyName}/${instance.getFilename()}")
                cache[instance] = PaintboxFontFreeType(
                    PaintboxFontParams(fileHandle, scaleToReferenceSize, referenceSize),
                    createIncrementalFtfParam().apply {
                        if (hinting != null) {
                            this.hinting = hinting
                        }
                        this.size = fontSize
                        this.borderWidth = 0f
                    }
                ).setAfterLoad(afterLoadFunc)
            }
        }
        
        uiHeadingFont = PaintboxFontFreeType(
            createDefaultFontParams(fontsFolder.child("Outfit/Outfit-SemiBold.ttf")),
            createIncrementalFtfParam().apply {
                hinting = Hinting.Slight
                size = 64
                borderWidth = 0f
            }
        ).setAfterLoad(defaultScaledFontAfterLoad)
        
        uiPromptFont = PaintboxFontFreeType(
            createDefaultFontParams(fontsFolder.child("PromptFont/promptfont.ttf")),
            createIncrementalFtfParam().apply {
                hinting = Hinting.Slight
                size = 32
                borderWidth = 0f
            }
        ).setAfterLoad(defaultScaledFontAfterLoad)
        
        addFontFamily(uiMainSerifFamily, fontSize = 32, hinting = Hinting.Medium)
        addFontFamily(uiMainSansSerifFamily, fontSize = 32, hinting = Hinting.Medium)
    }

    private fun bind(key: Any): FontBindingDelegate = FontBindingDelegate(key)
    
    private inner class FontBindingDelegate(private val key: Any) {

        operator fun getValue(thisRef: SolitaireFonts, property: KProperty<*>): PaintboxFont {
            return fontCache[key]
        }

        operator fun setValue(thisRef: SolitaireFonts, property: KProperty<*>, value: PaintboxFont) {
            fontCache[key] = value
        }
    }


    private enum class FontKeys {

        UI_HEADING,
        UI_PROMPTFONT,
        

    }
}