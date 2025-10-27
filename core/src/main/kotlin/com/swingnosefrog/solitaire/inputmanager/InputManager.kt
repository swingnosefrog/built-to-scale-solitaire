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
    
    private val glyphVarEmpty: ReadOnlyVar<List<IActionInputGlyph>> = ReadOnlyVar.const(emptyList())
    private val actionsToGlyphsVars: Map<IInputAction, Var<List<IActionInputGlyph>>> = actions.associateWith {
        Var(emptyList())
    }

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
                for (listener in listeners) {
                    val handled = listener.handleActionSourceChanged(mostRecentSource, newSource)
                    if (handled) break
                }
            }

            for (src in sources) {
                val actionsTracker = actionsTrackers.getValue(src)
                val diff = actionsTracker.differencesReturnMap
                if (diff.isEmpty()) {
                    continue
                }

                diff.forEach { (digitalAction, value) ->
                    for (l in listeners) {
                        val handled = if (value) {
                            l.handleDigitalActionPressed(src, digitalAction)
                        } else {
                            l.handleDigitalActionReleased(src, digitalAction)
                        }
                        if (handled) break
                    }
                }
            }
        }
        
        actions.forEach { action ->
            actionsToGlyphsVars.getValue(action).set(getCurrentGlyphsForAction(action))
        }
    }

    fun isDigitalActionPressed(action: IDigitalInputAction): Boolean {
        return sources.any { s -> s.isDigitalActionPressed(action) }
    }

    fun getCurrentGlyphsForAction(action: IInputAction): List<IActionInputGlyph> {
        return _mostRecentSource.getOrCompute().getGlyphsForAction(action)
    }

    fun getGlyphsVarForAction(action: IInputAction): ReadOnlyVar<List<IActionInputGlyph>> {
        return actionsToGlyphsVars[action] ?: glyphVarEmpty
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