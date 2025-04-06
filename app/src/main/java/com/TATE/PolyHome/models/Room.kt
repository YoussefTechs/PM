package com.TATE.PolyHome.models

/**
 * Représente une pièce (room) dans une maison.
 * @property id Identifiant unique de la pièce (interne à l'app, pas fourni par l'API)
 * @property name Nom de la pièce (ex: "Salon", "Chambre", etc.)
 * @property devices Liste des périphériques présents dans cette pièce
 */
data class Room(
    val id: Int,
    val name: String,
    val devices: MutableList<Device> = mutableListOf()
)
