package at.spengergasse.minesweeper.ui.game

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import at.spengergasse.minesweeper.R
import at.spengergasse.minesweeper.game.Board
import kotlin.math.roundToInt

class BoardAdapter(val board: Board, private val size: Float) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return TextView(parent.context).apply {
            gravity = Gravity.CENTER
            text = ""

            val dim = size.roundToInt()
            width = dim
            minWidth = dim
            height = dim
            minHeight = dim
            // TODO fix height
            setBackgroundResource(R.drawable.covered_square)
        }
    }

    override fun getItem(position: Int) = board[position / board.columns, position % board.columns]

    override fun getItemId(position: Int) = 0L

    override fun getCount() = board.rows * board.columns

}