package com.example.benergy

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class OurTrainingPlans : ActivityWithMenuButtons() {

    private var planosGym : ArrayList<Plan2> = ArrayList()
    private var planAdapter = BenergyPlanAdapter(planosGym)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_our_training_plans)

        val documentRef = FirebaseFirestore.getInstance().collection("planos de treino")

        val planos = findViewById<RecyclerView>(R.id.benergyPlanosLista)
        planos.layoutManager = LinearLayoutManager(this)

        documentRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                planosGym.add(Plan2((document.id), document.get("Number").toString()))
            }
            planos.adapter = planAdapter

        }

        getMenuButton()
    }

    override fun onBackPressed() {
        val intent = Intent(this, TrainingPlans::class.java)
        startActivity(intent)
    }
}