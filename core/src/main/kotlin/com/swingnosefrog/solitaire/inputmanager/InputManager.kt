package com.swingnosefrog.solitaire.inputmanager

import paintbox.binding.ReadOnlyVar
import paintbox.binding.Var
import java.util.concurrent.CopyOnWriteArrayList


class InputManager(
    val actions: List<IInputAction>,
    val sources: List<ActionSource>,
) {

    init {
        require(actions.isNotEmpty()) { "At least one input action must be provided." }
        require(sources.isNotEmpty()) { "At least one action source must be provided." }
    }

    private val allDigitalActions: List<IDigitalInputAction> = actions.filterIsInstance<IDigitalInputAction>()

    private val actionsTrackers: Map<ActionSource, ActionsTracker> = sources.associateWith { ActionsTracker(it) }

    private val listeners: CopyOnWriteArrayList<InputActionListener> = CopyOnWriteArrayList()

    private val _mostRecentSource: Var<ActionSource> = Var(sources.first())
    val mostRecentActionSource: ReadOnlyVar<ActionSource> = Var { _mostRecentSource.use() }

    fun frameUpdate() {
        val mostRecentSource = _mostRecentSource.getOrCompute()
        var anyDifferences = false
        var didAnyRecentSourceHaveDifferences = false
        var didAnyNonRecentSourceHaveDifferences = false
        sources.forEach { src ->
            val actionsTracker = actionsTrackers.getValue(src)

            src.frameUpdate()
            val differences = actionsTracker.checkForDifferences()
            if (differences) {
                anyDifferences = true
                if (src != mostRecentSource) {
                    didAnyNonRecentSourceHaveDifferences = true
                } else {
                    didAnyRecentSourceHaveDifferences = true
                }
            }
        }

        if (anyDifferences) {
            if (didAnyNonRecentSourceHaveDifferences && !didAnyRecentSourceHaveDifferences) {
                val newSource = sources.first { src ->
                    src != mostRecentSource && actionsTrackers.getValue(src).differencesReturnMap.isNotEmpty()
                }
                _mostRecentSource.set(newSource)
                listeners.forEach { listener ->
                    listener.onActionSourceChanged(mostRecentSource, newSource)
                }
            }

            for (src in sources) {
                val actionsTracker = actionsTrackers.getValue(src)
                val diff = actionsTracker.differencesReturnMap
                if (diff.isEmpty()) {
                    continue
                }

                diff.forEach { (digitalAction, value) ->
                    listeners.forEach { l ->
                        if (value) {
                            l.onDigitalActionPressed(src, digitalAction)
                        } else {
                            l.onDigitalActionReleased(src, digitalAction)
                        }
                    }
                }
            }
        }
    }

    fun isDigitalActionPressed(action: IDigitalInputAction): Boolean {
        return sources.any { s -> s.isDigitalActionPressed(action) }
    }

    fun getCurrentGlyphsForAction(action: IInputAction): List<IActionInputGlyph> {
        return _mostRecentSource.getOrCompute().getGlyphsForAction(action)
    }

    fun addInputActionListener(listener: InputActionListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun removeInputActionListener(listener: InputActionListener) {
        listeners.remove(listener)
    }

    fun getAllInputActionListeners(): List<InputActionListener> = listeners.toList()

    
    private inner class ActionsTracker(private val source: ActionSource) {

        private val digitalState: MutableMap<IDigitalInputAction, Boolean> = mutableMapOf()

        val differencesReturnMap: MutableMap<IDigitalInputAction, Boolean> = mutableMapOf()

        init {
            allDigitalActions.forEach { action ->
                digitalState[action] = source.isDigitalActionPressed(action)
            }
        }

        fun checkForDifferences(): Boolean {
            differencesReturnMap.clear()

            allDigitalActions.forEach { action ->
                val isPressed = source.isDigitalActionPressed(action)
                val old = digitalState.getValue(action)

                if (isPressed != old) {
                    differencesReturnMap[action] = isPressed
                    digitalState[action] = isPressed
                }
            }

            return differencesReturnMap.isNotEmpty()
        }
    }
}