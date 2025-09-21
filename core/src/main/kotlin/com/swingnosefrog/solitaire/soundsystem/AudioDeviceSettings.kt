package com.swingnosefrog.solitaire.soundsystem

import paintbox.util.SystemUtils


data class AudioDeviceSettings(val bufferSize: Int, val bufferCount: Int) {

    companion object {

        const val MINIMUM_BUFFER_SIZE: Int = 256
        const val MINIMUM_BUFFER_COUNT: Int = 3

        const val DEFAULT_BUFFER_SIZE: Int = 1024
        const val DEFAULT_BUFFER_COUNT: Int = 11

        const val DEFAULT_BUFFER_SIZE_WINDOWS: Int = 1024
        const val DEFAULT_BUFFER_COUNT_WINDOWS: Int = 9

        val DEFAULT_SETTINGS: AudioDeviceSettings = AudioDeviceSettings(getDefaultBufferSize(), getDefaultBufferCount())

        private fun isWindows(): Boolean = SystemUtils.isWindows()

        fun getDefaultBufferSize(): Int {
            return if (isWindows()) DEFAULT_BUFFER_SIZE_WINDOWS else DEFAULT_BUFFER_SIZE
        }

        fun getDefaultBufferCount(): Int {
            return if (isWindows()) DEFAULT_BUFFER_COUNT_WINDOWS else DEFAULT_BUFFER_COUNT
        }
    }
}