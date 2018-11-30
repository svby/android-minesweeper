package at.spengergasse.minesweeper.game.moves

import at.spengergasse.minesweeper.game.Board

interface Move {

    enum class Type {
        Reveal,
        Flag,
        RemoveFlag
    }

    fun execute(board: Board, changeSet: Board.ChangeSet)

}