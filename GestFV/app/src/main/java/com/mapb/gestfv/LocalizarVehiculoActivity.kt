package com.mapb.gestfv

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class LocalizarVehiculoActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_localizar_vehiculo)

        //
        val botonVolver: Button = findViewById(R.id.boton_volver_localizar_vehiculo)
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapa_localizar) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //
        val b = intent.getExtras()

        //
        latitud = b?.getDouble("latitud")!!
        longitud = b?.getDouble("longitud")!!

        //
        botonVolver.setOnClickListener {
            //
            finish()
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        //
        var location = LatLng(latitud, longitud)

        // Agregamos una marca en la localización del vehiculo
        map.addMarker(MarkerOptions().position(location))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))

        // Controles
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true
    }
}