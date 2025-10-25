package com.mapb.gestfv

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class RestablecerCredencialesActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var etEmail: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restablecer_credenciales)

        // Inicializamos Firebase Auth
        auth = com.google.firebase.Firebase.auth

        //
        etEmail = findViewById(R.id.et_email_usuario_registro_usuario)
        var botonRestablecer: Button = findViewById(R.id.boton_restablecer_credenciales)

        //
        botonRestablecer.setOnClickListener {
            if (etEmail.text.isNotEmpty()) {
                auth.sendPasswordResetEmail(etEmail.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("Restablecer credenciales", "Email enviado.")
                            Toast.makeText(
                                this, "Compruebe en su correo electrónico el email enviado para restablecer sus credenciales.", Toast.LENGTH_LONG
                            ).show()
                            etEmail.text.clear()
                        }
                    }.addOnFailureListener {
                        Log.d("Restablecer credenciales", "Error al enviar email de recuperación.")
                        Toast.makeText(
                            this, "Error, el email es inválido o no está registrado", Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                Toast.makeText(
                    this, "Error, el campo email no puede estar vacío", Toast.LENGTH_SHORT
                ).show()
                etEmail.error = "Campo vacio"
            }
        }
    }
}