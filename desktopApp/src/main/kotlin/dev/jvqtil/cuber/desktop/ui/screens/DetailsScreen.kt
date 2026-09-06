package dev.jvqtil.cuber.desktop.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jvqtil.cuber.desktop.CuberController
import dev.jvqtil.cuber.desktop.database.PENALTY_DNF
import dev.jvqtil.cuber.desktop.database.PENALTY_OK
import dev.jvqtil.cuber.desktop.database.PENALTY_PLUS_TWO
import dev.jvqtil.cuber.desktop.scramble.CubePreview
import dev.jvqtil.cuber.desktop.scramble.ScrambleGenerator
import dev.jvqtil.cuber.desktop.ui.components.TopBackButton
import dev.jvqtil.cuber.desktop.util.TimeUtils
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun DetailsScreen(
    solveId: Long,
    controller: CuberController,
    onBack: () -> Unit,
) {
    val solves by controller.solves.collectAsState()
    val solve = solves.firstOrNull { it.id == solveId } ?: return

    val scramble = remember(solve.scramble) {
        ScrambleGenerator.fromText(solve.scramble)
    }

    var comment by remember(solve.id) {
        mutableStateOf(solve.comment.orEmpty())
    }

    LaunchedEffect(solve.id) {
        comment = solve.comment.orEmpty()
    }

    LaunchedEffect(solve.id, comment) {
        val normalized = comment.trim().ifEmpty { null }

        if (normalized != solve.comment) {
            delay(250.milliseconds)
            controller.setComment(
                solve,
                comment,
            )
        }
    }

    val penalties = listOf(
        PENALTY_OK,
        PENALTY_PLUS_TWO,
        PENALTY_DNF,
    )

    val timeColor =
        if (solve.penalty == PENALTY_DNF) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.onBackground
        }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 30.dp,
                vertical = 22.dp,
            ),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Column(
            modifier = Modifier
                .width(380.dp)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TopBackButton(onBack)

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = "Solve details",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Text(
                    text = TimeUtils.formatDateTime(
                        solve.createdAt,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surfaceContainerHigh,
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "TIME",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Text(
                        text = TimeUtils.formatSolveTime(
                            solve.time,
                            solve.penalty,
                        ),
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontSize = 52.sp,
                            letterSpacing = (-1.5).sp,
                        ),
                        fontWeight = FontWeight.Bold,
                        color = timeColor,
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surfaceContainer,
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "Penalty",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        penalties.forEachIndexed { index, penalty ->
                            SegmentedButton(
                                selected = solve.penalty == penalty,
                                onClick = {
                                    controller.setPenalty(
                                        solve,
                                        penalty,
                                    )
                                },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = penalties.size,
                                ),
                                modifier = Modifier.weight(1f),
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor =
                                        MaterialTheme.colorScheme.primaryContainer,
                                    activeContentColor =
                                        MaterialTheme.colorScheme.onPrimaryContainer,
                                    activeBorderColor =
                                        MaterialTheme.colorScheme.primary,
                                    inactiveContainerColor =
                                        MaterialTheme.colorScheme.surfaceContainer,
                                    inactiveContentColor =
                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                    inactiveBorderColor =
                                        MaterialTheme.colorScheme.outline,
                                ),
                            ) {
                                Text(penalty)
                            }
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surfaceContainer,
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "Comment",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    OutlinedTextField(
                        value = comment,
                        onValueChange = {
                            comment = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        maxLines = 6,
                        placeholder = {
                            Text(
                                text = "Add a comment",
                                color =
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                        shape = MaterialTheme.shapes.medium,
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(2.dp),
            )

            Button(
                onClick = {
                    controller.deleteSolve(solve)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor =
                        MaterialTheme.colorScheme.errorContainer,
                    contentColor =
                        MaterialTheme.colorScheme.onErrorContainer,
                ),
            ) {
                Text("Delete solve")
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surfaceContainer,
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    CubePreview(
                        svgText = scramble.svg,
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(400.dp),
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surfaceContainer,
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(9.dp),
                ) {
                    Text(
                        text = "Scramble",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )

                    SelectionContainer {
                        Text(
                            text = solve.scramble,
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 920.dp),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 20.sp,
                                lineHeight = 30.sp,
                            ),
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            }
        }
    }
}