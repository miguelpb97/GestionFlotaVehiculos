package com.mapb.gestfv

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.runBlocking
import java.util.regex.Matcher
import java.util.regex.Pattern

class GestionarPerfilActivity : ComponentActivity() {

    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    lateinit var etCorreoCambioPass: EditText
    lateinit var etActualPass: EditText
    lateinit var etNuevaPass: EditText
    lateinit var etRepetirNuevaPass: EditText
    lateinit var etNuevoTelefono: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestionar_perfil)

        etCorreoCambioPass = findViewById(R.id.et_correo_cambio_pass_usuario)
        etActualPass = findViewById(R.id.et_contrasena_actual_usuario)
        etNuevaPass = findViewById(R.id.et_nueva_pass_usuario)
        etRepetirNuevaPass = findViewById(R.id.et_repetir_nueva_pass_usuario)
        etNuevoTelefono = findViewById(R.id.et_cambio_telefono_usuario)
        val botonCambiarPass: Button = findViewById(R.id.boton_cambiar_pass)
        val botonCambiarTelefono: Button = findViewById(R.id.boton_cambiar_telefono)

        //
        auth = Firebase.auth

        botonCambiarPass.setOnClickListener {
            if (etNuevaPass.text.toString() == etRepetirNuevaPass.text.toString()) {
                if (etCorreoCambioPass.text.isNotBlank() && etActualPass.text.isNotBlank() && etNuevaPass.text.isNotBlank() && etRepetirNuevaPass.text.isNotBlank()) {
                    cambiarCredenciales(
                        etCorreoCambioPass.text.toString(),
                        etActualPass.text.toString(),
                        etNuevaPass.text.toString()
                    )
                } else {
                    Toast.makeText(
                        this, "Faltan datos.", Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT
                ).show()
            }
        }

        botonCambiarTelefono.setOnClickListener {
            if (validarTelefono(etNuevoTelefono.text.toString())) {
                editarTelefono(etNuevoTelefono.text.toString())
            } else {
                Toast.makeText(
                    this, "El teléfono tiene que ser válido.", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /* EN TEORIA ASI SE BORRARIA UN USUARIO PERO NO FUNCIONA
    private fun borrarUsuario(email: String, pass: String) = run {
        val user = auth.currentUser!!

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        val credential = EmailAuthProvider
            .getCredential(email, pass)

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
            .addOnCompleteListener {
                Log.d("Borrar Usuario", "User re-authenticated.")
                auth.currentUser!!.delete()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("Borrar Usuario", "User account deleted.")
                            cerrarSesion()
                        }
                    }
            }
    }
    */

    private fun cambiarCredenciales(email: String, pass: String, nuevaPass: String) = run {
        val user = auth.currentUser!!

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        val credential = EmailAuthProvider
            .getCredential(email, pass)

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
            .addOnCompleteListener {
                Log.d("Cambiar credenciales", "User re-authenticated.")
                auth.currentUser!!.updatePassword(nuevaPass)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("Cambiar credenciales", "Contraseña del usuario cambiada.")
                            etCorreoCambioPass.text.clear()
                            etActualPass.text.clear()
                            etNuevaPass.text.clear()
                            etRepetirNuevaPass.text.clear()
                            finish()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Error al cambiar las credenciales, compruebe que los datos sean correctos.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
    }

    private fun editarTelefono(telefonoNuevo: String) = runBlocking {
        db.collection("usuarios")
            .whereEqualTo("uid", auth.uid)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val data = hashMapOf(
                        "telefono" to telefonoNuevo
                    )
                    db.collection("usuarios").document(document.id).set(data, SetOptions.merge())
                    Log.d("editarTelefono", "Usuario editado.")
                    Toast.makeText(
                        baseContext,
                        "Teléfono editado correctamente.",
                        Toast.LENGTH_LONG,
                    ).show()
                    etNuevoTelefono.text.clear()
                }
            }.addOnFailureListener { exception ->
                Log.w("editarTelefono", "Error al editar.", exception)
                Toast.makeText(
                    baseContext,
                    "Error al editar el teléfono.",
                    Toast.LENGTH_LONG,
                ).show()
            }
    }

    private fun validarTelefono(campo: String): Boolean {
        val pattern: Pattern = Pattern.compile("^[6|7]{1}+[0-9]{8}$")
        val matcher: Matcher = pattern.matcher(campo)
        val matchFound = matcher.find()
        if (!campo.isBlank() && matchFound) {
            return true
        } else {
            return false
            Toast.makeText(
                baseContext,
                "Algunos campos no tienen el formato válido.",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

}