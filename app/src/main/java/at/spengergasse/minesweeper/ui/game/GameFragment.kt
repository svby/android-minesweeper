package at.spengergasse.minesweeper.ui.game

import android.os.Bundle
import android.preference.PreferenceManager

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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
import kotlinx.android.synthetic.main.fragment_game.*
import kotlin.math.roundToInt

class GameFragment : Fragment() {

    private val generator = RandomFieldGenerator()
    private lateinit var settings: GameSettings

    private val adapter by lazy { BoardAdapter(board, cellPx) }
    private val cellPx by lazy { toPx(40.0f, resources) }

    private lateinit var field: Field
    private lateinit var board: Board
    private var started = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        initialSetup()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_game, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        resetLayout()

        grid.layoutParams.width = (board.columns * cellPx).roundToInt()
        grid.isEnabled = true

        grid.numColumns = board.columns
        grid.adapter = adapter

        updateTurn()

        grid.setOnItemClickListener { _, _, position, _ ->
            val row = position / board.columns
            val column = position % board.columns

            if (board.isFlagged(row, column)) return@setOnItemClickListener
            if (board.isRevealed(row, column)) return@setOnItemClickListener

            if (!started && settings.safe) board.ensureSafe(row, column)
            started = true

            val (state) = board.push(FloodRevealMove(row, column))

            adapter.notifyDataSetChanged()
            updateTurn()

            when (state) {
                Board.State.Win -> {
                    AlertDialog.Builder(requireContext())
                        .setMessage(R.string.dialog_win)
                        .setPositiveButton(R.string.action_restart) { _, _ -> restartGame() }
                        .setNegativeButton(R.string.action_new_game) { _, _ -> newGame() }
                        .create()
                        .show()

                    grid.isEnabled = false
                }
                Board.State.Loss -> {
                    AlertDialog.Builder(requireContext())
                        .setMessage(R.string.dialog_lose)
                        .setPositiveButton(R.string.action_restart) { _, _ -> restartGame() }
                        .setNegativeButton(R.string.action_new_game) { _, _ -> newGame() }
                        .setNeutralButton(R.string.action_undo_last) { _, _ -> undo() }
                        .create()
                        .show()

                    grid.isEnabled = false
                }
                Board.State.Neutral -> Unit
            }
        }

        grid.setOnItemLongClickListener { _, _, position, _ ->
            val (_, _) = board.push(ToggleFlagMove(position / board.columns, position % board.columns))

            adapter.notifyDataSetChanged()
            updateTurn()

            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> NavHostFragment.findNavController(this).navigate(R.id.action_gameFragment_to_settingsFragment)
            R.id.action_new -> newGame()
            R.id.action_restart -> restartGame()
            R.id.action_undo -> undo()
            else -> return false
        }
        return true
    }

    private fun updateTurn() {
        text_remaining.text = getString(R.string.remaining_flags).format(board.mines - board.flagged)
    }

    fun restartGame() {
        grid.isEnabled = true
        board.clear()
        adapter.board = board

        resetLayout()
    }

    private fun initialSetup() {
        started = false

        settings = GameSettings.load(PreferenceManager.getDefaultSharedPreferences(activity))
        field = generator.generate(settings.rows, settings.columns, FieldGenerationArguments(settings.mines))

        started = false
        board = Board(field)
    }

    fun newGame() {
        initialSetup()
        resetLayout()
        updateTurn()
    }

    fun resetLayout() {
        grid.isEnabled = true

        adapter.board = board

        grid.numColumns = board.columns
        grid.layoutParams.width = (board.columns * cellPx).roundToInt()

        updateTurn()
    }

    fun undo(): Boolean {
        grid.isEnabled = true
        val result = board.pop()
        if (!result) Toast.makeText(requireContext(), R.string.empty_undo_stack, Toast.LENGTH_SHORT).show()
        else {
            adapter.notifyDataSetChanged()
            updateTurn()
        }
        return result
    }

}
