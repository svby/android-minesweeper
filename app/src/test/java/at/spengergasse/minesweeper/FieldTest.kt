package at.spengergasse.minesweeper

import at.spengergasse.minesweeper.game.Field
import org.junit.Test

class FieldTest {

    @Test
    fun test() {
        val r = 3
        val c = 3

        val field = Field(r, c)
        for (i in 0 until r) {
            for (j in 0 until c) {
                field[i, j] = true
            }
        }

        field.preprocess()

        println(field.getAdjacent(1, 1))
    }

}