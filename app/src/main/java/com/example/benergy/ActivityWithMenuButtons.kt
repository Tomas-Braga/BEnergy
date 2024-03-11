package com.example.benergy

import android.content.Intent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


open class ActivityWithMenuButtons : AppCompatActivity() {

    fun getMenuButton() {
        val menuButton = findViewById<ImageButton>(R.id.menuButton)
        val homeButton = findViewById<ImageButton>(R.id.homeButton)
        val qrButton = findViewById<ImageButton>(R.id.qrButton)

        val profileButton = findViewById<Button>(R.id.buttonProfile)
        val logoutButton = findViewById<Button>(R.id.buttonLogout)
        val paymentButton = findViewById<Button>(R.id.buttonPayment)
        val objectivesButton = findViewById<Button>(R.id.buttonObjectives)
        val favoritesButton = findViewById<Button>(R.id.buttonFavorites)
        val plansButtons = findViewById<Button>(R.id.buttonPlans)
        val translucentBackground = findViewById<ImageView>(R.id.translucentBackground)
        val menu = findViewById<ConstraintLayout>(R.id.menu)

        val menuUserName = findViewById<TextView>(R.id.menuUserName)
        val menuUserImage = findViewById<CircleImageView>(R.id.menuUserImage)

        val uid = Firebase.auth.currentUser?.uid.toString()
        val documentRef = FirebaseFirestore.getInstance().collection("users").document(uid)
        val storagaRef = FirebaseStorage.getInstance().reference.child("$uid.png")
        val noImage = FirebaseStorage.getInstance().reference.child("noImage.png")
        documentRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                menuUserName.text = document?.get("Name").toString()
            } else {
                menuUserName.text = resources.getString(R.string.error)
            }
        }

        storagaRef.downloadUrl.addOnSuccessListener {
            if(it != null) {
                Picasso.get().load(it).into(menuUserImage)
            }

        }.addOnFailureListener{
            noImage.downloadUrl.addOnSuccessListener {
                Picasso.get().load(it).into(menuUserImage)
            }
        }

        var buttonsVisability = false

        menuButton.setOnClickListener{
            if(!buttonsVisability){
                menu.animation = AnimationUtils.loadAnimation(this, R.anim.left_in)
                menu.visibility = View.VISIBLE
                translucentBackground.visibility = View.VISIBLE
                menuButton.isEnabled = false
                homeButton.isEnabled = false
                qrButton.isEnabled = false
                buttonsVisability = true
            }
//            else{
//                menu.animation = AnimationUtils.loadAnimation(this, R.anim.right_in)
//                menu.visibility = View.GONE
//                menuButton.isEnabled = true
//                homeButton.isEnabled = true
//                translucentBackground.visibility = View.GONE
//                buttonsVisability = false
//            }
        }

        translucentBackground.setOnClickListener{
            menu.animation = AnimationUtils.loadAnimation(this, R.anim.right_in)
            menu.visibility = View.GONE
            menuButton.isEnabled = true
            homeButton.isEnabled = true
            qrButton.isEnabled = true
            translucentBackground.visibility = View.GONE
            buttonsVisability = false
        }

        menu.isSoundEffectsEnabled = false
        menu.setOnClickListener{}

        profileButton.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

        paymentButton.setOnClickListener {
            val intent = Intent(this, Payment::class.java)
            startActivity(intent)
        }

        plansButtons.setOnClickListener {
            val intent = Intent(this, TrainingPlans::class.java)
            startActivity(intent)
        }

        objectivesButton.setOnClickListener {
            val intent = Intent(this, Objectives::class.java)
            startActivity(intent)
        }

        favoritesButton.setOnClickListener {
            val intent = Intent(this, Favorites::class.java)
            startActivity(intent)
        }


        logoutButton.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        homeButton.setOnClickListener {
            val intent = Intent(this, Homepage::class.java)
            startActivity(intent)
        }

        qrButton.setOnClickListener {
            val intent = Intent(this, QRCodeScanner::class.java)
            startActivity(intent)
        }
    }
}