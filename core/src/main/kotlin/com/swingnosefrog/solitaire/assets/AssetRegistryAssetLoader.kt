package com.swingnosefrog.solitaire.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import paintbox.registry.AssetRegistry


class AssetRegistryAssetLoader : AssetLoaderBase<AssetRegistry>(AssetRegistry) {

    override fun addManagedAssets(manager: AssetManager) {
        super.addManagedAssets(manager)

        val assetRegistry = assetRegistryInstance
        
        assetRegistry.loadAsset<Texture>("ui_nut_icon", "textures/ui/nut_icon.png", linearTexture())
    }

    override fun addUnmanagedAssets(assets: MutableMap<String, Any>) {
        super.addUnmanagedAssets(assets)

        assetRegistryInstance.bindAsset("loading_icon_rod", "textures/loading/rod_loading.png").let { (key, filename) ->
            assets[key] = Texture(filename)
        }
    }
}