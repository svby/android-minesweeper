package me.stuhlmeier.minesweeper.game.generators

import me.stuhlmeier.minesweeper.game.Field
import kotlin.math.min

class SequentialFieldGenerator : FieldGenerator {
    override fun generate(rows: Int, columns: Int, args: FieldGenerationArguments): Field {
        val field = Field(rows, columns)

        for (index in 0 until (min(rows * columns, args.mines))) {
            field[index / columns, index % columns] = true
        }

        return field
    }
}
