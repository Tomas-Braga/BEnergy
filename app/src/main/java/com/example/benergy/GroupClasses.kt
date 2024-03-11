package com.example.benergy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.firestore.FirebaseFirestore

class GroupClasses : ActivityWithMenuButtons() {
    private var groupClasses : ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_classes)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            groupClasses
        )

        val documentRef = FirebaseFirestore.getInstance().collection("aulas de grupo")

        val group = findViewById<ListView>(R.id.listAulas)
        group.adapter = adapter

        documentRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                groupClasses.add(document.id)
            }
            adapter.notifyDataSetChanged()

            group.setOnItemClickListener { parent, _, position, _ ->
                val item = parent.getItemAtPosition(position).toString()
                val intent = Intent(this, GroupClassInfo::class.java)
                intent.putExtra("aula", item)
                startActivity(intent)
            }
        }

        getMenuButton()
    }

    override fun onBackPressed() {
        val intent = Intent(this, Schedule::class.java)
        startActivity(intent)
    }
}