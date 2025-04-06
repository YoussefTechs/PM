package com.TATE.PolyHome.models

/**
 * Représente une requête pour ajouter un utilisateur à une maison.
 * Utilisé lors de l'appel POST /houses/{houseId}/users
 */
data class AddUserToHouseRequest(
    val userLogin: String
)
