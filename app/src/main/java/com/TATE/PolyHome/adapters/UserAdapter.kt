package com.TATE.PolyHome.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.TATE.PolyHome.R

/**
 * Adaptateur pour afficher les utilisateurs associés à une maison dans un RecyclerView.
 * Affiche le login, le rôle (propriétaire ou invité), et un bouton pour retirer un utilisateur.
 */
class UserAdapter(
    private val users: List<Map<String, Any>>,              // Liste d'utilisateurs (données brutes reçues de l'API)
    private val onRemoveClick: (String) -> Unit              // Callback pour supprimer un utilisateur
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    /**
     * ViewHolder représentant un utilisateur dans la liste.
     */
    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userLoginTextView: TextView = view.findViewById(R.id.textViewUserLogin)
        val roleTextView: TextView = view.findViewById(R.id.textViewRole)
        val btnRemove: Button = view.findViewById(R.id.btnRemoveUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_users, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        val login = user["userLogin"]?.toString().orEmpty()         // Récupère le login, ou chaîne vide si null
        val isOwner = user["owner"] as? Boolean ?: false           // Vérifie si l'utilisateur est propriétaire

        holder.userLoginTextView.text = login
        holder.roleTextView.text = if (isOwner) "Propriétaire" else "Invité"

        // Vérifie si l'utilisateur affiché est l'utilisateur actuellement connecté
        val sharedPref = holder.itemView.context.getSharedPreferences("PolyHome", Context.MODE_PRIVATE)
        val currentLogin = sharedPref.getString("userLogin", null)

        // Ne pas afficher le bouton "Supprimer" pour le propriétaire ou pour soi-même
        if (isOwner || login == currentLogin) {
            holder.btnRemove.visibility = View.GONE
        } else {
            holder.btnRemove.visibility = View.VISIBLE
            holder.btnRemove.setOnClickListener {
                onRemoveClick(login)
            }
        }
    }

    override fun getItemCount(): Int = users.size
}
