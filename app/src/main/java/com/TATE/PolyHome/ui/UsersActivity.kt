package com.TATE.PolyHome.ui

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.TATE.PolyHome.R
import com.TATE.PolyHome.adapters.UserAdapter
import com.TATE.PolyHome.network.Api
import com.TATE.PolyHome.ui.dialogs.AddUserDialogFragment

class UsersActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private val users = mutableListOf<Map<String, Any>>()

    private var houseId: String? = null
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        // Récupérer l'ID de la maison
        houseId = intent.getStringExtra("houseId")

        // Récupérer le token d'auth depuis SharedPreferences
        val sharedPref = getSharedPreferences("PolyHome", Context.MODE_PRIVATE)
        token = sharedPref.getString("token", null)

        if (houseId == null || token == null) {
            Toast.makeText(this, "Erreur de chargement", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        recyclerView = findViewById(R.id.recyclerViewUsers)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UserAdapter(users) { userLogin ->
            removeUser(userLogin)
        }
        recyclerView.adapter = adapter

        findViewById<Button>(R.id.btnAddUser).setOnClickListener {
            showAddUserDialog()
        }

        loadUsers()
    }

    private fun loadUsers() {
        Api().get<List<Map<String, Any>>>(
            path = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users",
            onSuccess = { code, result ->
                runOnUiThread {
                    if (code == 200 && result != null) {
                        users.clear()
                        users.addAll(result)
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this, "Erreur chargement (code $code)", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            securityToken = token
        )
    }

    private fun removeUser(userLogin: String) {
        val currentUser = users.find { it["userLogin"] == userLogin }
        val isOwner = currentUser?.get("owner") as? Boolean ?: false

        val sharedPref = getSharedPreferences("PolyHome", Context.MODE_PRIVATE)
        val currentLogin = sharedPref.getString("userLogin", null)

        if (userLogin == currentLogin) {
            Toast.makeText(this, "Vous ne pouvez pas vous retirer vous-même.", Toast.LENGTH_SHORT).show()
            return
        }

        if (isOwner) {
            Toast.makeText(this, "Impossible de supprimer le propriétaire", Toast.LENGTH_SHORT).show()
            return
        }

        val data = mapOf("userLogin" to userLogin)
        Api().delete(
            path = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users",
            data = data,
            onSuccess = { code ->
                runOnUiThread {
                    if (code == 200) {
                        Toast.makeText(this, "Utilisateur supprimé", Toast.LENGTH_SHORT).show()
                        loadUsers()
                    } else {
                        Toast.makeText(this, "Erreur suppression (code $code)", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            securityToken = token
        )
    }


    private fun showAddUserDialog() {
        val dialog = AddUserDialogFragment { userLogin ->
            addUser(userLogin)
        }
        dialog.show(supportFragmentManager, "AddUserDialog")
    }

    private fun addUser(userLogin: String) {
        val data = mapOf("userLogin" to userLogin)
        Api().post(
            path = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users",
            data = data,
            onSuccess = { code ->
                runOnUiThread {
                    if (code == 200) {
                        Toast.makeText(this, "Utilisateur ajouté", Toast.LENGTH_SHORT).show()
                        loadUsers()
                    } else {
                        Toast.makeText(this, "Erreur ajout (code $code)", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            securityToken = token
        )
    }
}
