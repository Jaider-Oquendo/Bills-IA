package com.example.billsia

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.billsia.data.entities.UserEntity
import com.example.billsia.data.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private fun showAlert(title: String, message: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK", null)
        builder.show()
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var userViewModel: UserViewModel

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var etCedula: EditText
    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText
    private lateinit var etDireccion: EditText
    private lateinit var etTelefono: EditText
    private lateinit var ivTogglePassword: ImageView
    private lateinit var ivToggleConfirm: ImageView
    private lateinit var btnRegister: Button
    private lateinit var tvGoToLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        // Referencias UI
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        etCedula = findViewById(R.id.etCedula)
        etNombre = findViewById(R.id.etNombre)
        etApellido = findViewById(R.id.etApellido)
        etDireccion = findViewById(R.id.etDireccion)
        etTelefono = findViewById(R.id.etTelefono)
        ivTogglePassword = findViewById(R.id.ivTogglePassword)
        ivToggleConfirm = findViewById(R.id.ivToggleConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvGoToLogin = findViewById(R.id.tvGoToLogin)

        // Toggle visibilidad contraseña
        var pwdVisible = false
        ivTogglePassword.setOnClickListener {
            pwdVisible = !pwdVisible
            etPassword.inputType =
                if (pwdVisible) InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            etPassword.setSelection(etPassword.text.length)
            ivTogglePassword.setImageResource(
                if (pwdVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off
            )
        }

        // Toggle visibilidad confirmación
        var confirmVisible = false
        ivToggleConfirm.setOnClickListener {
            confirmVisible = !confirmVisible
            etConfirmPassword.inputType =
                if (confirmVisible) InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            etConfirmPassword.setSelection(etConfirmPassword.text.length)
            ivToggleConfirm.setImageResource(
                if (confirmVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off
            )
        }

        btnRegister.setOnClickListener { registerUser() }
        tvGoToLogin.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }

    private fun registerUser() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirm = etConfirmPassword.text.toString().trim()
        val cedula = etCedula.text.toString().trim()
        val nombre = etNombre.text.toString().trim()
        val apellido = etApellido.text.toString().trim()
        val direccion = etDireccion.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            toast("Correo inválido"); return
        }
        if (!Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}\$").matches(password)) {
            showAlert(
                "Contraseña inválida",
                "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial."
            )
            return
        }

        if (password != confirm) {
            toast("Las contraseñas no coinciden"); return
        }
        if (cedula.isEmpty() || nombre.isEmpty()) {
            toast("Cédula y nombre obligatorios"); return
        }

        // 1) Verificar email local
        userViewModel.fetchUserByEmail(email)
        userViewModel.userByEmail.observe(this) { userWithEmail ->
            if (userWithEmail != null) {
                toast("Ya existe un usuario registrado con ese correo")
                return@observe
            }
        // 2) Verificar cédula local
            userViewModel.getUserByCedula(cedula).observe(this) { existing ->
                if (existing != null) {
                    toast("Cédula ya registrada"); return@observe
                }

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = UserEntity(cedula, nombre, apellido, email, password, direccion, telefono)
                            lifecycleScope.launch(Dispatchers.IO) {
                                userViewModel.insertUser(user)
                            }
                            toast("Registro exitoso")
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        } else {
                            toast("Error: ${task.exception?.message}")
                        }
                    }
            }
        }

    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
