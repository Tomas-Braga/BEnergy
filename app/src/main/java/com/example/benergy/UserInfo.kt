package com.example.benergy

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

val uid = Firebase.auth.currentUser?.uid.toString()

class UserInfo : ActivityWithMenuButtons() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        val graphDia = findViewById<LineChart>(R.id.graphDia)
        val graphSemana = findViewById<LineChart>(R.id.graphSemana)
        val graphMes = findViewById<LineChart>(R.id.graphMes)

        val diaButton = findViewById<Button>(R.id.buttonDia)
        val semanaButton = findViewById<Button>(R.id.buttonSemana)
        val mesButton = findViewById<Button>(R.id.buttonMes)

        val diaText = findViewById<TextView>(R.id.wattDia)
        val semanaText = findViewById<TextView>(R.id.wattSemana)
        val mesText = findViewById<TextView>(R.id.wattMes)

        val formatter = SimpleDateFormat("dd-MM-yyyy")
        val date = Date()
        val current = formatter.format(date).toString()

        hashDia()
        hashSemana()
        hashMes()

        diaButton.setOnClickListener{
            graphDia.visibility = View.VISIBLE
            graphSemana.visibility = View.INVISIBLE
            graphMes.visibility = View.INVISIBLE
            diaText.visibility = View.VISIBLE
            semanaText.visibility = View.INVISIBLE
            mesText.visibility = View.INVISIBLE
        }
        semanaButton.setOnClickListener{
            graphDia.visibility = View.INVISIBLE
            graphSemana.visibility = View.VISIBLE
            graphMes.visibility = View.INVISIBLE
            diaText.visibility = View.INVISIBLE
            semanaText.visibility = View.VISIBLE
            mesText.visibility = View.INVISIBLE

        }
        mesButton.setOnClickListener{
            graphDia.visibility = View.INVISIBLE
            graphSemana.visibility = View.INVISIBLE
            graphMes.visibility = View.VISIBLE
            diaText.visibility = View.INVISIBLE
            semanaText.visibility = View.INVISIBLE
            mesText.visibility = View.VISIBLE
        }



        getMenuButton()
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    fun hashDia(){

        val lineChart = findViewById<LineChart>(R.id.graphDia)
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        val date = Date()
        val current = formatter.format(date).toString()
        val diaText = findViewById<TextView>(R.id.wattDia)

        val entries = mutableListOf<Entry>()
        entries.add(Entry(0f, 0f))


        val ref = FirebaseFirestore.getInstance().collection("energia/$uid/energia diaria").document(current)

        ref.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val energia = documentSnapshot.get("Energia do Dia").toString().toFloat()
                diaText.text = "Hoje Produziu: $energia W"
                val data = documentSnapshot.data
                if (data != null) {
                    for ((key, value) in data) {
                        if (key.toString() != "Energia do Dia") {
                            val x = convertTimeStringToFormattedString(key).toFloat()
                            val entry = Entry(x, value.toString().toFloat())
                            entries.add(entry)
                        }
                    }
                }
                entries.sortWith(Comparator { entry1, entry2 ->
                    // sort by x-axis value in ascending order
                    entry1.x.compareTo(entry2.x)
                })
            }
            val dataSet = LineDataSet(entries, "Sample Data")
            dataSet.color = resources.getColor(R.color.verde_titulo)
            dataSet.setCircleColor(resources.getColor(R.color.verde_titulo))
            dataSet.lineWidth = 5f
            dataSet.circleRadius = 5f
            dataSet.color = resources.getColor(R.color.verde_titulo)
            dataSet.setDrawFilled(true)
            dataSet.setDrawCircleHole(false)
            val lineData = LineData(dataSet)
            lineData.setValueTextColor(Color.BLACK)
            lineData.setValueTextSize(14f)
            lineChart.data = lineData
            lineChart.description.isEnabled = false
            lineChart.legend.isEnabled = false
            lineChart.invalidate()
            val xAxis = lineChart.xAxis
            xAxis.axisMinimum = 0f
            xAxis.axisMaximum = 24f
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            lineChart.axisLeft.isEnabled = false
            lineChart.axisRight.isEnabled = false
            val yAxis = lineChart.axisLeft
            val yAxis2 = lineChart.axisRight
            yAxis.axisMinimum = 0f
            yAxis.setDrawGridLines(false)
            yAxis.setDrawLabels(false)
            yAxis2.setDrawGridLines(false)
            yAxis2.setDrawLabels(false)

        }.addOnFailureListener { exception ->
            Log.d(TAG, "Error getting documents: ", exception)
        }


    }
    fun hashSemana(){
        val lineChart = findViewById<LineChart>(R.id.graphSemana)
        val entries = mutableListOf<Entry>()

        entries.add(Entry(0f, 0f))

        var energiaSemanal = 0f

        val sundayDate = getWeeklyDayDate(Calendar.SUNDAY)
        val mondayDate = getWeeklyDayDate(Calendar.MONDAY)
        val tuesdayDate = getWeeklyDayDate(Calendar.TUESDAY)
        val wednesdayDate = getWeeklyDayDate(Calendar.WEDNESDAY)
        val thursdayDate = getWeeklyDayDate(Calendar.THURSDAY)
        val fridayDate = getWeeklyDayDate(Calendar.FRIDAY)
        val saturdayDate = getWeeklyDayDate(Calendar.SATURDAY)

        val ref = FirebaseFirestore.getInstance().collection("energia/$uid/energia diaria")

        val semanaText = findViewById<TextView>(R.id.wattSemana)

        ref.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val data = document.id
                if (data == sundayDate){
                    val energia = document.get("Energia do Dia").toString()
                    energiaSemanal += energia.toFloat()
                    val entry = Entry(7f, energia.toFloat())
                    entries.add(entry)
                }
                if (data == mondayDate){
                    val energia = document.get("Energia do Dia").toString()
                    energiaSemanal += energia.toFloat()
                    val entry = Entry(1f, energia.toFloat())
                    entries.add(entry)
                }
                if (data == tuesdayDate){
                    val energia = document.get("Energia do Dia").toString()
                    energiaSemanal += energia.toFloat()
                    val entry = Entry(2f, energia.toFloat())
                    entries.add(entry)
                }
                if (data == wednesdayDate){
                    val energia = document.get("Energia do Dia").toString()
                    energiaSemanal += energia.toFloat()
                    val entry = Entry(3f, energia.toFloat())
                    entries.add(entry)
                }
                if (data == thursdayDate){
                    val energia = document.get("Energia do Dia").toString()
                    energiaSemanal += energia.toFloat()
                    val entry = Entry(4f, energia.toFloat())
                    entries.add(entry)
                }
                if (data == fridayDate){
                    val energia = document.get("Energia do Dia").toString()
                    energiaSemanal += energia.toFloat()
                    val entry = Entry(5f, energia.toFloat())
                    entries.add(entry)
                }
                if (data == saturdayDate){
                    val energia = document.get("Energia do Dia").toString()
                    energiaSemanal += energia.toFloat()
                    val entry = Entry(6f, energia.toFloat())
                    entries.add(entry)
                }
            }
            entries.sortWith(Comparator { entry1, entry2 ->
                // sort by x-axis value in ascending order
                entry1.x.compareTo(entry2.x)
            })

            semanaText.text = "Energia Semana: $energiaSemanal W"

            val dataSet = LineDataSet(entries, "Sample Data")
            dataSet.color = resources.getColor(R.color.verde_titulo)
            dataSet.setCircleColor(resources.getColor(R.color.verde_titulo))
            dataSet.lineWidth = 5f
            dataSet.circleRadius = 5f
            dataSet.color = resources.getColor(R.color.verde_titulo)
            dataSet.setDrawFilled(true)
            dataSet.setDrawCircleHole(false)
            val lineData = LineData(dataSet)
            lineData.setValueTextColor(Color.BLACK)
            lineData.setValueTextSize(14f)
            lineChart.data = lineData
            lineChart.description.isEnabled = false
            lineChart.legend.isEnabled = false
            lineChart.invalidate()
            val xAxis = lineChart.xAxis
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return when (value.toInt()) {
                        7 -> "Sun"
                        1 -> "Mon"
                        2 -> "Tue"
                        3 -> "Wed"
                        4 -> "Thu"
                        5 -> "Fri"
                        6 -> "Sat"
                        else -> "" // return empty string for other values
                    }
                }

                override fun getPointLabel(entry: Entry?): String {
                    return if (entry?.x == 0f) "" else entry?.y.toString()
                }
            }
            xAxis.axisMinimum = 0f
            xAxis.axisMaximum = 7f
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            lineChart.axisLeft.isEnabled = false
            lineChart.axisRight.isEnabled = false
            val yAxis = lineChart.axisLeft
            val yAxis2 = lineChart.axisRight
            yAxis.axisMinimum = 0f
            yAxis.setDrawGridLines(false)
            yAxis.setDrawLabels(false)
            yAxis2.setDrawGridLines(false)
            yAxis2.setDrawLabels(false)
        }.addOnFailureListener() { exception ->
            Log.d(TAG, "Error getting documents: ", exception)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    fun hashMes(){
        val lineChart = findViewById<LineChart>(R.id.graphMes)
        val entries = mutableListOf<Entry>()
        entries.add(Entry(0f, 0f))

        var energiaMensal = 0f

        val formatter = SimpleDateFormat("dd-MM-yyyy")
        val date = Date()
        val mes = date.month
        val ano = date.year
        val current = formatter.format(date).toString()
        val mesText = findViewById<TextView>(R.id.wattMes)

        val ref = FirebaseFirestore.getInstance().collection("energia/$uid/energia diaria")

        ref.get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val id = document.id
                    val idFormatted = formatter.parse(id)
                    if (idFormatted != null) {
                        if (idFormatted.month == mes && idFormatted.year == ano) {
                            val data = document.data
                            for ((key, value) in data) {
                                if (key.toString() == "Energia do Dia") {
                                    val x = idFormatted.date.toFloat()
                                    energiaMensal += value.toString().toFloat()
                                    val entry = Entry(x, value.toString().toFloat())
                                    entries.add(entry)
                                }
                            }
                        }
                    }
                }
            entries.sortWith(Comparator { entry1, entry2 ->
                entry1.x.compareTo(entry2.x)
            })

            val currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM")).capitalize()
            mesText.text = "Energia $currentMonth: $energiaMensal W"

            val dataSet = LineDataSet(entries, "Sample Data")
            dataSet.color = resources.getColor(R.color.verde_titulo)
            dataSet.setCircleColor(resources.getColor(R.color.verde_titulo))
            dataSet.lineWidth = 5f
            dataSet.circleRadius = 5f
            dataSet.color = resources.getColor(R.color.verde_titulo)
            dataSet.setDrawFilled(true)
            dataSet.setDrawCircleHole(false)
            val lineData = LineData(dataSet)
            lineData.setValueTextColor(Color.BLACK)
            lineData.setValueTextSize(14f)
            lineChart.data = lineData
            lineChart.description.isEnabled = false
            lineChart.legend.isEnabled = false
            lineChart.invalidate()
            val xAxis = lineChart.xAxis
            xAxis.axisMinimum = 0f
            xAxis.axisMaximum = 31f
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            lineChart.axisLeft.isEnabled = false
            lineChart.axisRight.isEnabled = false
            val yAxis = lineChart.axisLeft
            val yAxis2 = lineChart.axisRight
            yAxis.axisMinimum = 0f
            yAxis.setDrawGridLines(false)
            yAxis.setDrawLabels(false)
            yAxis2.setDrawGridLines(false)
            yAxis2.setDrawLabels(false)
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }


    }
    override fun onBackPressed() {
        val intent = Intent(this, Homepage::class.java)
        startActivity(intent)
    }

    private fun createDataSet(entries: List<Entry>, label: String, color: Int): LineDataSet {
        val dataSet = LineDataSet(entries, label)
        dataSet.color = color
        dataSet.setCircleColor(color)
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 4f
        return dataSet
    }

    private fun convertTimeStringToFormattedString(timeString: String): String {
        val parts = timeString.split(":")
        val hours = parts[0].toInt()
        val minutes = parts[1].toInt()
        val seconds = parts[2].toInt()
        return "$hours.$minutes$seconds"
    }

    private fun getWeeklyDayDate(day: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, day)
        val date = calendar.time
        val formater = SimpleDateFormat("dd-MM-yyyy")
        val dateString = formater.format(date)
        return dateString
    }
}