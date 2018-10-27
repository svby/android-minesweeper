package at.spengergasse.minesweeper.activities

import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import at.spengergasse.minesweeper.*
import at.spengergasse.minesweeper.game.Board
import at.spengergasse.minesweeper.game.Field
import at.spengergasse.minesweeper.game.generators.FieldGenerationArguments
import at.spengergasse.minesweeper.game.generators.RandomFieldGenerator
import at.spengergasse.minesweeper.game.moves.FloodRevealMove
import at.spengergasse.minesweeper.game.moves.ToggleFlagMove
import kotlinx.android.synthetic.main.activity_game.*
import kotlin.math.min

class GameActivity : AppCompatActivity() {

    private lateinit var field: Field
    private lateinit var board: Board

    private var started = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val rows = intent.getIntExtra(EXTRA_ROWS, 2)
        val columns = intent.getIntExtra(EXTRA_COLUMNS, 2)
        val providedMines = if (intent.hasExtra(EXTRA_MINES)) intent.getIntExtra(EXTRA_MINES, 0) else null
        val safe = intent.getBooleanExtra(EXTRA_SAFE, false)

        val mines = min(providedMines ?: 0, if (safe) rows * columns - 1 else rows * columns)

        val generator = RandomFieldGenerator()
        field = generator.generate(rows, columns, FieldGenerationArguments(mines))
//      field = Field.createSampleMedium()
        board = Board(field)

        // Initialize UI
        // region UI initialization
        val buttons = Array(rows) { Array(columns) { Button(this@GameActivity) } }

        fun freeze() {
            buttons.forEach { row -> row.forEach { it.isEnabled = false } }
        }

        fun unfreeze() {
            buttons.forEach { row -> row.forEach { it.isEnabled = true } }
        }

        for (i in 0 until rows) {
            val tableRow = TableRow(this)
            tableRow.layoutParams = TableLayout.LayoutParams().apply {
                weight = 1.0f
                gravity = Gravity.CENTER
            }

            for (j in 0 until columns) {
                val button = buttons[i][j].apply {
                    setBackgroundResource(R.drawable.covered_square)
                    text = ""
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
                        if (board.isFlagged(row, column)) target.setBackgroundResource(R.drawable.flagged_square)
                        else if (board.isRevealed(row, column)) target.setBackgroundResource(R.drawable.uncovered_square)
                        else target.setBackgroundResource(R.drawable.covered_square)
                    }

                    true
                }

                button.setOnClickListener {
                    if (!started && safe) board.ensureSafe(i, j)

                    started = true

                    if (board.isFlagged(i, j)) return@setOnClickListener
                    val (state, affected) = board.push(FloodRevealMove(i, j))

                    for ((row, column) in affected) {
                        val target = buttons[row][column]
                        when {
                            board.isRevealed(row, column) -> {
                                target.setBackgroundResource(R.drawable.uncovered_square)
                                target.isLongClickable = false
                                board.getAdjacentMines(row, column).let {
                                    when {
                                        board.isMine(row, column) -> {
                                            target.text = "X"
                                            target.setBackgroundResource(R.drawable.mine_square)
                                        }
                                        it == 0 -> target.text = ""
                                        else -> target.text = it.toString()
                                    }
                                }
                            }
                        }
                    }

                    when (state) {
                        Board.State.Win -> {
                            freeze()
                            AlertDialog.Builder(this)
                                    .setMessage(R.string.victory)
                                    .setPositiveButton(R.string.newGame) { _, _ -> finish() }
                                    .create()
                                    .show()
                        }
                        Board.State.Loss -> {
                            freeze()
                            AlertDialog.Builder(this)
                                    .setMessage(R.string.loss)
                                    .setPositiveButton(R.string.newGame) { _, _ -> finish() }
                                    .setNeutralButton(R.string.retry) { _, _ ->
                                        // Reset
                                        // Note: don't reset `started`, we need to keep the bomb positions the same
                                        board.clear()
                                        for (row in 0 until board.rows) {
                                            for (column in 0 until board.columns) {
                                                buttons[row][column].apply {
                                                    text = ""
                                                    setBackgroundResource(R.drawable.covered_square)
                                                }
                                            }
                                        }
                                        unfreeze()
                                    }
                                    .create()
                                    .show()
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
