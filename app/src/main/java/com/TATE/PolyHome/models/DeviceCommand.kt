package com.TATE.PolyHome.models

/**
 * Représente une commande envoyée à un périphérique.
 *
 * Utilisé dans l'appel POST :
 * /houses/{houseId}/devices/{deviceId}/command
 *
 * @property command Nom de la commande à exécuter ("on", "off", "open", "close")
 */
data class DeviceCommand(
    val command: String
)
