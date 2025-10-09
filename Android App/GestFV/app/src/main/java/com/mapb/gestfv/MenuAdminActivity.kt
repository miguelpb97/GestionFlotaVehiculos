package com.mapb.gestfv

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking

class MenuAdminActivity : ComponentActivity() {

    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private lateinit var tvBienvenida: TextView
    private val accionVerAlquileresAdmin: Int = 1
    private val accionVerUsuariosAdmin: Int = 2
    private val accionVerVehiculosAdmin: Int = 3
    private val privilegiosAdmin: Int = 9

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_admin)

        // Inicializamos Firebase Auth
        auth = com.google.firebase.Firebase.auth

        //
        tvBienvenida = findViewById(R.id.tv_bienvenida_admin)
        val botonGestionarVehiculos: Button = findViewById(R.id.boton_gestionar_vehiculos_admin)
        val botonGestionarUsuarios: Button = findViewById(R.id.boton_gestionar_usuarios_admin)
        val botonGestionarAlquileres: Button = findViewById(R.id.boton_gestionar_alquileres_admin)
        val botonGestionarPerfil: Button = findViewById(R.id.boton_gestionar_perfil_admin)
        val botonCerrarSesion: Button = findViewById(R.id.boton_cerrar_sesion_admin)

        //
        getNombreCompletoUsuario()

        //
        botonGestionarVehiculos.setOnClickListener {
            val intent =
                Intent(this@MenuAdminActivity, ListarItemsActivity::class.java)
            var b = Bundle()
            b.putInt("tipo_accion", accionVerVehiculosAdmin)
            b.putInt("tipo_usuario", privilegiosAdmin)
            intent.putExtra("SHOW_TOOLBAR", true)
            intent.putExtras(b)
            startActivity(intent)
        }

        //
        botonGestionarUsuarios.setOnClickListener {
            val intent =
                Intent(this@MenuAdminActivity, ListarItemsActivity::class.java)
            var b = Bundle()
            b.putInt("tipo_accion", accionVerUsuariosAdmin)
            b.putInt("tipo_usuario", privilegiosAdmin)
            intent.putExtra("SHOW_TOOLBAR", false)
            intent.putExtras(b)
            startActivity(intent)
        }

        //
        botonGestionarAlquileres.setOnClickListener {
            val intent =
                Intent(this@MenuAdminActivity, ListarItemsActivity::class.java)
            var b = Bundle()
            b.putInt("tipo_accion", accionVerAlquileresAdmin)
            b.putInt("tipo_usuario", privilegiosAdmin)
            intent.putExtra("SHOW_TOOLBAR", false)
            intent.putExtras(b)
            startActivity(intent)
        }

        //
        botonGestionarPerfil.setOnClickListener {
            startActivity(Intent(this@MenuAdminActivity, GestionarPerfilActivity::class.java))
        }

        //
        botonCerrarSesion.setOnClickListener {
            cerrarSesion()
        }

    }

    private fun getNombreCompletoUsuario() = runBlocking {
        db.collection("usuarios")
            .whereEqualTo("uid", auth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { result ->
                Log.d("Menu Admin", "Nombre del usuario obtenido correctamente.")
                for (document in result) {
                    tvBienvenida.text = buildString {
                        append("Bienvenido, ")
                        append(document.data["nombre"])
                    }
                }
            }.addOnFailureListener { exception ->
                Log.w("Menu Admin", "Error al obtener el nombre del usuario.", exception)
            }
    }

    private fun cerrarSesion() = runBlocking {
        com.google.firebase.Firebase.auth.signOut()
        startActivity(Intent(this@MenuAdminActivity, LoginActivity::class.java))
        finish()
    }

}