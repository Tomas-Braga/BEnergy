package com.example.benergy

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class ReceiveEnergy : ActivityWithMenuButtons() {
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receive_energy)

        val uid = Firebase.auth.currentUser?.uid.toString()
        val maquina = intent.getStringExtra("maquina").toString()
        val ref = FirebaseFirestore.getInstance().collection("maquinas").document(maquina)
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        val formatter2 = SimpleDateFormat("HH:mm:ss")
        val date = Date()
        val current = formatter.format(date).toString()
        val current2 = formatter2.format(date).toString()
        val userRef = FirebaseFirestore.getInstance().collection("energia/$uid/energia diaria").document(current)
        val userRef2 = FirebaseFirestore.getInstance().collection("energia").document(uid)
        var novaEnergia = 0f
        var energiaTotal = 0f
        var creditos = 0f
        var energiaMaquina = 0f

        var image = findViewById<ImageView>(R.id.imageView7)

        val storagaRef = FirebaseStorage.getInstance().reference.child("Machines/$maquina.png")
        val noImage = FirebaseStorage.getInstance().reference.child("Machines/NoImage.png")

        ref.get().addOnSuccessListener { d ->
            if (d.exists()) {
                energiaMaquina = d.get("Energia").toString().toFloat()
                findViewById<TextView>(R.id.textNumber).text = "$energiaMaquina W"
                findViewById<TextView>(R.id.nomeMaquina).text = maquina

                userRef2.get().addOnSuccessListener { document ->

                    if (document.exists()) {

                        val energiaTotal2 = document.get("Energia Total").toString()
                        val creditos2 = document.get("Creditos").toString()
                        if(energiaTotal2 != "null"){
                            energiaTotal = energiaTotal2.toFloat()
                        }
                        if(creditos2 != "null"){
                            creditos = creditos2.toFloat()
                        }

                    }

                    userRef.get().addOnSuccessListener { document2 ->
                        if (document2.exists()) {
                            val userEnergia = document2.get("Energia do Dia").toString()
                            if(userEnergia != "null") {
                                novaEnergia = userEnergia.toFloat()
                            }
                        } else {
                            userRef.set(mapOf("Energia do Dia" to 0))
                        }

                        storagaRef.downloadUrl.addOnSuccessListener {
                            if(it != null) {
                                Picasso.get().load(it).into(image)
                            }
                        }.addOnFailureListener() {
                            noImage.downloadUrl.addOnSuccessListener {
                                Picasso.get().load(it).into(image)
                            }
                        }

                        novaEnergia += energiaMaquina
                        if(energiaMaquina > 0) {
                            val formmattedNovaEnergia = (novaEnergia * 100.0).roundToInt() / 100.0
                            val formattedEnergiaMaquina = (energiaMaquina * 100.0).roundToInt() / 100.0

                            userRef.update("Energia do Dia", formmattedNovaEnergia)
                            userRef.set(hashMapOf(current2 to formattedEnergiaMaquina), SetOptions.merge())
                            energiaTotal += energiaMaquina
                            creditos += energiaMaquina
                            val formattedenergiaTotal = (energiaTotal * 100.0).roundToInt() / 100.0
                            val formattedcreditos = (creditos * 100.0).roundToInt() / 100.0
                            userRef2.set(
                                mapOf(
                                    "Energia Total" to formattedenergiaTotal,
                                    "Creditos" to formattedcreditos
                                )
                            )
                        }
                        ref.update("Energia", 0)
                    }
                }
            }
        }

        getMenuButton()

    }

    override fun onBackPressed() {
        val intent = Intent(this, Homepage::class.java)
        startActivity(intent)
    }
}