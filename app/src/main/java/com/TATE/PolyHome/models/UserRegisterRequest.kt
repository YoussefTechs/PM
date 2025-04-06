package com.TATE.PolyHome.models

/**
 * Représente les données envoyées lors de la création d'un compte utilisateur.
 *
 * Utilisé dans l'appel POST :
 * /api/users/register
 *
 * @property login Nom d'utilisateur à enregistrer
 * @property password Mot de passe associé
 */
data class UserRegisterRequest(
    val login: String,
    val password: String
)
