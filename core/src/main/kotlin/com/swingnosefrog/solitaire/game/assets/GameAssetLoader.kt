package com.swingnosefrog.solitaire.game.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.TextureLoader
import com.badlogic.gdx.graphics.Texture
import com.swingnosefrog.solitaire.assets.AssetLoaderBase
import com.swingnosefrog.solitaire.game.CardSuit
import com.swingnosefrog.solitaire.game.CardSymbol
import com.swingnosefrog.solitaire.game.audio.music.Track.Default.Stems as DefaultStems
import com.swingnosefrog.solitaire.game.audio.music.Track.Practice.Stems as PracticeStems
import com.swingnosefrog.solitaire.soundsystem.beads.BeadsSound

class GameAssetLoader : AssetLoaderBase<GameAssets>(GameAssets) {

    override fun addManagedAssets(manager: AssetManager) {
        super.addManagedAssets(manager)
        
        fun mipmappedLinearTexture() = TextureLoader.TextureParameter().apply {
            this.genMipMaps = true
            this.minFilter = Texture.TextureFilter.MipMapLinearLinear
            this.magFilter = Texture.TextureFilter.Linear
        }

        val registry = assetRegistryInstance

        addCardTextures(CardSkin.MODERN, "modern", textureParameterFactory = {
            mipmappedLinearTexture()
        })
        addCardTextures(CardSkin.CLASSIC, "classic", textureParameterFactory = { null })

        registry.loadAsset<Texture>("vfx_slam_large", "textures/game/vfx/slam_large.png", mipmappedLinearTexture())
        registry.loadAsset<Texture>("vfx_slam_small", "textures/game/vfx/slam_small.png", mipmappedLinearTexture())
        
        registry.loadAsset<BeadsSound>("sfx_game_dealing_loop", "sounds/game/dealing_loop.wav")
        registry.loadAsset<BeadsSound>("sfx_game_reshuffle", "sounds/game/reshuffle.wav")
        registry.loadAsset<BeadsSound>("sfx_game_pickup1", "sounds/game/pickup1.wav")
        registry.loadAsset<BeadsSound>("sfx_game_pickup2", "sounds/game/pickup2.wav")
        registry.loadAsset<BeadsSound>("sfx_game_pickup3", "sounds/game/pickup3.wav")
        registry.loadAsset<BeadsSound>("sfx_game_pickup_stack", "sounds/game/pickup_stack.wav")
        registry.loadAsset<BeadsSound>("sfx_game_place", "sounds/game/place.wav")
        registry.loadAsset<BeadsSound>("sfx_game_whoosh1", "sounds/game/whoosh1.wav")
        registry.loadAsset<BeadsSound>("sfx_game_whoosh2", "sounds/game/whoosh2.wav")
        registry.loadAsset<BeadsSound>("sfx_game_whoosh3", "sounds/game/whoosh3.wav")
        registry.loadAsset<BeadsSound>("sfx_game_won", "sounds/game/win.ogg")

        registry.loadAsset<BeadsSound>("sfx_game_foundation_widget", "sounds/game/flick.wav")
        registry.loadAsset<BeadsSound>("sfx_game_foundation_scale_0", "sounds/game/note_C3.wav")
        registry.loadAsset<BeadsSound>("sfx_game_foundation_scale_1", "sounds/game/note_D3.wav")
        registry.loadAsset<BeadsSound>("sfx_game_foundation_scale_2", "sounds/game/note_E3.wav")
        registry.loadAsset<BeadsSound>("sfx_game_foundation_scale_3", "sounds/game/note_F3.wav")
        registry.loadAsset<BeadsSound>("sfx_game_foundation_scale_4", "sounds/game/note_G3.wav")
        registry.loadAsset<BeadsSound>("sfx_game_foundation_scale_5", "sounds/game/note_A3.wav")
        registry.loadAsset<BeadsSound>("sfx_game_foundation_scale_6", "sounds/game/note_B3.wav")
        registry.loadAsset<BeadsSound>("sfx_game_foundation_scale_7", "sounds/game/note_C4.wav")
        
        registry.loadAsset<BeadsSound>(DefaultStems.DRUMS.assetKey, "music/gameplay/stem_drums.ogg")
        registry.loadAsset<BeadsSound>(DefaultStems.KEYS.assetKey, "music/gameplay/stem_keys.ogg")
        registry.loadAsset<BeadsSound>(DefaultStems.LEAD.assetKey, "music/gameplay/stem_lead.ogg")
        registry.loadAsset<BeadsSound>(DefaultStems.SIDE.assetKey, "music/gameplay/stem_side.ogg")
        registry.loadAsset<BeadsSound>(PracticeStems.BASS.assetKey, "music/tutorial/stem_bass.ogg")
        registry.loadAsset<BeadsSound>(PracticeStems.DRUMS.assetKey, "music/tutorial/stem_drums.ogg")
        registry.loadAsset<BeadsSound>(PracticeStems.KEYS.assetKey, "music/tutorial/stem_keys.ogg")
        registry.loadAsset<BeadsSound>(PracticeStems.SIDE.assetKey, "music/tutorial/stem_side.ogg")
    }

    private fun addCardTextures(
        skin: CardSkin,
        subdir: String,
        textureParameterFactory: (CardAssetKey) -> TextureLoader.TextureParameter? = { null },
    ) {
        fun CardAssetKey.loadTexture() {
            assetRegistryInstance.loadAsset(
                this.getAssetKey(skin),
                "textures/game/${subdir}/${this.skinlessAssetKey}.png",
                textureParameterFactory(this)
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