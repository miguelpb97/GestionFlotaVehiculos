package com.mapb.gestfv

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.mapb.gestfv.modelo.Usuario
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.regex.Matcher
import java.util.regex.Pattern

class EditarUsuarioActivity : ComponentActivity() {

    private val db = Firebase.firestore
    private var uidUsuario: String = ""
    private lateinit var tvMostrarUid: TextView
    private lateinit var etDni: EditText
    private lateinit var etNombre: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etFechaNacRegistro: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_usuario)

        //
        tvMostrarUid = findViewById(R.id.tv_mostrar_uid_usuario_editar)
        etDni = findViewById(R.id.et_mostrar_dni_editar_usuario)
        etNombre = findViewById(R.id.et_mostrar_nombre_editar_usuario)
        etTelefono = findViewById(R.id.et_mostrar_telefono_editar_usuario)
        etFechaNacRegistro =
            findViewById(R.id.et_mostrar_fecha_nac_editar_usuario)
        val botonEditarUsuario: Button = findViewById(R.id.boton_editar_usuario)

        //
        val b = intent.getExtras()
        uidUsuario = b?.getString("uid_usuario").toString()

        if (uidUsuario.isNotEmpty()) {
            //
            tvMostrarUid.text = uidUsuario
            //
            getDatosUsuario(uidUsuario)
        }

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
                //
                etFechaNacRegistro.error = null
            }

        //
        etFechaNacRegistro.setOnClickListener {
            DatePickerDialog(
                this@EditarUsuarioActivity, dateSetListener,
                calFechaNac.get(Calendar.YEAR),
                calFechaNac.get(Calendar.MONTH),
                calFechaNac.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        //
        botonEditarUsuario.setOnClickListener {
            if (etDni.text.isNotEmpty() && etNombre.text.isNotEmpty() && etDni.text.isNotEmpty() && etTelefono.text.isNotEmpty() && etFechaNacRegistro.text.isNotEmpty()) {
                editarUsuario(uidUsuario, etDni.text.toString(), etNombre.text.toString(), Timestamp(calFechaNac.time), etTelefono.text.toString())
            } else {
                Toast.makeText(
                    baseContext,
                    "Los datos nuevos no pueden estar incompletos",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    private fun getDatosUsuario(uid: String) {
        db.collection("usuarios")
            .whereEqualTo("uid", uid)
            .get().addOnSuccessListener { result ->
                Log.d("getDatosUsuario", "Documentos obtenidos correctamente.")
                for (document in result) {
                    var fechaNac: Timestamp = document.data["fechaNac"] as Timestamp
                    etNombre.setText(document.data["nombre"].toString())
                    etDni.setText(document.data["dni"].toString())
                    etTelefono.setText(document.data["telefono"].toString())
                    etFechaNacRegistro.setText(SimpleDateFormat("dd/MM/yyyy").format(fechaNac.toDate()))
                }
            }.addOnFailureListener { exception ->
                Log.w("getDatosUsuario", "Error getting documents.", exception)
            }
    }

    //
    private fun editarUsuario(
        uid: String,
        dniNuevo: String,
        nombreNuevo: String,
        fechaNacNueva: Timestamp,
        telefonoNuevo: String
    ) = runBlocking {
        db.collection("usuarios")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val data = hashMapOf(
                        "nombre" to nombreNuevo,
                        "dni" to dniNuevo.uppercase(),
                        "telefono" to telefonoNuevo,
                        "fechaNac" to fechaNacNueva
                    )
                    db.collection("usuarios").document(document.id).set(data, SetOptions.merge())
                    Log.d("editarUsuario", "Usuario editado.")
                    Toast.makeText(
                        baseContext,
                        "Usuario editado correctamente. Los cambios se reflejaran después de refrescar la página",
                        Toast.LENGTH_LONG,
                    ).show()
                    finish()
                }
            }.addOnFailureListener { exception ->
                Log.w("editarUsuario", "Error al editar.", exception)
                Toast.makeText(
                    baseContext,
                    "Error al editar el usuario.",
                    Toast.LENGTH_LONG,
                ).show()
            }

    }

}