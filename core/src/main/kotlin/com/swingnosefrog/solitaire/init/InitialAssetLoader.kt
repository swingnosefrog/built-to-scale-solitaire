package com.swingnosefrog.solitaire.init

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.Texture
import com.swingnosefrog.solitaire.soundsystem.BeadsMusic
import com.swingnosefrog.solitaire.soundsystem.BeadsMusicLoader
import com.swingnosefrog.solitaire.soundsystem.BeadsSound
import com.swingnosefrog.solitaire.soundsystem.BeadsSoundLoader
import paintbox.registry.AssetRegistry
import paintbox.registry.IAssetLoader


class InitialAssetLoader : IAssetLoader {

    override fun addManagedAssets(manager: AssetManager) {
        manager.setLoader(BeadsSound::class.java, BeadsSoundLoader(InternalFileHandleResolver()))
        manager.setLoader(BeadsMusic::class.java, BeadsMusicLoader(InternalFileHandleResolver()))


    }

    override fun addUnmanagedAssets(assets: MutableMap<String, Any>) {
        AssetRegistry.bindAsset("loading_icon_rod", "textures/loading/rod_loading.png").let { (key, filename) ->
            assets[key] = Texture(filename)
        }
    }
}