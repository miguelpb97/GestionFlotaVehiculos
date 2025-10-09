package com.mapb.gestfv.modelo

import com.google.firebase.Timestamp

data class Usuario(
    val admin: Boolean,
    val dni: String,
    val email: String,
    val fechaNac: Timestamp,
    val nombre: String,
    val telefono: String,
    val uid: String
)

fun Usuario() {

}
