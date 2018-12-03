package at.spengergasse.minesweeper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.game_toolbar)
        setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(toolbar, findNavController(R.id.nav_host))
    }

}
