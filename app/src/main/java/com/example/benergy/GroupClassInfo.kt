package com.example.benergy

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class GroupClassInfo : ActivityWithMenuButtons() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_class_info)

        val aula = intent.getStringExtra("aula").toString()
        val diaAula = findViewById<TextView>(R.id.diaAula)
        val horaInicioAula = findViewById<TextView>(R.id.horaInicioAula)
        val horaFimAula = findViewById<TextView>(R.id.horaFimAula)
        val lotacaoAula = findViewById<TextView>(R.id.lotacaoAula)
        val addAula = findViewById<Button>(R.id.addAula)
        val favAula = findViewById<ImageButton>(R.id.favAula)

        findViewById<TextView>(R.id.textView13).text = aula

        val uid = Firebase.auth.currentUser?.uid.toString()
        val docRef = FirebaseFirestore.getInstance().collection("aulas de grupo").document(aula)
        val docRef2 = FirebaseFirestore.getInstance().collection("aulas do user/$uid/aulas").document(aula)
        val docRef3 = FirebaseFirestore.getInstance().collection("favoritos/$uid/aulas").document(aula)

        docRef3.get().addOnSuccessListener { document ->
            if (document.exists()) {
                favAula.setImageResource(R.drawable.ic_favourite)
                favAula.tag = "favourite"
            } else {
                favAula.setImageResource(R.drawable.ic_non_favourite)
                favAula.tag = "non_favourite"
            }

        }

        favAula.setOnClickListener(){
            if (favAula.tag == "non_favourite"){
                favAula.setImageResource(R.drawable.ic_favourite)
                favAula.tag = "favourite"
                docRef3.set(hashMapOf("aula" to aula))
            } else {
                favAula.setImageResource(R.drawable.ic_non_favourite)
                favAula.tag = "non_favourite"
                docRef3.delete()
            }
        }

        docRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                diaAula.text = document.getString("Dia")
                horaInicioAula.text = document.getString("Inicio")
                horaFimAula.text = document.getString("Fim")
                lotacaoAula.text = "${document.get("Lotação Atual")}/${document.get("Lotação Máxima")}"
            } else {
                Toast.makeText(this, "Erro ao carregar a aula", Toast.LENGTH_SHORT).show()
            }
        }

        addAula.setOnClickListener{
            docRef2.get().addOnSuccessListener { document ->
                if (document.exists()){
                    Toast.makeText(this, "Aula já adicionada", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Schedule::class.java)
                    startActivity(intent)
                } else {
                    docRef.get().addOnSuccessListener { document2 ->
                        if (document2.exists()) {
                            val lotAtual = document2.get("Lotação Atual").toString().toInt()
                            val lotMax = document2.get("Lotação Máxima").toString().toInt()
                            if (lotAtual < lotMax){
                                docRef.update("Lotação Atual", lotAtual + 1)
                                docRef2.set(hashMapOf("Aula" to aula))
                                Toast.makeText(this, "Aula adicionada", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Aula Cheia", Toast.LENGTH_SHORT).show()
                            }
                            val intent = Intent(this, Schedule::class.java)
                            startActivity(intent)

                        } else {
                            Toast.makeText(this, "Erro ao carregar a aula", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }


        getMenuButton()
    }
    override fun onBackPressed() {
        val intent = Intent(this, Schedule::class.java)
        startActivity(intent)
    }
}