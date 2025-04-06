package com.TATE.PolyHome.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.TATE.PolyHome.R
import com.google.android.material.card.MaterialCardView

/**
 * Activité de choix après sélection d'une maison.
 * L'utilisateur peut choisir entre commandes détaillées ou commandes générales.
 * Une carte de conseils est également affichée à l'entrée.
 */
class ChoiceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choice)

        // Récupère l'identifiant de la maison sélectionnée (transmis via Intent)
        val houseId = intent.getIntExtra("houseId", -1)

        // Bouton : accéder aux commandes détaillées (DeviceListActivity)
        val buttonGestionPrecise = findViewById<Button>(R.id.buttonGestionPrecise)
        buttonGestionPrecise.setOnClickListener {
            val intent = Intent(this, DeviceListActivity::class.java)
            intent.putExtra("houseId", houseId)
            startActivity(intent)
        }

        // Bouton : accéder aux commandes générales (OptionalCommandsActivity)
        val buttonGestionGenerale = findViewById<Button>(R.id.buttonGestionGenerale)
        buttonGestionGenerale.setOnClickListener {
            val intent = Intent(this, OptionalCommandsActivity::class.java)
            intent.putExtra("houseId", houseId)
            startActivity(intent)
        }

        // Carte de conseils avec bouton de fermeture
        val adviceCard = findViewById<MaterialCardView>(R.id.adviceCard)
        val dismissButton = findViewById<Button>(R.id.btnDismissAdvice)
        dismissButton.setOnClickListener {
            adviceCard.visibility = View.GONE
        }
    }
}
