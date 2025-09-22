package com.swingnosefrog.solitaire.assets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Cursor
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import paintbox.registry.AssetRegistry


class AssetRegistryAssetLoader : AssetLoaderBase<AssetRegistry>(AssetRegistry) {

    override fun addManagedAssets(manager: AssetManager) {
        super.addManagedAssets(manager)

        val assetRegistry = assetRegistryInstance
        
        assetRegistry.loadAsset<Texture>("ui_nut_icon", "textures/ui/nut_icon.png", linearTexture())
        assetRegistry.loadAsset<Texture>("ui_x", "textures/ui/x.png", linearTexture())
        assetRegistry.loadAsset<Texture>("ui_x_bordered", "textures/ui/x_bordered.png", linearTexture())
    }

    override fun addUnmanagedAssets(assets: MutableMap<String, Any>) {
        super.addUnmanagedAssets(assets)
        
        fun createCursor(filename: String, xHotspot: Int, yHotspot: Int): Cursor {
            val pixmap = Pixmap(Gdx.files.internal(filename))
            val cursor = Gdx.graphics.newCursor(pixmap, xHotspot, yHotspot)
            pixmap.dispose()
            return cursor
        }

        assetRegistryInstance.bindAsset("cursor_normal", "cursors/cursor_normal.png").let { (key, filename) ->
            assets[key] = createCursor(filename, 0, 0)
        }
        assetRegistryInstance.bindAsset("cursor_normal_pressed", "cursors/cursor_normal_pressed.png").let { (key, filename) ->
            assets[key] = createCursor(filename, 0, 0)
        }
        assetRegistryInstance.bindAsset("cursor_normal_2x", "cursors/cursor_normal_2x.png").let { (key, filename) ->
            assets[key] = createCursor(filename, 0, 0)
        }
        assetRegistryInstance.bindAsset("cursor_normal_pressed_2x", "cursors/cursor_normal_pressed_2x.png").let { (key, filename) ->
            assets[key] = createCursor(filename, 0, 0)
        }
        assetRegistryInstance.bindAsset("cursor_normal_3x", "cursors/cursor_normal_3x.png").let { (key, filename) ->
            assets[key] = createCursor(filename, 0, 0)
        }
        assetRegistryInstance.bindAsset("cursor_normal_pressed_3x", "cursors/cursor_normal_pressed_3x.png").let { (key, filename) ->
            assets[key] = createCursor(filename, 0, 0)
        }
        
        assetRegistryInstance.bindAsset("loading_icon_rod", "textures/loading/rod_loading.png").let { (key, filename) ->
            assets[key] = Texture(filename)
        }
    }
}