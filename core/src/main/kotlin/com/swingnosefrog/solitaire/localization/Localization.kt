package com.swingnosefrog.solitaire.localization

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import paintbox.binding.ReadOnlyVar
import paintbox.i18n.LocalizationBase
import paintbox.i18n.LocalizationGroup


abstract class SolitaireLocalizationBase(baseHandle: FileHandle) : LocalizationBase(baseHandle, SolitaireLocalePicker)


private object BaseLocalization
    : SolitaireLocalizationBase(Gdx.files.internal("localization/default"))

object Localization : LocalizationGroup(
    SolitaireLocalePicker,
    listOf(
        BaseLocalization,
    )
) {
    
    operator fun get(key: String): ReadOnlyVar<String> {
        return this.getVar(key)
    }

    operator fun get(key: String, argsProvider: ReadOnlyVar<List<Any?>>): ReadOnlyVar<String> {
        return this.getVar(key, argsProvider)
    }

    operator fun get(key: String, staticArgs: List<Any?>): ReadOnlyVar<String> {
        return this.getVar(key, staticArgs)
    }
}
