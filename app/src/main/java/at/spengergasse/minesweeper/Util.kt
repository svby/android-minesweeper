package at.spengergasse.minesweeper

import android.view.View
import android.view.ViewGroup

const val EXTRA_ROWS = "at.spengergasse.minesweeper.ROWS"
const val EXTRA_COLUMNS = "at.spengergasse.minesweeper.COLUMNS"
const val EXTRA_MINES = "at.spengergasse.minesweeper.MINES"
const val EXTRA_SAFE = "at.spengergasse.minesweeper.SAFE"

typealias Point = Pair<Int, Int>

val ViewGroup.children: Sequence<View> get() = (0 until childCount).asSequence().map(::getChildAt)

val ViewGroup.childrenRecursive: Sequence<View>
    get() = sequence {
        (0 until childCount).asSequence().map(::getChildAt).forEach {
            yield(it)
            if (it is ViewGroup) yieldAll(it.childrenRecursive)
        }
    }