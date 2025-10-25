package com.mapb.gestfv

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private var esAdmin: Boolean = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializamos Firebase Auth
        auth = com.google.firebase.Firebase.auth

        // Variables de botones y edittext de la interfaz
        val etEmail: EditText = findViewById(R.id.et_email_login)
        val etPasswd: EditText = findViewById(R.id.et_contrasena_login)
        val botonIniciarSesion: Button = findViewById(R.id.boton_iniciar_sesion)
        val botonRegistrarse: Button = findViewById(R.id.boton_registrate_aqui)
        val botonRestablecerCredenciales: TextView = findViewById(R.id.tv_restablecer_credenciales)
        val botonObtenerAyuda: TextView = findViewById(R.id.tv_obtener_ayuda)

        //
        botonIniciarSesion.setOnClickListener {
            if (etEmail.text.isEmpty()) {
                etEmail.error = "Campo vacio"
            }
            if (etPasswd.text.isEmpty()) {
                etPasswd.error = "Campo vacio"
            }
            if (etEmail.getText().isNotEmpty() && etPasswd.getText().isNotEmpty()) {
                iniciarSesion(etEmail.getText().toString(), etPasswd.getText().toString())
            } else {
                Toast.makeText(
                    baseContext,
                    "Algunos campos estan vacíos.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }

        //
        botonRegistrarse.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegistrarUsuarioActivity::class.java))
        }

        botonRestablecerCredenciales.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RestablecerCredencialesActivity::class.java))
        }

        botonObtenerAyuda.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ObtenerAyudaActivity::class.java))
        }
    }

    public override fun onStart() = run {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("usuarios")
                .whereEqualTo("uid", auth.currentUser?.uid.toString())
                .get().addOnSuccessListener { result ->
                    for (document in result) {
                        var admin: Boolean = document.data["admin"] as Boolean
                        if (admin) {
                            esAdmin = true
                            Log.d("Login Admin", "signInWithEmail:success")
                            Toast.makeText(
                                baseContext,
                                "Recuperando inicio de sesion.",
                                Toast.LENGTH_SHORT,
                            ).show()
                            startActivity(
                                Intent(
                                    this@LoginActivity,
                                    MenuAdminActivity::class.java
                                )
                            )
                            finish()
                        } else if (!admin) {
                            esAdmin = false
                            Log.d("Login Normal", "signInWithEmail:success")
                            Toast.makeText(
                                baseContext,
                                "Recuperando inicio de sesion.",
                                Toast.LENGTH_SHORT,
                            ).show()
                            startActivity(
                                Intent(
                                    this@LoginActivity,
                                    MenuPrincipalActivity::class.java
                                )

                            )
                            finish()
                        } else {
                            Toast.makeText(
                                baseContext,
                                "Error, no se reconoce el usuario, puede que haya sido borrado.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
                }.addOnFailureListener { exception ->
                    Log.w("Login", "Error obteniendo tipo de usuario.", exception)
                }
        }
    }

    //
    private fun iniciarSesion(email: String, password: String) = run {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                db.collection("usuarios")
                    .whereEqualTo("uid", auth.currentUser?.uid.toString())
                    .get().addOnSuccessListener { result ->
                        for (document in result) {
                            var admin: Boolean = document.data["admin"] as Boolean
                            if (admin) {
                                Log.d("Login Admin", "signInWithEmail:success")
                                Toast.makeText(
                                    baseContext,
                                    "Inicio de sesion correcto.",
                                    Toast.LENGTH_SHORT,
                                ).show()
                                startActivity(
                                    Intent(
                                        this@LoginActivity,
                                        MenuAdminActivity::class.java
                                    )
                                )
                                finish()
                            } else if (!admin) {
                                Log.d("Login Normal", "signInWithEmail:success")
                                Toast.makeText(
                                    baseContext,
                                    "Inicio de sesion correcto.",
                                    Toast.LENGTH_SHORT,
                                ).show()
                                startActivity(
                                    Intent(
                                        this@LoginActivity,
                                        MenuPrincipalActivity::class.java
                                    )
                                )
                                finish()
                            }
                        }
                    }.addOnFailureListener { exception ->
                        Log.w("Login", "Error obteniendo tipo de usuario.", exception)
                    }
            } else {
                Log.w("Login", "signInWithEmail:failure", task.exception)
                Toast.makeText(
                    baseContext,
                    "Usuario o contraseña incorrectos.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }
}