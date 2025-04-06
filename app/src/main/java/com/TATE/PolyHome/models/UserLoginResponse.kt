package com.TATE.PolyHome.models

/**
 * Représente la réponse renvoyée par l'API après une connexion réussie.
 *
 * Utilisé dans :
 * POST /api/users/auth
 */
data class UserLoginResponse(
    val token: String
)
