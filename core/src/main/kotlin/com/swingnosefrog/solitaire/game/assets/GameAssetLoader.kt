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

        registry.loadAsset<BeadsSound>("sfx_game_widget_assemble", "sounds/game/widget_assemble_2.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_foundation_finish", "sounds/game/foundation_finish.ogg")
        
        registry.loadAsset<BeadsSound>("sfx_game_note_A2", "sounds/game/notes/note_A2.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_note_A3", "sounds/game/notes/note_A3.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_note_As2", "sounds/game/notes/note_As2.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_note_As3", "sounds/game/notes/note_As3.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_note_B2", "sounds/game/notes/note_B2.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_note_B3", "sounds/game/notes/note_B3.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_note_C3", "sounds/game/notes/note_C3.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_note_C4", "sounds/game/notes/note_C4.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_note_Cs3", "sounds/game/notes/note_Cs3.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_note_Cs4", "sounds/game/notes/note_Cs4.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_note_D3", "sounds/game/notes/note_D3.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_note_D4", "sounds/game/notes/note_D4.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_note_Ds3", "sounds/game/notes/note_Ds3.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_note_Ds4", "sounds/game/notes/note_Ds4.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_note_E3", "sounds/game/notes/note_E3.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_note_E4", "sounds/game/notes/note_E4.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_note_F3", "sounds/game/notes/note_F3.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_note_F4", "sounds/game/notes/note_F4.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_note_Fs3", "sounds/game/notes/note_Fs3.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_note_Fs4", "sounds/game/notes/note_Fs4.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_note_G2", "sounds/game/notes/note_G2.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_note_G3", "sounds/game/notes/note_G3.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_note_G4", "sounds/game/notes/note_G4.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_note_Gs2", "sounds/game/notes/note_Gs2.ogg")
        registry.loadAsset<BeadsSound>("sfx_game_note_Gs3", "sounds/game/notes/note_Gs3.ogg")
        
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