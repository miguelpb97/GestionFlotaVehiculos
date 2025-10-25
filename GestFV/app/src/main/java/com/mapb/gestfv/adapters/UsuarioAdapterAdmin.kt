package com.mapb.gestfv.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mapb.gestfv.R
import com.mapb.gestfv.modelo.Usuario
import java.text.SimpleDateFormat

class UsuarioAdapterAdmin(private var dataSet: ArrayList<Usuario>) :
    RecyclerView.Adapter<UsuarioAdapterAdmin.ViewHolder>() {

    var onItemClickBorrarUsuario: ((Usuario) -> Unit)? = null
    var onItemClickVerAlquileresUsuario: ((Usuario) -> Unit)? = null
    var onItemClickEditarUsuario: ((Usuario) -> Unit)? = null

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Define click listener for the ViewHolder's View
        var tvUidUsuario: TextView = view.findViewById(R.id.tv_mostrar_uid_usuario_item_admin)
        var tvNombre: TextView = view.findViewById(R.id.tv_mostrar_nombre_usuario_item_admin)
        var tvDni: TextView = view.findViewById(R.id.tv_mostrar_dni_usuario_item_admin)
        var tvTelefono: TextView = view.findViewById(R.id.tv_mostrar_telefono_usuario_item_admin)
        var tvFechaNac: TextView = view.findViewById(R.id.tv_mostrar_fecha_nac_usuario_item_admin)
        var tvEmail: TextView = view.findViewById(R.id.tv_mostrar_email_usuario_item_admin)
        var tvTipo: TextView = view.findViewById(R.id.tv_mostrar_tipo_usuario_item_admin)

        init {
            val botonBorrar: Button = view.findViewById(R.id.boton_borrar_usuario_item_admin)
            botonBorrar.setOnClickListener {
                onItemClickBorrarUsuario?.invoke(dataSet[adapterPosition])
            }
        }
        init {
            val botonVerAlquileresUsuario: Button = view.findViewById(R.id.boton_ver_alquileres_usuario_item_admin)
            botonVerAlquileresUsuario.setOnClickListener {
                onItemClickVerAlquileresUsuario?.invoke(dataSet[adapterPosition])
            }
        }
        init {
            val botonEditarUsuario: Button = view.findViewById(R.id.boton_editar_usuario_item_admin)
            botonEditarUsuario.setOnClickListener {
                onItemClickEditarUsuario?.invoke(dataSet[adapterPosition])
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        var view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_usuario_admin, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.tvUidUsuario.text = dataSet[position].uid
        viewHolder.tvNombre.text = dataSet[position].nombre
        viewHolder.tvDni.text = dataSet[position].dni
        viewHolder.tvTelefono.text = dataSet[position].telefono
        viewHolder.tvFechaNac.text = SimpleDateFormat("dd/MM/yyyy").format(dataSet[position].fechaNac.toDate())
        viewHolder.tvEmail.text = dataSet[position].email
        if (dataSet[position].admin) {
            viewHolder.tvTipo.text = "Admin"
        } else {
            viewHolder.tvTipo.text = "Normal"
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}