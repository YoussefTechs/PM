package com.TATE.PolyHome.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

/**
 * Boîte de dialogue permettant d'ajouter un utilisateur à une maison.
 * Appelle le callback [onUserAdded] avec le login saisi si le champ n'est pas vide.
 */
class AddUserDialogFragment(
    private val onUserAdded: (String) -> Unit // Callback déclenché si l'utilisateur confirme
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val input = EditText(requireContext())
        input.hint = "Login de l'utilisateur"

        return AlertDialog.Builder(requireContext())
            .setTitle("Ajouter un utilisateur")
            .setView(input)
            .setPositiveButton("Ajouter") { _, _ ->
                val login = input.text.toString()
                if (login.isNotBlank()) {
                    onUserAdded(login)
                }
            }
            .setNegativeButton("Annuler", null)
            .create()
    }
}
