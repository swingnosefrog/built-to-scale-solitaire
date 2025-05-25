package com.swingnosefrog.solitaire.menu

import com.swingnosefrog.solitaire.Localization
import com.swingnosefrog.solitaire.screen.main.menu.AbstractMenu
import paintbox.binding.BooleanVar
import paintbox.binding.ReadOnlyVar
import paintbox.binding.Var
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
            val useDefaultNextMenuOption = menuInput.source == MenuInputSource.KEYBOARD
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
            val stringVarConverter: StringVarConverter<T> = StringVarConverter.createDefaultConverter()
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
            
            private fun selectNext(indexChange: Int) {
                val list = options.getOrCompute()
                if (list.size >= 2) {
                    val currentIndex = list.indexOf(selectedOption.getOrCompute())
                    val nextIndex = MathHelper.indexWraparound(currentIndex, indexChange, list.size)
                    selectedOption.set(list[nextIndex])
                }
            }
        }
        
        val isSelected: BooleanVar = BooleanVar(false)

        override fun onSelect(controller: MenuController, menuInput: MenuInput) {
            if (!isSelected.get()) {
                isSelected.set(true)
            }
        }

        override fun onBack(controller: MenuController, menuInput: MenuInput) {
            if (isSelected.get()) {
                isSelected.set(false)
            }
        }
    }


    abstract fun onSelect(controller: MenuController, menuInput: MenuInput)

    open fun onBack(controller: MenuController, menuInput: MenuInput) {}
    open fun onLeft(controller: MenuController, menuInput: MenuInput) {}
    open fun onRight(controller: MenuController, menuInput: MenuInput) {}

}