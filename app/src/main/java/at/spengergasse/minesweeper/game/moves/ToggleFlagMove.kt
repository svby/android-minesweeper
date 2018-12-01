package at.spengergasse.minesweeper.game.moves

import at.spengergasse.minesweeper.game.Board

class ToggleFlagMove(val row: Int, val column: Int) : Move {

    override fun execute(board: Board, changeSet: Board.ChangeSet) {
        if (board.isFlagged(row, column) || board.isRevealed(row, column) || board.isMine(row, column))
            changeSet.unflag(row, column)
        else changeSet.flag(row, column)
    }

}