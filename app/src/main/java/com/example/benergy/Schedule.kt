package com.example.benergy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Schedule : ActivityWithMenuButtons() {

    private var lessonArrayList: ArrayList<Lesson> = ArrayList()
    private var adapter = LessonAdapter(lessonArrayList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        getMenuButton()

        getData()
    }

    private fun getData() {
        val recyclerview = findViewById<RecyclerView>(R.id.lessonsList)
        val calendarView = findViewById<CalendarView>(R.id.calendarView)

        recyclerview.layoutManager = LinearLayoutManager(this)

        Log.d("date2", Calendar.getInstance().time.toString())
        val sft = SimpleDateFormat("dd-MM-yyyy")
        val firstdate = Calendar.getInstance()
        val thefirstDate = sft.format(firstdate.time)
        Log.d("date", thefirstDate)
        val documentRef = FirebaseFirestore.getInstance().collection("aulas de grupo")
        documentRef.get().addOnSuccessListener { documents ->
            if (documents != null) {
                for (document in documents) {
                    val data = document.getString("Dia").toString()
                    if (thefirstDate.equals(data)) {
                        lessonArrayList.add(Lesson(document.id))
                    }
                }
                recyclerview.adapter = adapter
            }
            if (lessonArrayList.isEmpty()){
                lessonArrayList.add(Lesson("Não existem aulas para o dia selecionado!"))
            }
        }
        lessonArrayList.clear()

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val sft = SimpleDateFormat("dd-MM-yyyy")
            val date = Calendar.getInstance()
            date.set(year, month, dayOfMonth)
            val theDate = sft.format(date.time)

            val documentRef = FirebaseFirestore.getInstance().collection("aulas de grupo")
            documentRef.get().addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val data = document.getString("Dia").toString()
                        if (theDate.equals(data)) {
                            lessonArrayList.add(Lesson(document.id))
                        }
                    }
                    recyclerview.adapter = adapter
                }
                if (lessonArrayList.isEmpty()){
                    lessonArrayList.add(Lesson("Não existem aulas para o dia selecionado!"))
                }
            }
            lessonArrayList.clear()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, Homepage::class.java)
        startActivity(intent)
    }
}