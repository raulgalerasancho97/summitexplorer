package com.example.summitexplorer

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.summitexplorer.database.dao.UserDao
import com.example.summitexplorer.database.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var userDao: UserDao
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        setContentView(R.layout.activity_login)
        userDao = MyApp.database.userDao()
        val editTextEmail = findViewById<EditText>(R.id.editTextEmailLogin)
        val editTextPassword = findViewById<EditText>(R.id.editTextPasswordLogin)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val registerTextView = findViewById<TextView>(R.id.registerTextView)
        val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()

        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (email.isEmpty()) {
                editTextEmail.error = "Email requerido"
                return@setOnClickListener
            }
            if (!email.matches(emailPattern)) {
                editTextEmail.error = "Formato de email inválido"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                editTextPassword.setError("Introduce una contraseña", null)
                return@setOnClickListener
            }
            if (email.isNotEmpty() && password.isNotEmpty()) {
                GlobalScope.launch(Dispatchers.IO) {
                    if (checkIfUserExists(email)) {
                        if (userDao.getUserByEmailAndPassword(email, password) != null) {
                            val username = userDao.getUserByEmail(email)!!.username
                            sharedPreferences.edit().putBoolean("isLogged", true).apply()
                            sharedPreferences.edit().putString("userEmail", email).apply()
                            redirectToHome()
                        } else {
                            runOnUiThread {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Credenciales incorrectas",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@LoginActivity,
                                "El email no existe",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        registerTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private suspend fun checkIfUserExists(email: String): Boolean {
        val existingUser = userDao.getUserByEmail(email)
        return existingUser != null
    }

    private fun redirectToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}