package com.TATE.PolyHome.models

/**
 * Représente les identifiants envoyés lors de la connexion d'un utilisateur.
 *
 * Utilisé dans l'appel POST :
 * /api/users/auth
 *
 * @property login Nom d'utilisateur
 * @property password Mot de passe de l'utilisateur
 */
data class UserLoginRequest(
    val login: String,
    val password: String
)
