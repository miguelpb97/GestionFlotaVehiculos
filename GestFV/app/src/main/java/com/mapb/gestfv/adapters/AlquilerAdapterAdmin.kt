package com.mapb.gestfv.adapters

import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mapb.gestfv.LoginActivity
import com.mapb.gestfv.MenuAdminActivity
import com.mapb.gestfv.MenuPrincipalActivity
import com.mapb.gestfv.R
import com.mapb.gestfv.modelo.Alquiler
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId

class AlquilerAdapterAdmin(private var dataSet: ArrayList<Alquiler>) :
    RecyclerView.Adapter<AlquilerAdapterAdmin.ViewHolder>() {

    private val db = Firebase.firestore
    var onItemClickBorrarAlquiler: ((Alquiler) -> Unit)? = null
    var onItemClickVerUsuarioAlquiler: ((Alquiler) -> Unit)? = null

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Define click listener for the ViewHolder's View
        var imagenAlquiler: ImageView =
            view.findViewById(R.id.imagen_vehiculo_item_alquiler_admin)
        var tvFechaInicio: TextView =
            view.findViewById(R.id.tv_mostrar_fecha_inicio_alquiler_item_admin)
        var tvFechaFin: TextView = view.findViewById(R.id.tv_mostrar_fecha_fin_alquiler_item_admin)
        var tvMatriculaVehiculo: TextView =
            view.findViewById(R.id.tv_mostrar_matricula_vehiculo_alquiler_item_admin)
        var tvMetodoPago: TextView =
            view.findViewById(R.id.tv_mostrar_metodo_pago_alquiler_item_admin)
        var tvPrecioTotal: TextView =
            view.findViewById(R.id.tv_mostrar_precio_total_alquiler_item_admin)
        var tvEstado: TextView = view.findViewById(R.id.tv_mostrar_estado_total_alquiler_item_admin)
        var tvUidUsuario: TextView =
            view.findViewById(R.id.tv_mostrar_uid_usuario_alquiler_item_admin)

        init {
            var botonEliminarAlquiler: Button =
                view.findViewById(R.id.boton_borrar_alquiler_item_admin)
            botonEliminarAlquiler.setOnClickListener {
                onItemClickBorrarAlquiler?.invoke(dataSet[adapterPosition])
            }
            var botonVerUsuarioAlquiler: Button =
                view.findViewById(R.id.boton_ver_usuario_alquiler_item_admin)
            botonVerUsuarioAlquiler.setOnClickListener {
                onItemClickVerUsuarioAlquiler?.invoke(dataSet[adapterPosition])
            }
        }

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        var view =
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_alquiler_admin, viewGroup, false)

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
        viewHolder.tvUidUsuario.text = dataSet[position].uidUsuario.toString()

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