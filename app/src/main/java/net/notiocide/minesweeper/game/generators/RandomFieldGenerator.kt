package net.notiocide.minesweeper.game.generators

import net.notiocide.minesweeper.game.Field

class RandomFieldGenerator : FieldGenerator {

    override fun generate(rows: Int, columns: Int, args: FieldGenerationArguments): Field {
        val field = Field(rows, columns)

        val numbers = (0 until rows * columns).shuffled().take(args.mines)
        for (index in numbers) {
            field[index / columns, index % columns] = true
        }

        return field
    }

}