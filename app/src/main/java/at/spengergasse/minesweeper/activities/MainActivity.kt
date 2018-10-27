package at.spengergasse.minesweeper.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import at.spengergasse.minesweeper.*
import kotlinx.android.synthetic.main.activity_main.*
import java.math.BigInteger

class MainActivity : AppCompatActivity() {

    companion object {

        private val MAX_ROWS_BIGINTEGER = BigInteger.valueOf(MAX_ROWS.toLong())
        private val MAX_COLUMNS_BIGINTEGER = BigInteger.valueOf(MAX_COLUMNS.toLong())

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        layout_settings.visibility = View.GONE

        group_difficulty.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_easy, R.id.radio_medium -> layout_settings.visibility = View.GONE
                R.id.radio_custom -> layout_settings.visibility = View.VISIBLE
            }
        }

        button_start.setOnClickListener {
            layout_settings.childrenRecursive.forEach { it.isEnabled = true }
            val intent = Intent(this, GameActivity::class.java).apply {
                putExtra(EXTRA_SAFE, switch_safe.isChecked)

                when (group_difficulty.checkedRadioButtonId) {
                    R.id.radio_easy -> {
                        putExtra(EXTRA_ROWS, 9)
                        putExtra(EXTRA_COLUMNS, 9)
                        putExtra(EXTRA_MINES, 10)
                    }
                    R.id.radio_medium -> {
                        putExtra(EXTRA_ROWS, 16)
                        putExtra(EXTRA_COLUMNS, 16)
                        putExtra(EXTRA_MINES, 40)
                    }
                    R.id.radio_custom -> {
                        val rows = text_height.getInt(MAX_ROWS_BIGINTEGER)
                        val columns = text_width.getInt(MAX_COLUMNS_BIGINTEGER)

                        putExtra(EXTRA_ROWS, rows)
                        putExtra(EXTRA_COLUMNS, columns)
                        putExtra(EXTRA_MINES, text_mines.getInt(BigInteger.valueOf((rows * columns).toLong())))
                    }
                }
            }
            startActivity(intent)
        }
    }

}
