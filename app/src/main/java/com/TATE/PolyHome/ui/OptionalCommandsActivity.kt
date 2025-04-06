package com.TATE.PolyHome.ui

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.TATE.PolyHome.R
import com.TATE.PolyHome.models.Device
import com.TATE.PolyHome.models.DeviceCommand
import com.TATE.PolyHome.models.DevicesResponse
import com.TATE.PolyHome.network.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OptionalCommandsActivity : AppCompatActivity() {

    private val api = Api()
    private var token: String? = null
    private var houseId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_optional_commands)

        houseId = intent.getIntExtra("houseId", -1)
        if (houseId == -1) {
            Toast.makeText(this, "Erreur: ID maison invalide", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val sharedPref = getSharedPreferences("PolyHome", Context.MODE_PRIVATE)
        token = sharedPref.getString("token", null)

        if (token == null) {
            Toast.makeText(this, "Vous devez vous connecter", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Boutons pour les lumières
        findViewById<Button>(R.id.buttonTurnOffAllLights).setOnClickListener {
            lifecycleScope.launch { performDeviceAction("light", "TURN OFF", "Lumières éteintes") }
        }

        findViewById<Button>(R.id.buttonTurnOnAllLights).setOnClickListener {
            lifecycleScope.launch { performDeviceAction("light", "TURN ON", "Lumières allumées") }
        }

        // Boutons pour les volets
        findViewById<Button>(R.id.buttonOpenAllShutters).setOnClickListener {
            lifecycleScope.launch { performDeviceAction("rolling shutter", "OPEN", "Volets ouverts") }
        }

        findViewById<Button>(R.id.buttonCloseAllShutters).setOnClickListener {
            lifecycleScope.launch { performDeviceAction("rolling shutter", "CLOSE", "Volets fermés") }
        }
        findViewById<Button>(R.id.buttonCloseAllAndTurnOffLights).setOnClickListener {
            lifecycleScope.launch {
                performDeviceAction("rolling shutter", "CLOSE", "Volets fermés")
                performDeviceAction("garage door", "CLOSE", "Volets fermés")
                performDeviceAction("light", "TURN OFF", "Lumières éteintes")
            }
        }

        findViewById<Button>(R.id.btnOpenAllAndTurnOnLights).setOnClickListener {
            lifecycleScope.launch {
                performDeviceAction("rolling shutter", "OPEN", "Volets ouverts")
                performDeviceAction("garage door", "OPEN", "Volets ouvert")

                performDeviceAction("light", "TURN ON", "Lumières allumées")
            }
        }
    }


    private suspend fun performDeviceAction(typeFilter: String, command: String, actionDescription: String) {
        val url = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices"
        withContext(Dispatchers.IO) {
            api.get<DevicesResponse>(
                url,
                securityToken = token,
                onSuccess = { code, response ->
                    if (code == 200 && response != null) {
                        val devices = response.devices.filter { it.type.contains(typeFilter, true) }
                        lifecycleScope.launch {
                            executeCommands(devices, command, actionDescription, url)
                        }
                    } else {
                        lifecycleScope.launch(Dispatchers.Main) {
                            Toast.makeText(this@OptionalCommandsActivity, "Erreur chargement périphériques", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }
    }

    private suspend fun executeCommands(
        devices: List<Device>,
        command: String,
        actionDescription: String,
        baseUrl: String
    ) {
        val results = withContext(Dispatchers.IO) {
            devices.map { device ->
                async {
                    var resultCode = -1
                    api.post<DeviceCommand>(
                        path = "$baseUrl/${device.id}/command",
                        data = DeviceCommand(command),
                        onSuccess = { code -> resultCode = code },
                        securityToken = token
                    )
                    resultCode
                }
            }.awaitAll()
        }

        val success = results.count { it == 200 }


    }

}
