package com.swingnosefrog.solitaire.game.assets

import com.badlogic.gdx.assets.AssetManager
import com.swingnosefrog.solitaire.game.CardSuit
import com.swingnosefrog.solitaire.game.CardSymbol
import paintbox.registry.AssetRegistryInstance
import paintbox.registry.IAssetLoader

class GameAssetLoader(private val assetRegistryInstance: AssetRegistryInstance) : IAssetLoader {

    override fun addManagedAssets(manager: AssetManager) {
        addCardTextures("modern")
    }

    override fun addUnmanagedAssets(assets: MutableMap<String, Any>) {
    }
    
    private fun addCardTextures(subdir: String) {
        fun CardAssetKey.loadTexture() {
            val key = this.getAssetKey()
            assetRegistryInstance.loadAsset(key, "textures/game/${subdir}/${key}.png", linearTexture())
        }
        
        val allSuits = CardSuit.entries
        val allSymbols = CardSymbol.entries

        for (suit in allSuits) {
            for (symbol in allSymbols) {
                val cardAssetKey = CardAssetKey.Front(suit, symbol)
                cardAssetKey.loadTexture()
            }
        }

        CardAssetKey.Back.loadTexture()
        CardAssetKey.Slot.loadTexture()
    }
}