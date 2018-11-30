package at.spengergasse.minesweeper

import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import java.math.BigInteger

const val EXTRA_ROWS = "at.spengergasse.minesweeper.ROWS"
const val EXTRA_COLUMNS = "at.spengergasse.minesweeper.COLUMNS"
const val EXTRA_MINES = "at.spengergasse.minesweeper.MINES"
const val EXTRA_SAFE = "at.spengergasse.minesweeper.SAFE"

const val MAX_ROWS = 30
const val MAX_COLUMNS = 20

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