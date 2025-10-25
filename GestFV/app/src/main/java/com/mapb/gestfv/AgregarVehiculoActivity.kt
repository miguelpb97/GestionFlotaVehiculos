package com.mapb.gestfv

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mapb.gestfv.modelo.Vehiculo
import java.util.regex.Pattern
import kotlin.text.toLong

class AgregarVehiculoActivity : ComponentActivity() {
    private var regexMatricula: String = "^[0-9]{4}+[a-zA-Z]{3}$"
    private var regexNumerico: String = "^[0-9]{1,10}$"
    private lateinit var etMarca: EditText
    private lateinit var etModelo: EditText
    private lateinit var etMatricula: EditText
    private lateinit var etAnio: EditText
    private lateinit var etPotencia: EditText
    private lateinit var etKilometraje: EditText
    private lateinit var etPrecioDia: EditText
    private lateinit var etUrlImagen: EditText
    private lateinit var etRgTipoCombustible: RadioGroup
    private lateinit var etRbGasolina: RadioButton
    private lateinit var etRbDiesel: RadioButton
    private lateinit var etRbHibrido: RadioButton
    private lateinit var etRgDisponibilidad: RadioGroup
    private lateinit var etRbDisponible: RadioButton
    private lateinit var etRbNoDisponible: RadioButton
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_vehiculo)

        //
        val etBotonAgregarVehiculo: Button = findViewById(R.id.boton_agregar_vehiculo)
        etMarca = findViewById(R.id.et_marca_agregar_vehiculo)
        etModelo = findViewById(R.id.et_modelo_agregar_vehiculo)
        etMatricula = findViewById(R.id.et_matricula_agregar_vehiculo)
        etAnio = findViewById(R.id.et_anio_agregar_vehiculo)
        etPotencia = findViewById(R.id.et_potencia_agregar_vehiculo)
        etKilometraje = findViewById(R.id.et_kilometraje_agregar_vehiculo)
        etPrecioDia = findViewById(R.id.et_precio_dia_agregar_vehiculo)
        etUrlImagen = findViewById(R.id.et_url_imagen_agregar_vehiculo)
        etRgTipoCombustible = findViewById(R.id.rg_tipo_combustible)
        etRbGasolina = findViewById(R.id.rb_gasolina)
        etRbDiesel = findViewById(R.id.rb_diesel)
        etRbHibrido = findViewById(R.id.rb_hibrido)
        etRgDisponibilidad = findViewById(R.id.rg_disponibilidad)
        etRbDisponible = findViewById(R.id.rb_disponible)
        etRbNoDisponible = findViewById(R.id.rb_no_disponible)

        //
        etBotonAgregarVehiculo.setOnClickListener {
            if (etMarca.text.isNotEmpty() &&
                etModelo.text.isNotEmpty() &&
                validarCampo(etMatricula.text.toString(), regexMatricula) &&
                validarCampo(etAnio.text.toString(), regexNumerico) &&
                validarCampo(etPotencia.text.toString(), regexNumerico) &&
                validarCampo(etKilometraje.text.toString(), regexNumerico) &&
                validarCampo(etPrecioDia.text.toString(), regexNumerico) &&
                etUrlImagen.text.isNotEmpty()
            ) {
                if (etRbGasolina.isChecked) {
                    var anio: String = etAnio.text.toString()
                    var km: String = etKilometraje.text.toString()
                    var potencia: String = etPotencia.text.toString()
                    var precioDia: String = etPrecioDia.text.toString()
                    if (etRbDisponible.isChecked) {
                        agregarVehiculo(
                            anio.toLong(),
                            "Gasolina",
                            true,
                            km.toLong(),
                            etMarca.text.toString(),
                            etMatricula.text.toString(),
                            etModelo.text.toString(),
                            potencia.toLong(),
                            precioDia.toLong(),
                            etUrlImagen.text.toString()
                        )
                    } else if (etRbNoDisponible.isChecked) {
                        agregarVehiculo(
                            anio.toLong(),
                            "Gasolina",
                            false,
                            km.toLong(),
                            etMarca.text.toString(),
                            etMatricula.text.toString(),
                            etModelo.text.toString(),
                            potencia.toLong(),
                            precioDia.toLong(),
                            etUrlImagen.text.toString()
                        )
                    }
                } else if (etRbDiesel.isChecked) {
                    var anio: String = etAnio.text.toString()
                    var km: String = etKilometraje.text.toString()
                    var potencia: String = etPotencia.text.toString()
                    var precioDia: String = etPrecioDia.text.toString()
                    if (etRbDisponible.isChecked) {
                        agregarVehiculo(
                            anio.toLong(),
                            "Diésel",
                            true,
                            km.toLong(),
                            etMarca.text.toString(),
                            etMatricula.text.toString(),
                            etModelo.text.toString(),
                            potencia.toLong(),
                            precioDia.toLong(),
                            etUrlImagen.text.toString()
                        )
                    } else if (etRbNoDisponible.isChecked) {
                        agregarVehiculo(
                            anio.toLong(),
                            "Diésel",
                            false,
                            km.toLong(),
                            etMarca.text.toString(),
                            etMatricula.text.toString(),
                            etModelo.text.toString(),
                            potencia.toLong(),
                            precioDia.toLong(),
                            etUrlImagen.text.toString()
                        )
                    }
                } else if (etRbHibrido.isChecked) {
                    var anio: String = etAnio.text.toString()
                    var km: String = etKilometraje.text.toString()
                    var potencia: String = etPotencia.text.toString()
                    var precioDia: String = etPrecioDia.text.toString()
                    if (etRbDisponible.isChecked) {
                        agregarVehiculo(
                            anio.toLong(),
                            "Híbrido",
                            true,
                            km.toLong(),
                            etMarca.text.toString(),
                            etMatricula.text.toString(),
                            etModelo.text.toString(),
                            potencia.toLong(),
                            precioDia.toLong(),
                            etUrlImagen.text.toString()
                        )
                    } else if (etRbNoDisponible.isChecked) {
                        agregarVehiculo(
                            anio.toLong(),
                            "Híbrido",
                            false,
                            km.toLong(),
                            etMarca.text.toString(),
                            etMatricula.text.toString(),
                            etModelo.text.toString(),
                            potencia.toLong(),
                            precioDia.toLong(),
                            etUrlImagen.text.toString()
                        )
                    }
                }
            } else {
                Toast.makeText(
                    baseContext,
                    "Error, algunos campos estan vacios o no son validos.",
                    Toast.LENGTH_LONG,
                ).show()
            }
        }
    }

    private fun validarCampo(campo: String, regex : String): Boolean {
        var esValido = false
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(campo)
        val matchFound = matcher.find()
        if (matchFound) {
            esValido = true
        } else {
            esValido = false
            Toast.makeText(baseContext, "Error, formato de DNI inválido", Toast.LENGTH_SHORT).show()
        }
        return esValido
    }

    //
    private fun agregarVehiculo(
        anio: Long,
        combustible: String,
        disponibilidad: Boolean,
        km: Long,
        marca: String,
        matricula: String,
        modelo: String,
        potencia: Long,
        precioDia: Long,
        urlImagen: String

    ) {
        val datos = Vehiculo(
            anio,
            combustible,
            disponibilidad,
            km,
            GeoPoint(36.85014905217382, -2.465136759961395),
            marca,
            matricula.uppercase(),
            modelo,
            potencia,
            precioDia,
            urlImagen
        )
        db.collection("vehiculos").add(datos).addOnSuccessListener { documentReference ->
            Log.d(
                "Agregar Vehiculo",
                "Vehiculo creado con exito. ID del vehiculo: ${documentReference.id}"
            )
            Toast.makeText(
                baseContext,
                "Vehiculo creado con exito.",
                Toast.LENGTH_LONG,
            ).show()
            etAnio.text.clear()
            etKilometraje.text.clear()
            etMarca.text.clear()
            etMatricula.text.clear()
            etModelo.text.clear()
            etPotencia.text.clear()
            etPrecioDia.text.clear()
            etUrlImagen.text.clear()
            etRgTipoCombustible.clearCheck()
            etRgDisponibilidad.clearCheck()
        }.addOnFailureListener { e ->
            Log.w("Agregar Vehiculo", "Error al crear el vehiculo", e)
            Toast.makeText(
                baseContext,
                "Error al crear el vehículo.",
                Toast.LENGTH_LONG,
            ).show()
        }
    }
}