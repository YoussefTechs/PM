package com.TATE.PolyHome.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.TATE.PolyHome.R
import com.TATE.PolyHome.models.DeviceCommand
import com.TATE.PolyHome.models.Room
import com.TATE.PolyHome.network.Api
import com.TATE.PolyHome.models.DevicesResponse


class DeviceListActivity : AppCompatActivity() {
    private val api = Api()
    private var token: String? = null
    private var houseId: Int = -1
    private val rooms = mutableListOf<Room>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devices)

        // Récupérer l'ID de la maison passé en paramètre
        houseId = intent.getIntExtra("houseId", -1)
        if (houseId == -1) {
            Toast.makeText(this, "Erreur: ID de maison non valide", Toast.LENGTH_SHORT).show()
            Log.e("DeviceListActivity", "ID de maison non valide")
            finish()
            return
        }

        Log.d("DeviceListActivity", "ID de maison reçu: $houseId")

        // Récupérer le token stocké
        val sharedPref = getSharedPreferences("PolyHome", Context.MODE_PRIVATE)
        token = sharedPref.getString("token", null)
        if (token == null) {
            Toast.makeText(this, "Vous devez vous connecter", Toast.LENGTH_SHORT).show()
            Log.e("DeviceListActivity", "Token non trouvé dans les SharedPreferences")
            finish()
            return
        }

        Log.d("DeviceListActivity", "Token récupéré: ${token?.substring(0, Math.min(10, token?.length ?: 0))}...")

        // Configurer le titre
        title = "Périphériques - Maison #$houseId"

        // Configurer le bouton de déconnexion
        //val logoutButton = findViewById<Button>(R.id.buttonLogout)
        //logoutButton.setOnClickListener {
         //   logout()
        //}


        // Charger les périphériques
        loadDevices()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.device_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                loadDevices()
                true
            }
            R.id.action_back -> {
                onBackPressed()
                true
            }
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadDevices() {
        Toast.makeText(this, "Chargement des périphériques...", Toast.LENGTH_SHORT).show()

        val url = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices"
        Log.d("DeviceListActivity", "Envoi de la requête à : $url")

        api.get<DevicesResponse>(
            url,
            onSuccess = { code, response ->
                Log.d("DeviceListActivity", "Code de réponse : $code")

                if (code == 200) {
                    if (response != null) {
                        // Extract the list of devices from the response
                        val deviceList = response.devices
                        Log.d("DeviceListActivity", "Réponse non null")
                        Log.d("DeviceListActivity", "Taille de la réponse : ${deviceList.size}")

                        // Réinitialise les pièces
                        rooms.clear()

                        // Création des pièces
                        val livingRoom = Room(1, "Salon")
                        val bedroom = Room(2, "Chambre")
                        val kitchen = Room(3, "Cuisine")
                        val outside = Room(4, "Extérieur")

                        // Répartition des périphériques
                        for (device in deviceList) {
                            val id = device.id.lowercase()

                            when {
                                id.contains("light") -> livingRoom.devices.add(device)
                                id.contains("shutter") && !id.contains("rolling") -> bedroom.devices.add(device)
                                id.contains("rolling") -> kitchen.devices.add(device)
                                id.contains("garage") -> outside.devices.add(device)
                                else -> livingRoom.devices.add(device) // Par défaut
                            }
                        }

                        // Ajoute uniquement les pièces contenant des devices
                        if (livingRoom.devices.isNotEmpty()) rooms.add(livingRoom)
                        if (bedroom.devices.isNotEmpty()) rooms.add(bedroom)
                        if (kitchen.devices.isNotEmpty()) rooms.add(kitchen)
                        if (outside.devices.isNotEmpty()) rooms.add(outside)

                        runOnUiThread {
                            updateRoomsUI()
                            Toast.makeText(
                                this,
                                "${deviceList.size} périphériques répartis dans ${rooms.size} pièces",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Log.d("DeviceListActivity", "Réponse null malgré code $code")
                        runOnUiThread {
                            Toast.makeText(this, "Aucun périphérique trouvé", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else if (code == 403) {
                    Log.e("DeviceListActivity", "Accès interdit (403)")
                    runOnUiThread {
                        Toast.makeText(this, "Accès non autorisé", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    Log.e("DeviceListActivity", "Erreur lors du chargement (code $code)")
                    runOnUiThread {
                        Toast.makeText(
                            this,
                            "Erreur lors du chargement des périphériques (code $code)",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            securityToken = token
        )
    }


    private fun updateRoomsUI() {
        val container = findViewById<LinearLayout>(R.id.roomsContainer)
        container.removeAllViews()

        for (room in rooms) {
            // Inflate le layout de la salle
            val roomContainer = layoutInflater.inflate(R.layout.room_containers, container, false)

            // Récupère et configure le header de la salle
            val roomNameTextView = roomContainer.findViewById<TextView>(R.id.textViewRoomName)
            roomNameTextView.text = room.name  // Définit le nom de la salle

            // Récupère le conteneur des périphériques dans la salle
            val deviceContainer = roomContainer.findViewById<LinearLayout>(R.id.deviceContainer)

            // Pour chaque périphérique dans cette salle, on l'inflate et on le configure
            for (device in room.devices) {
                val deviceView = layoutInflater.inflate(R.layout.item_device, deviceContainer, false)
                deviceView.findViewById<TextView>(R.id.textViewDeviceId).text = device.id
                deviceView.findViewById<TextView>(R.id.textViewDeviceType).text =
                    "Type: ${getReadableDeviceType(device.type)}"
                val statusText = when {
                    device.opening != null -> "Ouverture: ${device.opening}"
                    device.power != null -> "Puissance: ${device.power}"
                    else -> "Statut inconnu"
                }
                deviceView.findViewById<TextView>(R.id.textViewDeviceStatus).text = statusText

                // Configuration des commandes
                val commandsLayout = deviceView.findViewById<LinearLayout>(R.id.layoutCommands)
                commandsLayout.removeAllViews()
                for (command in device.availableCommands) {
                    val button = layoutInflater.inflate(
                        R.layout.device_commands,
                        commandsLayout,
                        false
                    ) as com.google.android.material.button.MaterialButton
                    button.text = getCommandLabel(command)
                    button.setOnClickListener {
                        sendCommand(device.id, command)
                    }
                    commandsLayout.addView(button)
                }

                deviceContainer.addView(deviceView)
            }

            container.addView(roomContainer)
        }
    }



    private fun sendCommand(deviceId: String, command: String) {
        val deviceCommand = DeviceCommand(command)
        Log.d("DeviceListActivity", "Envoi de la commande '$command' au périphérique '$deviceId'")

        api.post<DeviceCommand>(
            "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$deviceId/command",
            deviceCommand,
            onSuccess = { code ->
                Log.d("DeviceListActivity", "Réponse pour la commande: code $code")

                if (code == 200) {
                    Log.d("DeviceListActivity", "Commande $command envoyée avec succès à $deviceId")
                    runOnUiThread {
                        Toast.makeText(this, "Commande envoyée", Toast.LENGTH_SHORT).show()
                        // Recharger les périphériques pour voir les changements
                        loadDevices()
                    }
                } else {
                    Log.e("DeviceListActivity", "Erreur lors de l'envoi de la commande (code $code)")
                    runOnUiThread {
                        Toast.makeText(this, "Erreur lors de l'envoi de la commande (code $code)", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            securityToken = token
        )
    }

    private fun logout() {
        val sharedPref = getSharedPreferences("PolyHome", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove("token")
            remove("userLogin")
            apply()
        }

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun getReadableDeviceType(type: String): String {
        return when (type) {
            "sliding-shutter" -> "Volet coulissant"
            "rolling-shutter" -> "Volet roulant"
            "garage-door" -> "Porte de garage"
            "light" -> "Lumière"
            else -> type
        }
    }

    private fun getCommandLabel(command: String): String {
        return when (command) {
            "open" -> "Ouvrir"
            "close" -> "Fermer"
            "on" -> "Allumer"
            "off" -> "Éteindre"
            "stop" -> "Stop"
            else -> command
        }
    }
}