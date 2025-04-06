package com.TATE.PolyHome.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.TATE.PolyHome.R
import com.TATE.PolyHome.adapters.HouseAdapter
import com.TATE.PolyHome.models.AddUserToHouseRequest
import com.TATE.PolyHome.models.House
import com.TATE.PolyHome.network.Api

class HousesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HouseAdapter
    private val api = Api()
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ✅ Bouton "À propos"
        val btnAbout = findViewById<Button>(R.id.btnAbout)
        btnAbout.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }

        // Récupérer le token depuis les préférences
        val sharedPref = getSharedPreferences("PolyHome", Context.MODE_PRIVATE)
        token = sharedPref.getString("token", null)

        if (token == null) {
            // Rediriger vers login si pas de token
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Configurer le RecyclerView
        recyclerView = findViewById(R.id.recyclerViewHouses)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = HouseAdapter(
            emptyList(),
            onHouseClick = { house ->
                val intent = Intent(this, ChoiceActivity::class.java)
                intent.putExtra("houseId", house.houseId)
                startActivity(intent)
            },
            onManageUsersClick = { house ->
                val intent = Intent(this, UsersActivity::class.java)
                intent.putExtra("houseId", house.houseId.toString())
                startActivity(intent)
            }
        )

        recyclerView.adapter = adapter

        // Charger les maisons
        loadHouses()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_house -> {
                showAddHouseDialog()
                true
            }
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadHouses() {
        api.get<List<House>>(
            "https://polyhome.lesmoulinsdudev.com/api/houses",
            onSuccess = { code, response ->
                if (code == 200 && response != null) {
                    Log.d("HousesActivity", "Maisons reçues: ${response.size}")
                    runOnUiThread {
                        adapter.updateHouses(response)
                    }
                } else if (code == 403) {
                    runOnUiThread {
                        Toast.makeText(this, "Session expirée, veuillez vous reconnecter", Toast.LENGTH_SHORT).show()
                        clearToken()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Erreur lors du chargement des maisons", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            securityToken = token
        )
    }

    private fun showAddHouseDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Attribuer une maison")

        val input = EditText(this)
        input.hint = "Entrez l'ID de la maison"
        builder.setView(input)

        builder.setPositiveButton("Attribuer") { dialog, _ ->
            val houseIdText = input.text.toString()
            if (houseIdText.isNotEmpty()) {
                try {
                    val houseId = houseIdText.toInt()
                    addUserToHouse(houseId)
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "L'ID de la maison doit être un nombre", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Annuler") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun addUserToHouse(houseId: Int) {
        val userLogin = getUserLoginFromSharedPreferences()
        if (userLogin == null) {
            Toast.makeText(this, "Information utilisateur manquante", Toast.LENGTH_SHORT).show()
            return
        }

        val request = AddUserToHouseRequest(userLogin = userLogin)
        api.post<AddUserToHouseRequest>(
            "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users",
            request,
            onSuccess = { code ->
                runOnUiThread {
                    when (code) {
                        200 -> {
                            Toast.makeText(this, "Accès à la maison accordé avec succès", Toast.LENGTH_SHORT).show()
                            loadHouses()
                        }
                        403 -> Toast.makeText(this, "Accès interdit. Vous n'êtes pas propriétaire.", Toast.LENGTH_SHORT).show()
                        409 -> Toast.makeText(this, "Vous avez déjà accès à cette maison", Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(this, "Erreur lors de l'attribution de la maison (code $code)", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            securityToken = token
        )
    }

    private fun logout() {
        clearToken()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun clearToken() {
        val sharedPref = getSharedPreferences("PolyHome", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove("token")
            remove("userLogin")
            apply()
        }
    }

    private fun getUserLoginFromSharedPreferences(): String? {
        val sharedPref = getSharedPreferences("PolyHome", Context.MODE_PRIVATE)
        return sharedPref.getString("userLogin", null)
    }
}
