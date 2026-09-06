package dev.jvqtil.cuber.desktop.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import dev.jvqtil.cuber.desktop.database.PENALTY_DNF
import dev.jvqtil.cuber.desktop.database.PENALTY_PLUS_TWO
import dev.jvqtil.cuber.desktop.database.SolveEntity
import kotlin.math.max
import kotlin.math.min

@Composable
fun SolveTimeChart(
    solves: List<SolveEntity>,
    modifier: Modifier = Modifier,
) {
    val values = remember(solves) {
        solves
            .sortedBy(SolveEntity::createdAt)
            .takeLast(100)
            .mapNotNull { solve ->
                when (solve.penalty) {
                    PENALTY_DNF -> null
                    PENALTY_PLUS_TWO -> solve.time + 2_000L
                    else -> solve.time
                }
            }
    }

    if (values.size < 2) {
        return
    }

    val lineColor = MaterialTheme.colorScheme.primary
    val fillColor = lineColor.copy(alpha = 0.10f)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = "Solve time chart",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
        ) {
            val minValue = values.minOrNull() ?: return@Canvas
            val maxValue = values.maxOrNull() ?: return@Canvas
            val range = max(
                maxValue - minValue,
                1L,
            ).toFloat()

            val left = 8.dp.toPx()
            val right = 8.dp.toPx()
            val top = 8.dp.toPx()
            val bottom = 12.dp.toPx()

            val chartWidth = size.width - left - right
            val chartHeight = size.height - top - bottom

            val lastIndex = (values.size - 1).coerceAtLeast(1)

            val points = values.mapIndexed { index, value ->
                Offset(
                    x = left +
                            chartWidth *
                            index /
                            lastIndex.toFloat(),
                    y = top +
                            chartHeight *
                            (
                                    1f -
                                            (value - minValue) / range
                                    ),
                )
            }

            val path = Path().apply {
                moveTo(
                    points.first().x,
                    points.first().y,
                )

                for (i in 0 until points.lastIndex) {
                    val p0 = points[
                        max(0, i - 1)
                    ]
                    val p1 = points[i]
                    val p2 = points[i + 1]
                    val p3 = points[
                        min(points.lastIndex, i + 2)
                    ]

                    val c1 = Offset(
                        p1.x + (p2.x - p0.x) / 6f,
                        p1.y + (p2.y - p0.y) / 6f,
                    )

                    val c2 = Offset(
                        p2.x - (p3.x - p1.x) / 6f,
                        p2.y - (p3.y - p1.y) / 6f,
                    )

                    cubicTo(
                        c1.x,
                        c1.y,
                        c2.x,
                        c2.y,
                        p2.x,
                        p2.y,
                    )
                }
            }

            val fillPath = Path().apply {
                addPath(path)
                lineTo(
                    points.last().x,
                    size.height - bottom,
                )
                lineTo(
                    points.first().x,
                    size.height - bottom,
                )
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        fillColor,
                        fillColor.copy(alpha = 0f),
                    ),
                ),
            )

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
            )
        }
    }
}