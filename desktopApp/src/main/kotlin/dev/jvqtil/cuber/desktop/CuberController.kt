package dev.jvqtil.cuber.desktop

import dev.jvqtil.cuber.desktop.database.PENALTY_DNF
import dev.jvqtil.cuber.desktop.database.PENALTY_OK
import dev.jvqtil.cuber.desktop.database.PENALTY_PLUS_TWO
import dev.jvqtil.cuber.desktop.database.SolveEntity
import dev.jvqtil.cuber.desktop.database.SolveRepository
import dev.jvqtil.cuber.desktop.scramble.ScrambleGenerator
import dev.jvqtil.cuber.desktop.timer.TimerController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel

class CuberController(
    private val repository: SolveRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val solves: StateFlow<List<SolveEntity>> =
        repository.solves.stateIn(
            scope,
            SharingStarted.Eagerly,
            emptyList(),
        )

    val timer = TimerController(
        initialScramble = ScrambleGenerator.generate(),
        generateScramble = ScrambleGenerator::generate,
        onSolve = { time, scramble ->
            repository.save(time, scramble)
        },
    )

    fun setPenalty(solve: SolveEntity, penalty: String) {
        if (
            penalty != PENALTY_OK &&
            penalty != PENALTY_PLUS_TWO &&
            penalty != PENALTY_DNF
        ) return

        scope.launch {
            repository.updatePenalty(
                solve = solve,
                penalty = penalty,
            )
        }
    }

    fun setComment(solve: SolveEntity, comment: String) {
        scope.launch {
            repository.updateComment(
                solve = solve,
                comment = comment.trim().ifEmpty { null },
            )
        }
    }

    fun deleteSolve(solve: SolveEntity): Job =
        scope.launch {
            repository.delete(solve)
        }

    fun close() {
        timer.dispose()
        scope.coroutineContext.cancel()
    }
}
