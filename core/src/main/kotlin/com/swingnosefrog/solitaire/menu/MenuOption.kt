package com.swingnosefrog.solitaire.menu

import com.swingnosefrog.solitaire.localization.Localization
import com.swingnosefrog.solitaire.screen.main.menu.AbstractMenu
import paintbox.binding.*
import paintbox.ui.StringVarConverter
import paintbox.util.MathHelper


sealed class MenuOption(
    val text: ReadOnlyVar<String>,
) {

    companion object {

        fun createNoOp(text: ReadOnlyVar<String>): MenuOption {
            return Simple(text) {}
        }
    }
    
    class Separator() : MenuOption("".toConstVar()) {

        init {
            this.disabled.set(true)
        }
        
        override fun onSelect(controller: MenuController, menuInput: MenuInput) {
        }
    }

    class Simple(
        text: ReadOnlyVar<String>,
        private val onSelectAction: (MenuController) -> Unit,
    ) : MenuOption(text) {

        override fun onSelect(controller: MenuController, menuInput: MenuInput) {
            onSelectAction(controller)
        }
    }

    class SubMenu(
        text: ReadOnlyVar<String>,
        private val nextMenuGetter: (MenuController) -> AbstractMenu,
    ) : MenuOption(text) {

        override fun onSelect(controller: MenuController, menuInput: MenuInput) {
            val useDefaultNextMenuOption = menuInput.source == MenuInputSource.KEYBOARD_OR_BUTTON
            controller.goToNextMenu(nextMenuGetter(controller), useDefaultNextMenuOption)
        }
    }

    class Back(
        text: ReadOnlyVar<String> = Localization["common.backOutOfMenu"],
        private val callback: (MenuController) -> Unit = {},
    ) : MenuOption(text) {

        override fun onSelect(controller: MenuController, menuInput: MenuInput) {
            controller.backOutOfMenu()
            callback(controller)
        }
    }

    sealed class OptionWidget(text: ReadOnlyVar<String>) : MenuOption(text) {

        class Cycle<T>(
            text: ReadOnlyVar<String>,
            val options: ReadOnlyVar<List<T>>,
            val selectedOption: Var<T>,
            val stringVarConverter: StringVarConverter<T> = StringVarConverter.createDefaultConverter(),
        ) : OptionWidget(text) {

            override fun onLeft(controller: MenuController, menuInput: MenuInput) {
                if (isSelected.get()) {
                    selectNext(-1)
                }
            }

            override fun onRight(controller: MenuController, menuInput: MenuInput) {
                if (isSelected.get()) {
                    selectNext(+1)
                }
            }

            fun selectNext(indexChange: Int) {
                val list = options.getOrCompute()
                if (list.size >= 2) {
                    val currentIndex = list.indexOf(selectedOption.getOrCompute())
                    val nextIndex = MathHelper.indexWraparound(currentIndex, indexChange, list.size)
                    selectedOption.set(list[nextIndex])
                }
            }
        }

        class Checkbox(
            text: ReadOnlyVar<String>,
            val selectedState: BooleanVar,
        ) : OptionWidget(text) {

            override fun onSelect(controller: MenuController, menuInput: MenuInput) {
                selectedState.invert()
            }

            override fun onBack(controller: MenuController, menuInput: MenuInput) {
                // Intentionally don't toggle isSelected
            }
        }

        class Slider(
            text: ReadOnlyVar<String>,
            val minimum: ReadOnlyFloatVar,
            val maximum: ReadOnlyFloatVar,
            val tickUnit: ReadOnlyFloatVar,
            val value: FloatVar,
        ) : OptionWidget(text) {

            fun setValue(value: Float) {
                val tick = tickUnit.get().coerceAtLeast(0f)
                val snapped = if (tick > 0f) {
                    MathHelper.snapToNearest(value, tick)
                } else value
                this.value.set(snapped.coerceIn(minimum.get(), maximum.get()))
            }

            fun nudgeValue(direction: Int) {
                if (direction < 0) {
                    setValue(value.get() - tickUnit.get())
                } else if (direction > 0) {
                    setValue(value.get() + tickUnit.get())
                }
            }

            override fun onLeft(controller: MenuController, menuInput: MenuInput) {
                if (isSelected.get()) {
                    nudgeValue(-1)
                }
            }

            override fun onRight(controller: MenuController, menuInput: MenuInput) {
                if (isSelected.get()) {
                    nudgeValue(+1)
                }
            }
        }


        override fun onSelect(controller: MenuController, menuInput: MenuInput) {
            if (!isSelected.get()) {
                isSelected.set(true)
            } else {
                // Only deselect if not clicking on it (clicking has an action for cycle options)
                if (menuInput.source != MenuInputSource.MOUSE) {
                    isSelected.set(false)
                }
            }
        }

        override fun onBack(controller: MenuController, menuInput: MenuInput) {
            if (isSelected.get()) {
                isSelected.set(false)
            }
        }
    }


    val isSelected: BooleanVar = BooleanVar(false)
    val disabled: BooleanVar = BooleanVar(false)

    abstract fun onSelect(controller: MenuController, menuInput: MenuInput)

    open fun onBack(controller: MenuController, menuInput: MenuInput) {}
    open fun onLeft(controller: MenuController, menuInput: MenuInput) {}
    open fun onRight(controller: MenuController, menuInput: MenuInput) {}

}