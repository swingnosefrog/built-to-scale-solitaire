package com.swingnosefrog.solitaire.game.assets

import com.badlogic.gdx.assets.AssetManager
import com.swingnosefrog.solitaire.assets.AssetLoaderBase
import com.swingnosefrog.solitaire.game.CardSuit
import com.swingnosefrog.solitaire.game.CardSymbol
import com.swingnosefrog.solitaire.soundsystem.beads.BeadsSound

class GameAssetLoader : AssetLoaderBase<GameAssets>(GameAssets) {

    override fun addManagedAssets(manager: AssetManager) {
        super.addManagedAssets(manager)

        val registry = assetRegistryInstance

        addCardTextures(CardSkin.MODERN, "modern", linearFiltering = true)
        addCardTextures(CardSkin.CLASSIC, "classic", linearFiltering = false)

        registry.loadAsset<BeadsSound>("sfx_game_dealing_loop", "sounds/game/dealing_loop.wav")
        registry.loadAsset<BeadsSound>("sfx_game_collection", "sounds/game/collection.wav")
        registry.loadAsset<BeadsSound>("sfx_game_pickup1", "sounds/game/pickup1.wav")
        registry.loadAsset<BeadsSound>("sfx_game_pickup2", "sounds/game/pickup2.wav")
        registry.loadAsset<BeadsSound>("sfx_game_pickup3", "sounds/game/pickup3.wav")
        registry.loadAsset<BeadsSound>("sfx_game_pickup_stack", "sounds/game/pickup_stack.wav")
        registry.loadAsset<BeadsSound>("sfx_game_place", "sounds/game/place.wav")
        registry.loadAsset<BeadsSound>("sfx_game_whoosh1", "sounds/game/whoosh1.wav")
        registry.loadAsset<BeadsSound>("sfx_game_whoosh2", "sounds/game/whoosh2.wav")
        registry.loadAsset<BeadsSound>("sfx_game_whoosh3", "sounds/game/whoosh3.wav")
        registry.loadAsset<BeadsSound>("sfx_game_won", "sounds/game/win.ogg")

        registry.loadAsset<BeadsSound>("sfx_game_foundation_widget", "sounds/game/flick.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_foundation_scale_0", "sounds/game/note_C3.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_foundation_scale_1", "sounds/game/note_D3.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_foundation_scale_2", "sounds/game/note_E3.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_foundation_scale_3", "sounds/game/note_F3.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_foundation_scale_4", "sounds/game/note_G3.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_foundation_scale_5", "sounds/game/note_A3.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_foundation_scale_6", "sounds/game/note_B3.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_foundation_scale_7", "sounds/game/note_C4.ogg")
        
        registry.loadAsset<BeadsSound>("music_gameplay_stem_drums", "music/gameplay/stem_drums.ogg")
        registry.loadAsset<BeadsSound>("music_gameplay_stem_keys", "music/gameplay/stem_keys.ogg")
        registry.loadAsset<BeadsSound>("music_gameplay_stem_lead", "music/gameplay/stem_lead.ogg")
        registry.loadAsset<BeadsSound>("music_gameplay_stem_side", "music/gameplay/stem_side.ogg")
    }

    private fun addCardTextures(skin: CardSkin, subdir: String, linearFiltering: Boolean) {
        fun CardAssetKey.loadTexture() {
            assetRegistryInstance.loadAsset(
                this.getAssetKey(skin),
                "textures/game/${subdir}/${this.skinlessAssetKey}.png",
                if (linearFiltering) linearTexture() else null
            )
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
        CardAssetKey.Silhouette.loadTexture()
        CardAssetKey.Hover.loadTexture()
        CardAssetKey.CardCursorArrow.loadTexture()
        CardAssetKey.CardCursorArrowPressed.loadTexture()
    }
}