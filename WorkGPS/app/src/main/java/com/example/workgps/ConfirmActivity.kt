package com.example.workgps

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class ConfirmActivity : AppCompatActivity() {

    private lateinit var tvConfirm: TextView
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm)

        tvConfirm = findViewById(R.id.tvConfirmation)
        btnBack = findViewById(R.id.btnBack)

        // Recibir datos de MainActivity
        val hora = intent.getStringExtra("hora")
        val ubicacion = intent.getStringExtra("ubicacion")
        val tipo = intent.getStringExtra("tipo")?: "Entrada/Salida"

        tvConfirm.text = "$tipo registrada a las $hora en $ubicacion"

        btnBack.setOnClickListener {
            finish() // Vuelve a MainActivity
        }
    }
}
