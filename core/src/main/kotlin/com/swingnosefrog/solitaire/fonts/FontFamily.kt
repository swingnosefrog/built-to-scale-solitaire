package com.swingnosefrog.solitaire.fonts


data class FontFamily(
    val familyName: String,

    val weights: Set<FontWeight>,
    val styles: Set<FontStyle>,

    val filenamePrefix: String = familyName.replace(" ", ""),
    val fileExtension: String = "ttf",
) {
    
    fun createAllInstances(): List<FontFamilyInstance> {
        return buildList { 
            weights.forEach { weight ->
                styles.forEach { style ->
                    this.add(createInstance(weight, style))
                }
            }
        }
    }
    
    fun createInstance(weight: FontWeight, style: FontStyle): FontFamilyInstance {
        if (weight !in weights)
            throw IllegalArgumentException("The weight $weight is not supported by this font family (supported: $weights)")
        if (style !in styles)
            throw IllegalArgumentException("The style $style is not supported by this font family (supported: $styles)")
        
        return FontFamilyInstance(this, weight, style)
    }
    
    operator fun get(weight: FontWeight, style: FontStyle): FontFamilyInstance {
        return createInstance(weight, style)
    }
}
