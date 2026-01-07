package com.swingnosefrog.solitaire

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import paintbox.Paintbox
import paintbox.binding.ReadOnlyVar
import paintbox.i18n.LocalePickerBase
import paintbox.i18n.LocalizationBase
import paintbox.i18n.LocalizationGroup


object SolitaireLocalePicker : LocalePickerBase(
    if (Solitaire.isDevVersion)
        Gdx.files.internal("localization/langs_dev.json")
    else Gdx.files.internal("localization/langs.json")
) {
    
    init {
        if (Solitaire.isDevVersion) {
            checkForLocaleOrder()
        }
    }
    
    private fun checkForLocaleOrder() {
        val loggingTag = "SolitaireLocalePicker"
        val allLocales = this.namedLocales
        
        val firstNamedLocale = allLocales.first()
        if (firstNamedLocale.locale.toString() != "") {
            Paintbox.LOGGER.warn("First locale isn't blank: got \'${firstNamedLocale.locale}\' (${firstNamedLocale.name})", tag = loggingTag)
        }
        
        val tail = allLocales.drop(1)
        if (tail.size >= 2) {
            val tailInOrder = tail.sortedBy { it.name }
            if (tail != tailInOrder) {
                Paintbox.LOGGER.warn("Non-default ${tail.size} locales aren't in expected name order. Expected to be: ${tailInOrder.map { it.toString() }}", tag = loggingTag)
            }
        }
    }
}

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
