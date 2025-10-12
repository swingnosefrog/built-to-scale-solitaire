package com.swingnosefrog.solitaire.persistence

import com.swingnosefrog.solitaire.Solitaire
import java.io.File


object GameSaveLocationHelper {
    
    private const val SAVES_DIRECTORY_NAME: String = "Saves"
    
    val saveDirectory: File = Solitaire.DOT_DIRECTORY.resolve(SAVES_DIRECTORY_NAME).apply { 
        mkdirs()
    }
    
}