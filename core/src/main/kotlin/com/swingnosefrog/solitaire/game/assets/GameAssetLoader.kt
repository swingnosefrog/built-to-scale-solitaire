package com.swingnosefrog.solitaire.game.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.swingnosefrog.solitaire.game.CardSuit
import com.swingnosefrog.solitaire.game.CardSymbol
import paintbox.registry.AssetRegistryInstance
import paintbox.registry.IAssetLoader

class GameAssetLoader(private val assetRegistryInstance: AssetRegistryInstance) : IAssetLoader {

    override fun addManagedAssets(manager: AssetManager) {
        val registry = assetRegistryInstance
        
        addCardTextures("modern")
        
        registry.loadAsset<Sound>("sfx_game_dealing_loop", "sounds/game/dealing_loop.wav")
        registry.loadAsset<Sound>("sfx_game_pickup1", "sounds/game/pickup1.wav")
        registry.loadAsset<Sound>("sfx_game_pickup2", "sounds/game/pickup2.wav")
        registry.loadAsset<Sound>("sfx_game_pickup3", "sounds/game/pickup3.wav")
        registry.loadAsset<Sound>("sfx_game_pickup_stack", "sounds/game/pickup_stack.wav")
        registry.loadAsset<Sound>("sfx_game_place", "sounds/game/place.wav")
        registry.loadAsset<Sound>("sfx_game_won", "sounds/game/win.ogg")
        
        registry.loadAsset<Sound>("sfx_game_foundation_widget", "sounds/game/flick.ogg")
        registry.loadAsset<Sound>("sfx_game_foundation_scale_0", "sounds/game/note_C3.ogg")
        registry.loadAsset<Sound>("sfx_game_foundation_scale_1", "sounds/game/note_D3.ogg")
        registry.loadAsset<Sound>("sfx_game_foundation_scale_2", "sounds/game/note_E3.ogg")
        registry.loadAsset<Sound>("sfx_game_foundation_scale_3", "sounds/game/note_F3.ogg")
        registry.loadAsset<Sound>("sfx_game_foundation_scale_4", "sounds/game/note_G3.ogg")
        registry.loadAsset<Sound>("sfx_game_foundation_scale_5", "sounds/game/note_A3.ogg")
        registry.loadAsset<Sound>("sfx_game_foundation_scale_6", "sounds/game/note_B3.ogg")
        registry.loadAsset<Sound>("sfx_game_foundation_scale_7", "sounds/game/note_C4.ogg")
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