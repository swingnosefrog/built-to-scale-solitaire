package com.swingnosefrog.solitaire.statistics.formatter

import paintbox.binding.GenericVar
import paintbox.binding.ReadOnlyIntVar
import paintbox.binding.ReadOnlyVar
import paintbox.util.DecimalFormats

object DurationMsStatFormatter : StatFormatter {

    override fun format(value: ReadOnlyIntVar): ReadOnlyVar<String> {
        return GenericVar {
            val elapsedMs = value.use()
            val elapsedSecondsInt = elapsedMs / 1000
            val elapsedMinutesPart = elapsedSecondsInt / 60
            val elapsedSecondsPart = elapsedSecondsInt % 60
            val elapsedCentisecondsPart = (elapsedMs / 10) % 100
            
            val decimalFormat2Digit = DecimalFormats["00"]
            val decimalSeparator = decimalFormat2Digit.decimalFormatSymbols.decimalSeparator
            val clockPortion =
                "${decimalFormat2Digit.format(elapsedMinutesPart)}:${decimalFormat2Digit.format(elapsedSecondsPart)}[scale=0.75]${decimalSeparator}${decimalFormat2Digit.format(elapsedCentisecondsPart)}[]"

            clockPortion
        }
    }
}