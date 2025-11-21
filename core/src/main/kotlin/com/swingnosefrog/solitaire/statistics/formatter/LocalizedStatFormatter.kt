package com.swingnosefrog.solitaire.statistics.formatter

import com.swingnosefrog.solitaire.Localization
import paintbox.binding.ReadOnlyIntVar
import paintbox.binding.ReadOnlyVar
import paintbox.binding.Var
import paintbox.i18n.ILocalization

open class LocalizedStatFormatter(
    val localizationKey: String,
    val localizationBase: ILocalization = Localization,
) : StatFormatter {

    companion object {

        val DEFAULT: LocalizedStatFormatter = LocalizedStatFormatter("statistics.formatter.default", Localization)
        val MOVES: LocalizedStatFormatter = LocalizedStatFormatter("statistics.formatter.moves", Localization)
    }

    override fun format(value: ReadOnlyIntVar): ReadOnlyVar<String> {
        return localizationBase.getVar(localizationKey, Var { listOf(value.use()) })
    }
}