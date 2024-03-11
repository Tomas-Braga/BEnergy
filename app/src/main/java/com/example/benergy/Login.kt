package com.example.benergy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

@Suppress("DEPRECATION")
class Login : AppCompatActivity(){
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        FirebaseFirestore.getInstance()
        setContentView(R.layout.activity_login)


        val button = findViewById<Button>(R.id.button)
        val editTextTextEmailAddress = findViewById<EditText>(R.id.editTextTextEmailAddress)
        val editTextTextPassword = findViewById<EditText>(R.id.editTextTextPassword2)
        val textView4 = findViewById<TextView>(R.id.textView4)
        val textView5 = findViewById<TextView>(R.id.textView5)
        val textView3 = findViewById<TextView>(R.id.textView3)

        editTextTextEmailAddress.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                textView4.setTextColor(resources.getColor(R.color.verde_titulo))
            } else {
                textView4.setTextColor(resources.getColor(R.color.cinza_escuro))
            }
        }

        editTextTextPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                textView5.setTextColor(resources.getColor(R.color.verde_titulo))
            } else {
                textView5.setTextColor(resources.getColor(R.color.cinza_escuro))
            }
        }

        textView3.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        button.setOnClickListener {
            login()
        }

    }

    private fun login() {
        val email = findViewById<EditText>(R.id.editTextTextEmailAddress).text.toString()
        val password = findViewById<EditText>(R.id.editTextTextPassword2).text.toString()


        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "SUCCESS.",
                        Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Homepage::class.java)
                    startActivity(intent)

                } else {
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to login: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}