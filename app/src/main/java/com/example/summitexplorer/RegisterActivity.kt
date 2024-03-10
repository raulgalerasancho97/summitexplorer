package com.example.summitexplorer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.summitexplorer.database.dao.UserDao
import com.example.summitexplorer.database.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        userDao = MyApp.database.userDao()
        val editTextName = findViewById<EditText>(R.id.editTextName)
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
        buttonRegister.setOnClickListener {
            val name = editTextName.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (name.isEmpty()) {
                editTextName.error = "Nombre requerido"
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                editTextEmail.error = "Email requerido"
                return@setOnClickListener
            }
            if (!email.matches(emailPattern)) {
                editTextEmail.error = "Formato de email inválido"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                editTextPassword.error = "Contraseña requerida"
                return@setOnClickListener
            }
            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                GlobalScope.launch(Dispatchers.IO) {
                    if (checkIfUserExists(email)) {
                        runOnUiThread { //Toast cant run on thread that is not main
                            Toast.makeText(
                                this@RegisterActivity,
                                "El email ya existe",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        //Toast.makeText(context,"El email ya esta en uso",Toast.LENGTH_LONG).show();
                        Log.d("UserDebug", "El email ya existe");
                    } else {
                        val newUser = User(username = name, email = email, password = password)
                        userDao.insertUser(newUser)
                        redirectToHome()
                    }
                }
            } else {
                // Mostrar mensaje de error indicando que todos los campos son obligatorios
            }
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
