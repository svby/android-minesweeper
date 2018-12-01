package at.spengergasse.minesweeper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import at.spengergasse.minesweeper.ui.settings.SettingsFragment

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container_settings, SettingsFragment())
                .commitNow()
        }
    }
}
