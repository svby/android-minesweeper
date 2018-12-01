package at.spengergasse.minesweeper.ui.game

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import at.spengergasse.minesweeper.R
import at.spengergasse.minesweeper.game.Board
import kotlin.math.roundToInt

class BoardAdapter(private var _board: Board, private val size: Float) : BaseAdapter() {

    var board: Board
        get() = _board
        set(value) {
            _board = value
            notifyDataSetChanged()
        }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return TextView(parent.context).apply {
            gravity = Gravity.CENTER
            text = ""

            val cell = _board[position / _board.columns, position % _board.columns]

            when {
                cell.isRevealed -> {
                    if (cell.isMine) {
                        text = "X"
                        setBackgroundResource(R.drawable.mine_square)
                    } else {
                        val adjacent = _board.getAdjacentMines(cell.row, cell.column)
                        text = if (adjacent == 0) "" else adjacent.toString()
                        setBackgroundResource(R.drawable.uncovered_square)
                    }
                }
                cell.isFlagged -> {
                    text = "?"
                    setBackgroundResource(R.drawable.flagged_square)
                }
                else -> {
                    text = ""
                    setBackgroundResource(R.drawable.covered_square)
                }
            }

            size.roundToInt().let {
                width = it
                minWidth = it
                height = it
                minHeight = it
            }
        }
    }

    override fun getItem(position: Int) = _board[position / _board.columns, position % _board.columns]

    override fun getItemId(position: Int) = 0L

    override fun getCount() = _board.rows * _board.columns

}