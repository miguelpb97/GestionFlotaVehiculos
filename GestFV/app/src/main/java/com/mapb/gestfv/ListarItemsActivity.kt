package com.mapb.gestfv

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mapb.gestfv.R.*
import com.mapb.gestfv.adapters.AlquilerAdapterAdmin
import com.mapb.gestfv.adapters.AlquilerAdapterUsuario
import com.mapb.gestfv.adapters.RevisionAdapterAdmin
import com.mapb.gestfv.adapters.UsuarioAdapterAdmin
import com.mapb.gestfv.adapters.VehiculoAdapterAdmin
import com.mapb.gestfv.adapters.VehiculoAdapterUsuario
import com.mapb.gestfv.modelo.Alquiler
import com.mapb.gestfv.modelo.Revision
import com.mapb.gestfv.modelo.Usuario
import com.mapb.gestfv.modelo.Vehiculo
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalQueries.localDate
import java.util.Date


class ListarItemsActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var vehiculoAdapterUsuario: VehiculoAdapterUsuario
    private lateinit var alquilerAdapterUsuario: AlquilerAdapterUsuario
    private lateinit var usuarioAdapterAdmin: UsuarioAdapterAdmin
    private lateinit var alquilerAdapterAdmin: AlquilerAdapterAdmin
    private lateinit var vehiculoAdapterAdmin: VehiculoAdapterAdmin
    private lateinit var revisionesAdapterAdmin: RevisionAdapterAdmin
    private var listaUsuarios = ArrayList<Usuario>()
    private var listaRevisiones = ArrayList<Revision>()
    private var listaVehiculos = ArrayList<Vehiculo>()
    private var listaAlquileres = ArrayList<Alquiler>()
    private var tipoAccion: Int = 0
    private var tipoUsuario: Int = 0
    private val accionVerAlquileresAdmin: Int = 1
    private val accionVerUsuariosAdmin: Int = 2
    private val accionVerVehiculosAdmin: Int = 3
    private val accionVerRevisionesVehiculoAdmin: Int = 4
    private val accionBusquedaVehiculos: Int = 5
    private val accionVerAlquileresUsuario: Int = 6
    private val accionVerHistorialVehiculoAdmin: Int = 7
    private val accionVerUsuarioAlquiler: Int = 8
    private val privilegiosAdmin: Int = 9
    private val privilegiosUsuario: Int = 10
    private var matriculaVehiculo: String = ""
    private var idAlquiler: String = ""
    private var uidUsuario: String = ""
    private var fechaInicio: Long = 0
    private var fechaFin: Long = 0

    @SuppressLint("NotifyDataSetChanged", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_listar_items)

        // Inicializamos Firebase Auth
        auth = com.google.firebase.Firebase.auth

        //
        val recyclerView: RecyclerView = findViewById(id.recycler_view_listar_items)
        val tvTitulo: TextView = findViewById(id.tv_titulo_activity_lista_items)
        val botonVolver: Button = findViewById(id.boton_volver_ver_items)
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        //
        val b = intent.getExtras()

        //
        tipoAccion = b?.getInt("tipo_accion")!!
        tipoUsuario = b?.getInt("tipo_usuario")!!
        matriculaVehiculo = b?.getString("matricula_vehiculo").toString()
        idAlquiler = b?.getString("id_alquiler").toString()
        uidUsuario = b?.getString("uid_usuario").toString()
        fechaInicio = b?.getLong("fecha_inicio")!!
        fechaFin = b?.getLong("fecha_fin")!!

        //
        val showToolbar = intent.getBooleanExtra("SHOW_TOOLBAR", false)
        if (showToolbar) {
            toolbar.visibility = View.VISIBLE
            setSupportActionBar(toolbar)
            val customOverflow = ContextCompat.getDrawable(this, R.drawable.ic_more_vertical_white)
            toolbar.overflowIcon = customOverflow
        } else {
            toolbar.visibility = View.GONE
        }

        //
        botonVolver.setOnClickListener {
            onBackPressed()
        }

        //
        if (tipoAccion == accionBusquedaVehiculos && tipoUsuario == privilegiosUsuario) {
            //
            tvTitulo.text = resources.getString(string.listado_vehiculos)

            //
            vehiculoAdapterUsuario = VehiculoAdapterUsuario(listaVehiculos)

            //
            getVehiculosDisponibles(fechaInicio, fechaFin)

            // Creamos un layout manager y le pasamos por parametro dicha layout al recycleview
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
            recyclerView.setLayoutManager(layoutManager)
            recyclerView.setAdapter(vehiculoAdapterUsuario)

            //
            vehiculoAdapterUsuario.onItemClickAlquilar = { vehiculo ->
                //
                val intent =
                    Intent(this@ListarItemsActivity, CompletarReservaVehiculoActivity::class.java)
                var b = Bundle()
                b.putInt("precio_dia", vehiculo.precioDia.toInt())
                b.putString("marca_modelo", vehiculo.marca + " " + vehiculo.modelo)
                b.putString("matricula", vehiculo.matricula)
                b.putString("imagen", vehiculo.urlImagen)
                b.putLong("fecha_inicio", fechaInicio)
                b.putLong("fecha_fin", fechaFin)
                intent.putExtras(b)
                startActivity(intent)
                finish()
            }
        }

        //
        if (tipoAccion == accionVerAlquileresUsuario && tipoUsuario == privilegiosUsuario) {
            //
            tvTitulo.text = resources.getString(string.listado_alquileres)

            //
            alquilerAdapterUsuario = AlquilerAdapterUsuario(listaAlquileres)

            //
            getAlquileresUsuario(auth.uid.toString())

            // Creamos un layout manager y le pasamos por parametro dicha layout al recycleview
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
            recyclerView.setLayoutManager(layoutManager)
            recyclerView.setAdapter(alquilerAdapterUsuario)
        }

        //
        if (tipoAccion == accionVerUsuariosAdmin && tipoUsuario == privilegiosAdmin) {
            //
            tvTitulo.text = resources.getString(string.listado_usuarios)

            //
            usuarioAdapterAdmin = UsuarioAdapterAdmin(listaUsuarios)

            //
            getUsuariosAdmin()

            // Creamos un layout manager y le pasamos por parametro dicha layout al recycleview
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
            recyclerView.setLayoutManager(layoutManager)
            recyclerView.setAdapter(usuarioAdapterAdmin)

            //
            usuarioAdapterAdmin.onItemClickBorrarUsuario = { usuario ->
                if (usuario.admin) {
                    Toast.makeText(
                        this,
                        "No se puede borrar usuarios con privilegios de administración.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    //
                    val builder = AlertDialog.Builder(this)
                    //
                    builder.setTitle("Borrar usuario")
                    //
                    builder.setMessage("Estás seguro que desea borrarlo? Esta acción es irreversible.")
                    //
                    builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                        borrarUsuario(usuario.uid)
                        listaUsuarios.remove(usuario)
                        usuarioAdapterAdmin.notifyDataSetChanged()
                    }
                    //
                    builder.setNegativeButton(android.R.string.no) { dialog, which ->
                        Toast.makeText(
                            this, "No se han realizado cambios.", Toast.LENGTH_SHORT
                        ).show()
                    }
                    //
                    builder.show()
                }

            }
            //
            usuarioAdapterAdmin.onItemClickVerAlquileresUsuario = { usuario ->
                val intent = Intent(this@ListarItemsActivity, ListarItemsActivity::class.java)
                var b = Bundle()
                b.putInt("tipo_accion", accionVerAlquileresUsuario)
                b.putInt("tipo_usuario", privilegiosAdmin)
                b.putString("uid_usuario", usuario.uid)
                intent.putExtra("SHOW_TOOLBAR", false)
                intent.putExtras(b)
                startActivity(intent)
            }
            //
            usuarioAdapterAdmin.onItemClickEditarUsuario = { usuario ->
                val intent = Intent(this@ListarItemsActivity, EditarUsuarioActivity::class.java)
                var b = Bundle()
                b.putString("uid_usuario", usuario.uid)
                intent.putExtras(b)
                startActivity(intent)
            }
        }

        //
        if (tipoAccion == accionVerAlquileresUsuario && tipoUsuario == privilegiosAdmin) {
            //
            tvTitulo.text = resources.getString(string.listado_alquileres)

            //
            alquilerAdapterAdmin = AlquilerAdapterAdmin(listaAlquileres)

            //
            getAlquileresPorUidUsuario(uidUsuario)

            // Creamos un layout manager y le pasamos por parametro dicha layout al recycleview
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
            recyclerView.setLayoutManager(layoutManager)
            recyclerView.setAdapter(alquilerAdapterAdmin)


            //
            alquilerAdapterAdmin.onItemClickBorrarAlquiler = { alquiler ->
                //
                val builder = AlertDialog.Builder(this)
                //
                builder.setTitle("Borrar alquiler")
                //
                builder.setMessage("Estás seguro que desea borrarlo? Esta acción es irreversible.")
                //
                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                    borrarAlquiler(
                        alquiler.fechaInicio,
                        alquiler.fechaFin,
                        alquiler.matriculaVehiculo
                    )
                    listaAlquileres.remove(alquiler)
                    alquilerAdapterAdmin.notifyDataSetChanged()
                }
                //
                builder.setNegativeButton(android.R.string.no) { dialog, which ->
                    Toast.makeText(
                        this, "No se han realizado cambios.", Toast.LENGTH_SHORT
                    ).show()
                }
                //
                builder.show()
            }
            alquilerAdapterAdmin.onItemClickVerUsuarioAlquiler = { alquiler ->
                val intent =
                    Intent(this@ListarItemsActivity, ListarItemsActivity::class.java)
                var b = Bundle()
                b.putInt("tipo_accion", accionVerUsuarioAlquiler)
                b.putInt("tipo_usuario", privilegiosAdmin)
                b.putString("uid_usuario", alquiler.uidUsuario)
                intent.putExtra("SHOW_TOOLBAR", false)
                intent.putExtras(b)
                startActivity(intent)
            }

        }

        if (tipoAccion == accionVerUsuarioAlquiler && tipoUsuario == privilegiosAdmin) {
            //
            tvTitulo.text = resources.getString(string.listado_usuarios)

            //
            usuarioAdapterAdmin = UsuarioAdapterAdmin(listaUsuarios)

            //
            getUsuarioAlquilerVehiculo(uidUsuario)

            // Creamos un layout manager y le pasamos por parametro dicha layout al recycleview
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
            recyclerView.setLayoutManager(layoutManager)
            recyclerView.setAdapter(usuarioAdapterAdmin)

            //
            usuarioAdapterAdmin.onItemClickBorrarUsuario = { usuario ->
                if (usuario.admin) {
                    Toast.makeText(
                        this,
                        "No se puede borrar usuarios con privilegios de administración.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    //
                    val builder = AlertDialog.Builder(this)
                    //
                    builder.setTitle("Borrar usuario")
                    //
                    builder.setMessage("Estás seguro que desea borrarlo? Esta acción es irreversible.")
                    //
                    builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                        borrarUsuario(usuario.uid)
                        listaUsuarios.remove(usuario)
                        usuarioAdapterAdmin.notifyDataSetChanged()
                    }
                    //
                    builder.setNegativeButton(android.R.string.no) { dialog, which ->
                        Toast.makeText(
                            this, "No se han realizado cambios.", Toast.LENGTH_SHORT
                        ).show()
                    }
                    //
                    builder.show()
                }

                usuarioAdapterAdmin.onItemClickVerAlquileresUsuario = { usuario ->
                    val intent = Intent(this@ListarItemsActivity, ListarItemsActivity::class.java)
                    var b = Bundle()
                    b.putInt("tipo_accion", accionVerAlquileresUsuario)
                    b.putInt("tipo_usuario", privilegiosAdmin)
                    b.putString("uid_usuario", usuario.uid)
                    intent.putExtra("SHOW_TOOLBAR", false)
                    intent.putExtras(b)
                    startActivity(intent)
                }

            }

            //
            usuarioAdapterAdmin.onItemClickVerAlquileresUsuario = { usuario ->
                val intent = Intent(this@ListarItemsActivity, ListarItemsActivity::class.java)
                var b = Bundle()
                b.putInt("tipo_accion", accionVerAlquileresUsuario)
                b.putInt("tipo_usuario", privilegiosAdmin)
                b.putString("uid_usuario", usuario.uid)
                intent.putExtra("SHOW_TOOLBAR", false)
                intent.putExtras(b)
                startActivity(intent)
            }

            //
            usuarioAdapterAdmin.onItemClickEditarUsuario = { usuario ->
                val intent = Intent(this@ListarItemsActivity, EditarUsuarioActivity::class.java)
                var b = Bundle()
                b.putString("uid_usuario", usuario.uid)
                intent.putExtras(b)
                startActivity(intent)
            }

        }

        //
        if (tipoAccion == accionVerAlquileresAdmin && tipoUsuario == privilegiosAdmin) {
            //
            tvTitulo.text = resources.getString(string.listado_alquileres)

            //
            alquilerAdapterAdmin = AlquilerAdapterAdmin(listaAlquileres)

            //
            getAlquileresAdmin()

            // Creamos un layout manager y le pasamos por parametro dicha layout al recycleview
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
            recyclerView.setLayoutManager(layoutManager)
            recyclerView.setAdapter(alquilerAdapterAdmin)

            //
            alquilerAdapterAdmin.onItemClickBorrarAlquiler = { alquiler ->
                var fechafin: LocalDate = alquiler.fechaFin.toDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                if (fechafin.isAfter(LocalDate.now())) {
                    Toast.makeText(
                        this,
                        "No se pueden borrar alquileres activos.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    //
                    val builder = AlertDialog.Builder(this)
                    //
                    builder.setTitle("Borrar alquiler")
                    //
                    builder.setMessage("Estás seguro que desea borrarlo? Esta acción es irreversible.")
                    //
                    builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                        borrarAlquiler(
                            alquiler.fechaInicio,
                            alquiler.fechaFin,
                            alquiler.matriculaVehiculo
                        )
                        listaAlquileres.remove(alquiler)
                        alquilerAdapterAdmin.notifyDataSetChanged()
                    }
                    //
                    builder.setNegativeButton(android.R.string.no) { dialog, which ->
                        Toast.makeText(
                            this, "No se han realizado cambios.", Toast.LENGTH_SHORT
                        ).show()
                    }
                    //
                    builder.show()
                }
            }

            alquilerAdapterAdmin.onItemClickVerUsuarioAlquiler = { alquiler ->
                //
                tvTitulo.text = resources.getString(string.listado_usuarios)

                //
                usuarioAdapterAdmin = UsuarioAdapterAdmin(listaUsuarios)

                //
                getUsuarioAlquilerVehiculo(alquiler.uidUsuario)

                // Creamos un layout manager y le pasamos por parametro dicha layout al recycleview
                val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                recyclerView.setLayoutManager(layoutManager)
                recyclerView.setAdapter(usuarioAdapterAdmin)

                //
                usuarioAdapterAdmin.onItemClickBorrarUsuario = { usuario ->
                    if (usuario.admin) {
                        Toast.makeText(
                            this,
                            "No se puede borrar usuarios con privilegios de administración.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        //
                        val builder = AlertDialog.Builder(this)
                        //
                        builder.setTitle("Borrar usuario")
                        //
                        builder.setMessage("Estás seguro que desea borrarlo? Esta acción es irreversible.")
                        //
                        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                            borrarUsuario(usuario.uid)
                            listaUsuarios.remove(usuario)
                            usuarioAdapterAdmin.notifyDataSetChanged()
                        }
                        //
                        builder.setNegativeButton(android.R.string.no) { dialog, which ->
                            Toast.makeText(
                                this, "No se han realizado cambios.", Toast.LENGTH_SHORT
                            ).show()
                        }
                        //
                        builder.show()
                    }

                }
                //
                usuarioAdapterAdmin.onItemClickVerAlquileresUsuario = { usuario ->
                    val intent =
                        Intent(this@ListarItemsActivity, ListarItemsActivity::class.java)
                    var b = Bundle()
                    b.putInt("tipo_accion", accionVerAlquileresUsuario)
                    b.putInt("tipo_usuario", privilegiosAdmin)
                    b.putString("uid_usuario", alquiler.uidUsuario)
                    intent.putExtra("SHOW_TOOLBAR", false)
                    intent.putExtras(b)
                    startActivity(intent)
                }
                //
                usuarioAdapterAdmin.onItemClickEditarUsuario = { usuario ->
                    val intent =
                        Intent(this@ListarItemsActivity, EditarUsuarioActivity::class.java)
                    var b = Bundle()
                    b.putString("uid_usuario", usuario.uid)
                    intent.putExtras(b)
                    startActivity(intent)
                }
            }

        }

        //
        if (tipoAccion == accionVerVehiculosAdmin && tipoUsuario == privilegiosAdmin) {
            //
            tvTitulo.text = resources.getString(string.listado_vehiculos)

            //
            vehiculoAdapterAdmin = VehiculoAdapterAdmin(listaVehiculos)

            //
            getVehiculos()

            // Creamos un layout manager y le pasamos por parametro dicha layout al recycleview
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
            recyclerView.setLayoutManager(layoutManager)
            recyclerView.setAdapter(vehiculoAdapterAdmin)

            vehiculoAdapterAdmin.onItemClickLocalizar = { vehiculo ->
                val intent =
                    Intent(this@ListarItemsActivity, LocalizarVehiculoActivity::class.java)
                var b = Bundle()
                b.putDouble("latitud", vehiculo.localizacion.latitude)
                b.putDouble("longitud", vehiculo.localizacion.longitude)
                intent.putExtras(b)
                startActivity(intent)
            }

            vehiculoAdapterAdmin.onItemClickVerRevisiones = { vehiculo ->
                val intent = Intent(this@ListarItemsActivity, ListarItemsActivity::class.java)
                var b = Bundle()
                b.putInt("tipo_accion", accionVerRevisionesVehiculoAdmin)
                b.putInt("tipo_usuario", privilegiosAdmin)
                b.putString("matricula_vehiculo", vehiculo.matricula)
                intent.putExtra("SHOW_TOOLBAR", false)
                intent.putExtras(b)
                startActivity(intent)
            }

            vehiculoAdapterAdmin.onItemClickCambiarEstado = { vehiculo ->
                //
                cambiarEstadoVehiculo(vehiculo.matricula)
                refrescarListaVehiculosAdmin()
                //
                Toast.makeText(
                    this, "Visibilidad del vehiculo cambiada.", Toast.LENGTH_SHORT
                ).show()
            }

            vehiculoAdapterAdmin.onItemClickBorrarVehiculo = { vehiculo ->
                //
                val builder = AlertDialog.Builder(this)
                //
                builder.setTitle("Borrar vehículo")
                //
                builder.setMessage("Estás seguro que desea borrarlo? Esta acción es irreversible.")
                //
                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                    borrarVehiculo(vehiculo.matricula)
                    listaVehiculos.remove(vehiculo)
                    vehiculoAdapterAdmin.notifyDataSetChanged()
                }
                //
                builder.setNegativeButton(android.R.string.no) { dialog, which ->
                    Toast.makeText(
                        this, "No se han realizado cambios.", Toast.LENGTH_SHORT
                    ).show()
                }
                //
                builder.show()
            }

            //
            vehiculoAdapterAdmin.onItemClickAgregarRevision = { vehiculo ->
                //
                val intent =
                    Intent(
                        this@ListarItemsActivity,
                        AgregarRevisionVehiculoActivity::class.java
                    )
                var b = Bundle()
                b.putString("matricula_vehiculo", vehiculo.matricula)
                intent.putExtras(b)
                startActivity(intent)
            }

            //
            vehiculoAdapterAdmin.onItemClickVerHistorialAlquileres = { vehiculo ->
                //
                val intent =
                    Intent(this@ListarItemsActivity, ListarItemsActivity::class.java)
                var b = Bundle()
                b.putInt("tipo_accion", accionVerHistorialVehiculoAdmin)
                b.putInt("tipo_usuario", privilegiosAdmin)
                b.putString("matricula_vehiculo", vehiculo.matricula)
                intent.putExtra("SHOW_TOOLBAR", false)
                intent.putExtras(b)
                startActivity(intent)
            }

        }

        if (tipoAccion == accionVerHistorialVehiculoAdmin && tipoUsuario == privilegiosAdmin) {
            tvTitulo.text = resources.getString(string.listado_alquileres)

            //
            alquilerAdapterAdmin = AlquilerAdapterAdmin(listaAlquileres)

            //
            getHistorialAlquileresVehiculo(matriculaVehiculo)

            // Creamos un layout manager y le pasamos por parametro dicha layout al recycleview
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
            recyclerView.setLayoutManager(layoutManager)
            recyclerView.setAdapter(alquilerAdapterAdmin)

            //
            alquilerAdapterAdmin.onItemClickBorrarAlquiler = { alquiler ->
                //
                val builder = AlertDialog.Builder(this)
                //
                builder.setTitle("Borrar alquiler")
                //
                builder.setMessage("Estás seguro que desea borrarlo? Esta acción es irreversible.")
                //
                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                    borrarAlquiler(
                        alquiler.fechaInicio,
                        alquiler.fechaFin,
                        alquiler.matriculaVehiculo
                    )
                    listaAlquileres.remove(alquiler)
                    alquilerAdapterAdmin.notifyDataSetChanged()
                }
                //
                builder.setNegativeButton(android.R.string.no) { dialog, which ->
                    Toast.makeText(
                        this, "No se han realizado cambios.", Toast.LENGTH_SHORT
                    ).show()
                }
                //
                builder.show()
            }
        }

        //
        if (tipoAccion == accionVerRevisionesVehiculoAdmin && tipoUsuario == privilegiosAdmin) {
            //
            tvTitulo.text = resources.getString(string.lista_revisiones)

            //
            revisionesAdapterAdmin = RevisionAdapterAdmin(listaRevisiones)

            //
            getRevisionesVehiculo(matriculaVehiculo)

            // Creamos un layout manager y le pasamos por parametro dicha layout al recycleview
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
            recyclerView.setLayoutManager(layoutManager)
            recyclerView.setAdapter(revisionesAdapterAdmin)

            revisionesAdapterAdmin.onItemClickBorrarRevision = { revision ->
                //
                val builder = AlertDialog.Builder(this)
                //
                builder.setTitle("Borrar revisión")
                //
                builder.setMessage("Estás seguro que desea borrarla? Esta acción es irreversible.")
                //
                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                    borrarRevision(
                        revision.fechaRevision,
                        revision.tipoRevision,
                        revision.matriculaVehiculo
                    )
                    listaRevisiones.remove(revision)
                    revisionesAdapterAdmin.notifyDataSetChanged()
                }
                //
                builder.setNegativeButton(android.R.string.no) { dialog, which ->
                    Toast.makeText(
                        this, "No se han realizado cambios.", Toast.LENGTH_SHORT
                    ).show()
                }
                //
                builder.show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            id.item_agregar_vehiculo -> {
                startActivity(
                    Intent(
                        this@ListarItemsActivity,
                        AgregarVehiculoActivity::class.java
                    )
                )
                true
            }

            id.item_recargar_lista_vehiculos -> {
                refrescarListaVehiculosAdmin()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getAlquileresPorUidUsuario(uid: String) = runBlocking {
        db.collection("alquileres").whereEqualTo("uidUsuario", uid).get()
            .addOnSuccessListener { result ->
                Log.d(
                    "Listar Alquileres Usuario por UID",
                    "Documentos obtenidos correctamente."
                )
                for (document in result) {
                    listaAlquileres.add(
                        Alquiler(
                            document.data["fechaFin"] as Timestamp,
                            document.data["fechaInicio"] as Timestamp,
                            document.data["matriculaVehiculo"].toString(),
                            document.data["metodoPago"].toString(),
                            document.data["precioTotal"] as Long,
                            document.data["uidUsuario"].toString()
                        )
                    )
                    alquilerAdapterAdmin.notifyDataSetChanged()
                }
            }.addOnFailureListener { exception ->
                Log.w(
                    "Listar Alquileres Usuario por UID",
                    "Error getting documents.",
                    exception
                )
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getVehiculosDisponibles(fechaInicio: Long, fechaFin: Long) = runBlocking {
        val dateFechaInicio = Date(fechaInicio)
        val dateFechaFin = Date(fechaFin)
        val vehiculosOcupados = mutableSetOf<String>()
        db.collection("alquileres")
            .get()
            .addOnSuccessListener { result ->
                Log.d("Listar Alquileres Usuario", "Alquileres obtenidos correctamente.")
                for (document in result) {
                    val fechaInicioAlquiler =
                        (document.data["fechaInicio"] as Timestamp).toDate()
                    val fechaFinAlquiler = (document.data["fechaFin"] as Timestamp).toDate()

                    // Si hay solapamiento entre el alquiler y las fechas buscadas
                    if (fechaInicioAlquiler <= dateFechaFin && fechaFinAlquiler >= dateFechaInicio) {
                        val matricula = document.data["matriculaVehiculo"].toString()
                        vehiculosOcupados.add(matricula)
                    }
                }
                // Ahora consultamos los vehículos disponibles
                db.collection("vehiculos")
                    .whereEqualTo("disponibilidad", true)
                    .get()
                    .addOnSuccessListener { resultVehiculos ->
                        Log.d("Listar Alquileres Usuario", "Vehículos obtenidos correctamente.")
                        listaVehiculos.clear()
                        for (document in resultVehiculos) {
                            val matricula = document.data["matricula"].toString()
                            // Solo añadimos los que NO están en la lista de ocupados
                            if (!vehiculosOcupados.contains(matricula)) {
                                listaVehiculos.add(
                                    Vehiculo(
                                        document.data["anio"] as Long,
                                        document.data["combustible"].toString(),
                                        document.data["disponibilidad"] as Boolean,
                                        document.data["km"] as Long,
                                        document.data["localizacion"] as GeoPoint,
                                        document.data["marca"].toString(),
                                        matricula,
                                        document.data["modelo"].toString(),
                                        document.data["potencia"] as Long,
                                        document.data["precioDia"] as Long,
                                        document.data["urlImagen"].toString()
                                    )
                                )
                            }
                        }
                        vehiculoAdapterUsuario.notifyDataSetChanged()
                    }
                    .addOnFailureListener { exception ->
                        Log.w(
                            "Listar Alquileres Usuario",
                            "Error al obtener vehículos",
                            exception
                        )
                    }
            }
            .addOnFailureListener { exception ->
                Log.w("Listar Alquileres Usuario", "Error al obtener alquileres", exception)
            }
    }

    //
    @SuppressLint("NotifyDataSetChanged")
    private fun getAlquileresUsuario(uid: String) = runBlocking {
        db.collection("alquileres").whereEqualTo("uidUsuario", uid).get()
            .addOnSuccessListener { result ->
                Log.d("Listar Alquileres Usuario", "Documentos obtenidos correctamente.")
                for (document in result) {
                    listaAlquileres.add(
                        Alquiler(
                            document.data["fechaFin"] as Timestamp,
                            document.data["fechaInicio"] as Timestamp,
                            document.data["matriculaVehiculo"].toString(),
                            document.data["metodoPago"].toString(),
                            document.data["precioTotal"] as Long,
                            document.data["uidUsuario"].toString()
                        )
                    )
                    alquilerAdapterUsuario.notifyDataSetChanged()
                }
            }.addOnFailureListener { exception ->
                Log.w("Listar Alquileres Usuario", "Error getting documents.", exception)
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getVehiculos() = runBlocking {
        db.collection("vehiculos").get().addOnSuccessListener { result ->
            Log.d("Listar Vehiculos Admin", "Documentos obtenidos correctamente.")
            for (document in result) {
                listaVehiculos.add(
                    Vehiculo(
                        document.data["anio"] as Long,
                        document.data["combustible"].toString(),
                        document.data["disponibilidad"] as Boolean,
                        document.data["km"] as Long,
                        document.data["localizacion"] as GeoPoint,
                        document.data["marca"].toString(),
                        document.data["matricula"].toString(),
                        document.data["modelo"].toString(),
                        document.data["potencia"] as Long,
                        document.data["precioDia"] as Long,
                        document.data["urlImagen"].toString()
                    )
                )
                vehiculoAdapterAdmin.notifyDataSetChanged()
            }
        }.addOnFailureListener { exception ->
            Log.w("Listar Vehiculos Admin", "Error getting documents.", exception)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getAlquileresAdmin() = runBlocking {
        db.collection("alquileres").get().addOnSuccessListener { result ->
            Log.d("Listar Alquileres Admin", "Documentos obtenidos correctamente.")
            for (document in result) {
                listaAlquileres.add(
                    Alquiler(
                        document.data["fechaFin"] as Timestamp,
                        document.data["fechaInicio"] as Timestamp,
                        document.data["matriculaVehiculo"].toString(),
                        document.data["metodoPago"].toString(),
                        document.data["precioTotal"] as Long,
                        document.data["uidUsuario"].toString()
                    )
                )
                alquilerAdapterAdmin.notifyDataSetChanged()
            }
        }.addOnFailureListener { exception ->
            Log.w("Listar Alquileres Admin", "Error getting documents.", exception)
        }
    }

    //
    @SuppressLint("NotifyDataSetChanged")
    private fun getUsuariosAdmin() = runBlocking {
        db.collection("usuarios").get().addOnSuccessListener { result ->
            Log.d("Listar Usuarios Admin", "Documentos obtenidos correctamente.")
            for (document in result) {
                listaUsuarios.add(
                    Usuario(
                        document.data["admin"] as Boolean,
                        document.data["dni"].toString(),
                        document.data["email"].toString(),
                        document.data["fechaNac"] as Timestamp,
                        document.data["nombre"].toString(),
                        document.data["telefono"].toString(),
                        document.data["uid"].toString(),
                    )
                )
                usuarioAdapterAdmin.notifyDataSetChanged()
            }
        }.addOnFailureListener { exception ->
            Log.w("Listar Usuarios Admin", "Error getting documents.", exception)
        }
    }

    //
    @SuppressLint("NotifyDataSetChanged")
    private fun getRevisionesVehiculo(matricula: String) = runBlocking {
        db.collection("revisiones").whereEqualTo("matriculaVehiculo", matricula).get()
            .addOnSuccessListener { result ->
                Log.d("Listar Revisiones Vehiculo", "Documentos obtenidos correctamente.")
                for (document in result) {
                    listaRevisiones.add(
                        Revision(
                            document.data["fechaRevision"] as Timestamp,
                            document.data["kmLeido"] as Long,
                            document.data["matriculaVehiculo"].toString(),
                            document.data["precio"] as Long,
                            document.data["siguienteRevision"] as Timestamp,
                            document.data["tipoRevision"].toString()
                        )
                    )
                    revisionesAdapterAdmin.notifyDataSetChanged()
                }
            }.addOnFailureListener { exception ->
                Log.w("Listar Revisiones Vehiculo", "Error getting documents.", exception)
            }
    }

    //
    private fun cambiarEstadoVehiculo(matricula: String) = runBlocking {
        db.collection("vehiculos")
            .whereEqualTo("matricula", matricula)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var estado: Boolean = document.data["disponibilidad"] as Boolean
                    if (estado) {
                        val data = hashMapOf("disponibilidad" to false)
                        db.collection("vehiculos").document(document.id)
                            .set(data, SetOptions.merge())
                        Log.d("Cambiar Estado Vehiculo", "Estado del vehiculo cambiado.")
                    } else {
                        val data = hashMapOf("disponibilidad" to true)
                        db.collection("vehiculos").document(document.id)
                            .set(data, SetOptions.merge())
                        Log.d("Cambiar Estado Vehiculo", "Estado del vehiculo cambiado.")
                    }
                }
            }.addOnFailureListener { exception ->
                Log.w("Cambiar Estado Vehiculo", "Error al cambiar estado.", exception)
            }
    }

    //
    private fun borrarUsuario(uid: String) = runBlocking {
        db.collection("usuarios")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    borrarDocumento("usuarios", document.id)
                }
            }.addOnFailureListener { exception ->
                Log.w("Listar Alquileres", "Error getting documents.", exception)
            }
    }

    //
    private fun borrarVehiculo(
        matricula: String
    ) = runBlocking {
        db.collection("vehiculos")
            .whereEqualTo("matricula", matricula)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    borrarDocumento("vehiculos", document.id)
                }
            }.addOnFailureListener { exception ->
                Log.w("Listar Alquileres", "Error getting documents.", exception)
            }
    }

    //
    private fun borrarAlquiler(
        fechaInicio: Timestamp,
        fechaFin: Timestamp,
        matriculaVehiculo: String
    ) = runBlocking {
        db.collection("alquileres")
            .whereEqualTo("matriculaVehiculo", matriculaVehiculo)
            .whereEqualTo("fechaInicio", fechaInicio)
            .whereEqualTo("fechaFin", fechaFin)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    borrarDocumento("alquileres", document.id)
                }
            }.addOnFailureListener { exception ->
                Log.w("Listar Alquileres", "Error getting documents.", exception)
            }
    }

    //
    private fun borrarRevision(
        fechaRevision: Timestamp,
        tipoRevision: String,
        matriculaVehiculo: String
    ) = runBlocking {
        db.collection("revisiones")
            .whereEqualTo("matriculaVehiculo", matriculaVehiculo)
            .whereEqualTo("fechaRevision", fechaRevision)
            .whereEqualTo("tipoRevision", tipoRevision)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    borrarDocumento("revisiones", document.id)
                }
            }.addOnFailureListener { exception ->
                Log.w("Listar Revisiones", "Error getting documents.", exception)
            }
    }

    //
    private fun borrarDocumento(
        coleccion: String,
        idDocumento: String
    ) = runBlocking {
        db.collection(coleccion).document(idDocumento).delete().addOnSuccessListener {
            Log.d(
                "Borrar Documento", "Documento borrado con exito"
            )
        }.addOnFailureListener { e ->
            Log.w("Borrar Documento", "Error al borrar el documento", e)
        }
    }

    private fun refrescarListaVehiculosAdmin() = runBlocking {
        val intent =
            Intent(this@ListarItemsActivity, ListarItemsActivity::class.java)
        var b = Bundle()
        b.putInt("tipo_accion", accionVerVehiculosAdmin)
        b.putInt("tipo_usuario", privilegiosAdmin)
        intent.putExtra("SHOW_TOOLBAR", true)
        intent.putExtras(b)
        startActivity(intent)
        finish()
        delay(1000)
    }

    //
    @SuppressLint("NotifyDataSetChanged")
    private fun getHistorialAlquileresVehiculo(matricula: String) = runBlocking {
        db.collection("alquileres").whereEqualTo("matriculaVehiculo", matricula)
            .get()
            .addOnSuccessListener { result ->
                Log.d("Listar Alquileres Vehiculo", "Documentos obtenidos correctamente.")
                for (document in result) {
                    listaAlquileres.add(
                        Alquiler(
                            document.data["fechaFin"] as Timestamp,
                            document.data["fechaInicio"] as Timestamp,
                            document.data["matriculaVehiculo"].toString(),
                            document.data["metodoPago"].toString(),
                            document.data["precioTotal"] as Long,
                            document.data["uidUsuario"].toString()
                        )
                    )
                    alquilerAdapterAdmin.notifyDataSetChanged()
                }
            }.addOnFailureListener { exception ->
                Log.w("Listar Alquileres Vehiculo", "Error getting documents.", exception)
            }
    }

    //
    @SuppressLint("NotifyDataSetChanged")
    private fun getUsuarioAlquilerVehiculo(uid: String) = runBlocking {
        db.collection("usuarios").whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { result ->
                Log.d("Listar Usuario Alquiler Vehiculo", "Documentos obtenidos correctamente.")
                for (document in result) {
                    listaUsuarios.add(
                        Usuario(
                            document.data["admin"] as Boolean,
                            document.data["dni"].toString(),
                            document.data["email"].toString(),
                            document.data["fechaNac"] as Timestamp,
                            document.data["nombre"].toString(),
                            document.data["telefono"].toString(),
                            document.data["uid"].toString(),
                        )
                    )
                    usuarioAdapterAdmin.notifyDataSetChanged()
                }
            }.addOnFailureListener { exception ->
                Log.w("Listar Usuario Alquiler Vehiculo", "Error getting documents.", exception)
            }
    }

}