package me.stuhlmeier.minesweeper.ui.game

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_game.*
import me.stuhlmeier.minesweeper.KEY_BOARD
import me.stuhlmeier.minesweeper.PREFS_NAME
import me.stuhlmeier.minesweeper.R
import me.stuhlmeier.minesweeper.game.Board
import me.stuhlmeier.minesweeper.game.GameSettings
import me.stuhlmeier.minesweeper.game.generators.FieldGenerationArguments
import me.stuhlmeier.minesweeper.game.generators.RandomFieldGenerator

class GameFragment : Fragment() {

    private val prefs by lazy { requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    private val generator = RandomFieldGenerator()
    private lateinit var settings: GameSettings

    private lateinit var board: Board
    private var started = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        setRetainInstance(true)
        initialSetup(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_game, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        resetLayout()

        updateHeader()

        board_view.board = board
        settings = GameSettings.load(prefs)
        board_view.settings = settings

        board_view.moveListener = object : BoardView.OnMoveListener {
            override fun onMove(board: Board, state: Board.State) {
                when (state) {
                    Board.State.Win -> {
                        board_view.isEnabled = false

                        AlertDialog.Builder(requireContext())
                            .setMessage(R.string.dialog_win)
                            .setPositiveButton(R.string.action_restart) { _, _ -> restartGame() }
                            .setNegativeButton(R.string.action_new_game) { _, _ -> newGame() }
                            .create()
                            .show()
                    }
                    Board.State.Loss -> {
                        board_view.isEnabled = false

                        AlertDialog.Builder(requireContext())
                            .setMessage(R.string.dialog_lose)
                            .setPositiveButton(R.string.action_restart) { _, _ -> restartGame() }
                            .setNegativeButton(R.string.action_new_game) { _, _ -> newGame() }
                            .setNeutralButton(R.string.action_undo_last) { _, _ -> undo() }
                            .create()
                            .show()
                    }
                    Board.State.Neutral -> {
                        board_view.isEnabled = true

                        updateHeader()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) = inflater.inflate(R.menu.game_menu, menu)

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        Log.v("Minesweeper", "saving")
        with(outState) {
            putParcelable(KEY_BOARD, board)
            // TODO persist entire state
        }
    }

    private fun updateHeader() {
        text_remaining.text = getString(R.string.remaining_flags).format(board.mines - board.flagged)
    }

    private fun initialSetup(bundle: Bundle?) {
        started = false

        val stored = bundle?.getParcelable<Board>(KEY_BOARD)
        Log.v("Minesweeper", stored.toString())

        settings = GameSettings.load(prefs)
        val field = generator.generate(settings.rows, settings.columns, FieldGenerationArguments(settings.mines))

        started = false
        board = Board(field)
    }

    fun restartGame() {
        board.restart()
        resetLayout()
    }

    fun newGame() {
        initialSetup(null)
        resetLayout()
    }

    fun resetLayout() {
        board_view.board = board
        updateHeader()
    }

    fun undo() {
        if (!board_view.undo()) Toast.makeText(requireContext(), R.string.empty_undo_stack, Toast.LENGTH_SHORT).show()
    }

}
