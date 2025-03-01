package com.swingnosefrog.solitaire

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import paintbox.i18n.LocalePickerBase
import paintbox.i18n.LocalizationBase
import paintbox.i18n.LocalizationGroup


private object SolitaireLocalePicker : LocalePickerBase(Gdx.files.internal("localization/langs.json"))

abstract class SolitaireLocalizationBase(baseHandle: FileHandle) : LocalizationBase(baseHandle, SolitaireLocalePicker)


private object BaseLocalization
    : SolitaireLocalizationBase(Gdx.files.internal("localization/default"))

object Localization : LocalizationGroup(
    SolitaireLocalePicker,
    listOf(
        BaseLocalization,
    )
)
