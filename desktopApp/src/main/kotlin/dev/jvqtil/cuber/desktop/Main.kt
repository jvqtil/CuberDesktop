package dev.jvqtil.cuber.desktop

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.jvqtil.cuber.desktop.database.DesktopDatabase
import dev.jvqtil.cuber.desktop.database.SolveRepository
import dev.jvqtil.cuber.desktop.ui.DesktopApp
import dev.jvqtil.cuber.desktop.ui.theme.DesktopTheme
import dev.jvqtil.cuber.desktop.ui.DesktopInput
import java.awt.KeyboardFocusManager
import java.awt.event.KeyEvent

fun main() = application {
    val windowState = rememberWindowState(
        width = 1200.dp,
        height = 760.dp,
    )
    val database = remember { DesktopDatabase.create() }
    val controller = remember {
        CuberController(
            SolveRepository(database.solveDao())
        )
    }
    val icon = painterResource("cuber-logo.svg")
    val input = remember { DesktopInput() }

    Window(
        onCloseRequest = {
            controller.close()
            database.close()
            exitApplication()
        },
        title = "Cuber",
        icon = icon,
        state = windowState,
        resizable = true,
        focusable = true,
    ) {
        val darkTheme = isSystemInDarkTheme()

        LaunchedEffect(Unit) {
            NativeWindow.configure(window, darkTheme)
            window.background = java.awt.Color(0x12, 0x12, 0x12)
            window.toFront()
            window.requestFocus()
        }

        LaunchedEffect(darkTheme) {
            NativeWindow.configure(window, darkTheme)
            window.background = if (darkTheme) {
                java.awt.Color(0x12, 0x12, 0x12)
            } else {
                java.awt.Color(0xF3, 0xF3, 0xF3)
            }
        }

        DisposableEffect(Unit) {
            val manager =
                KeyboardFocusManager.getCurrentKeyboardFocusManager()

            val dispatcher = java.awt.KeyEventDispatcher { event ->
                if (event.component == null) {
                    return@KeyEventDispatcher false
                }

                val activeWindow = manager.activeWindow

                if (activeWindow !== window) {
                    return@KeyEventDispatcher false
                }

                if (event.keyCode != KeyEvent.VK_ESCAPE) {
                    return@KeyEventDispatcher false
                }

                when (event.id) {
                    KeyEvent.KEY_PRESSED -> {
                        input.handleEscape(true)
                        true
                    }

                    KeyEvent.KEY_RELEASED -> {
                        input.handleEscape(false)
                        true
                    }

                    else -> false
                }
            }

            manager.addKeyEventDispatcher(dispatcher)

            onDispose {
                manager.removeKeyEventDispatcher(dispatcher)
                NativeWindow.reset(window)
            }
        }

        DesktopTheme {
            DesktopApp(
                controller = controller,
                input = input,
            )
        }
    }
}
