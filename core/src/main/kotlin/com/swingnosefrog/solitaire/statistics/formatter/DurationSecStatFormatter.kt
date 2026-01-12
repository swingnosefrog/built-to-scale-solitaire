package com.swingnosefrog.solitaire.statistics.formatter

import com.swingnosefrog.solitaire.localization.Localization
import paintbox.binding.ReadOnlyIntVar
import paintbox.binding.ReadOnlyVar
import paintbox.binding.Var
import paintbox.i18n.ILocalization
import paintbox.util.DecimalFormats

class DurationSecStatFormatter(
    val localizationKey: String,
    val localizationBase: ILocalization = Localization,
) : StatFormatter {

    companion object {

        val DEFAULT: DurationSecStatFormatter = DurationSecStatFormatter("statistics.formatter.duration", Localization)
    }

    override fun format(value: ReadOnlyIntVar): ReadOnlyVar<String> {
        return localizationBase.getVar(localizationKey, Var {
            val totalSeconds = value.use()
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds / 60) % 60
            val seconds = totalSeconds % 60
            val decimalFormat = DecimalFormats["00"]
            listOf(
                decimalFormat.format(hours.toLong()),
                decimalFormat.format(minutes.toLong()),
                decimalFormat.format(seconds.toLong())
            )
        })
    }
}