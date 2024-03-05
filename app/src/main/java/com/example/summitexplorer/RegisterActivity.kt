package com.example.summitexplorer

import android.content.Intent
import android.os.Bundle
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

        buttonRegister.setOnClickListener {
            val name = editTextName.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                GlobalScope.launch(Dispatchers.IO) {
                    if (checkIfUserExists(email)) {
                        Toast.makeText(this@RegisterActivity, "Email no valido", Toast.LENGTH_SHORT)
                            .show();
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
