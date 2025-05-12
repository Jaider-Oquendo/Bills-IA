package com.example.billsia

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModelProvider
import com.example.billsia.data.viewmodel.UserViewModel


class AuthActivity : AppCompatActivity() {

    // Elementos de la UI
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: SignInClient
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginButton: Button
    private lateinit var googleSignInButton: Button
    private lateinit var registerButton: TextView

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            try {
                val credential = googleSignInClient.getSignInCredentialFromIntent(result.data)
                val idToken = credential.googleIdToken
                if (idToken != null) {
                    firebaseAuthWithGoogle(idToken)
                }
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Error en Google Sign-In", e)
                Toast.makeText(this, "Fallo en Google Sign-In", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        auth = FirebaseAuth.getInstance()
        googleSignInClient = Identity.getSignInClient(this)

        // Enlace con los elementos de la UI
        emailField = findViewById(R.id.etEmail)
        passwordField = findViewById(R.id.etPassword)
        loginButton = findViewById(R.id.btnLogin)
        googleSignInButton = findViewById(R.id.btnGoogleSignIn)
        registerButton = findViewById(R.id.txtRegister)

        // Iniciar sesión con email y contraseña
        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginWithEmail(email, password)
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Iniciar sesión con Google
        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }

        // Redirigir a la pantalla de registro
        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    goToHome()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun signInWithGoogle() {
        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            ).build()

        googleSignInClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                googleSignInLauncher.launch(
                    IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                )
            }
            .addOnFailureListener { e ->
                Log.e("GoogleSignIn", "Error al iniciar sesión con Google", e)
                Toast.makeText(this, "Google Sign-In falló", Toast.LENGTH_SHORT).show()
            }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val email = auth.currentUser?.email
                    if (email != null) {
                        val userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

                        userViewModel.fetchUserByEmail(email)
                        userViewModel.userByEmail.observe(this) { user ->
                            if (user == null) {
                                // El usuario no existe, redirige a completar el perfil
                                startActivity(Intent(this@AuthActivity, CompleteProfileActivity::class.java))
                                finish()
                            } else {
                                // El usuario existe, redirige a la pantalla de inicio
                                goToHome()
                            }
                        }
                    } else {
                        // Si no hay email, redirigir al home
                        goToHome()
                    }
                } else {
                    Toast.makeText(this, "Error autenticando con Google", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun goToHome() {
        startActivity(Intent(this, SplashActivity::class.java))
        finish()
    }
}
