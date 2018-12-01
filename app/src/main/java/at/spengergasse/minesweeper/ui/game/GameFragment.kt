package at.spengergasse.minesweeper.ui.game

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import at.spengergasse.minesweeper.R
import at.spengergasse.minesweeper.game.Board
import at.spengergasse.minesweeper.game.Field
import at.spengergasse.minesweeper.game.GameSettings
import at.spengergasse.minesweeper.game.generators.FieldGenerationArguments
import at.spengergasse.minesweeper.game.generators.FullFieldGenerator
import at.spengergasse.minesweeper.game.moves.FloodRevealMove
import at.spengergasse.minesweeper.game.moves.ToggleFlagMove
import at.spengergasse.minesweeper.toPx
import kotlin.math.roundToInt

class GameFragment : Fragment() {

    private lateinit var field: Field
    private lateinit var board: Board
    var started = false

    lateinit var settings: GameSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settings = GameSettings.load(PreferenceManager.getDefaultSharedPreferences(activity))

        field = FullFieldGenerator().generate(
            settings.rows,
            settings.columns,
            FieldGenerationArguments(settings.mines)
        )
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
        val adapter = BoardAdapter(board, dim)
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
            val (_, _) = board.push(ToggleFlagMove(position / board.columns, position % board.columns))

            adapter.notifyDataSetChanged()

            true
        }

        return loaded
    }

}
