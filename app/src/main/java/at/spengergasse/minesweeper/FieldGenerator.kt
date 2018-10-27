package at.spengergasse.minesweeper

interface FieldGenerator {

    fun generate(rows: Int, columns: Int, args: FieldGenerationArguments): Field

}

data class FieldGenerationArguments(
        val mines: Int
)