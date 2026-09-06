package dev.jvqtil.cuber.desktop.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.jvqtil.cuber.desktop.CuberController
import dev.jvqtil.cuber.desktop.ui.screens.DetailsScreen
import dev.jvqtil.cuber.desktop.ui.screens.SolvesScreen
import dev.jvqtil.cuber.desktop.ui.screens.TimerScreen

class DesktopInput {
    private var escapeDown = false

    var onEscape: (() -> Unit)? = null

    fun handleEscape(pressed: Boolean) {
        if (pressed) {
            if (escapeDown) return

            escapeDown = true
            onEscape?.invoke()
            return
        }

        escapeDown = false
    }
}

private enum class Screen {
    TIMER,
    SOLVES,
    DETAILS,
}

@Composable
fun DesktopApp(
    controller: CuberController,
    input: DesktopInput,
) {
    var screen by remember { mutableStateOf(Screen.TIMER) }
    var selectedSolveId by remember { mutableStateOf<Long?>(null) }
    var direction by remember { mutableIntStateOf(1) }
    val solves by controller.solves.collectAsState()

    fun navigate(next: Screen, solveId: Long? = null) {
        direction = 1
        selectedSolveId = solveId
        screen = next
    }

    fun back() {
        when (screen) {
            Screen.TIMER -> Unit
            Screen.SOLVES -> {
                direction = -1
                screen = Screen.TIMER
            }
            Screen.DETAILS -> {
                direction = -1
                screen = Screen.SOLVES
            }
        }
    }

    DisposableEffect(screen) {
        input.onEscape = if (screen == Screen.TIMER) {
            { navigate(Screen.SOLVES) }
        } else {
            ::back
        }

        onDispose {
            input.onEscape = null
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                if (direction > 0) {
                    (slideInHorizontally { it / 10 } + fadeIn())
                        .togetherWith(
                            slideOutHorizontally { -it / 18 } + fadeOut()
                        )
                } else {
                    (slideInHorizontally { -it / 10 } + fadeIn())
                        .togetherWith(
                            slideOutHorizontally { it / 18 } + fadeOut()
                        )
                }
            },
            label = "navigation",
        ) { current ->
            when (current) {
                Screen.TIMER -> TimerScreen(
                    controller = controller,
                    onOpenSolves = { navigate(Screen.SOLVES) },
                )
                Screen.SOLVES -> SolvesScreen(
                    solves = solves,
                    onBack = ::back,
                    onOpenSolve = { navigate(Screen.DETAILS, it) },
                )
                Screen.DETAILS -> {
                    selectedSolveId?.let { id ->
                        DetailsScreen(
                            solveId = id,
                            controller = controller,
                            onBack = ::back,
                        )
                    }
                }
            }
        }
    }
}
