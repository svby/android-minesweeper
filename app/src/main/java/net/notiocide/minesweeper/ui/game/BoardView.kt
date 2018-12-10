package net.notiocide.minesweeper.ui.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import net.notiocide.minesweeper.game.Board
import kotlin.math.ceil
import kotlin.math.min

class BoardView(context: Context, attrs: AttributeSet?, board: Board?) : View(context, attrs) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, null)
    constructor(context: Context, board: Board) : this(context, null, board)
    constructor(context: Context) : this(context, null)

    private val paint = Paint()

    private var _board = board

    var board
        get() = _board
        set(value) {
            _board = value
        }

    private val dp by lazy { TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.0f, resources.displayMetrics) }

    private var boardWidth = 0f
    private var boardHeight = 0f

    private var viewportX = 0f
    private var viewportY = 0f

    private var doubleDividerSize = 0f
    private var dividerSize = 0f
    private var totalCellSize = 0f
    private var cellSize = 0f

    private fun recalculate() {
        dividerSize = 5 * dp
        doubleDividerSize = 2 * dividerSize

        cellSize = 50 * dp
        totalCellSize = doubleDividerSize + cellSize

        viewportX = 0f
        viewportY = 0f

        val board = board

        if (board == null) {
            boardWidth = 0f
            boardHeight = 0f
        } else {
            if (board.rows == 0) boardHeight = 0f
            else {
                boardHeight = (board.rows * cellSize + (board.rows - 1) * doubleDividerSize)
            }

            if (board.columns == 0) boardWidth = 0f
            else {
                boardWidth = (board.columns * cellSize + (board.columns - 1) * doubleDividerSize)
            }
        }
    }

    private val rect = RectF(0f, 0f, 2000000f, 2000000f)

    init {
        recalculate()
    }

    val cellPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.RED
    }

    val dividerPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = doubleDividerSize
        color = Color.BLUE
    }

    override fun onDraw(canvas: Canvas) {
        board?.let { board ->
            with(canvas) {
                for (row in 0 until min(board.rows.toFloat(), ceil(height / totalCellSize)).toInt()) {
                    for (column in 0 until min(board.columns.toFloat(), ceil(width / totalCellSize)).toInt()) {
                        val offsetX = column * totalCellSize
                        val offsetY = row * totalCellSize

                        drawRect(
                            offsetX + dividerSize, offsetY + dividerSize,
                            offsetX + cellSize + doubleDividerSize, offsetY + cellSize + doubleDividerSize,
                            dividerPaint
                        )
                        drawRect(
                            offsetX + doubleDividerSize, offsetY + doubleDividerSize,
                            offsetX + cellSize + dividerSize, offsetY + cellSize + dividerSize,
                            cellPaint
                        )
                    }
                }
            }
        }
    }

}