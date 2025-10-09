package com.mapb.gestfv.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mapb.gestfv.R
import com.mapb.gestfv.modelo.Revision
import java.text.SimpleDateFormat

class RevisionAdapterAdmin(private var dataSet: ArrayList<Revision>) :
    RecyclerView.Adapter<RevisionAdapterAdmin.ViewHolder>() {

    private val db = Firebase.firestore
    var onItemClickBorrarRevision: ((Revision) -> Unit)? = null

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Define click listener for the ViewHolder's View
        var ivIcono: ImageView = view.findViewById(R.id.icono_item_revision)
        var tvMatriculaVehiculo: TextView = view.findViewById(R.id.tv_mostrar_matricula_vehiculo_revision_item)
        var tvTipoRevision: TextView = view.findViewById(R.id.tv_mostrar_tipo_revision_item)
        var tvKilometraje: TextView = view.findViewById(R.id.tv_mostrar_kilometraje_revision_item)
        var tvFechaRevision: TextView = view.findViewById(R.id.tv_mostrar_fecha_revision_item)
        var tvSiguienteRevision: TextView = view.findViewById(R.id.tv_mostrar_siguiente_fecha_revision_item)
        var tvPrecio: TextView = view.findViewById(R.id.tv_mostrar_precio_revision_item)

        init {
            val botonBorrar: Button = view.findViewById(R.id.boton_borrar_revision_admin)
            botonBorrar.setOnClickListener {
                onItemClickBorrarRevision?.invoke(dataSet[adapterPosition])
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        var view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_revision, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if (dataSet[position].tipoRevision == "Reparación") {
            viewHolder.tvSiguienteRevision.text = "No procede"
            viewHolder.ivIcono.setImageResource(R.drawable.ic_repair)
        } else if (dataSet[position].tipoRevision == "ITV") {
            viewHolder.tvSiguienteRevision.text = SimpleDateFormat("dd/MM/yyyy").format(dataSet[position].siguienteRevision.toDate())
            viewHolder.ivIcono.setImageResource(R.drawable.ic_itv)
        } else if (dataSet[position].tipoRevision == "Mantenimiento") {
            viewHolder.tvSiguienteRevision.text = SimpleDateFormat("dd/MM/yyyy").format(dataSet[position].siguienteRevision.toDate())
            viewHolder.ivIcono.setImageResource(R.drawable.ic_car_maintenance)
        }
        viewHolder.tvMatriculaVehiculo.text = dataSet[position].matriculaVehiculo
        viewHolder.tvTipoRevision.text = dataSet[position].tipoRevision
        viewHolder.tvKilometraje.text = dataSet[position].kmLeido.toString()
        viewHolder.tvFechaRevision.text = SimpleDateFormat("dd/MM/yyyy").format(dataSet[position].fechaRevision.toDate())
        viewHolder.tvPrecio.text = dataSet[position].precio.toString()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}