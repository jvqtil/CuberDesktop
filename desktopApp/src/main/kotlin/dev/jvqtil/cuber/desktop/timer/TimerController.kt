package dev.jvqtil.cuber.desktop.timer

import dev.jvqtil.cuber.desktop.scramble.Scramble
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.time.Duration.Companion.milliseconds

data class TimerState(
    val started: Boolean = false,
    val running: Boolean = false,
    val elapsedMs: Long = 0L,
    val scramble: Scramble,
)

class TimerController(
    initialScramble: Scramble,
    private val generateScramble: () -> Scramble,
    private val onSolve: suspend (Long, String) -> Unit,
) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private val _state = MutableStateFlow(TimerState(scramble = initialScramble))
    val state: StateFlow<TimerState> = _state.asStateFlow()

    private var startedAtNs = 0L
    private var timerJob: Job? = null

    fun start() {
        if (_state.value.running || _state.value.started) return

        startedAtNs = System.nanoTime()

        _state.update {
            it.copy(
                started = true,
                running = true,
                elapsedMs = 0L,
            )
        }

        timerJob?.cancel()
        timerJob = scope.launch {
            while (_state.value.running) {
                _state.update {
                    it.copy(
                        elapsedMs = max(
                            0L,
                            (System.nanoTime() - startedAtNs) / 1_000_000L,
                        ),
                    )
                }
                delay(10L.milliseconds)
            }
        }
    }

    fun stop() {
        if (!_state.value.running) return

        val elapsed = max(
            0L,
            (System.nanoTime() - startedAtNs) / 1_000_000L,
        )

        timerJob?.cancel()
        timerJob = null

        val scramble = _state.value.scramble.text

        _state.update {
            it.copy(
                running = false,
                elapsedMs = elapsed,
            )
        }

        scope.launch {
            onSolve(elapsed, scramble)
        }
    }

    fun toggle() {
        when {
            _state.value.running -> stop()
            !_state.value.started -> start()
        }
    }

    fun reset() {
        timerJob?.cancel()
        timerJob = null
        startedAtNs = 0L

        _state.value = TimerState(
            started = false,
            running = false,
            elapsedMs = 0L,
            scramble = generateScramble(),
        )
    }

    fun dispose() {
        timerJob?.cancel()
        scope.cancel()
    }
}
