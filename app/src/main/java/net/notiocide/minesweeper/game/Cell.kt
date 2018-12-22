package net.notiocide.minesweeper.game

data class Cell(
    val row: Int,
    val column: Int,
    private val board: Board
) {

    val isRevealed get() = board.isRevealed(row, column)
    val isFlagged get() = board.isFlagged(row, column)
    val isMine get() = board.isMine(row, column)

    val adjacentMines get() = board.getAdjacentMines(row, column)

}