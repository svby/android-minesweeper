package net.notiocide.minesweeper.game.generators

import net.notiocide.minesweeper.game.Field

interface FieldGenerator {

    fun generate(rows: Int, columns: Int, args: FieldGenerationArguments): Field

}

data class FieldGenerationArguments(
    val mines: Int
)