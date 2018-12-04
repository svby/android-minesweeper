package at.spengergasse.minesweeper.game

import android.os.Parcel
import android.os.Parcelable
import at.spengergasse.minesweeper.Cell
import at.spengergasse.minesweeper.Point
import at.spengergasse.minesweeper.boundsCheck
import at.spengergasse.minesweeper.game.moves.Move
import java.io.ObjectInputStream
import java.io.Serializable
import java.util.*
import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

class Board private constructor(
    private val field: Field, private val state: ByteArray, revealed: Int, flagged: Int
) : Parcelable, Serializable {

    enum class State { Win, Loss, Neutral }

    val rows get() = field.rows
    val columns get() = field.columns
    val mines get() = field.mines

    @Transient
    var revealed = revealed
        private set

    @Transient
    var flagged = flagged
        private set

    constructor(field: Field) : this(Field(field), createEmptyDataArray(field.rows, field.columns), 0, 0)

    private constructor(field: Field, state: ByteArray) : this(field, state, 0, 0) {
        recalculate()
    }

    // region Implementation

    private fun recalculate() {
        revealed = 0
        flagged = 0
        for (row in 0 until rows) for (column in 0 until columns) {
            if (isRevealedUnchecked(field, state, row, column)) revealed++
            else if (isFlaggedUnchecked(field, state, row, column)) flagged++
        }
    }

    fun ensureSafe(row: Int, column: Int) {
        if (field[row, column]) {
            for (i in 0 until field.rows) {
                for (j in 0 until field.columns) {
                    if (!field[i, j]) {
                        field[row, column] = false
                        field[i, j] = true
                        return
                    }
                }
            }
            // Couldn't find a safe spot, delete the mine
            field[row, column] = false
        }
    }

    fun isRevealed(row: Int, column: Int): Boolean {
        boundsCheck(rows, columns, row, column)
        return isRevealedUnchecked(field, state, row, column)
    }

    fun isFlagged(row: Int, column: Int): Boolean {
        boundsCheck(rows, columns, row, column)
        return isFlaggedUnchecked(field, state, row, column)
    }

    fun isMine(row: Int, column: Int): Boolean {
        boundsCheck(rows, columns, row, column)
        return field[row, column]
    }

    fun getAdjacentMines(row: Int, column: Int) = field.getAdjacentMines(row, column)

    fun setRevealed(row: Int, column: Int, revealed: Boolean) {
        val whichField = field.columns * row + column
        val whichByte = whichField ushr 2
        val whichQuarter = whichField % 4
        val shift = whichQuarter shl 1

        val oldValue = state[whichByte]
        val newValue = if (revealed) oldValue or (1 shl shift).toByte() else oldValue and (1 shl shift).toByte().inv()

        state[whichByte] = newValue
    }

    fun setFlagged(row: Int, column: Int, flagged: Boolean) {
        val whichField = field.columns * row + column
        val whichByte = whichField ushr 2
        val whichQuarter = whichField % 4
        val shift = (whichQuarter shl 1) + 1

        val oldValue = state[whichByte]
        val newValue = if (flagged) oldValue or (1 shl shift).toByte() else oldValue and (1 shl shift).toByte().inv()

        if (oldValue != newValue) this.flagged += when (flagged) {
            true -> 1
            false -> -1
        }

        state[whichByte] = newValue
    }

    operator fun get(row: Int, column: Int): Cell {
        boundsCheck(rows, columns, row, column)
        return Cell(row, column, this)
    }

    inner class ChangeSet {
        private val _moves = LinkedList<Pair<Move.Type, Point>>()

        val moves: List<Pair<Move.Type, Point>> get() = Collections.unmodifiableList(_moves)

        fun reveal(row: Int, column: Int) {
            boundsCheck(field.rows, field.columns, row, column)
            _moves.add(Pair(Move.Type.Reveal, Point(row, column)))
        }

        fun flag(row: Int, column: Int) {
            boundsCheck(field.rows, field.columns, row, column)
            _moves.add(Pair(Move.Type.Flag, Point(row, column)))
        }

        fun unflag(row: Int, column: Int) {
            boundsCheck(field.rows, field.columns, row, column)
            _moves.add(Pair(Move.Type.RemoveFlag, Point(row, column)))
        }
    }

    private fun isRedundant(type: Move.Type, point: Point) = when (type) {
        Move.Type.Reveal -> isRevealedUnchecked(field, state, point.first, point.second)
        Move.Type.Flag -> isFlaggedUnchecked(field, state, point.first, point.second)
        Move.Type.RemoveFlag -> !isFlaggedUnchecked(field, state, point.first, point.second)
    }

    private val stack = ArrayDeque<List<Pair<Move.Type, Point>>>()

    private fun applyChanges(changes: List<Pair<Move.Type, Point>>) {
        for ((type, point) in changes) {
            when (type) {
                Move.Type.Reveal -> {
                    setRevealed(point.first, point.second, true)
                    revealed++
                }
                Move.Type.Flag -> setFlagged(point.first, point.second, true)
                Move.Type.RemoveFlag -> setFlagged(point.first, point.second, false)
            }
        }
    }

    private fun revertChanges(changes: List<Pair<Move.Type, Point>>) {
        for ((type, point) in changes) {
            when (type) {
                Move.Type.Reveal -> {
                    setRevealed(point.first, point.second, false)
                    revealed--
                }
                Move.Type.Flag -> setFlagged(point.first, point.second, false)
                Move.Type.RemoveFlag -> setFlagged(point.first, point.second, true)
            }
        }
    }

    fun push(move: Move): Pair<State, List<Point>> {
        val changeSet = ChangeSet()
        move.execute(this, changeSet)

        val reduced = changeSet.moves.filterNot { (type, point) -> isRedundant(type, point) }
        applyChanges(reduced)

        val affected = reduced.map { it.second }

        val result = when {
            reduced.any {
                it.first == Move.Type.Reveal && isMine(
                    it.second.first,
                    it.second.second
                )
            } -> Pair(State.Loss, affected)
            revealed == field.fields - field.mines -> Pair(State.Win, affected)
            else -> Pair(State.Neutral, affected)
        }

        stack.push(reduced)

        return result
    }

    fun pop(): Boolean {
        if (stack.isEmpty()) return false

        val top = stack.pop()
        revertChanges(top)

        return true
    }

    fun clear() {
        Arrays.fill(state, 0)
        revealed = 0
        flagged = 0
    }

    // endregion Implementation

    // region Util

    private companion object {
        @JvmStatic
        private fun createEmptyDataArray(rows: Int, columns: Int) =
            ByteArray(Math.ceil(rows * columns / 4.0).toInt()) { 0 }

        @JvmStatic
        private fun isRevealedUnchecked(field: Field, data: ByteArray, row: Int, column: Int): Boolean {
            val whichField = field.columns * row + column
            val whichByte = whichField ushr 2
            val whichQuarter = whichField % 4
            val shift = whichQuarter shl 1

            val bits = data[whichByte].toInt() ushr shift
            return (bits and 1) == 1
        }

        @JvmStatic
        private fun isFlaggedUnchecked(field: Field, data: ByteArray, row: Int, column: Int): Boolean {
            val whichField = field.columns * row + column
            val whichByte = whichField ushr 2
            val whichQuarter = whichField % 4
            val shift = whichQuarter shl 1

            val bits = data[whichByte].toInt() ushr shift
            return (bits and 0b10) == 0b10
        }

        @JvmField
        val CREATOR = object : Parcelable.Creator<Board> {
            override fun createFromParcel(parcel: Parcel): Board {
                val field = parcel.readParcelable<Field>(Board::class.java.classLoader)
                    ?: throw IllegalStateException("Field could not be deparceled")
                val state = createEmptyDataArray(field.rows, field.columns)
                parcel.readByteArray(state)
                return Board(field, state)
            }

            override fun newArray(size: Int) = arrayOfNulls<Board>(size)
        }
    }

    // endregion Util

    // region Parcelable

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        with(parcel) {
            writeParcelable(field, 0)
            writeByteArray(state)
        }
    }

    override fun describeContents() = 0

    // endregion Parcelable

    // region Serializable

    private fun readObject(stream: ObjectInputStream) {
        stream.defaultReadObject()
        recalculate()
    }

    // endregion Serializable

}