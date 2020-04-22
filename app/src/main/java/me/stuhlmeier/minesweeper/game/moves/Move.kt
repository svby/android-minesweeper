package me.stuhlmeier.minesweeper.game.moves

import me.stuhlmeier.minesweeper.game.Board

interface Move {
    enum class Type {
        Reveal,
        Flag,
        RemoveFlag
    }

    fun execute(board: Board, changeSet: Board.ChangeSet)
}
