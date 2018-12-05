package net.notiocide.minesweeper.ui.game

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import net.notiocide.minesweeper.R
import net.notiocide.minesweeper.game.Board
import kotlin.math.roundToInt

class BoardAdapter(board: Board, private val size: Float) : BaseAdapter() {

    var displayAll = false

    var board: Board = board
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return TextView(parent.context).apply {
            gravity = Gravity.CENTER
            text = ""

            val cell = board[position / board.columns, position % board.columns]

            if (displayAll) {
                if (cell.isMine) {
                    text = "X"
                    setBackgroundResource(R.drawable.mine_square)
                } else {
                    text = ""
                    setBackgroundResource(R.drawable.uncovered_square)
                }
            } else {
                when {
                    cell.isRevealed -> {
                        if (cell.isMine) {
                            text = "X"
                            setBackgroundResource(R.drawable.mine_square)
                        } else {
                            val adjacent = board.getAdjacentMines(cell.row, cell.column)
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
            }

            size.roundToInt().let {
                width = it
                minWidth = it
                height = it
                minHeight = it
            }
        }
    }

    override fun getItem(position: Int) = board[position / board.columns, position % board.columns]

    override fun getItemId(position: Int) = 0L

    override fun getCount() = board.rows * board.columns

}