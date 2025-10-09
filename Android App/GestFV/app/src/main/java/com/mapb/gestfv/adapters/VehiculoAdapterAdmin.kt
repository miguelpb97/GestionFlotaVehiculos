package com.mapb.gestfv.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.mapb.gestfv.R
import com.mapb.gestfv.modelo.Vehiculo

class VehiculoAdapterAdmin (private val dataSet: ArrayList<Vehiculo>): RecyclerView.Adapter<VehiculoAdapterAdmin.ViewHolder>() {

    var onItemClickVerRevisiones: ((Vehiculo) -> Unit)? = null
    var onItemClickLocalizar: ((Vehiculo) -> Unit)? = null
    var onItemClickCambiarEstado: ((Vehiculo) -> Unit)? = null
    var onItemClickBorrarVehiculo: ((Vehiculo) -> Unit)? = null
    var onItemClickAgregarRevision: ((Vehiculo) -> Unit)? = null
    var onItemClickVerHistorialAlquileres: ((Vehiculo) -> Unit)? = null

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Define click listener for the ViewHolder's View
        val imagenVehiculo: ImageView = view.findViewById(R.id.imagen_item_vehiculo_admin)
        val tvMarca: TextView = view.findViewById(R.id.tv_mostrar_marca_vehiculo_item_admin)
        val tvModelo: TextView = view.findViewById(R.id.tv_mostrar_modelo_vehiculo_item_admin)
        val tvPotencia: TextView = view.findViewById(R.id.tv_mostrar_potencia_vehiculo_item_admin)
        val tvPrecioDia: TextView = view.findViewById(R.id.tv_mostrar_precio_diario_vehiculo_item_admin)
        val tvAnio: TextView = view.findViewById(R.id.tv_mostrar_anio_vehiculo_item_admin)
        val tvCombustible: TextView = view.findViewById(R.id.tv_mostrar_combustible_vehiculo_item_admin)
        val tvKm: TextView = view.findViewById(R.id.tv_mostrar_kilometraje_vehiculo_item_admin)
        val tvMatricula: TextView = view.findViewById(R.id.tv_mostrar_matricula_vehiculo_item_admin)
        val tvDispoibilidad: TextView = view.findViewById(R.id.tv_mostrar_disponibilidad_vehiculo_item_admin)

        init {
            val botonVerRevisiones: Button = view.findViewById(R.id.boton_ver_revisiones_vehiculo_item_admin)
            botonVerRevisiones.setOnClickListener {
                onItemClickVerRevisiones?.invoke(dataSet[adapterPosition])
            }
            val botonLocalizarVehiculo: Button = view.findViewById(R.id.boton_localizar_vehiculo_item_admin)
            botonLocalizarVehiculo.setOnClickListener {
                onItemClickLocalizar?.invoke(dataSet[adapterPosition])
            }
            val botonDeshabilitar: Button = view.findViewById(R.id.boton_deshabilitar_vehiculo_item_admin)
            botonDeshabilitar.setOnClickListener {
                onItemClickCambiarEstado?.invoke(dataSet[adapterPosition])
            }
            val botonBorrar: Button = view.findViewById(R.id.boton_borrar_vehiculo_item_admin)
            botonBorrar.setOnClickListener {
                onItemClickBorrarVehiculo?.invoke(dataSet[adapterPosition])
            }
            val botonAgregarRevision: Button = view.findViewById(R.id.boton_agregar_revision_vehiculo_item_admin)
            botonAgregarRevision.setOnClickListener {
                onItemClickAgregarRevision?.invoke(dataSet[adapterPosition])
            }
            val botonVerHistorialAlquileres: Button = view.findViewById(R.id.boton_ver_historial_alquileres_vehiculo_item_admin)
            botonVerHistorialAlquileres.setOnClickListener {
                onItemClickVerHistorialAlquileres?.invoke(dataSet[adapterPosition])
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_vehiculo_admin, viewGroup, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        Glide.with(viewHolder.itemView)
            .load(dataSet[position].urlImagen)
            .transform(RoundedCorners(50))
            .into(viewHolder.imagenVehiculo)
        viewHolder.tvMarca.text = dataSet[position].marca.toString()
        viewHolder.tvModelo.text = dataSet[position].modelo.toString()
        viewHolder.tvPotencia.text = dataSet[position].potencia.toString()
        viewHolder.tvPrecioDia.text = dataSet[position].precioDia.toString()
        viewHolder.tvAnio.text = dataSet[position].anio.toString()
        viewHolder.tvKm.text = dataSet[position].km.toString()
        viewHolder.tvCombustible.text = dataSet[position].combustible.toString()
        viewHolder.tvMatricula.text = dataSet[position].matricula.toString()
        if (dataSet[position].disponibilidad) {
            viewHolder.tvDispoibilidad.text = "Disponible"
        } else {
            viewHolder.tvDispoibilidad.text = "No disponible"
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}