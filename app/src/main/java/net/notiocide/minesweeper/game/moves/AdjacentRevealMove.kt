package net.notiocide.minesweeper.game.moves

import net.notiocide.minesweeper.forEachEightNeighbor
import net.notiocide.minesweeper.game.Board

class AdjacentRevealMove(val row: Int, val column: Int) : Move {

    override fun execute(board: Board, changeSet: Board.ChangeSet) {
        if (!board.isRevealed(row, column)) return
        board.forEachEightNeighbor(row, column) { row, column ->
            if (!board.isFlagged(row, column)) changeSet.reveal(row, column)
        }
    }

}