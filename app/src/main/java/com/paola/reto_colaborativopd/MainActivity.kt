package com.paola.reto_colaborativopd

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var etUsuario: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnIngresar: Button
    private lateinit var tvResultado: TextView

    private val prefs by lazy { getSharedPreferences("sesion", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etUsuario = findViewById(R.id.etUsuario)
        etPassword = findViewById(R.id.etPassword)
        btnIngresar = findViewById(R.id.btnIngresar)
        tvResultado = findViewById(R.id.tvResultado)

        btnIngresar.setOnClickListener {
            val usuario = etUsuario.text.toString().trim()
            val password = etPassword.text.toString()
            if (usuario.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Escribe usuario y contraseña", Toast.LENGTH_SHORT).show()
            } else {
                hacerLogin(usuario, password)
            }
        }

        // Mejora 3: si ya hay token guardado, saltar el formulario
        val tokenGuardado = prefs.getString("token", null)
        if (tokenGuardado != null) {
            mostrarFormulario(false)
            pedirDatos(tokenGuardado)
        }
    }

    // Paso 7: login -> guardar token -> pedir datos
    private fun hacerLogin(usuario: String, password: String) {
        lifecycleScope.launch {
            try {
                val respuesta = RetrofitClient.api.login(LoginRequest(usuario, password))

                if (respuesta.isSuccessful && respuesta.body() != null) {
                    val token = respuesta.body()!!.token
                    prefs.edit().putString("token", token).apply()
                    mostrarFormulario(false)
                    pedirDatos(token)
                } else {
                    // RETO FINAL: el servidor rechazó las credenciales
                    Toast.makeText(
                        this@MainActivity,
                        "Login fallido: usuario o contraseña incorrectos",
                        Toast.LENGTH_LONG
                    ).show()
                    tvResultado.text = "Login fallido"
                }
            } catch (e: Exception) {
                // RETO FINAL: no hubo conexión con el servidor
                Toast.makeText(this@MainActivity, "No se pudo conectar", Toast.LENGTH_LONG).show()
                tvResultado.text = "Error de conexión"
            }
        }
    }

    private fun pedirDatos(token: String) {
        lifecycleScope.launch {
            try {
                val respuesta = RetrofitClient.api.getUsuario("Bearer $token")
                val usuario = respuesta.body()

                if (respuesta.isSuccessful && usuario != null) {
                    tvResultado.text = "Nombre: ${usuario.nombre}\nCorreo: ${usuario.correo}"
                } else {
                    // Token vencido o inválido: borrarlo y volver al formulario
                    prefs.edit().remove("token").apply()
                    mostrarFormulario(true)
                    tvResultado.text = "La sesión expiró, ingresa de nuevo"
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "No se pudo conectar", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun mostrarFormulario(mostrar: Boolean) {
        val visibilidad = if (mostrar) android.view.View.VISIBLE else android.view.View.GONE
        etUsuario.visibility = visibilidad
        etPassword.visibility = visibilidad
        btnIngresar.visibility = visibilidad
    }
}