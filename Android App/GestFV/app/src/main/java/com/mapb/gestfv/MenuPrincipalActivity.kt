package com.mapb.gestfv

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ktx.firestore
import kotlinx.coroutines.runBlocking

class MenuPrincipalActivity : ComponentActivity() {

    private val db = com.google.firebase.ktx.Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private lateinit var tvBienvenida: TextView
    private val accionVerAlquileresUsuario: Int = 6
    private val privilegiosUsuario: Int = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal)

        // Inicializamos Firebase Auth
        auth = Firebase.auth

        //
        tvBienvenida = findViewById(R.id.tv_bienvenida)
        val botonAlquilarVehiculo: Button = findViewById(R.id.boton_alquilar_un_vehiculo)
        val botonVerAlquileres: Button = findViewById(R.id.boton_ver_alquileres)
        val botonGestionarPerfil: Button = findViewById(R.id.boton_gestion_perfil)
        val botonCerrarSesion: Button = findViewById(R.id.boton_cerrar_sesion_admin)

        //
        getNombreCompletoUsuario()

        //
        botonAlquilarVehiculo.setOnClickListener {
            startActivity(Intent(this@MenuPrincipalActivity, BuscarVehiculosActivity::class.java))
        }

        //
        botonVerAlquileres.setOnClickListener {
            val intent =
                Intent(this@MenuPrincipalActivity, ListarItemsActivity::class.java)
            var b = Bundle()
            b.putInt("tipo_accion", accionVerAlquileresUsuario)
            b.putInt("tipo_usuario", privilegiosUsuario)
            intent.putExtra("SHOW_TOOLBAR", false)
            intent.putExtras(b)
            startActivity(intent)
        }

        //
        botonGestionarPerfil.setOnClickListener {
            val intent = Intent(this@MenuPrincipalActivity, GestionarPerfilActivity::class.java)
            startActivity(intent)
        }

        //
        botonCerrarSesion.setOnClickListener {
            cerrarSesion()
        }
    }

    //
    private fun getNombreCompletoUsuario() = runBlocking {
        db.collection("usuarios")
            .whereEqualTo("uid", auth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { result ->
                Log.d("Menu Principal", "Nombre del usuario obtenido correctamente.")
                for (document in result) {
                    tvBienvenida.text = buildString {
                        append("Bienvenido, ")
                        append(document.data["nombre"])
                    }
                }
            }.addOnFailureListener { exception ->
                Log.w("Menu Principal", "Error al obtener el nombre del usuario.", exception)
            }
    }

    //
    private fun cerrarSesion() = runBlocking {
        Firebase.auth.signOut()
        startActivity(Intent(this@MenuPrincipalActivity, LoginActivity::class.java))
        finish()
    }
}





