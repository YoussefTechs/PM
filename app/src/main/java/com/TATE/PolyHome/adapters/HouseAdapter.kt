package com.TATE.PolyHome.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.TATE.PolyHome.R
import com.TATE.PolyHome.models.House

/**
 * Adaptateur pour afficher la liste des maisons dans un RecyclerView.
 * Chaque élément montre l'ID de la maison, le rôle de l'utilisateur et un bouton de gestion.
 */
class HouseAdapter(
    private var houses: List<House>, // Liste des maisons
    private val onHouseClick: (House) -> Unit, // Callback quand on clique sur la maison
    private val onManageUsersClick: (House) -> Unit // Callback pour le bouton "Gérer les utilisateurs"
) : RecyclerView.Adapter<HouseAdapter.HouseViewHolder>() {

    /**
     * ViewHolder représentant un item de maison.
     */
    inner class HouseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val houseIdTextView: TextView = view.findViewById(R.id.textViewHouseId)
        val ownerStatusTextView: TextView = view.findViewById(R.id.textViewOwnerStatus)
        val manageUsersButton: Button = view.findViewById(R.id.buttonManageUsers)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HouseViewHolder {
        // On utilise un layout représentant un seul item (ligne) de maison
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_houses_list, parent, false)
        return HouseViewHolder(view)
    }

    override fun onBindViewHolder(holder: HouseViewHolder, position: Int) {
        val house = houses[position]

        // Affichage des données de la maison
        holder.houseIdTextView.text = "Maison #${house.houseId}"
        holder.ownerStatusTextView.text = if (house.owner) "Propriétaire" else "Invité"

        // Action quand on clique sur l'item (voir périphériques par exemple)
        holder.itemView.setOnClickListener {
            onHouseClick(house)
        }

        // Action quand on clique sur le bouton "Gérer les utilisateurs"
        holder.manageUsersButton.setOnClickListener {
            onManageUsersClick(house)
        }
    }

    override fun getItemCount(): Int = houses.size

    /**
     * Met à jour la liste des maisons et rafraîchit l'affichage.
     */
    fun updateHouses(newHouses: List<House>) {
        houses = newHouses
        notifyDataSetChanged()
    }
}
