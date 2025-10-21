package com.example.workgps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var tvShift: TextView
    private lateinit var btnCheckIn: Button
    private lateinit var btnCheckOut: Button
    private lateinit var btnLogout: Button

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var usuario: String? = null
    private val LOCATION_PERMISSION_REQUEST = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvWelcome = findViewById(R.id.tvWelcome)
        tvShift = findViewById(R.id.tvShift)
        btnCheckIn = findViewById(R.id.btnCheckIn)
        btnCheckOut = findViewById(R.id.btnCheckOut)
        btnLogout = findViewById(R.id.btnLogout)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        usuario = intent.getStringExtra("usuario")
        tvWelcome.text = "Bienvenido, $usuario"
        tvShift.text = "Tu turno comienza a las 09:00 AM"

        btnCheckIn.setOnClickListener { getRealLocation("Entrada") }
        btnCheckOut.setOnClickListener { getRealLocation("Salida") }

        btnLogout.setOnClickListener {
            cerrarSesion()
        }
    }

    private fun getAddressFromLocation(location: Location): String {
        return try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                "${address.thoroughfare ?: ""} ${address.subThoroughfare ?: ""}, ${address.locality ?: ""}"
            } else {
                "Lat: ${location.latitude}, Lon: ${location.longitude}"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Lat: ${location.latitude}, Lon: ${location.longitude}"
        }
    }

    private fun getRealLocation(tipo: String) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val direccion = getAddressFromLocation(location)
                saveAttendance(direccion, tipo)
            } else {
                Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveAttendance(direccion: String, tipo: String) {
        val hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val prefs = getSharedPreferences("registros", MODE_PRIVATE)
        val editor = prefs.edit()
        val registrosExistentes = prefs.getString("lista", "") ?: ""
        val nuevoRegistro = "$usuario - $tipo - $hora - $direccion\n"
        editor.putString("lista", registrosExistentes + nuevoRegistro)
        editor.apply()

        val intent = Intent(this, ConfirmActivity::class.java).apply {
            putExtra("hora", hora)
            putExtra("ubicacion", direccion)
            putExtra("tipo", tipo)
        }
        startActivity(intent)
    }

    private fun cerrarSesion() {
        val prefs = getSharedPreferences("login", MODE_PRIVATE)
        prefs.edit().clear().apply() // borra el usuario y rol guardados

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
