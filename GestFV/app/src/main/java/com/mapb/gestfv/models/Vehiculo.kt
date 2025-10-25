package com.mapb.gestfv.modelo

import com.google.firebase.firestore.GeoPoint

data class Vehiculo(
    val anio: Long,
    val combustible: String,
    val disponibilidad: Boolean,
    val km: Long,
    val localizacion: GeoPoint,
    val marca: String,
    val matricula: String,
    val modelo: String,
    val potencia: Long,
    val precioDia: Long,
    val urlImagen: String
)

fun Vehiculo() {

}

