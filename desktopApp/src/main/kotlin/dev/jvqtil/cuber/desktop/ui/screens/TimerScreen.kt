package dev.jvqtil.cuber.desktop.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jvqtil.cuber.desktop.CuberController
import dev.jvqtil.cuber.desktop.scramble.CubePreview
import dev.jvqtil.cuber.desktop.util.TimeUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

private const val RESET_DELAY_MS = 500L

@Composable
fun TimerScreen(
    controller: CuberController,
    onOpenSolves: () -> Unit,
) {
    val state by controller.timer.state.collectAsState()

    val focusRequester = remember {
        FocusRequester()
    }

    val scope = rememberCoroutineScope()

    var spaceDown by remember {
        mutableStateOf(false)
    }

    var resetTriggered by remember {
        mutableStateOf(false)
    }

    var resetPressed by remember {
        mutableStateOf(false)
    }

    var resetJob by remember {
        mutableStateOf<Job?>(null)
    }

    var mouseResetGesture by remember {
        mutableStateOf(false)
    }

    fun cancelReset() {
        resetJob?.cancel()
        resetJob = null
    }

    fun beginResetHold() {
        if (
            state.running ||
            !state.started ||
            resetJob != null
        ) {
            return
        }

        resetTriggered = false
        resetPressed = true
        cancelReset()

        resetJob = scope.launch {
            delay(RESET_DELAY_MS.milliseconds)

            if (resetPressed) {
                resetTriggered = true
                controller.timer.reset()
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    DisposableEffect(Unit) {
        onDispose {
            cancelReset()
        }
    }

    val resetProgress by animateFloatAsState(
        targetValue = if (resetPressed) 1f else 0f,
        animationSpec = tween(
            durationMillis = RESET_DELAY_MS.toInt(),
            easing = FastOutSlowInEasing,
        ),
        label = "resetProgress",
    )

    val uiAlpha by animateFloatAsState(
        targetValue = if (state.running) 0f else 1f,
        animationSpec = tween(220),
        label = "uiAlpha",
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background,
            )
            .focusRequester(focusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->
                if (event.key != Key.Spacebar) {
                    return@onPreviewKeyEvent false
                }

                when (event.type) {
                    KeyEventType.KeyDown -> {
                        if (spaceDown) {
                            true
                        } else {
                            spaceDown = true
                            resetTriggered = false

                            if (
                                !state.running &&
                                state.started
                            ) {
                                beginResetHold()
                            }

                            true
                        }
                    }

                    KeyEventType.KeyUp -> {
                        if (!spaceDown) {
                            true
                        } else {
                            spaceDown = false

                            val wasReset =
                                resetTriggered

                            cancelReset()
                            resetPressed = false
                            resetTriggered = false

                            if (!wasReset) {
                                when {
                                    state.running -> {
                                        controller.timer.stop()
                                    }

                                    !state.started -> {
                                        controller.timer.start()
                                    }
                                }
                            }

                            true
                        }
                    }

                    else -> true
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        focusRequester.requestFocus()

                        val canReset =
                            controller.timer.state.value.started &&
                                    !controller.timer.state.value.running

                        mouseResetGesture = canReset

                        if (canReset) {
                            beginResetHold()
                        }

                        try {
                            awaitRelease()
                        } finally {
                            cancelReset()
                            resetPressed = false
                        }
                    },
                    onTap = {
                        if (!mouseResetGesture) {
                            controller.timer.toggle()
                        }

                        mouseResetGesture = false
                        resetTriggered = false
                        focusRequester.requestFocus()
                    },
                )
            },
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 36.dp,
                    vertical = 28.dp,
                ),
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center,
            ) {
                val timerShift by animateDpAsState(
                    targetValue = if (state.running) {
                        maxWidth / 2
                    } else {
                        0.dp
                    },
                    animationSpec = spring(
                        dampingRatio = 0.9f,
                        stiffness = Spring.StiffnessMediumLow,
                    ),
                    label = "timerShift",
                )

                Column(
                    modifier = Modifier.offset(
                        x = timerShift,
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier.width(360.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = TimeUtils.format(
                                state.elapsedMs,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 96.sp,
                                lineHeight = 104.sp,
                                letterSpacing = (-2).sp,
                            ),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(190.dp)
                            .height(7.dp),
                    ) {
                        ResetProgress(
                            progress = resetProgress,
                            visible = resetPressed,
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .graphicsLayer {
                        alpha = uiAlpha
                    },
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    CubePreview(
                        svgText = state.scramble.svg,
                        modifier = Modifier.size(400.dp),
                    )

                    Text(
                        text = state.scramble.text,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 28.dp,
                                vertical = 18.dp,
                            ),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 21.sp,
                            lineHeight = 30.sp,
                        ),
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(1.dp)
                .fillMaxHeight(0.58f)
                .graphicsLayer {
                    alpha = uiAlpha
                }
                .background(
                    MaterialTheme.colorScheme.outlineVariant.copy(
                        alpha = 0.6f,
                    ),
                ),
        )

        Surface(
            onClick = onOpenSolves,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(22.dp)
                .graphicsLayer {
                    alpha = uiAlpha
                },
            color = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
        ) {
            Text(
                text = "Solves",
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 10.dp,
                ),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun ResetProgress(
    progress: Float,
    visible: Boolean,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                alpha = if (visible) 1f else 0f
            }
            .background(
                MaterialTheme.colorScheme.surfaceContainerHigh,
                RoundedCornerShape(50),
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(
                    progress.coerceIn(
                        0f,
                        1f,
                    ),
                )
                .fillMaxHeight()
                .background(
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(50),
                ),
        )
    }
}
