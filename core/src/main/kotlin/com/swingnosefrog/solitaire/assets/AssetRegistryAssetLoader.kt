package com.swingnosefrog.solitaire.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import paintbox.registry.AssetRegistry


class AssetRegistryAssetLoader : AssetLoaderBase<AssetRegistry>(AssetRegistry) {

    override fun addManagedAssets(manager: AssetManager) {
        super.addManagedAssets(manager)

    }

    override fun addUnmanagedAssets(assets: MutableMap<String, Any>) {
        super.addUnmanagedAssets(assets)

        assetRegistryInstance.bindAsset("loading_icon_rod", "textures/loading/rod_loading.png").let { (key, filename) ->
            assets[key] = Texture(filename)
        }
    }
}