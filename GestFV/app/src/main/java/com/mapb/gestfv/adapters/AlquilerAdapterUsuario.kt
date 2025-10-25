package com.mapb.gestfv.adapters

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mapb.gestfv.R
import com.mapb.gestfv.modelo.Alquiler
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId

class AlquilerAdapterUsuario(private var dataSet: ArrayList<Alquiler>) :
    RecyclerView.Adapter<AlquilerAdapterUsuario.ViewHolder>() {

    private val db = Firebase.firestore

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Define click listener for the ViewHolder's View
        var imagenAlquiler: ImageView =
            view.findViewById(R.id.imagen_vehiculo_item_alquiler)
        var tvFechaInicio: TextView = view.findViewById(R.id.tv_mostrar_fecha_inicio_alquiler_item)
        var tvFechaFin: TextView = view.findViewById(R.id.tv_mostrar_fecha_fin_alquiler_item)
        var tvMatriculaVehiculo: TextView =
            view.findViewById(R.id.tv_mostrar_matricula_vehiculo_alquiler_item)
        var tvMetodoPago: TextView = view.findViewById(R.id.tv_mostrar_metodo_pago_alquiler_item)
        var tvPrecioTotal: TextView = view.findViewById(R.id.tv_mostrar_precio_total_alquiler_item)
        var tvEstado: TextView = view.findViewById(R.id.tv_mostrar_estado_total_alquiler_item)

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        var view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_alquiler, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        db.collection("vehiculos")
            .whereEqualTo("matricula", dataSet[position].matriculaVehiculo.toString())
            .get().addOnSuccessListener { result ->
                for (document in result) {
                    Glide.with(viewHolder.itemView)
                        .load(document.data["urlImagen"].toString())
                        .transform(RoundedCorners(50))
                        .into(viewHolder.imagenAlquiler)
                }
            }.addOnFailureListener { exception ->
                Log.w("Adapter Alquiler", "Error obteniendo la imagen del vehiculo.", exception)
            }
        viewHolder.tvFechaInicio.text =
            SimpleDateFormat("dd/MM/yyyy").format(dataSet[position].fechaInicio.toDate())
        viewHolder.tvFechaFin.text =
            SimpleDateFormat("dd/MM/yyyy").format(dataSet[position].fechaFin.toDate())
        viewHolder.tvMatriculaVehiculo.text = dataSet[position].matriculaVehiculo.toString()
        viewHolder.tvMetodoPago.text = dataSet[position].metodoPago.toString()
        viewHolder.tvPrecioTotal.text = dataSet[position].precioTotal.toString()

        if (LocalDate.now().isBefore(
                LocalDate.ofInstant(
                    dataSet[position].fechaFin.toDate().toInstant(), ZoneId.systemDefault()
                )
            )
        ) {
            viewHolder.tvEstado.text = "Activo"
        } else {
            viewHolder.tvEstado.text = "Vencido"
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}