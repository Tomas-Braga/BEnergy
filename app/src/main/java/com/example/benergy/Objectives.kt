package com.example.benergy

import android.content.Intent
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

@Suppress("DEPRECATION")
class Objectives : ActivityWithMenuButtons() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_objectives)
        val uid = Firebase.auth.currentUser?.uid.toString()

        findViewById<Button>(R.id.buttonObjectives).isEnabled = false
        findViewById<Button>(R.id.buttonObjectives).setBackgroundColor(resources.getColor(R.color.menubuttonpressed))

        getMenuButton()

        val objectivesText = findViewById<EditText>(R.id.objectiveText)
        val saveObjectivesButton = findViewById<Button>(R.id.saveObjectives)

        val documentRef = FirebaseFirestore.getInstance().collection("objetivos user").document(uid)
        documentRef.get().addOnSuccessListener{ document ->
            if(document.exists()){
                objectivesText.setText(document.get("Objetivos").toString())
            }
        }



        saveObjectivesButton.setOnClickListener {
            documentRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    documentRef.update("Objetivos", objectivesText.text.toString())
                } else {
                    documentRef.set(hashMapOf("Objetivos" to objectivesText.text.toString()))
                }
            }
            Toast.makeText(this, "Objetivos guardados", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, Homepage::class.java)
        startActivity(intent)
    }
}