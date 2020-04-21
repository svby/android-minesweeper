package me.stuhlmeier.minesweeper.game

import android.os.Parcel
import android.os.Parcelable
import me.stuhlmeier.minesweeper.Point
import me.stuhlmeier.minesweeper.boundsCheck
import me.stuhlmeier.minesweeper.game.moves.Move
import java.io.ObjectInputStream
import java.io.Serializable
import java.util.*
import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

class Board private constructor(
    private val field: Field, private var data: ByteArray, state: State, started: Boolean, revealed: Int, flagged: Int
) : Parcelable, Serializable {

    enum class State { Win, Loss, Neutral }

    val rows get() = field.rows
    val columns get() = field.columns
    val mines get() = field.mines

    var started = started
        private set

    var state = state
        private set

    fun reset() {
        restart()
        started = false
    }

    fun restart() {
        state = State.Neutral
        data = createEmptyDataArray(field.rows, field.columns)
        revealed = 0
        flagged = 0
    }

    @Transient
    var revealed = revealed
        private set

    @Transient
    var flagged = flagged
        private set

    constructor(field: Field)
            : this(Field(field), createEmptyDataArray(field.rows, field.columns), State.Neutral, false, 0, 0)

    private constructor(field: Field, state: ByteArray, started: Boolean)
            : this(field, state, State.Neutral, started, 0, 0) {
        recalculate()
    }

    // region Implementation

    private fun recalculate() {
        revealed = 0
        flagged = 0
        for (row in 0 until rows) for (column in 0 until columns) {
            if (isRevealedUnchecked(field, data, row, column)) {
                revealed++
                if (field[row, column]) state = State.Loss
            } else if (isFlaggedUnchecked(field, data, row, column)) flagged++
        }
        if (state == State.Neutral && revealed == field.rows * field.columns - field.mines) state = State.Win
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
        return isRevealedUnchecked(field, data, row, column)
    }

    fun isFlagged(row: Int, column: Int): Boolean {
        boundsCheck(rows, columns, row, column)
        return isFlaggedUnchecked(field, data, row, column)
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

        val oldValue = data[whichByte]
        val newValue = if (revealed) oldValue or (1 shl shift).toByte() else oldValue and (1 shl shift).toByte().inv()

        data[whichByte] = newValue
    }

    fun setFlagged(row: Int, column: Int, flagged: Boolean) {
        val whichField = field.columns * row + column
        val whichByte = whichField ushr 2
        val whichQuarter = whichField % 4
        val shift = (whichQuarter shl 1) + 1

        val oldValue = data[whichByte]
        val newValue = if (flagged) oldValue or (1 shl shift).toByte() else oldValue and (1 shl shift).toByte().inv()

        if (oldValue != newValue) this.flagged += when (flagged) {
            true -> 1
            false -> -1
        }

        data[whichByte] = newValue
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
        Move.Type.Reveal -> isRevealedUnchecked(field, data, point.first, point.second)
        Move.Type.Flag -> isFlaggedUnchecked(field, data, point.first, point.second)
        Move.Type.RemoveFlag -> !isFlaggedUnchecked(field, data, point.first, point.second)
    }

    private val stack = ArrayDeque<Pair<State, List<Pair<Move.Type, Point>>>>()

    private fun applyChanges(changes: List<Pair<Move.Type, Point>>) {
        for ((type, point) in changes) {
            when (type) {
                Move.Type.Reveal -> {
                    started = true
                    setRevealed(point.first, point.second, true)
                    revealed++
                }
                Move.Type.Flag -> {
                    setFlagged(point.first, point.second, true)
                }
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

    fun push(move: Move): List<Point> {
        if (state != State.Neutral) return emptyList()

        val changeSet = ChangeSet()
        move.execute(this, changeSet)

        val reduced = changeSet.moves.filterNot { (type, point) -> isRedundant(type, point) }
        applyChanges(reduced)

        val affected = reduced.map { it.second }

        val oldState = state

        when {
            reduced.any {
                it.first == Move.Type.Reveal && isMine(
                    it.second.first,
                    it.second.second
                )
            } -> state = State.Loss
            revealed == field.fields - field.mines -> state = State.Win
            else -> state = State.Neutral
        }

        stack.push(Pair(oldState, reduced))

        return affected
    }

    fun pop(): Boolean {
        if (stack.isEmpty()) return false

        val (oldState, top) = stack.pop()
        state = oldState
        revertChanges(top)

        return true
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
                val started = parcel.readInt() == 1
                parcel.readByteArray(state)
                return Board(field, state, started)
            }

            override fun newArray(size: Int) = arrayOfNulls<Board>(size)
        }
    }

    // endregion Util

    // region Parcelable

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        with(parcel) {
            writeParcelable(field, 0)
            writeInt(if (started) 1 else 0)
            writeByteArray(data)
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
