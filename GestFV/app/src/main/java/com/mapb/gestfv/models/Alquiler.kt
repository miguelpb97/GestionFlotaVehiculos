package com.mapb.gestfv.modelo

import com.google.firebase.Timestamp

data class Alquiler(
    var fechaFin: Timestamp,
    var fechaInicio: Timestamp,
    var matriculaVehiculo: String,
    var metodoPago: String,
    var precioTotal: Long,
    var uidUsuario: String
)

fun Alquiler() {

}