package com.swingnosefrog.solitaire.statistics

import com.swingnosefrog.solitaire.statistics.formatter.StatFormatter
import paintbox.binding.IntVar
import paintbox.binding.ReadOnlyIntVar
import java.util.concurrent.CopyOnWriteArrayList


class Stat(
    val id: String, val formatter: StatFormatter,
    val initialValue: Int = 0,
    val resetValue: Int = initialValue,
    val localizationId: String = "statistics.name.$id"
) {

    val value: ReadOnlyIntVar 
        field = IntVar(initialValue)

    val triggers: MutableList<StatTrigger> = CopyOnWriteArrayList()

    /**
     * Increments this stat by the given amount, and runs any [triggers]. If amount is non-positive, nothing changes.
     */
    fun increment(amount: Int = 1): Int {
        val oldValue = value.get()
        if (amount <= 0) return oldValue
        val newValue = value.incrementAndGetBy(amount)
        triggers.forEach { it.onIncremented(this, oldValue, newValue) }
        return newValue
    }

    /**
     * Sets the value of the stat to the given amount WITHOUT running any [triggers]. Used for persistence and resets.
     */
    fun setValue(newAmount: Int) {
        value.set(newAmount)
    }
}
