package com.TATE.PolyHome.models

/**
 * Représente une maison à laquelle l'utilisateur a accès.
 *
 * Utilisé dans la réponse de l'API :
 * GET /houses
 *
 * @property houseId Identifiant unique de la maison
 * @property owner Indique si l'utilisateur est propriétaire (true) ou invité (false)
 */
data class House(
    val houseId: Int,
    val owner: Boolean
)
