package me.stuhlmeier.minesweeper.game.generators

import me.stuhlmeier.minesweeper.game.Field

interface FieldGenerator {
    fun generate(rows: Int, columns: Int, args: FieldGenerationArguments): Field
}

data class FieldGenerationArguments(
    val mines: Int
)
