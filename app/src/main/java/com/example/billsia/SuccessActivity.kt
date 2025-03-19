package com.example.billsia

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)

        val successMessage = findViewById<TextView>(R.id.successMessage)
        val btnContinue = findViewById<Button>(R.id.btnContinue)

        // Acción del botón
        btnContinue.setOnClickListener {
            // Regresar al inicio o ir a otra actividad principal
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
