package com.example.summitexplorer

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class AuthManager {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun registerWithEmailPassword(
        email: String,
        password: String,
        callback: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // El usuario se registró exitosamente
                    callback(true, null)
                } else {
                    // Hubo un error al registrar el usuario
                    val errorMessage = when (val exception = task.exception) {
                        is FirebaseAuthUserCollisionException -> "El usuario ya existe"
                        else -> "Error al registrar el usuario: ${exception?.message}"
                    }
                    Log.e("AuthManager", "Error registering user", task.exception)
                    callback(false, errorMessage)
                }
            }
    }
}
