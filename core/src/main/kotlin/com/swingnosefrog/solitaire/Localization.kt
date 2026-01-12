package com.swingnosefrog.solitaire

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.swingnosefrog.solitaire.credits.LocalizationCredits
import paintbox.Paintbox
import paintbox.binding.ReadOnlyVar
import paintbox.i18n.LocalePickerBase
import paintbox.i18n.LocalizationBase
import paintbox.i18n.LocalizationGroup
import paintbox.i18n.NamedLocale
import java.util.Locale


object SolitaireLocalePicker : LocalePickerBase(
    if (Solitaire.isDevVersion)
        Gdx.files.internal("localization/langs_dev.json")
    else Gdx.files.internal("localization/langs.json")
) {
    
    const val METADATA_KEY_STEAM_API_LANGUAGE_CODE: String = "steam:api_language_code"
    const val METADATA_KEY_CREDITS_PRIMARY: String = "credits:primary"
    const val METADATA_KEY_CREDITS_SECONDARY: String = "credits:secondary"
    
    private val namedLocalesBySteamApiLanguageCode: Map<String, List<NamedLocale>>
    private val namedLocaleCredits: Map<NamedLocale, LocalizationCredits>
    
    init {
        namedLocalesBySteamApiLanguageCode = this.namedLocales
            .filter { it.metadata.containsKey(METADATA_KEY_STEAM_API_LANGUAGE_CODE) }
            .groupBy { it.metadata.getValue(METADATA_KEY_STEAM_API_LANGUAGE_CODE) }
            .mapValues { (_, list) ->
                list.sortedBy { it.locale.toString() }
            }
        namedLocaleCredits = this.namedLocales.associateWith {
            LocalizationCredits(
                it.metadata[METADATA_KEY_CREDITS_PRIMARY]?.parseCreditsString() ?: emptyList(),
                it.metadata[METADATA_KEY_CREDITS_SECONDARY]?.parseCreditsString() ?: emptyList()
            )
        }
        
        if (Solitaire.isDevVersion) {
            checkForLocaleOrder()
        }
    }
    
    fun getCredits(namedLocale: NamedLocale): LocalizationCredits =
        namedLocaleCredits.getOrElse(namedLocale) { LocalizationCredits(listOf("<MISSING>"), emptyList()) }
    
    fun getNamedLocaleFromSteamLanguageCode(steamLanguageCode: String): NamedLocale? {
        return namedLocalesBySteamApiLanguageCode[steamLanguageCode]?.firstOrNull()
    }
    
    fun getFallbackNamedLocale(): NamedLocale = this.namedLocales.first()
    
    private fun checkForLocaleOrder() {
        val loggingTag = "SolitaireLocalePicker"
        val allLocales = this.namedLocales
        
        val firstNamedLocale = allLocales.first()
        if (firstNamedLocale.locale.toString() != "") {
            Paintbox.LOGGER.error("First locale isn't blank: got \'${firstNamedLocale.locale}\' (${firstNamedLocale.name})", tag = loggingTag)
        }
        
        val tail = allLocales.drop(1)
        if (tail.size >= 2) {
            val tailInOrder = tail.sortedBy { it.name }
            if (tail != tailInOrder) {
                Paintbox.LOGGER.error("Non-default ${tail.size} locales aren't in expected name order. Expected to be: ${tailInOrder.map { it.toString() }}", tag = loggingTag)
            }
            
            val missingPrimaryCredits = tail.filter { !namedLocaleCredits.containsKey(it) }
            if (missingPrimaryCredits.isNotEmpty()) {
                Paintbox.LOGGER.error("Locales missing primary credits: ${missingPrimaryCredits.map { it.toString() }}", tag = loggingTag)
            }
        }
    }

    private fun String.parseCreditsString(): List<String> {
        return this.split('\t')
            .filterNot { it.isBlank() }
            .sortedBy { it.lowercase(Locale.ROOT) }
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
