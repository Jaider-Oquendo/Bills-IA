package com.example.billsia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.billsia.data.AppDatabase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentPerfil : Fragment() {

    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etDireccion: EditText
    private lateinit var btnSave: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        // Inicializando vistas
        etNombre = view.findViewById(R.id.etNombre)
        etApellido = view.findViewById(R.id.etApellido)
        etTelefono = view.findViewById(R.id.etTelefono)
        etDireccion = view.findViewById(R.id.etDireccion)
        btnSave = view.findViewById(R.id.btnSave)

        // Seteando el listener del botón de guardar
        btnSave.setOnClickListener { saveProfile() }

        // Cargando los datos del usuario
        loadUserData()

        return view
    }

    private fun loadUserData() {
        val email = FirebaseAuth.getInstance().currentUser?.email
        if (email != null) {
            // Llamada a la base de datos en el hilo IO
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val userDao = AppDatabase.getDatabase(requireContext()).userDao()
                    val user = userDao.getUserByEmail(email)

                    // Si el usuario fue encontrado, actualizar UI en el hilo principal
                    if (user != null) {
                        withContext(Dispatchers.Main) {
                            etNombre.setText(user.nombre)
                            etApellido.setText(user.apellido)
                            etTelefono.setText(user.telefono)
                            etDireccion.setText(user.direccion)
                        }
                    }
                } catch (e: Exception) {
                    // Manejo de errores en caso de que falle la consulta
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(requireContext(), "No se encuentra sesión iniciada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProfile() {
        val email = FirebaseAuth.getInstance().currentUser?.email
        if (email != null) {
            // Llamada a la base de datos en el hilo IO
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val userDao = AppDatabase.getDatabase(requireContext()).userDao()
                    val user = userDao.getUserByEmail(email)

                    if (user != null) {
                        val updatedUser = user.copy(
                            nombre = etNombre.text.toString(),
                            apellido = etApellido.text.toString(),
                            direccion = etDireccion.text.toString(),
                            telefono = etTelefono.text.toString()
                        )

                        // Actualizando el usuario en la base de datos
                        userDao.updateUser(updatedUser)

                        // Mostrando mensaje en el hilo principal
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    // Manejo de errores en caso de que falle la actualización
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error al actualizar el perfil", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(requireContext(), "No se encuentra sesión iniciada", Toast.LENGTH_SHORT).show()
        }
    }
}
