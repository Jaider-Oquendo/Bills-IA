package com.example.billsia

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Usamos un Handler para esperar 2 segundos antes de ir a la siguiente actividad
        Handler().postDelayed({
            // Iniciar la HomeActivity después de la espera
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Finalizar la actividad Splash para que no se quede en el stack
        }, 2000) // 2000 milisegundos = 2 segundos
    }
}
