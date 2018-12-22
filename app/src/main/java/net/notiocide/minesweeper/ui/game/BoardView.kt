package net.notiocide.minesweeper.ui.game

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.OverScroller
import androidx.core.view.GestureDetectorCompat
import net.notiocide.minesweeper.game.Board
import net.notiocide.minesweeper.roundUp
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
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
            recalculate()
        }

    private val dp by lazy { TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.0f, resources.displayMetrics) }

    private var boardWidth = 0f
    private var boardHeight = 0f

    private var viewportX = 0f
    private var viewportY = 0f
    private val viewportMaxX get() = max(0f, boardWidth - width)
    private val viewportMaxY get() = max(0f, boardHeight - height)

    private var doubleDividerSize = 0f
    private var dividerSize = 0f
    private var halfDividerSize = 0f
    private var totalCellSize = 0f
    private var cellSize = 0f

    private fun recalculate() {
        dividerSize = 5 * dp
        doubleDividerSize = 2 * dividerSize
        halfDividerSize = dividerSize / 2

        cellSize = 50 * dp
        totalCellSize = doubleDividerSize + cellSize

        viewportX = 0f
        viewportY = 0f

        val board = board

        if (board == null) {
            Log.i("Minesweeper", "No board")
            boardWidth = 0f
            boardHeight = 0f
        } else {
            boardWidth =
                    if (board.columns == 0) 0f
                    else (board.columns * cellSize + (board.columns + 1) * dividerSize)

            boardHeight =
                    if (board.rows == 0) 0f
                    else (board.rows * cellSize + (board.rows + 1) * dividerSize)
        }
    }

    private val detector = GestureDetectorCompat(context, GestureListener())
    private val scroller = OverScroller(context)

    init {
        recalculate()
        detector.setIsLongpressEnabled(true)
    }

    val cellPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.RED
    }

    val dividerPaint = Paint().apply {
        flags = Paint.ANTI_ALIAS_FLAG
        style = Paint.Style.STROKE
        strokeWidth = dividerSize
        color = Color.BLACK
    }

    val debugPaint = Paint().apply {
        flags = Paint.ANTI_ALIAS_FLAG
        style = Paint.Style.FILL
        color = Color.BLACK
        textSize = 50.0f
    }

    private fun scrollTo(x: Float, y: Float) {
        viewportX = max(0f, min(viewportMaxX, x))
        viewportY = max(0f, min(viewportMaxY, y))
    }

    private inner class GestureListener : GestureDetector.OnGestureListener {

        override fun onShowPress(e: MotionEvent) = Unit
        override fun onSingleTapUp(e: MotionEvent) = false
        override fun onLongPress(e: MotionEvent) = Unit

        override fun onDown(e: MotionEvent) = true

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            scrollTo(viewportX + distanceX, viewportY + distanceY)
//            Log.i(
//                "Minesweeper",
//                "Viewport: $viewportMaxX ($viewportX, $viewportY) ($distanceX, $distanceY) ($boardWidth, $boardHeight)"
//            )
            invalidate()
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            Log.i("Minesweeper", "Viewport fling detected")

            scroller.forceFinished(true)
            scroller.fling(
                viewportX.toInt(), viewportY.toInt(),
                -velocityX.toInt(), -velocityY.toInt(),
                0, viewportMaxX.roundUp(), 0, viewportMaxY.roundUp()
            )

            post(FlingRunnable(scroller))

            return true
        }

        private inner class FlingRunnable(private val scroller: OverScroller) : Runnable {

            override fun run() {
                if (scroller.computeScrollOffset()) {
                    scrollTo(scroller.currX.toFloat(), scroller.currY.toFloat())
                    invalidate()
                    post(this)
                }
            }

        }

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        detector.onTouchEvent(event)
        return true
    }

    override fun onDraw(canvas: Canvas) {
        board?.let { board ->
            with(canvas) {
                val startRow = max(0f, floor((viewportY - dividerSize) / (dividerSize + cellSize))).toInt()
                val endRow =
                    min(
                        board.rows.toFloat(),
                        ceil((viewportY + height - dividerSize) / (dividerSize + cellSize)) + 1
                    ).toInt()

                val startColumn = max(0f, floor((viewportX - dividerSize) / (dividerSize + cellSize))).toInt()
                val endColumn =
                    min(
                        board.columns.toFloat(),
                        ceil((viewportX + width - dividerSize) / (dividerSize + cellSize)) + 1
                    ).toInt()


                for (row in startRow until endRow) {
                    for (column in startColumn until endColumn) {
                        val offsetX = column * (dividerSize + cellSize) - viewportX
                        val offsetY = row * (dividerSize + cellSize) - viewportY

                        run {
                            val rectX = offsetX + halfDividerSize
                            val rectY = offsetY + halfDividerSize
                            val rectW = cellSize + dividerSize
                            val rectH = cellSize + dividerSize

                            drawRect(rectX, rectY, rectX + rectW, rectY + rectH, dividerPaint)
                        }

                        run {
                            val rectX = offsetX + dividerSize
                            val rectY = offsetY + dividerSize
                            val rectW = cellSize
                            val rectH = cellSize

                            drawRect(rectX, rectY, rectX + rectW, rectY + rectH, cellPaint)
                            drawText("$row:$column", rectX + 10, rectY + 50, debugPaint)
                        }
                    }
                }

//                drawRect(50.0f, 50.0f, 50.0f + dividerSize, 50.0f + dividerSize, Paint().apply {
//                    style = Paint.Style.FILL
//                    color = Color.GREEN
//                })
            }
        }
    }

}
