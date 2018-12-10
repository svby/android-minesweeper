package net.notiocide.minesweeper.game.moves

import net.notiocide.minesweeper.game.Board

interface Move {

    enum class Type {
        Reveal,
        Flag,
        RemoveFlag
    }

    fun execute(board: Board, changeSet: Board.ChangeSet)

}