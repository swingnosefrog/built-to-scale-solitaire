package com.swingnosefrog.solitaire.statistics.formatter

import paintbox.binding.ReadOnlyIntVar
import paintbox.binding.ReadOnlyVar
import paintbox.binding.Var


fun interface StatFormatter {

    companion object {

        val NO_FORMAT: StatFormatter = StatFormatter { value -> Var { "$value" } }
    }

    fun format(value: ReadOnlyIntVar): ReadOnlyVar<String>

}