package com.mapb.gestfv

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BuscarVehiculosActivity : ComponentActivity() {

    private var calFechaInicio = Calendar.getInstance()
    private var calFechaFin = Calendar.getInstance()
    private val accionBusquedaVehiculos: Int = 5
    private val privilegiosUsuario: Int = 10

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buscar_vehiculos)

        //
        val botonIniciarBusqueda: Button = findViewById(R.id.boton_iniciar_busqueda)
        val etFechaInicio: EditText = findViewById(R.id.et_fecha_inicio_alquiler)
        val etFechaFin: EditText = findViewById(R.id.et_fecha_fin_alquiler)

        //
        val dateSetListenerFechaInicio =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                calFechaInicio.set(Calendar.YEAR, year)
                calFechaInicio.set(Calendar.MONTH, monthOfYear)
                calFechaInicio.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd/MM/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                etFechaInicio.setText(sdf.format(calFechaInicio.time))
            }

        //
        val dateSetListenerFechaFin =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                calFechaFin.set(Calendar.YEAR, year)
                calFechaFin.set(Calendar.MONTH, monthOfYear)
                calFechaFin.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd/MM/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                etFechaFin.setText(sdf.format(calFechaFin.time))

            }

        //
        etFechaInicio.setOnClickListener {
            //
            DatePickerDialog(
                this@BuscarVehiculosActivity,
                dateSetListenerFechaInicio,
                calFechaInicio.get(Calendar.YEAR),
                calFechaInicio.get(Calendar.MONTH),
                calFechaInicio.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        //
        etFechaFin.setOnClickListener {
            //
            DatePickerDialog(
                this@BuscarVehiculosActivity,
                dateSetListenerFechaFin,
                calFechaFin.get(Calendar.YEAR),
                calFechaFin.get(Calendar.MONTH),
                calFechaFin.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        //
        botonIniciarBusqueda.setOnClickListener {
            //
            var ldFechaInicio: LocalDate = calFechaInicio.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            //
            if (etFechaInicio.text.isNotEmpty() && etFechaFin.text.isNotEmpty() && calFechaFin.after(
                    calFechaInicio
                ) && ldFechaInicio.isAfter(LocalDate.now())
            ) {
                //
                val intent =
                    Intent(
                        this@BuscarVehiculosActivity,
                        ListarItemsActivity::class.java
                    )
                var b = Bundle()
                var dateFechaInicio: Date = calFechaInicio.time
                var dateFechaFin: Date = calFechaFin.time
                b.putInt("tipo_accion", accionBusquedaVehiculos)
                b.putInt("tipo_usuario", privilegiosUsuario)
                b.putLong("fecha_inicio", dateFechaInicio.time)
                b.putLong("fecha_fin", dateFechaFin.time)
                intent.putExtras(b)
                startActivity(intent)
                finish()
            } else
                Toast.makeText(
                    baseContext,
                    "Error en las fechas",
                    Toast.LENGTH_LONG,
                ).show()
        }
    }
}
