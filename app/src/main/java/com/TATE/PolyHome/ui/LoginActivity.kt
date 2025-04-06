package com.TATE.PolyHome.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.TATE.PolyHome.R
import com.TATE.PolyHome.models.UserLoginRequest
import com.TATE.PolyHome.models.UserLoginResponse
import com.TATE.PolyHome.network.Api

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Références aux éléments de l'interface utilisateur
        val emailInput = findViewById<EditText>(R.id.txtMail)
        val passwordInput = findViewById<EditText>(R.id.txtPassword)
        val loginButton = findViewById<Button>(R.id.btnConect)
        val registerButton = findViewById<Button>(R.id.btnGoToRegister)

        // Gestion du bouton de connexion
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Veuillez entrer vos identifiants", Toast.LENGTH_SHORT).show()
            }
        }

        // Gestion du bouton d'inscription
        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        Log.d("LoginActivity", "Tentative de connexion avec $email")

        val api = Api()
        val loginRequest = UserLoginRequest(login = email, password = password)

        val jsonEnvoye = api.toJSON(loginRequest)
        Log.d("LoginActivity", "Données envoyées: $jsonEnvoye")
        api.post<UserLoginRequest, UserLoginResponse>(
            "https://polyhome.lesmoulinsdudev.com/api/users/auth",
            loginRequest,
            onSuccess = { code, response ->
                Log.d("LoginActivity", "Réponse reçue: $code")

                if (code == 200 && response != null) {
                    Log.d("LoginActivity", "Token: ${response.token}")

                    val sharedPref = getSharedPreferences("PolyHome", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("token", response.token)
                        putString("userLogin", email)
                        apply()
                    }

                    runOnUiThread {
                        Toast.makeText(this, "Connexion réussie", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, HousesActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Identifiants incorrects", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            securityToken = null
        )
    }
}