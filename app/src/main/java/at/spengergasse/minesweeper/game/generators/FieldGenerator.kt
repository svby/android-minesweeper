package at.spengergasse.minesweeper.game.generators

import at.spengergasse.minesweeper.game.Field

interface FieldGenerator {

    fun generate(rows: Int, columns: Int, args: FieldGenerationArguments): Field

}

data class FieldGenerationArguments(
    val mines: Int
)