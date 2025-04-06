package com.TATE.PolyHome.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.TATE.PolyHome.R
import com.google.android.material.card.MaterialCardView

class ChoiceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choice)

        val houseId = intent.getIntExtra("houseId", -1)

        // ✅ Bouton 1 - Precise Commands
        val buttonGestionPrecise = findViewById<Button>(R.id.buttonGestionPrecise)
        buttonGestionPrecise.setOnClickListener {
            val intent = Intent(this, DeviceListActivity::class.java)
            intent.putExtra("houseId", houseId)
            startActivity(intent)
        }

        // ✅ Bouton 2 - General Commands
        val buttonGestionGenerale = findViewById<Button>(R.id.buttonGestionGenerale)
        buttonGestionGenerale.setOnClickListener {
            val intent = Intent(this, OptionalCommandsActivity::class.java)
            intent.putExtra("houseId", houseId)
            startActivity(intent)
        }

        // ✅ Bouton pour fermer la carte de conseils
        val adviceCard = findViewById<MaterialCardView>(R.id.adviceCard)
        val dismissButton = findViewById<Button>(R.id.btnDismissAdvice)
        dismissButton.setOnClickListener {
            adviceCard.visibility = View.GONE
        }
    }
}
