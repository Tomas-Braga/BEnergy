package com.example.benergy

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.*

@Suppress("DEPRECATION", "NAME_SHADOWING")
class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContentView(R.layout.activity_register)

        val button = findViewById<Button>(R.id.button)
        val textLogin = findViewById<TextView>(R.id.textLogin)
        val textName = findViewById<TextView>(R.id.textName)
        val textEmail = findViewById<TextView>(R.id.textEmail)
        val textPassword = findViewById<TextView>(R.id.textPassword)
        val textConfirmPassword = findViewById<TextView>(R.id.textRepeatPassword)
        val textNumber = findViewById<TextView>(R.id.textNumber)
        val editTextTextPersonName = findViewById<EditText>(R.id.editTextTextPersonName)
        val editTextTextEmailAddress = findViewById<EditText>(R.id.editEmail)
        val editTextTextPassword = findViewById<EditText>(R.id.editPassword)
        val editTextTextPassword2 = findViewById<EditText>(R.id.editRepeatPassword)
        val editTextPhone = findViewById<EditText>(R.id.editTextPhone)

        editTextTextEmailAddress.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                textEmail.setTextColor(resources.getColor(R.color.verde_titulo))
            } else {
                textEmail.setTextColor(resources.getColor(R.color.cinza_escuro))
            }
        }

        editTextTextPersonName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                textName.setTextColor(resources.getColor(R.color.verde_titulo))
            } else {
                textName.setTextColor(resources.getColor(R.color.cinza_escuro))
            }
        }

        editTextTextPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                textPassword.setTextColor(resources.getColor(R.color.verde_titulo))
            } else {
                textPassword.setTextColor(resources.getColor(R.color.cinza_escuro))
            }
        }

        editTextTextPassword2.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                textConfirmPassword.setTextColor(resources.getColor(R.color.verde_titulo))
            } else {
                textConfirmPassword.setTextColor(resources.getColor(R.color.cinza_escuro))
            }
        }

        editTextPhone.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                textNumber.setTextColor(resources.getColor(R.color.verde_titulo))
            } else {
                textNumber.setTextColor(resources.getColor(R.color.cinza_escuro))
            }
        }

        textLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        button.setOnClickListener {
            signUp()
        }


    }

    @SuppressLint("SimpleDateFormat")
    private fun signUp() {
        val email = findViewById<EditText>(R.id.editEmail).text.toString()
        val password = findViewById<EditText>(R.id.editPassword).text.toString()
        val repeatPassword = findViewById<EditText>(R.id.editRepeatPassword).text.toString()
        val name = findViewById<EditText>(R.id.editTextTextPersonName).text.toString()
        val phone = findViewById<EditText>(R.id.editTextPhone).text.toString()

        if(phone.length != 9 || phone[0] != '9') {
            Toast.makeText(baseContext, "Número de telemóvel inválido", Toast.LENGTH_SHORT).show()
            return
        }

        if(email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(baseContext, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
            return
        }

        if(!password.equals(repeatPassword) ) {
            Toast.makeText(baseContext, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    val db = FirebaseFirestore.getInstance()
                    val hashMap = hashMapOf<String, Any>(
                        "Name" to name,
                        "Phone Number" to phone,
                        "Email" to email,
                    )

                    db.collection("users").document(uid.toString()).set(hashMap).addOnCompleteListener{ task ->
                        if (task.isSuccessful) {

//                            val formatter = SimpleDateFormat("dd-MM-yyyy")
//                            val date = Date()
//                            val current = formatter.format(date).toString()
//                            val energia = hashMapOf<String, Any>(
//                                current to "00",
//                            )
//
//                            db.collection("energia").document(uid.toString()).set(energia)
                            Toast.makeText(baseContext, "User created", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, Welcome::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(baseContext, "User not created", Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Process failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener(this) { exception ->
                Toast.makeText(baseContext, exception.localizedMessage,
                    Toast.LENGTH_SHORT).show()
            }
    }
    override fun onBackPressed() {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }

}