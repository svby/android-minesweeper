package me.stuhlmeier.minesweeper.game.generators

import me.stuhlmeier.minesweeper.game.Field
import kotlin.math.min

class StringPresetGenerator(val string: String, val mineChar: Char = 'X') : FieldGenerator {
    override fun generate(rows: Int, columns: Int, args: FieldGenerationArguments): Field {
        val split = string.split("\n")
        return Field(
            min(split.size, rows),
            min(columns, split.maxBy { it.length }?.length ?: 1)
        ).apply {
            for (row in 0 until this.rows) {
                val line = split[row]
                for (column in 0 until this.columns) {
                    if (line.getOrNull(column) == mineChar) this[row, column] = true
                }
            }
        }
    }
}
