package com.mapb.gestfv

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.mapb.gestfv.modelo.Revision
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.regex.Pattern

class AgregarRevisionVehiculoActivity : ComponentActivity() {
    private var regexNumerico: String = "^[0-9]{1,10}$"
    private val db = com.google.firebase.ktx.Firebase.firestore
    private var matriculaVehiculo: String = ""
    private var calFechaRevision = Calendar.getInstance()
    private var calFechaProximaRevision = Calendar.getInstance()
    private lateinit var etMatriculaVehiculo: TextView
    private lateinit var etKmLeidosRevision: EditText
    private lateinit var rbItv: RadioButton
    private lateinit var rgTipoRevision: RadioGroup
    private lateinit var rbMantenimiento: RadioButton
    private lateinit var rbReparacion: RadioButton
    private lateinit var etFechaRevision: EditText
    private lateinit var etFechaProximaRevision: EditText
    private lateinit var etPrecioRevision: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_revision_vehiculo)

        //
        val botonAgregarRevision: Button = findViewById(R.id.boton_agregar_revision)
        etMatriculaVehiculo = findViewById(R.id.et_matricula_vehiculo_revision)
        etKmLeidosRevision = findViewById(R.id.et_km_leidos_revision)
        rbItv = findViewById(R.id.rb_itv)
        rgTipoRevision = findViewById(R.id.rg_tipo_revision)
        rbMantenimiento = findViewById(R.id.rb_mantenimiento)
        rbReparacion = findViewById(R.id.rb_reparacion)
        etFechaRevision = findViewById(R.id.et_fecha_revision)
        etFechaProximaRevision = findViewById(R.id.et_fecha_siguiente_revision)
        etPrecioRevision = findViewById(R.id.et_precio_revision)

        //
        etMatriculaVehiculo.setFocusable(false)
        etFechaRevision.setFocusable(false)
        etFechaProximaRevision.setFocusable(false)
        etKmLeidosRevision.setFocusableInTouchMode(true)
        etPrecioRevision.setFocusableInTouchMode(true)

        //
        val b = intent.getExtras()

        matriculaVehiculo = b?.getString("matricula_vehiculo").toString()

        etMatriculaVehiculo.text = matriculaVehiculo

        rbMantenimiento.setOnClickListener {
            if (rbMantenimiento.isPressed && rbMantenimiento.error != null) {
                rbItv.error = null
                rbMantenimiento.error = null
                rbReparacion.error = null
            }
        }

        rbItv.setOnClickListener {
            if (rbItv.isPressed && rbItv.error != null) {
                rbItv.error = null
                rbMantenimiento.error = null
                rbReparacion.error = null
            }
        }

        rbReparacion.setOnClickListener {
            if (rbReparacion.isPressed && rbReparacion.error != null) {
                rbItv.error = null
                rbMantenimiento.error = null
                rbReparacion.error = null
            }
        }

        //
        val dateSetListenerFechaInicio =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                calFechaRevision.set(Calendar.YEAR, year)
                calFechaRevision.set(Calendar.MONTH, monthOfYear)
                calFechaRevision.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd/MM/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                etFechaRevision.setText(sdf.format(calFechaRevision.time))

                etFechaRevision.error = null
            }

        //
        val dateSetListenerFechaFin =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                calFechaProximaRevision.set(Calendar.YEAR, year)
                calFechaProximaRevision.set(Calendar.MONTH, monthOfYear)
                calFechaProximaRevision.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd/MM/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                etFechaProximaRevision.setText(sdf.format(calFechaProximaRevision.time))

                etFechaProximaRevision.error = null
            }

        etFechaRevision.setOnClickListener {
            DatePickerDialog(
                this@AgregarRevisionVehiculoActivity,
                dateSetListenerFechaInicio,
                calFechaRevision.get(Calendar.YEAR),
                calFechaRevision.get(Calendar.MONTH),
                calFechaRevision.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        //
        etFechaProximaRevision.setOnClickListener {
            DatePickerDialog(
                this@AgregarRevisionVehiculoActivity,
                dateSetListenerFechaFin,
                calFechaProximaRevision.get(Calendar.YEAR),
                calFechaProximaRevision.get(Calendar.MONTH),
                calFechaProximaRevision.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        //
        botonAgregarRevision.setOnClickListener {
            if (etFechaRevision.text.isEmpty()) {
                etFechaRevision.error = "Campo vacio"
            }
            if (etFechaProximaRevision.text.isEmpty()) {
                etFechaProximaRevision.error = "Campo vacio"
            }
            if (etKmLeidosRevision.text.isEmpty()) {
                etKmLeidosRevision.error = "Campo vacio"
            }
            if (etPrecioRevision.text.isEmpty()) {
                etPrecioRevision.error = "Campo vacio"
            }
            if (rbItv.isChecked) {
                if (etFechaRevision.text.isNotEmpty() && etFechaProximaRevision.text.isNotEmpty() && validarCampo(etKmLeidosRevision.text.toString(), regexNumerico) && validarCampo(etPrecioRevision.text.toString(), regexNumerico)) {
                    var km: String = etKmLeidosRevision.text.toString()
                    var precio: String = etPrecioRevision.text.toString()
                    crearRevision(
                        Timestamp(calFechaRevision.time),
                        km.toLong(),
                        matriculaVehiculo,
                        precio.toLong(),
                        Timestamp(calFechaProximaRevision.time),
                        "ITV"
                    )
                    rgTipoRevision.clearCheck()
                    etKmLeidosRevision.text.clear()
                    etFechaRevision.text.clear()
                    etFechaProximaRevision.text.clear()
                    etPrecioRevision.text.clear()
                } else {
                    Toast.makeText(
                        baseContext,
                        "Error, algunos datos no estan completos",
                        Toast.LENGTH_LONG,
                    ).show()
                }
            } else if (rbMantenimiento.isChecked) {
                if (etFechaRevision.text.isNotEmpty() && etFechaProximaRevision.text.isNotEmpty() && validarCampo(
                        etKmLeidosRevision.text.toString(), regexNumerico
                    ) && validarCampo(
                        etPrecioRevision.text.toString(), regexNumerico
                    )) {
                    var km: String = etKmLeidosRevision.text.toString()
                    var precio: String = etPrecioRevision.text.toString()
                    crearRevision(
                        Timestamp(calFechaRevision.time),
                        km.toLong(),
                        matriculaVehiculo,
                        precio.toLong(),
                        Timestamp(calFechaProximaRevision.time),
                        "Mantenimiento"
                    )
                    rgTipoRevision.clearCheck()
                    etKmLeidosRevision.text.clear()
                    etFechaRevision.text.clear()
                    etFechaProximaRevision.text.clear()
                    etPrecioRevision.text.clear()
                } else {
                    Toast.makeText(
                        baseContext,
                        "Error, algunos datos no estan completos",
                        Toast.LENGTH_LONG,
                    ).show()
                }
            } else if (rbReparacion.isChecked) {
                if (etFechaRevision.text.isNotEmpty() && validarCampo(etKmLeidosRevision.text.toString(), regexNumerico) && validarCampo(etPrecioRevision.text.toString(), regexNumerico)) {
                    var km: String = etKmLeidosRevision.text.toString()
                    var precio: String = etPrecioRevision.text.toString()
                    crearRevision(
                        Timestamp(calFechaRevision.time),
                        km.toLong(),
                        matriculaVehiculo,
                        precio.toLong(),
                        Timestamp(0, 0),
                        "Reparación"
                    )
                    rgTipoRevision.clearCheck()
                    etKmLeidosRevision.text.clear()
                    etFechaRevision.text.clear()
                    etFechaProximaRevision.text.clear()
                    etPrecioRevision.text.clear()
                } else {
                    Toast.makeText(
                        baseContext,
                        "Error, algunos datos no estan completos",
                        Toast.LENGTH_LONG,
                    ).show()
                }
            } else {
                Toast.makeText(
                    baseContext,
                    "Error, debe elegir el tipo de revision primero",
                    Toast.LENGTH_LONG,
                ).show()
                rbReparacion.error = "Sin marcar"
                rbMantenimiento.error = "Sin marcar"
                rbItv.error = "Sin marcar"
            }
        }

        rbReparacion.setOnClickListener {
            rbReparacion.error = null
            rbItv.error = null
            rbMantenimiento.error = null
            etFechaProximaRevision.isEnabled = false
            etFechaProximaRevision.error = null
        }

        rbItv.setOnClickListener {
            rbReparacion.error = null
            rbItv.error = null
            rbMantenimiento.error = null
            etFechaProximaRevision.isEnabled = true
        }

        rbMantenimiento.setOnClickListener {
            rbReparacion.error = null
            rbItv.error = null
            rbMantenimiento.error = null
            etFechaProximaRevision.isEnabled = true
        }
    }

    private fun crearRevision(
        fechaRevision: Timestamp,
        kmLeido: Long,
        matriculaVehiculo: String,
        precio: Long,
        siguienteRevision: Timestamp,
        tipoRevision: String
    ) {
        val datos = Revision(
            fechaRevision,
            kmLeido,
            matriculaVehiculo,
            precio,
            siguienteRevision,
            tipoRevision
        )
        db.collection("revisiones").add(datos).addOnSuccessListener { documentReference ->
            Log.d(
                "Crear Revision",
                "Revision creada con exito. ID de la revision: ${documentReference.id}"
            )
            Toast.makeText(
                baseContext,
                "Revisión creada con exito al vehiculo matricula: " + matriculaVehiculo,
                Toast.LENGTH_LONG,
            ).show()
            etKmLeidosRevision.text.clear()
            etFechaRevision.text.clear()
            etFechaProximaRevision.text.clear()
            etPrecioRevision.text.clear()
            rgTipoRevision.clearCheck()
        }.addOnFailureListener { e ->
            Log.w("Crear Revision", "Error al crear la revision", e)
            Toast.makeText(
                baseContext,
                "Error al crear el vehículo.",
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    private fun validarCampo(campo: String, regex: String): Boolean {
        var esValido = false
        val pattern = Pattern.compile("^[0-9]{1,10}$")
        val matcher = pattern.matcher(campo)
        val matchFound = matcher.find()
        if (matchFound) {
            esValido = true
        } else {
            esValido = false
            Toast.makeText(
                baseContext,
                "Error, formato del campo inválido",
                Toast.LENGTH_SHORT
            ).show()
        }
        return esValido
    }

}