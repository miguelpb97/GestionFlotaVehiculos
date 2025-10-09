package com.mapb.gestfv

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.net.toUri

class ObtenerAyudaActivity : ComponentActivity() {

    private lateinit var etAsunto : EditText
    private lateinit var etMensaje : EditText
    private val emailContactoAdmin: String = "miguelpb1097@gmail.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_obtener_ayuda)

        //
        etAsunto = findViewById(R.id.et_asunto_obtener_ayuda)
        etMensaje = findViewById(R.id.et_mensaje_obtener_ayuda)
        var botonEnviar: Button = findViewById(R.id.boton_enviar_obtener_ayuda)

        //
        botonEnviar.setOnClickListener {
            var asunto = etAsunto.text
            var texto = etMensaje.text
            if (texto.isEmpty() || asunto.isEmpty()) {
                Toast.makeText(this, "Alguno de los campos está vacio", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = "mailto:".toUri()
                    putExtra(Intent.EXTRA_EMAIL, emailContactoAdmin)
                    putExtra(Intent.EXTRA_SUBJECT, etAsunto.text)
                    putExtra(Intent.EXTRA_TEXT, etMensaje.text)
                }
                if (intent.resolveActivity(packageManager) != null){
                    startActivity(intent)
                    etAsunto.text.clear()
                    etMensaje.text.clear()
                    Toast.makeText(this, "Gracias por ponerte en contacto con nosotros, en breve le contactaremos.", Toast.LENGTH_LONG).show()

                } else {
                    Toast.makeText(this, "Aplicacion de mail requerida no instalada.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}