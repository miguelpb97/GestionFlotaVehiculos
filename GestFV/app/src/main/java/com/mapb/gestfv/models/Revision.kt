package com.mapb.gestfv.modelo

import com.google.firebase.Timestamp

data class Revision(
    var fechaRevision: Timestamp,
    var kmLeido: Long,
    var matriculaVehiculo: String,
    var precio: Long,
    var siguienteRevision: Timestamp,
    var tipoRevision: String
)

fun Revision() {

}