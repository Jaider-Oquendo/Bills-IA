package com.example.billsia

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.billsia.data.entities.UserEntity
import com.example.billsia.data.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

class CompleteProfileActivity : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_profile)

        auth = FirebaseAuth.getInstance()
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val btnSave = findViewById<Button>(R.id.btnSave)
        val etCedula = findViewById<EditText>(R.id.etCedula)
        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etApellido = findViewById<EditText>(R.id.etApellido)
        val etDireccion = findViewById<EditText>(R.id.etDireccion)
        val etTelefono = findViewById<EditText>(R.id.etTelefono)

        btnSave.setOnClickListener {
            val cedula = etCedula.text.toString().trim()
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val direccion = etDireccion.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()

            val email = auth.currentUser?.email
            if (email.isNullOrEmpty()) {
                Toast.makeText(this, "No se pudo obtener el correo electrónico. Inicia sesión nuevamente.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (cedula.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || direccion.isEmpty() || telefono.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userEntity = UserEntity(
                cedula = cedula,
                nombre = nombre,
                apellido = apellido,
                email = email,
                password = "", // Vacío porque viene de Google
                direccion = direccion,
                telefono = telefono
            )

            userViewModel.insertUser(userEntity)

            Toast.makeText(this, "Perfil completado correctamente", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
        }
    }
}
