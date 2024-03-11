package com.example.benergy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class Favorites : ActivityWithMenuButtons() {

    private var favArrayList: ArrayList<Fav> = ArrayList()
    private var lessonArrayList: ArrayList<Fav> = ArrayList()
    private var adapter = FavoriteAdapter(lessonArrayList, "aulas")
    private var adapter2 = FavoriteAdapter(favArrayList, "planos")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        findViewById<Button>(R.id.buttonFavorites).isEnabled = false
        findViewById<Button>(R.id.buttonFavorites).setBackgroundColor(resources.getColor(R.color.menubuttonpressed))

        getMenuButton()

        getData()
    }

    private fun getData() {
        val recyclerview = findViewById<RecyclerView>(R.id.favList)
        val recyclerview2 = findViewById<RecyclerView>(R.id.lessonsList)

        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview2.layoutManager = LinearLayoutManager(this)

        val uid = Firebase.auth.currentUser?.uid.toString()
        val documentRef = FirebaseFirestore.getInstance().collection("favoritos/$uid/planos")
        documentRef.get().addOnSuccessListener { documents ->
            if (documents != null) {
                for (document in documents) {
                    val exercicio = document.id
                    val fav = Fav(exercicio)
                    favArrayList.add(fav)
                }

                recyclerview.adapter = adapter2
            }
        }

        val documentRef2 = FirebaseFirestore.getInstance().collection("favoritos/$uid/aulas")
        documentRef2.get().addOnSuccessListener { documents ->
            if (documents != null) {
                for (document in documents) {
                    val exercicio = document.id
                    val fav = Fav(exercicio)
                    lessonArrayList.add(fav)
                }
                recyclerview2.adapter = adapter
            }
        }
    }

    private fun delete(){

    }

    override fun onBackPressed() {
        val intent = Intent(this, Homepage::class.java)
        startActivity(intent)
    }
}