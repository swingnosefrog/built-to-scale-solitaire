package com.swingnosefrog.solitaire.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.TextureLoader
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.Texture
import com.swingnosefrog.solitaire.soundsystem.beads.BeadsMusic
import com.swingnosefrog.solitaire.soundsystem.beads.BeadsMusicLoader
import com.swingnosefrog.solitaire.soundsystem.beads.BeadsSound
import com.swingnosefrog.solitaire.soundsystem.beads.BeadsSoundLoader
import paintbox.registry.AssetRegistryInstance
import paintbox.registry.IAssetLoader


abstract class AssetLoaderBase<Registry : AssetRegistryInstance>(
    protected val assetRegistryInstance: Registry,
) : IAssetLoader {
    
    fun mipmappedLinearTexture() = TextureLoader.TextureParameter().apply {
        this.genMipMaps = true
        this.minFilter = Texture.TextureFilter.MipMapLinearLinear
        this.magFilter = Texture.TextureFilter.Linear
    }
    
    override fun addManagedAssets(manager: AssetManager) {
        manager.setLoader(BeadsSound::class.java, BeadsSoundLoader(InternalFileHandleResolver()))
        manager.setLoader(BeadsMusic::class.java, BeadsMusicLoader(InternalFileHandleResolver()))
    }

    override fun addUnmanagedAssets(assets: MutableMap<String, Any>) {
    }
}