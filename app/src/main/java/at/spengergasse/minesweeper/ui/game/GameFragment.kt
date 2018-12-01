package at.spengergasse.minesweeper.ui.game

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import at.spengergasse.minesweeper.R
import at.spengergasse.minesweeper.game.Board
import at.spengergasse.minesweeper.game.Field
import at.spengergasse.minesweeper.game.generators.FieldGenerationArguments
import at.spengergasse.minesweeper.game.generators.FullFieldGenerator
import at.spengergasse.minesweeper.game.moves.FloodRevealMove
import at.spengergasse.minesweeper.game.moves.ToggleFlagMove
import at.spengergasse.minesweeper.toPx
import kotlin.math.roundToInt

class GameFragment : Fragment() {

    companion object {
        fun newInstance() = GameFragment()
    }

    private lateinit var field: Field
    private lateinit var board: Board
    var started = false

    var rows = -1
    var columns = -1
    var mines = -1
    var safe = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)

        rows = prefs.getInt("rows", 90)
        columns = prefs.getInt("columns", 9)
        mines = prefs.getInt("mines", 10)
        safe = prefs.getBoolean("safe", true)

        field = FullFieldGenerator().generate(rows, columns, FieldGenerationArguments(mines))
        board = Board(field)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val loaded = inflater.inflate(R.layout.game_fragment, container, false) // as HorizontalScrollView

        val grid = loaded.findViewById<GridView>(R.id.grid)
        val dim = toPx(40.0f, resources)
        grid.layoutParams.apply {
            width = (board.columns * dim).roundToInt()
        }

        grid.numColumns = board.columns
        grid.adapter = BoardAdapter(board, dim)

        grid.setOnItemClickListener { parent, view, position, id ->
            val row = position / board.columns
            val column = position % board.columns

            if (board.isFlagged(row, column)) return@setOnItemClickListener
            if (board.isRevealed(row, column)) return@setOnItemClickListener

            if (!started && safe) board.ensureSafe(row, column)
            started = true

            val (state, affected) = board.push(FloodRevealMove(row, column))

            for ((row, column) in affected) {
                val target = grid.getChildAt(row * board.columns + column) as TextView

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
                    AlertDialog.Builder(requireContext())
                        .setMessage(R.string.victory)
                        .create()
                        .show()
                }
                Board.State.Loss -> {
                    AlertDialog.Builder(requireContext())
                        .setMessage(R.string.loss)
                        .create()
                        .show()
                }
                Board.State.Neutral -> Unit
            }
        }

        grid.setOnItemLongClickListener { parent, view, position, id ->
            val (_, affected) = board.push(ToggleFlagMove(position / board.columns, position % board.columns))

            for ((row, column) in affected) {
                val target = grid.getChildAt(row * board.columns + column) as TextView

                when {
                    board.isFlagged(row, column) -> {
                        target.setBackgroundResource(R.drawable.flagged_square)
                        target.text = "?"
                    }
                    board.isRevealed(row, column) -> target.setBackgroundResource(R.drawable.uncovered_square)
                    else -> {
                        target.setBackgroundResource(R.drawable.covered_square)
                        target.text = ""
                    }
                }
            }

            true
        }

        return loaded
    }

}
