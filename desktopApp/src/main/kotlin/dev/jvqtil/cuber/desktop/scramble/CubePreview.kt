package dev.jvqtil.cuber.desktop.scramble

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import org.jetbrains.skia.Data
import org.jetbrains.skia.svg.SVGDOM
import org.jetbrains.skia.svg.SVGLengthContext
import kotlin.math.min

@Composable
fun CubePreview(
    svgText: String,
    modifier: Modifier = Modifier.Companion,
) {
    val svg = remember(svgText) {
        SVGDOM(
            Data.makeFromBytes(
                svgText.encodeToByteArray(),
            ),
        )
    }

    Canvas(
        modifier = modifier.fillMaxSize(),
    ) {
        if (size.width <= 0f || size.height <= 0f) {
            return@Canvas
        }

        drawIntoCanvas { canvas ->
            val root = svg.root ?: return@drawIntoCanvas

            val intrinsic = root.getIntrinsicSize(
                SVGLengthContext(
                    size.width,
                    size.height,
                ),
            )

            val sourceWidth = when {
                intrinsic.x > 0f -> intrinsic.x
                root.viewBox != null && root.viewBox!!.width > 0f ->
                    root.viewBox!!.width

                else -> size.width
            }

            val sourceHeight = when {
                intrinsic.y > 0f -> intrinsic.y
                root.viewBox != null && root.viewBox!!.height > 0f ->
                    root.viewBox!!.height

                else -> size.height
            }

            val scale = min(
                size.width / sourceWidth,
                size.height / sourceHeight,
            )

            val renderedWidth = sourceWidth * scale
            val renderedHeight = sourceHeight * scale

            val offsetX = (size.width - renderedWidth) / 2f
            val offsetY = (size.height - renderedHeight) / 2f

            canvas.save()

            canvas.translate(
                offsetX,
                offsetY,
            )

            canvas.scale(
                scale,
                scale,
            )

            svg.setContainerSize(
                sourceWidth,
                sourceHeight,
            )

            svg.render(
                canvas.nativeCanvas,
            )

            canvas.restore()
        }
    }
}