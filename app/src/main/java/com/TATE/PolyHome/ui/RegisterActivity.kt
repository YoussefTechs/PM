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

/**
 * Écran d'inscription utilisateur.
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var edtLogin: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        edtLogin = findViewById(R.id.txtRegisterName)
        edtPassword = findViewById(R.id.txtRegisterPassword)
        btnRegister = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val login = edtLogin.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(login, password)
            }
        }
    }

    private fun registerUser(login: String, password: String) {
        val registerRequest = UserRegisterRequest(login, password)
        Log.d("RegisterActivity", "Inscription pour : $login")

        Api().post<UserRegisterRequest>(
            path = "https://polyhome.lesmoulinsdudev.com/api/users/register",
            data = registerRequest,
            onSuccess = { code ->
                runOnUiThread {
                    when (code) {
                        200 -> {
                            Toast.makeText(this, "Inscription réussie", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                        409 -> Toast.makeText(this, "Login déjà utilisé", Toast.LENGTH_SHORT).show()
                        400 -> Toast.makeText(this, "Données incorrectes", Toast.LENGTH_SHORT).show()
                        500 -> Toast.makeText(this, "Erreur serveur", Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(this, "Erreur inconnue : $code", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }
}
