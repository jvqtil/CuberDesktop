package dev.jvqtil.cuber.desktop.scramble

import org.worldcubeassociation.tnoodle.puzzle.CubePuzzle
import kotlin.random.Random
import kotlin.random.asJavaRandom

object ScrambleGenerator {
    private val puzzle = CubePuzzle(3)

    fun generate(): Scramble {
        val text = puzzle.generateWcaScramble(Random.asJavaRandom())
        val svg = puzzle.drawScramble(text, null).toString()
        return Scramble(text, svg)
    }

    fun fromText(text: String): Scramble {
        val svg = puzzle.drawScramble(text, null).toString()
        return Scramble(text, svg)
    }
}

data class Scramble(
    val text: String,
    val svg: String
)
