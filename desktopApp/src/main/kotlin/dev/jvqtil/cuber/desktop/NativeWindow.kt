package dev.jvqtil.cuber.desktop

import com.sun.jna.Library
import com.sun.jna.Memory
import com.sun.jna.Native
import com.sun.jna.Pointer
import java.awt.Window

object NativeWindow {
    fun configure(window: Window, dark: Boolean) {
        if (isWindows()) {
            setDarkTitleBar(window, dark)
        }
    }

    fun reset(window: Window) {
        if (isWindows()) {
            setDarkTitleBar(window, false)
        }
    }

    private fun isWindows(): Boolean =
        System.getProperty("os.name").orEmpty().contains("Windows", ignoreCase = true)

    private fun setDarkTitleBar(window: Window, enabled: Boolean) {
        runCatching {
            val value = Memory(4).apply {
                setInt(0, if (enabled) 1 else 0)
            }
            try {
                val hwnd = Native.getWindowPointer(window)
                val api = DwmApi.INSTANCE
                var result = api.DwmSetWindowAttribute(hwnd, 20, value, 4)
                if (result != 0) {
                    result = api.DwmSetWindowAttribute(hwnd, 19, value, 4)
                }
                result == 0
            } finally {
                value.close()
            }
        }
    }

    private interface DwmApi : Library {
        fun DwmSetWindowAttribute(
            hwnd: Pointer,
            attribute: Int,
            value: Memory,
            valueSize: Int,
        ): Int

        companion object {
            val INSTANCE: DwmApi = Native.load(
                "dwmapi",
                DwmApi::class.java,
            )
        }
    }
}
