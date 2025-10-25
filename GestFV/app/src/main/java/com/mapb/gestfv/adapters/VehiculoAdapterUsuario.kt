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

class VehiculoAdapterUsuario (private val dataSet: ArrayList<Vehiculo>): RecyclerView.Adapter<VehiculoAdapterUsuario.ViewHolder>() {

    var onItemClickAlquilar: ((Vehiculo) -> Unit)? = null

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Define click listener for the ViewHolder's View
        val imagenVehiculo: ImageView = view.findViewById(R.id.imagen_item_vehiculo)
        val tvMarca: TextView = view.findViewById(R.id.tv_mostrar_marca_vehiculo_item)
        val tvModelo: TextView = view.findViewById(R.id.tv_mostrar_modelo_vehiculo_item)
        val tvPotencia: TextView = view.findViewById(R.id.tv_mostrar_potencia_vehiculo_item)
        val tvPrecioDia: TextView = view.findViewById(R.id.tv_mostrar_precio_diario_vehiculo_item)
        val tvAnio: TextView = view.findViewById(R.id.tv_mostrar_anio_vehiculo_item)
        val tvKm: TextView = view.findViewById(R.id.tv_mostrar_kilometraje_vehiculo_item)
        val tvCombustible: TextView = view.findViewById(R.id.tv_mostrar_tv_combustible_vehiculo_item)

        init {
            val botonAlquilar: Button = view.findViewById(R.id.boton_alquilar_vehiculo_item)
            botonAlquilar.setOnClickListener {
                onItemClickAlquilar?.invoke(dataSet[adapterPosition])
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_vehiculo_alquiler, viewGroup, false)
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
        viewHolder.tvCombustible.text = dataSet[position].combustible.toString()
        viewHolder.tvKm.text = dataSet[position].km.toString()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}