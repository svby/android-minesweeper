package at.spengergasse.minesweeper.game.moves

import at.spengergasse.minesweeper.Point
import at.spengergasse.minesweeper.game.Board
import java.util.*
import kotlin.collections.HashSet

class FloodRevealMove(val row: Int, val column: Int) : Move {

    private fun isMine(board: Board, row: Int, column: Int): Boolean {
        if (row !in 0 until board.rows || column !in 0 until board.columns) return false
        return board.isMine(row, column)
    }

//    fun directNeighbors(board: Board, row: Int, column: Int) = sequence {
//        Point(row, column - 1).let { if (it.first in 0 until board.rows && it.second in 0 until board.columns) yield(it) }
//        Point(row - 1, column).let { if (it.first in 0 until board.rows && it.second in 0 until board.columns) yield(it) }
//        Point(row, column + 1).let { if (it.first in 0 until board.rows && it.second in 0 until board.columns) yield(it) }
//        Point(row + 1, column).let { if (it.first in 0 until board.rows && it.second in 0 until board.columns) yield(it) }
//    }

    fun neighbors(board: Board, row: Int, column: Int) = sequence {
        Point(row - 1, column - 1).let { if (it.first in 0 until board.rows && it.second in 0 until board.columns) yield(it) }
        Point(row, column - 1).let { if (it.first in 0 until board.rows && it.second in 0 until board.columns) yield(it) }
        Point(row + 1, column - 1).let { if (it.first in 0 until board.rows && it.second in 0 until board.columns) yield(it) }
        Point(row - 1, column).let { if (it.first in 0 until board.rows && it.second in 0 until board.columns) yield(it) }
        Point(row - 1, column + 1).let { if (it.first in 0 until board.rows && it.second in 0 until board.columns) yield(it) }
        Point(row, column + 1).let { if (it.first in 0 until board.rows && it.second in 0 until board.columns) yield(it) }
        Point(row + 1, column + 1).let { if (it.first in 0 until board.rows && it.second in 0 until board.columns) yield(it) }
        Point(row + 1, column).let { if (it.first in 0 until board.rows && it.second in 0 until board.columns) yield(it) }
    }

    override fun execute(board: Board, changeSet: Board.ChangeSet) {
        if (board.isMine(row, column)) {
            changeSet.reveal(row, column)
            return
        }

        val points = ArrayDeque<Point>()
        val seen = HashSet<Point>()

        points.push(Point(row, column))

        while (!points.isEmpty()) {
            val top = points.pop()
            val (row, column) = top
            if (top in seen || row !in 0 until board.rows || column !in 0 until board.columns) continue
            seen.add(top)

            if (!board.isMine(row, column)) changeSet.reveal(row, column)

            if (neighbors(board, row, column).none { board.isMine(it.first, it.second) })
                neighbors(board, row, column).filter { it !in seen }.forEach(points::push)
        }
    }
}