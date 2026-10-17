package com.paola.reto_colaborativopd

import android.os.Bundle
import android.util.Log
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

        // Si ya hay token guardado en SharedPreferences, saltar el formulario
        val tokenGuardado = prefs.getString("token", null)
        if (tokenGuardado != null) {
            mostrarFormulario(false)
            pedirDatos(tokenGuardado)
        }
    }

    // PASO A: POST auth/login -> recibir accessToken -> manejar errores
    private fun hacerLogin(usuario: String, password: String) {
        lifecycleScope.launch {
            try {
                val respuesta = RetrofitClient.api.login(LoginRequest(usuario, password))

                if (respuesta.isSuccessful && respuesta.body() != null) {
                    val token = respuesta.body()?.accessToken
                    if (!token.isNullOrEmpty()) {
                        Log.d("API", "Token recibido correctamente: $token")
                        // NOTA DE SEGURIDAD: Guardar el token en SharedPreferences sin cifrar representa un riesgo
                        // si alguien accede al almacenamiento local del dispositivo.
                        prefs.edit().putString("token", token).apply()
                        mostrarFormulario(false)
                        pedirDatos(token)
                    } else {
                        Log.e("API", "accessToken es nulo o vacío")
                        tvResultado.text = "Error: Token nulo"
                    }
                } else {
                    // Manejo de error cuando el servidor rechaza las credenciales (e.g., 401/400)
                    Log.e("API", "Login falló con código: ${respuesta.code()}")
                    Toast.makeText(
                        this@MainActivity,
                        "Login fallido: usuario o contraseña incorrectos",
                        Toast.LENGTH_LONG
                    ).show()
                    tvResultado.text = "Login fallido (Error ${respuesta.code()})"
                }
            } catch (e: Exception) {
                // Manejo de error de conexión/red
                Log.e("API", "Error de conexión: ${e.message}")
                Toast.makeText(this@MainActivity, "No se pudo conectar al servidor", Toast.LENGTH_LONG).show()
                tvResultado.text = "Error de conexión"
            }
        }
    }

    // La función recibe el token por parámetro para ser reutilizable y no depender de variables globales
    private fun pedirDatos(token: String) {
        lifecycleScope.launch {
            try {
                val respuesta = RetrofitClient.api.getCurrentUser("Bearer $token")
                val usuario = respuesta.body()

                if (respuesta.isSuccessful && usuario != null) {
                    Log.d("API", "Usuario obtenido: ${usuario.firstName} - ${usuario.email}")
                    tvResultado.text = "Nombre: ${usuario.firstName}\nCorreo: ${usuario.email}"
                } else {
                    // Token vencido o inválido: borrarlo y volver al formulario
                    Log.e("API", "Error al obtener usuario. Código: ${respuesta.code()}")
                    prefs.edit().remove("token").apply()
                    mostrarFormulario(true)
                    tvResultado.text = "La sesión expiró o es inválida, ingresa de nuevo"
                }
            } catch (e: Exception) {
                Log.e("API", "Error de red al pedir datos: ${e.message}")
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