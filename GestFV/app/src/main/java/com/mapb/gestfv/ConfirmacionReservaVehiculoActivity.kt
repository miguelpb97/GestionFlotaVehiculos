package com.mapb.gestfv

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import java.text.SimpleDateFormat

class ConfirmacionReservaVehiculoActivity : ComponentActivity() {
    private var precioTotal: Long = 0
    private var fechaInicio: Long = 0
    private var fechaFin: Long = 0
    private var idAlquiler: String = ""
    private lateinit var tvIdAlquiler: TextView
    private lateinit var tvFechaInicio: TextView
    private lateinit var tvFechaFin: TextView
    private lateinit var tvTotalPagar: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmacion_reserva_vehiculo)

        // Asignamos los componentes de layout a las variables
        tvIdAlquiler = findViewById(R.id.tv_mostrar_id_alquiler_confirmacion_reserva)
        tvFechaInicio = findViewById(R.id.tv_mostrar_fecha_inicio_confirmacion_reserva)
        tvFechaFin = findViewById(R.id.tv_mostrar_fecha_fin_confirmacion_reserva)
        tvTotalPagar = findViewById(R.id.tv_mostrar_precio_total_confirmacion_reserva)
        val botonVolverMenuPrincipal: TextView = findViewById(R.id.boton_volver_menu_principal_confirmacion_reserva)

        // Obtenemos los datos pasados por bundle
        val b = intent.getExtras()
        precioTotal = b?.getLong("precio_total")!!
        idAlquiler = b.getString("id_alquiler").toString()
        fechaInicio = b.getLong("fecha_inicio")
        fechaFin = b.getLong("fecha_fin")

        // Asignamos los datos a los textview de la layout
        tvIdAlquiler.text = idAlquiler.toString()
        tvTotalPagar.text = precioTotal.toString()
        tvFechaInicio.text = SimpleDateFormat("dd/MM/yyyy").format(fechaInicio)
        tvFechaFin.text = SimpleDateFormat("dd/MM/yyyy").format(fechaFin)


        // Al pulsar boton pago efectivo
        botonVolverMenuPrincipal.setOnClickListener {
            startActivity(Intent(this@ConfirmacionReservaVehiculoActivity, MenuPrincipalActivity::class.java))
            finish()
        }

    }

}