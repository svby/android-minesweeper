package at.spengergasse.minesweeper

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

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
                        putExtra(EXTRA_ROWS, text_height.text.toString().toInt())
                        putExtra(EXTRA_COLUMNS, text_width.text.toString().toInt())
                        putExtra(EXTRA_MINES, text_mines.text.toString().toInt())
                    }
                }
            }
            startActivity(intent)
        }
    }

}
