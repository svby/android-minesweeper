package at.spengergasse.minesweeper

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

    private lateinit var field: Field
    private lateinit var board: Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val rows = intent.getIntExtra(EXTRA_ROWS, 2)
        val columns = intent.getIntExtra(EXTRA_COLUMNS, 2)
        val mines = if (intent.hasExtra(EXTRA_MINES)) intent.getIntExtra(EXTRA_MINES, 0) else null

        val generator = RandomFieldGenerator()
        field = generator.generate(rows, columns, FieldGenerationArguments(mines ?: 0))
//      field = Field.createSampleMedium()
        board = Board(field)

        // Initialize UI
        // region UI initialization
        val buttons = Array(rows) { Array(columns) { Button(this@GameActivity) } }

        for (i in 0 until rows) {
            val tableRow = TableRow(this)
            tableRow.layoutParams = TableLayout.LayoutParams().apply {
                weight = 1.0f
                gravity = Gravity.CENTER
            }

            for (j in 0 until columns) {
                val button = buttons[i][j].apply {
                    setBackgroundColor(Color.LTGRAY)
                    isLongClickable = true
                }
                button.layoutParams = TableRow.LayoutParams().apply {
                    width = TableRow.LayoutParams.MATCH_PARENT
                    height = TableRow.LayoutParams.MATCH_PARENT
                }

                button.setOnLongClickListener { _ ->
                    val (_, affected) = board.push(ToggleFlagMove(i, j))

                    for ((row, column) in affected) {
                        val target = buttons[row][column]
                        if (board.isFlagged(row, column)) target.setBackgroundColor(Color.GRAY)
                        else target.setBackgroundColor(Color.LTGRAY)
                    }

                    true
                }

                button.setOnClickListener {
                    if (board.isFlagged(i, j)) return@setOnClickListener
                    val (state, affected) = board.push(FloodRevealMove(i, j))

                    for ((row, column) in affected) {
                        val target = buttons[row][column]
                        when {
                            board.isRevealed(row, column) -> {
                                target.setBackgroundColor(Color.WHITE)
                                target.text = board.getAdjacentMines(row, column).let {
                                    when {
                                        board.isMine(row, column) -> "X"
                                        it == 0 -> ""
                                        else -> it.toString()
                                    }
                                }
                            }
                        }
                    }

                    when (state) {
                        Board.State.Win -> {
                            Toast.makeText(this, "You win!", Toast.LENGTH_LONG).show()
                        }
                        Board.State.Loss -> {
                            Toast.makeText(this, "You lost.", Toast.LENGTH_LONG).show()
                        }
                        else -> Unit
                    }
                }
                tableRow.addView(button)
            }

            layout_game.addView(tableRow)
        }
        // endregion
    }

}
