package com.example.benergy



import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import me.bastanfar.semicirclearcprogressbar.SemiCircleArcProgressBar
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


@Suppress("NAME_SHADOWING")
class Homepage : ActivityWithMenuButtons() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        getUserWatt()

        val infobutton = findViewById<Button>(R.id.infoButton)
        val scheduleButton = findViewById<Button>(R.id.scheduleButton)


        infobutton.setOnClickListener {
            val intent = Intent(this, UserInfo::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.left_out,R.anim.right_in)
        }

        scheduleButton.setOnClickListener {
            val intent = Intent(this, Schedule::class.java)
            startActivity(intent)
        }

        getMenuButton()
        findViewById<ImageButton>(R.id.homeButton).visibility = View.GONE





    }

    @SuppressLint("SimpleDateFormat", "CutPasteId", "SetTextI18n")
    private fun getUserWatt(){
        val progressBar = findViewById<SemiCircleArcProgressBar>(R.id.progressBar2)
        val ponteiro = findViewById<ImageView>(R.id.ponteiro)
        val userWatt = findViewById<TextView>(R.id.userWatt)
        val uid = Firebase.auth.currentUser?.uid.toString()
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        val date = Date()
        val current = formatter.format(date).toString()
        val documentRef = FirebaseFirestore.getInstance().collection("energia/$uid/energia diaria").document(current)
        var watt = 0f


        documentRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val watts = document.get("Energia do Dia").toString()
                if (watts != "null") {
                    watt = watts.toFloat()
                }
            }

            val decimalFormat = DecimalFormat("#0.00")

            val numberAnimator = ValueAnimator.ofFloat(0f, ((watt*100.0).roundToInt()/100f))
            numberAnimator.duration = (watt*30).toLong()
            numberAnimator.addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                userWatt.text = decimalFormat.format(value) + " W" }
            numberAnimator.start()

            val t = Thread {
                if(watt.toInt() > 0) {
                    for (i in 1..watt.toInt()) {
//                        findViewById<ProgressBar>(R.id.progressBar).progress += 1
                        if(i <100) {
                            progressBar.setPercent(i)
                            ponteiro.rotation = (i * 180 / 100).toFloat()
                        }
                        try {
                            Thread.sleep(30)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            t.start()

        }
    }

    override fun onBackPressed() {
        finishAffinity();
        finish();
    }

}