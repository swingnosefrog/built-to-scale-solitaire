package com.swingnosefrog.solitaire.soundsystem.beads

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array
import com.swingnosefrog.solitaire.soundsystem.sample.GdxAudioReader


class BeadsSoundLoader(resolver: FileHandleResolver) : AsynchronousAssetLoader<BeadsSound, BeadsSoundLoader.BeadsSoundLoaderParam>(
    resolver
) {

    class BeadsSoundLoaderParam : AssetLoaderParameters<BeadsSound>()

    var sound: BeadsSound? = null

    override fun getDependencies(
        fileName: String?,
        file: FileHandle?,
        parameter: BeadsSoundLoaderParam?,
    ): Array<AssetDescriptor<Any>>? {
        return null
    }

    override fun loadAsync(
        manager: AssetManager,
        fileName: String,
        file: FileHandle,
        parameter: BeadsSoundLoaderParam?,
    ) {
        sound = GdxAudioReader.newSound(file)
    }

    override fun loadSync(
        manager: AssetManager,
        fileName: String,
        file: FileHandle,
        parameter: BeadsSoundLoaderParam?,
    ): BeadsSound? {
        val s = sound
        sound = null
        return s
    }
}

class BeadsMusicLoader(resolver: FileHandleResolver) : AsynchronousAssetLoader<BeadsMusic, BeadsMusicLoader.BeadsMusicLoaderParam>(
    resolver
) {

    class BeadsMusicLoaderParam : AssetLoaderParameters<BeadsMusic>()

    private var music: BeadsMusic? = null

    override fun getDependencies(
        fileName: String?,
        file: FileHandle?,
        parameter: BeadsMusicLoaderParam?,
    ): Array<AssetDescriptor<Any>>? {
        return null
    }

    override fun loadAsync(
        manager: AssetManager,
        fileName: String,
        file: FileHandle,
        parameter: BeadsMusicLoaderParam?,
    ) {
        music = GdxAudioReader.newMusic(file)
    }

    override fun loadSync(
        manager: AssetManager,
        fileName: String,
        file: FileHandle,
        parameter: BeadsMusicLoaderParam?,
    ): BeadsMusic? {
        val s = music
        music = null
        return s
    }
}
