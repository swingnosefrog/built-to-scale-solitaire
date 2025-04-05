package com.swingnosefrog.solitaire.game.assets

import com.badlogic.gdx.Gdx
import com.swingnosefrog.solitaire.SolitaireGame
import paintbox.registry.AssetRegistryInstance
import paintbox.util.gdxutils.disposeQuietly


object GameAssets : AssetRegistryInstance() {

    init {
        Gdx.app.postRunnable {
            SolitaireGame.instance.addDisposeCall {
                this.disposeQuietly()
            }
        }
    }
}