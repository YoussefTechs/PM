package com.TATE.PolyHome.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.TATE.PolyHome.R
import com.TATE.PolyHome.models.UserRegisterRequest
import com.TATE.PolyHome.network.Api
import android.util.Log


class RegisterActivity : AppCompatActivity() {

    private lateinit var edtLogin: EditText
    private lateinit var edtPassword: EditText
   // private lateinit var edtMail: EditText
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register) // <-- votre layout

        // 1. Récupérer les références des vues
        edtLogin = findViewById(R.id.txtRegisterName)
        //edtMail = findViewById(R.id.txtRegisterMail)
        edtPassword = findViewById(R.id.txtRegisterPassword)
        btnRegister = findViewById(R.id.btnRegister)

        // 2. Gérer le clic sur le bouton "Enregistrer"
        btnRegister.setOnClickListener {
            val login = edtLogin.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            // Vérifier si les champs ne sont pas vides
            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(login, password)
            }
        }
    }

    private fun registerUser(login: String, password: String) {
        // Préparer la requête à envoyer
        val registerRequest = UserRegisterRequest(login = login, password = password)
        Log.d("RegisterActivity", "Envoi de la requête d'inscription pour : $login")
        // Appeler la fonction POST de l’API
        Api().post<UserRegisterRequest>(
            path = "https://polyhome.lesmoulinsdudev.com/api/users/register",
            data = registerRequest,
            onSuccess = { code ->
                // Le code s’exécute dans un thread secondaire (Dispatchers.IO).
                // Pour interagir avec l’UI (Toast, Intent...), repassez sur le thread principal :
                Log.d("RegisterActivity", "Code de réponse de l'inscription : $code")

                runOnUiThread {
                    when (code) {
                        200 -> {
                            Toast.makeText(this, "Inscription réussie", Toast.LENGTH_SHORT).show()
                            // Rediriger éventuellement vers l’écran de connexion
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        409 -> {
                            Toast.makeText(this, "Le login est déjà utilisé", Toast.LENGTH_SHORT).show()
                        }
                        400 -> {
                            Toast.makeText(this, "Données incorrectes", Toast.LENGTH_SHORT).show()
                        }
                        500 -> {
                            Toast.makeText(this, "Erreur côté serveur", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(this, "Erreur inconnue : $code", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        )
    }
}
