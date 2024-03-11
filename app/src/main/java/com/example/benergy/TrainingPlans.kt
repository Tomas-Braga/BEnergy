package com.example.benergy

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class TrainingPlans : ActivityWithMenuButtons() {

    private var planosUser : ArrayList<Plan> = ArrayList()
    private var planAdapter = PlanAdapter(planosUser)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_plans)

        findViewById<Button>(R.id.buttonPlans).isEnabled = false
        findViewById<Button>(R.id.buttonPlans).setBackgroundColor(resources.getColor(R.color.menubuttonpressed))


        val uid = Firebase.auth.currentUser?.uid.toString()
        val documentRef = FirebaseFirestore.getInstance().collection("planos do user/$uid/planos")

        val button2 = findViewById<Button>(R.id.button2)
        val button3 = findViewById<Button>(R.id.button3)

        val planos = findViewById<RecyclerView>(R.id.planosLista)
        planos.layoutManager = LinearLayoutManager(this)

        documentRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                planosUser.add(Plan(document.id))
            }
            planos.adapter = planAdapter

        }

        getMenuButton()

        button2.setOnClickListener{
            val intent = Intent(this, AddEditPlan::class.java)
            startActivity(intent)
        }

        button3.setOnClickListener{
            val intent = Intent(this, OurTrainingPlans::class.java)
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
        val intent = Intent(this, Homepage::class.java)
        startActivity(intent)
    }
}