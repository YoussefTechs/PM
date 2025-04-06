package com.TATE.PolyHome.models

/**
 * Représente un périphérique connecté dans une maison.
 *
 * Utilisé lors de la récupération des périphériques via l'API :
 * GET /houses/{houseId}/devices
 *
 * @property id Identifiant unique du périphérique
 * @property type Type du périphérique (ex: "light", "garage-door", etc.)
 * @property availableCommands Liste des commandes possibles (ex: ["on", "off"])
 * @property opening Pourcentage d'ouverture (null si non applicable)
 * @property power Puissance allumée (null si non applicable)
 */
data class Device(
    val id: String,
    val type: String,
    val availableCommands: List<String>,
    val opening: Double? = null,
    val power: Double? = null
)
