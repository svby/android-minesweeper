package at.spengergasse.minesweeper.ui.game

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import at.spengergasse.minesweeper.R
import at.spengergasse.minesweeper.game.Board
import at.spengergasse.minesweeper.game.Field
import at.spengergasse.minesweeper.game.GameSettings
import at.spengergasse.minesweeper.game.generators.FieldGenerationArguments
import at.spengergasse.minesweeper.game.generators.RandomFieldGenerator
import at.spengergasse.minesweeper.game.moves.FloodRevealMove
import at.spengergasse.minesweeper.game.moves.ToggleFlagMove
import at.spengergasse.minesweeper.toPx
import kotlinx.android.synthetic.main.game_fragment.*
import kotlin.math.roundToInt

class GameFragment : Fragment() {

    private val generator = RandomFieldGenerator()

    private lateinit var field: Field
    private lateinit var board: Board
    private var started = false

    private lateinit var settings: GameSettings
    private lateinit var adapter: BoardAdapter

    private val cellSize: Float = 40.0f
    private var cellPx = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cellPx = toPx(cellSize, resources)

        board = newBoard()
        adapter = BoardAdapter(board, cellPx)

        resetGame(board)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val loaded = inflater.inflate(R.layout.game_fragment, container, false) // as HorizontalScrollView

        setHasOptionsMenu(true)

        val grid = loaded.findViewById<GridView>(R.id.grid)
        grid.layoutParams.width = (board.columns * cellPx).roundToInt()

        grid.numColumns = board.columns
        grid.adapter = adapter

        grid.setOnItemClickListener { parent, view, position, id ->
            val row = position / board.columns
            val column = position % board.columns

            if (board.isFlagged(row, column)) return@setOnItemClickListener
            if (board.isRevealed(row, column)) return@setOnItemClickListener

            if (!started && settings.safe) board.ensureSafe(row, column)
            started = true

            val (state) = board.push(FloodRevealMove(row, column))

            adapter.notifyDataSetChanged()

            when (state) {
                Board.State.Win -> {
                    AlertDialog.Builder(requireContext())
                        .setMessage(R.string.victory)
                        .setPositiveButton(R.string.action_restart) { _, _ -> restartGame() }
                        .setNegativeButton(R.string.action_new) { _, _ -> newGame() }
                        .create()
                        .show()
                }
                Board.State.Loss -> {
                    AlertDialog.Builder(requireContext())
                        .setMessage(R.string.loss)
                        .setPositiveButton(R.string.action_restart) { _, _ -> restartGame() }
                        .setNegativeButton(R.string.action_new) { _, _ -> newGame() }
                        .setNeutralButton(R.string.undo_last) { _, _ -> undo() }
                        .create()
                        .show()
                }
                Board.State.Neutral -> Unit
            }
        }

        grid.setOnItemLongClickListener { parent, view, position, id ->
            val (_, _) = board.push(ToggleFlagMove(position / board.columns, position % board.columns))

            adapter.notifyDataSetChanged()

            true
        }

        return loaded
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                NavHostFragment.findNavController(this).navigate(R.id.action_gameFragment_to_settingsFragment)
                // TODO use navigation component
            }
            R.id.action_new -> newGame()
            R.id.action_restart -> restartGame()
            R.id.action_undo -> undo()
            else -> return false
        }
        return true
    }

    fun newBoard(): Board {
        settings = GameSettings.load(PreferenceManager.getDefaultSharedPreferences(activity))
        field = generator.generate(settings.rows, settings.columns, FieldGenerationArguments(settings.mines))

        started = false
        return Board(field)
    }

    fun resetGame(board: Board = this.board) {
        board.clear()
        this.board = board
        adapter.board = board
    }

    fun restartGame() = resetGame()

    fun newGame() {
        started = false
        resetGame(newBoard())

        grid.numColumns = board.columns
        grid.layoutParams.width = (board.columns * cellPx).roundToInt()
    }

    fun undo(): Boolean {
        val result = board.pop()
        if (!result) Toast.makeText(requireContext(), R.string.empty_undo_stack, Toast.LENGTH_SHORT).show()
        else adapter.notifyDataSetChanged()
        return result
    }

}
