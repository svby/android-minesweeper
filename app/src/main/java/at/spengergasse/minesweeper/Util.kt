package at.spengergasse.minesweeper

import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import java.math.BigInteger

const val KEY_BOARD = "minesweeper:state:board"

const val KEY_PRESET = "minesweeper:config:preset"
const val KEY_ROWS = "minesweeper:config:rows"
const val KEY_COLUMNS = "minesweeper:config:columns"
const val KEY_MINES = "minesweeper:config:mines"
const val KEY_SAFE = "minesweeper:config:safe"

const val PRESET_EASY = 0
const val PRESET_MEDIUM = 1
const val PRESET_HARD = 2

const val EASY_ROWS = 9
const val EASY_COLUMNS = 9
const val EASY_MINES = 10

const val MEDIUM_ROWS = 16
const val MEDIUM_COLUMNS = 16
const val MEDIUM_MINES = 40

const val HARD_ROWS = 30
const val HARD_COLUMNS = 16
const val HARD_MINES = 99

const val MAX_SIZE = 99

typealias Point = Pair<Int, Int>

val ViewGroup.children: Sequence<View> get() = (0 until childCount).asSequence().map(::getChildAt)

val ViewGroup.childrenRecursive: Sequence<View>
    get() = sequence {
        (0 until childCount).asSequence().map(::getChildAt).forEach {
            yield(it)
            if (it is ViewGroup) yieldAll(it.childrenRecursive)
        }
    }

val INT_MAX = BigInteger.valueOf(Int.MAX_VALUE.toLong())

fun EditText.getInt(limit: BigInteger = INT_MAX): Int {
    return text.toString().toBigIntegerOrNull()?.min(limit)?.toInt() ?: 0
}

fun EditText.getBigInteger(): BigInteger {
    return text.toString().toBigIntegerOrNull() ?: BigInteger.ZERO
}

fun <T> MutableLiveData<T>.notify() {
    value = value
}

fun toPx(value: Float, resources: Resources) =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, resources.displayMetrics)

fun boundsCheck(rows: Int, columns: Int, row: Int, column: Int) {
    if (row !in 0 until rows) throw ArrayIndexOutOfBoundsException(row)
    if (column !in 0 until columns) throw ArrayIndexOutOfBoundsException(column)
}