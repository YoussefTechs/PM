package com.TATE.PolyHome.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.TATE.PolyHome.R

/**
 * Activité "À propos" de l'application.
 * Affiche des informations générales sur l'app (version, développeur, liens, etc.).
 */
class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Affiche le layout associé à la page À propos
        setContentView(R.layout.activity_about)
    }
}
