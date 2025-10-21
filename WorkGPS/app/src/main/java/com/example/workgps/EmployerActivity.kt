package com.example.workgps

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EmployerActivity : AppCompatActivity() {

    private lateinit var tvRegistros: TextView
    private lateinit var btnClear: Button
    private lateinit var btnLogoutEmployer: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employer)

        tvRegistros = findViewById(R.id.tvRegistros)
        btnClear = findViewById(R.id.btnClear)
        btnLogoutEmployer = findViewById(R.id.btnLogoutEmployer)

        mostrarRegistros()

        btnClear.setOnClickListener {
            val prefs = getSharedPreferences("registros", MODE_PRIVATE)
            prefs.edit().remove("lista").apply()
            tvRegistros.text = "No hay registros todavía."
            Toast.makeText(this, "Registros borrados", Toast.LENGTH_SHORT).show()
        }

        btnLogoutEmployer.setOnClickListener {
            cerrarSesion()
        }
    }

    private fun mostrarRegistros() {
        val prefs = getSharedPreferences("registros", MODE_PRIVATE)
        val registros = prefs.getString("lista", "")
        tvRegistros.text = if (registros.isNullOrEmpty()) {
            "No hay registros todavía."
        } else {
            registros
        }
    }

    private fun cerrarSesion() {
        val prefs = getSharedPreferences("login", MODE_PRIVATE)
        prefs.edit().clear().apply()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
