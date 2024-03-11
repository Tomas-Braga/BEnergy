package com.example.benergy

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlin.math.floor

class Payment : ActivityWithMenuButtons() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        val uid = Firebase.auth.currentUser?.uid.toString()

        val visaAndMastercard = findViewById<ImageButton>(R.id.visaAndMastercard)
        val multibanco = findViewById<ImageButton>(R.id.multibanco)
        val paypal = findViewById<ImageButton>(R.id.paypal)
        val mbway = findViewById<ImageButton>(R.id.mbway)

        val creditos = findViewById<TextView>(R.id.creditos)
        var cred = 0.0
        var valor = 0.0

        val userRef = FirebaseFirestore.getInstance().collection("energia").document(uid)

        userRef.get().addOnSuccessListener {
            if(it.exists()) {
                cred = it.get("Creditos").toString().toDouble()
                valor = floor(cred.toDouble() / 1000.0 * 100) / 100
                creditos.text = "Créditos: $cred ($valor €)"
            }
        }

        visaAndMastercard.setOnClickListener {
            val dialog = Dialog(this)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.visa_mastercard_input)
            val nomeVisaMastercard = dialog.findViewById<EditText>(R.id.nomeVisaMastercard)
            val cvc = dialog.findViewById<EditText>(R.id.cvc)
            val numeroCartao = dialog.findViewById<EditText>(R.id.nCartaoVisaMastercard)
            val dataValidade = dialog.findViewById<EditText>(R.id.dateCard)

            val confirmButton = dialog.findViewById<Button>(R.id.visaAndMastercardCheckout)

            confirmButton.setOnClickListener{
                userRef.update("Creditos", 0)
                creditos.text = "Créditos: 0 (0.0 €)"
                Toast.makeText(this, "Pagamento efetuado com sucesso!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            dialog.show()
        }

        multibanco.setOnClickListener {
            val dialog = Dialog(this)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.multibanco_input)
            val confirmButton = dialog.findViewById<Button>(R.id.multibancoCheckout)
            var montante = dialog.findViewById<TextView>(R.id.textView20)
            val montanteAposDesconto = 35.0 - valor
            montante.text = "Montante: $montanteAposDesconto €"

            confirmButton.setOnClickListener{
                dialog.dismiss()
            }
            dialog.show()
        }
        paypal.setOnClickListener {
            val dialog = Dialog(this)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.paypal_input)
            val confirmButton = dialog.findViewById<ImageButton>(R.id.paypalCheckout)

            confirmButton.setOnClickListener{
                userRef.update("Creditos", 0)
                creditos.text = "Créditos: 0 (0.0 €)"
                Toast.makeText(this, "Pagamento efetuado com sucesso!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            dialog.show()
        }
        mbway.setOnClickListener {
            val dialog = Dialog(this)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.mbway_input)
            val confirmButton = dialog.findViewById<Button>(R.id.mbwayCheckout)
            val phoneNumber = dialog.findViewById<EditText>(R.id.mbwayPhone)
            val uid = Firebase.auth.currentUser?.uid.toString()
            val documentRef = FirebaseFirestore.getInstance().collection("users").document(uid)

            documentRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val phone = document.getString("Phone Number")
                    phoneNumber.setText(phone)
                }
                confirmButton.setOnClickListener{
                    userRef.update("Creditos", 0)
                    creditos.text = "Créditos: 0 (0.0 €)"
                    Toast.makeText(this, "Pagamento efetuado com sucesso!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                dialog.show()
            }


        }
        getMenuButton()
        findViewById<Button>(R.id.buttonPayment).isEnabled = false
        findViewById<Button>(R.id.buttonPayment).setBackgroundColor(resources.getColor(R.color.menubuttonpressed))


    }

    override fun onBackPressed() {
        val intent = Intent(this, Homepage::class.java)
        startActivity(intent)
    }
}