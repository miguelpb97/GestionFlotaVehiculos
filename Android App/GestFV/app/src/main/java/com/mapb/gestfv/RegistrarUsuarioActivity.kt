package com.mapb.gestfv

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.mapb.gestfv.modelo.Usuario
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.regex.Matcher
import java.util.regex.Pattern

class RegistrarUsuarioActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_usuario)

        // Inicializamos Firebase Auth
        auth = Firebase.auth

        //
        val etEmailRegistro: EditText = findViewById(R.id.et_email_usuario_registro_usuario)
        val etPasswdRegistro: EditText = findViewById(R.id.et_passwd_usuario_registro_usuario)
        val etRepetirPasswdRegistro: EditText =
            findViewById(R.id.et_repetir_passwd_usuario_registro_usuario)
        val etDniRegistro: EditText = findViewById(R.id.et_mostrar_dni_registro_usuario)
        val etNombreRegistro: EditText = findViewById(R.id.et_mostrar_nombre_registro_usuario)
        val etTelefonoRegistro: EditText = findViewById(R.id.et_mostrar_telefono_registro_usuario)
        val etFechaNacRegistro: EditText = findViewById(R.id.et_mostrar_fecha_nac_registro_usuario)
        val botonRegistrarse: Button = findViewById(R.id.boton_registrar_usuario)

        //
        var calFechaNac = Calendar.getInstance()

        //
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                calFechaNac.set(Calendar.YEAR, year)
                calFechaNac.set(Calendar.MONTH, monthOfYear)
                calFechaNac.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd/MM/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                etFechaNacRegistro.setText(sdf.format(calFechaNac.time))

                etFechaNacRegistro.error = null
            }

        //
        etFechaNacRegistro.setOnClickListener {
            DatePickerDialog(
                this@RegistrarUsuarioActivity, dateSetListener,
                calFechaNac.get(Calendar.YEAR),
                calFechaNac.get(Calendar.MONTH),
                calFechaNac.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        //
        botonRegistrarse.setOnClickListener {
            if (etEmailRegistro.text.isEmpty()) {
                etEmailRegistro.error = "Campo vacio"
            }
            if (etPasswdRegistro.text.isEmpty()) {
                etPasswdRegistro.error = "Campo vacio"
            }
            if (etRepetirPasswdRegistro.text.isEmpty()) {
                etRepetirPasswdRegistro.error = "Campo vacio"
            }
            if (etDniRegistro.text.isEmpty()) {
                etDniRegistro.error = "Campo vacio"
            }
            if (etNombreRegistro.text.isEmpty()) {
                etNombreRegistro.error = "Campo vacio"
            }
            if (etTelefonoRegistro.text.isEmpty()) {
                etTelefonoRegistro.error = "Campo vacio"
            }
            if (etFechaNacRegistro.text.isEmpty()) {
                etFechaNacRegistro.error = "Campo vacio"
            }
            if (etPasswdRegistro.getText().toString() != etRepetirPasswdRegistro.getText()
                    .toString()
            ) {
                Toast.makeText(
                    baseContext,
                    "Las contraseñas no coinciden.",
                    Toast.LENGTH_SHORT,
                ).show()
            } else {
                if (etEmailRegistro.getText().toString().isNotEmpty()
                    && etPasswdRegistro.getText().toString().isNotEmpty()
                    && etRepetirPasswdRegistro.getText().toString().isNotEmpty()
                    && validarDni(etDniRegistro.getText().toString())
                    && etNombreRegistro.getText().toString().isNotEmpty()
                    && validarTelefono(etTelefonoRegistro.getText().toString())
                    && validarFechaNacimiento(calFechaNac.time)
                ) {
                    crearUsuario(
                        etEmailRegistro.getText().toString(),
                        etPasswdRegistro.getText().toString(),
                        etDniRegistro.getText().toString(),
                        etNombreRegistro.getText().toString(),
                        Timestamp(calFechaNac.time),
                        etTelefonoRegistro.getText().toString()
                    )
                } else {
                    Toast.makeText(
                        baseContext,
                        "Algunos campos estan vacios o incompletos.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        }
    }

    //
    private fun crearUsuario(
        email: String,
        password: String,
        dni: String,
        nombre: String,
        fechaNac: Timestamp,
        telefono: String
    ) = run {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                Log.d("Registro", "createUserWithEmail:success")
                Toast.makeText(
                    baseContext,
                    "Usuario registrado con exito.",
                    Toast.LENGTH_SHORT,
                ).show()
                asignarDatosUsuario(
                    dni,
                    fechaNac,
                    nombre,
                    telefono,
                    user?.uid.toString(),
                    user?.email.toString()
                )
                startActivity(
                    Intent(
                        this@RegistrarUsuarioActivity, MenuPrincipalActivity::class.java
                    )
                )
                finish()
            }
        }.addOnFailureListener {
            Log.w("Registro", "createUserWithEmail:failure")
            Toast.makeText(
                baseContext,
                "Error al registrar usuario. recuerde que la contraseña debe tener al menos una letras mayuscula, minuscula, carácter especial, número y que mínimo tenga 6 carácteres de longitud",
                Toast.LENGTH_LONG,
            ).show()
        }

    }

    fun validarFechaNacimiento(fecha: Date): Boolean {
        val fechaNueva: LocalDate? = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        if (ChronoUnit.YEARS.between(fechaNueva, LocalDate.now()) >= 18) {
            return true
        } else {
            return false
            Toast.makeText(
                baseContext,
                "Debe ser mayor de edad.",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    private fun validarDni(campo: String): Boolean {
        var esValido = false
        val pattern = Pattern.compile("^[0-9]{8}+[A-Za-z]{1}$")
        val matcher = pattern.matcher(campo)
        val matchFound = matcher.find()
        if (matchFound) {
            esValido = true
        } else {
            esValido = false
            Toast.makeText(
                baseContext,
                "Error, formato de DNI inválido",
                Toast.LENGTH_SHORT
            ).show()
        }
        return esValido
    }

    fun validarTelefono(campo: String): Boolean {
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

    //
    private fun asignarDatosUsuario(
        dni: String,
        fechaNac: Timestamp,
        nombre: String,
        telefono: String,
        uid: String,
        email: String
    ) = run {
        val datos = Usuario(
            false,
            dni.uppercase(),
            email,
            fechaNac,
            nombre,
            telefono,
            uid
        )
        db.collection("usuarios")
            .add(datos)
            .addOnSuccessListener { documentReference ->
                Log.d("Registro", "DocumentSnapshot written with ID: ${documentReference.id}")
            }.addOnFailureListener { e ->
                Log.w("Registro", "Error adding document", e)
            }
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(
            Intent(
                this@RegistrarUsuarioActivity, LoginActivity::class.java
            )
        )
        finish()
    }
}