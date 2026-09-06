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
import dev.jvqtil.cuber.desktop.ui.DesktopInput
import dev.jvqtil.cuber.desktop.ui.theme.DesktopTheme
import kotlinx.coroutines.delay
import java.awt.KeyboardFocusManager
import java.awt.event.KeyEvent
import kotlin.time.Duration.Companion.milliseconds

fun main() {
    if (System.getProperty("os.name").orEmpty().contains("mac", ignoreCase = true)) {
        System.setProperty("apple.awt.application.appearance", "system")
    }

    application {
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
        val icon = painterResource("cuber.png")
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
                delay(80.milliseconds)
                NativeWindow.configure(window, darkTheme)
                window.toFront()
                window.requestFocus()
            }

            LaunchedEffect(darkTheme) {
                NativeWindow.configure(window, darkTheme)
            }

            DisposableEffect(Unit) {
                val manager = KeyboardFocusManager.getCurrentKeyboardFocusManager()

                val dispatcher = java.awt.KeyEventDispatcher { event ->
                    if (event.component == null) {
                        false
                    } else {
                        val activeWindow = manager.activeWindow

                        if (activeWindow !== window) {
                            false
                        } else if (event.keyCode != KeyEvent.VK_ESCAPE) {
                            false
                        } else {
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
}