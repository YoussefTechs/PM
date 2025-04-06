package com.TATE.PolyHome.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.TATE.PolyHome.R
import com.TATE.PolyHome.models.Device

/**
 * Adaptateur pour afficher une liste de périphériques dans un RecyclerView.
 * Chaque périphérique affiche son ID, type, statut, et des boutons pour envoyer des commandes.
 */
class DeviceAdapter(
    private var devices: List<Device>, // Liste des périphériques à afficher
    private val onCommandClick: (Device, String) -> Unit // Callback quand on clique sur une commande
) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    /**
     * ViewHolder pour un périphérique.
     * Contient les vues nécessaires pour afficher les infos et les commandes.
     */
    inner class DeviceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val deviceIdTextView: TextView = view.findViewById(R.id.textViewDeviceId)
        val deviceTypeTextView: TextView = view.findViewById(R.id.textViewDeviceType)
        val deviceStatusTextView: TextView = view.findViewById(R.id.textViewDeviceStatus)
        val commandsLayout: LinearLayout = view.findViewById(R.id.layoutCommands)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        // Inflate le layout XML pour chaque élément de la liste
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = devices[position]

        // Affiche l'ID et le type du périphérique
        holder.deviceIdTextView.text = device.id
        holder.deviceTypeTextView.text = "Type : ${getReadableDeviceType(device.type)}"

        // Affiche le statut (ouverture ou puissance selon le type)
        holder.deviceStatusTextView.text = when {
            device.opening != null -> "Ouverture : ${device.opening}%"
            device.power != null -> "Puissance : ${device.power}%"
            else -> "Statut inconnu"
        }

        // Nettoie les boutons de commandes précédents
        holder.commandsLayout.removeAllViews()

        // Crée dynamiquement un bouton pour chaque commande disponible
        device.availableCommands.forEach { command ->
            val button = Button(holder.itemView.context).apply {
                text = getCommandLabel(command) // Libellé lisible en français
                setOnClickListener { onCommandClick(device, command) } // Action à exécuter
            }
            holder.commandsLayout.addView(button) // Ajoute le bouton au layout
        }
    }

    override fun getItemCount(): Int = devices.size

    /**
     * Permet de mettre à jour la liste des périphériques à afficher.
     */
    fun updateDevices(newDevices: List<Device>) {
        devices = newDevices
        notifyDataSetChanged()
    }

    /**
     * Traduit les types techniques en libellés lisibles pour l'utilisateur.
     */
    private fun getReadableDeviceType(type: String): String = when (type) {
        "sliding-shutter" -> "Volet coulissant"
        "rolling-shutter" -> "Volet roulant"
        "garage-door" -> "Porte de garage"
        "light" -> "Lumière"
        else -> type // Affiche brut si inconnu
    }

    /**
     * Traduit les commandes techniques en libellés lisibles pour les boutons.
     */
    private fun getCommandLabel(command: String): String = when (command) {
        "open" -> "Ouvrir"
        "close" -> "Fermer"
        "on" -> "Allumer"
        "off" -> "Éteindre"
        "stop" -> "Stop"
        else -> command
    }
}
