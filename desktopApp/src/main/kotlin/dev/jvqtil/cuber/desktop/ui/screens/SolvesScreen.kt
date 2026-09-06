package dev.jvqtil.cuber.desktop.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.jvqtil.cuber.desktop.database.PENALTY_DNF
import dev.jvqtil.cuber.desktop.database.PENALTY_PLUS_TWO
import dev.jvqtil.cuber.desktop.database.SolveEntity
import dev.jvqtil.cuber.desktop.util.TimeUtils
import dev.jvqtil.cuber.desktop.ui.components.SolveRow
import dev.jvqtil.cuber.desktop.ui.components.SolveTimeChart
import dev.jvqtil.cuber.desktop.ui.components.TopBackButton
import java.util.Calendar

@Composable
fun SolvesScreen(
    solves: List<SolveEntity>,
    onBack: () -> Unit,
    onOpenSolve: (Long) -> Unit,
) {
    val stats = remember(solves) {
        buildStats(solves)
    }

    val listState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 28.dp,
                vertical = 18.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TopBackButton(onBack)

            Column(
                modifier = Modifier.padding(start = 12.dp),
            ) {
                Text(
                    text = "Solves",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Text(
                    text = solveCountLabel(solves.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        StatsBar(stats)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                SolveTimeChart(
                    solves = solves,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Column(
                modifier = Modifier
                    .width(440.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = "History",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                )

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        bottom = 4.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    if (solves.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        vertical = 24.dp,
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "Your solves will appear here.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    } else {
                        groupSolvesByDay(solves).forEach { group ->
                            item(
                                key = "day-${group.title}",
                            ) {
                                Text(
                                    text = group.title,
                                    modifier = Modifier.padding(
                                        top = 4.dp,
                                        bottom = 2.dp,
                                    ),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }

                            items(
                                items = group.solves,
                                key = SolveEntity::id,
                            ) { solve ->
                                SolveRow(
                                    solve = solve,
                                    onClick = {
                                        onOpenSolve(solve.id)
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsBar(
    stats: SolveStats,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatItem(
            label = "Best",
            value = TimeUtils.formatNullable(stats.best),
            emphasized = true,
        )

        StatItem(
            label = "Avg",
            value = TimeUtils.formatNullable(stats.mean),
        )

        StatItem(
            label = "Ao5",
            value = TimeUtils.formatNullable(stats.ao5),
        )

        StatItem(
            label = "Ao12",
            value = TimeUtils.formatNullable(stats.ao12),
        )

        StatItem(
            label = "Ao50",
            value = TimeUtils.formatNullable(stats.ao50),
        )

        StatItem(
            label = "Ao100",
            value = TimeUtils.formatNullable(stats.ao100),
        )
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    emphasized: Boolean = false,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (emphasized) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainerLow
            },
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 15.dp,
                vertical = 10.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (emphasized) {
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(
                        alpha = 0.72f,
                    )
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = if (emphasized) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        }
    }
}

private data class SolveStats(
    val best: Long?,
    val mean: Long?,
    val ao5: Long?,
    val ao12: Long?,
    val ao50: Long?,
    val ao100: Long?,
)

private fun buildStats(
    solves: List<SolveEntity>,
): SolveStats {
    val valid = solves.filter {
        it.penalty != PENALTY_DNF
    }

    val values = valid
        .map(::effectiveTime)
        .sorted()

    val mean = values
        .takeIf {
            it.isNotEmpty()
        }
        ?.average()
        ?.toLong()

    return SolveStats(
        best = values.minOrNull(),
        mean = mean,
        ao5 = calculateAo(
            solves,
            5,
        ),
        ao12 = calculateAo(
            solves,
            12,
        ),
        ao50 = calculateAo(
            solves,
            50,
        ),
        ao100 = calculateAo(
            solves,
            100,
        ),
    )
}

private fun calculateAo(
    solves: List<SolveEntity>,
    count: Int,
): Long? {
    if (solves.size < count) {
        return null
    }

    val window = solves.take(count)

    if (
        window.count {
            it.penalty == PENALTY_DNF
        } > 1
    ) {
        return null
    }

    val values = window
        .map {
            if (it.penalty == PENALTY_DNF) {
                Long.MAX_VALUE
            } else {
                effectiveTime(it)
            }
        }
        .sorted()

    val trim = maxOf(
        1,
        count / 20,
    )

    val trimmed = values
        .drop(trim)
        .dropLast(trim)

    if (
        trimmed.any {
            it == Long.MAX_VALUE
        }
    ) {
        return null
    }

    return trimmed
        .average()
        .toLong()
}

private fun effectiveTime(
    solve: SolveEntity,
): Long {
    return if (
        solve.penalty == PENALTY_PLUS_TWO
    ) {
        solve.time + 2_000L
    } else {
        solve.time
    }
}

private fun solveCountLabel(
    count: Int,
): String {
    return when (count) {
        0 -> "No solves recorded"
        1 -> "1 solve"
        else -> "$count solves"
    }
}

private data class DayGroup(
    val title: String,
    val solves: List<SolveEntity>,
)

private fun groupSolvesByDay(
    solves: List<SolveEntity>,
): List<DayGroup> {
    val today = Calendar.getInstance()

    val yesterday = Calendar.getInstance().apply {
        add(
            Calendar.DAY_OF_YEAR,
            -1,
        )
    }

    return solves
        .sortedByDescending(
            SolveEntity::createdAt,
        )
        .groupBy {
            Calendar.getInstance()
                .apply {
                    timeInMillis = it.createdAt
                }
                .let {
                    Triple(
                        it.get(Calendar.YEAR),
                        it.get(Calendar.MONTH),
                        it.get(Calendar.DAY_OF_MONTH),
                    )
                }
        }
        .map { (_, daySolves) ->
            val first = daySolves.first()

            val calendar = Calendar.getInstance().apply {
                timeInMillis = first.createdAt
            }

            DayGroup(
                title = when {
                    sameDay(
                        calendar,
                        today,
                    ) -> "Today"

                    sameDay(
                        calendar,
                        yesterday,
                    ) -> "Yesterday"

                    else -> TimeUtils.formatDate(
                        first.createdAt,
                    )
                },
                solves = daySolves,
            )
        }
}

private fun sameDay(
    first: Calendar,
    second: Calendar,
): Boolean {
    return first.get(Calendar.YEAR) ==
            second.get(Calendar.YEAR) &&
            first.get(Calendar.DAY_OF_YEAR) ==
            second.get(Calendar.DAY_OF_YEAR)
}