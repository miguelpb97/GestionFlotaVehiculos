package com.mapb.gestfv

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ktx.firestore
import com.mapb.gestfv.modelo.Alquiler
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date

class CompletarReservaVehiculoActivity : ComponentActivity() {

    private val db = com.google.firebase.ktx.Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private var precioTotal: Long = 0
    private var precioDia: Int = 0
    private var fechaInicio: Long = 0
    private var fechaFin: Long = 0
    private var marcaModelo: String = ""
    private var imagenVehiculo: String = ""
    private var matriculaVehiculo: String = ""
    private lateinit var tvFechaInicio: TextView
    private lateinit var tvFechaFin: TextView
    private lateinit var tvPrecioDia: TextView
    private lateinit var tvTotalPagar: TextView
    private lateinit var tvMarcaModelo: TextView
    private lateinit var ivImagen: ImageView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completar_reserva_vehiculo)

        // Inicializamos Firebase Auth
        auth = Firebase.auth

        // Asignamos los componentes de layout a las variables
        val botonPagoEfectivo: TextView = findViewById(R.id.boton_pago_efectivo_reserva)
        val botonVolver: TextView = findViewById(R.id.boton_volver_reserva_vehiculo)
        tvFechaInicio = findViewById(R.id.tv_mostrar_fecha_inicio_reserva)
        tvFechaFin = findViewById(R.id.tv_mostrar_fecha_fin_reserva)
        tvPrecioDia = findViewById(R.id.tv_mostrar_precio_dia_vehiculo_reserva)
        tvTotalPagar = findViewById(R.id.tv_mostrar_precio_alquiler_reserva)
        tvMarcaModelo = findViewById(R.id.tv_mostrar_marca_modelo_vehiculo_reserva)
        ivImagen = findViewById(R.id.imagen_vehiculo_reserva)

        // Obtenemos los datos pasados por bundle
        val b = intent.getExtras()
        precioDia = b?.getInt("precio_dia")!!
        marcaModelo = b.getString("marca_modelo").toString()
        imagenVehiculo = b.getString("imagen").toString()
        matriculaVehiculo = b.getString("matricula").toString()
        fechaInicio = b.getLong("fecha_inicio")
        fechaFin = b.getLong("fecha_fin")

        //
        val fechaInicioDate =
            Instant.ofEpochMilli(fechaInicio).atZone(ZoneId.systemDefault()).toLocalDate()
        val fechaFinDate =
            Instant.ofEpochMilli(fechaFin).atZone(ZoneId.systemDefault()).toLocalDate()

        //
        precioTotal = ChronoUnit.DAYS.between(fechaInicioDate, fechaFinDate) * precioDia

        // Asignamos los datos a los textview de la layout
        tvPrecioDia.text = precioDia.toString()
        tvTotalPagar.text = precioTotal.toString()
        tvMarcaModelo.text = marcaModelo.toString()
        tvFechaInicio.text = SimpleDateFormat("dd/MM/yyyy").format(fechaInicio)
        tvFechaFin.text = SimpleDateFormat("dd/MM/yyyy").format(fechaFin)
        Glide.with(ivImagen)
            .load(imagenVehiculo)
            .transform(RoundedCorners(50))
            .into(ivImagen)

        // Al pulsar boton pago efectivo
        botonPagoEfectivo.setOnClickListener {
            //
            val builder = AlertDialog.Builder(this)
            //
            builder.setTitle("Reservar vehiculo")
            //
            builder.setMessage("¿Estas seguro que deseas realizar la reserva y que todos los datos son correctos?")
            //
            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                alquilarVehiculo(Date(fechaFin), Date(fechaInicio), matriculaVehiculo, "Efectivo", precioTotal, auth.uid.toString() )
            }
            //
            builder.setNegativeButton(android.R.string.no) { dialog, which ->
                Toast.makeText(
                    this, "No se han realizado la reserva.", Toast.LENGTH_SHORT
                ).show()
            }
            //
            builder.show()
        }

        botonVolver.setOnClickListener {
            onBackPressed()
        }

    }

    // Metodo encargado de registrar el alquiler en la bd
    private fun alquilarVehiculo(
        fechaFin: Date,
        fechaInicio: Date,
        matriculaVehiculo: String,
        metodoPago: String,
        precioTotal: Long,
        uidUsuario: String
    ) = run {
        //
        val datos = Alquiler(
            Timestamp(fechaFin),
            Timestamp(fechaInicio),
            matriculaVehiculo,
            metodoPago,
            precioTotal,
            uidUsuario
        )
        //
        db.collection("alquileres").add(datos).addOnSuccessListener { documentReference ->
            Log.d(
                "Reservar Vehiculo",
                "Alquiler creado con exito. ID del alquiler: ${documentReference.id}"
            )
            val intent =
                Intent(this@CompletarReservaVehiculoActivity, ConfirmacionReservaVehiculoActivity::class.java)
            var b = Bundle()
            b.putLong("precio_total", precioTotal)
            b.putString("id_alquiler", documentReference.id)
            b.putLong("fecha_inicio", fechaInicio.time)
            b.putLong("fecha_fin", fechaFin.time)
            intent.putExtras(b)
            startActivity(intent)
            finish()
        }.addOnFailureListener { e ->
            Log.w("Reservar Vehiculo", "Error al crear el alquiler", e)
        }
    }

}