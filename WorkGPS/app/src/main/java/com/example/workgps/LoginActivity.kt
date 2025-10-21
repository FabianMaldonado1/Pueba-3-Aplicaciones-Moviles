package com.example.workgps

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        // 🔹 Verificar si ya hay sesión guardada
        val prefs = getSharedPreferences("login", MODE_PRIVATE)
        val savedUser = prefs.getString("usuario", null)
        val savedRole = prefs.getString("rol", null)

        if (savedUser != null && savedRole != null) {
            when (savedRole) {
                "admin" -> {
                    startActivity(Intent(this, EmployerActivity::class.java).apply {
                        putExtra("usuario", savedUser)
                    })
                    finish()
                }
                "trabajador" -> {
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        putExtra("usuario", savedUser)
                    })
                    finish()
                }
            }
        }

        btnLogin.setOnClickListener {
            val user = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (user.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa usuario y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔹 Determinar rol automáticamente
            val role = if (user.equals("admin", ignoreCase = true) || user.equals("empleador", ignoreCase = true)) {
                "admin"
            } else {
                "trabajador"
            }

            // 🔹 Guardar sesión
            val prefsEditor = getSharedPreferences("login", MODE_PRIVATE).edit()
            prefsEditor.putString("usuario", user)
            prefsEditor.putString("rol", role)
            prefsEditor.apply()

            // 🔹 Redirigir según el rol
            if (role == "admin") {
                startActivity(Intent(this, EmployerActivity::class.java).apply {
                    putExtra("usuario", user)
                })
            } else {
                startActivity(Intent(this, MainActivity::class.java).apply {
                    putExtra("usuario", user)
                })
            }

            finish()
        }
    }
}
