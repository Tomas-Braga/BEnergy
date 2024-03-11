package com.example.benergy

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

@Suppress("NAME_SHADOWING", "LABEL_NAME_CLASH")
class AddEditPlan : ActivityWithMenuButtons() {

    private var exerciciosPlano : ArrayList<Exercicio> = ArrayList()
    private lateinit var adapter : ExerciseAdapter
    private var availableExercises : ArrayList<String> = ArrayList()
    private var gym : Boolean = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_plan)


        val uid = Firebase.auth.currentUser?.uid.toString()
        val nome = intent.getStringExtra("plano").toString()
        gym = intent.getBooleanExtra("gym", false)
        val addEditButton = findViewById<Button>(R.id.addAltPlan)
        val addExerciseButton = findViewById<Button>(R.id.addExercise)
        val nomePlano = findViewById<TextView>(R.id.nomePlano)
        val nomeCriador = findViewById<TextView>(R.id.nomeCriador)
        val editPlanName = findViewById<EditText>(R.id.editPlanName)
        val userRef = FirebaseFirestore.getInstance().collection("planos do user/$uid/planos")
        val planRef = FirebaseFirestore.getInstance().collection("planos de treino").document(nome)

        var creator = ""
        var creatorID = uid


        var exist = false
        var isUserPlan = false

        val exercicios = findViewById<RecyclerView>(R.id.planExercises)
        exercicios.layoutManager = LinearLayoutManager(this)


        if(nome != "null" && !gym) {
            nomePlano.text =nome
            editPlanName.setText(nome)
            exist = true

            userRef.document(nome).get().addOnSuccessListener {
                if (it != null) {
                    creator = it.get("Creator").toString()
                    creatorID = it.get("Creator ID").toString()
                    if(creatorID == uid){
                        isUserPlan = true
                        nomePlano.visibility = View.INVISIBLE
                        editPlanName.visibility = View.VISIBLE
                        addExerciseButton.visibility = View.VISIBLE
                        addEditButton.text = "ALTERAR PLANO"
                    } else {
                        addEditButton.text = "ADICIONAR PLANO"
                    }
                    nomeCriador.text = "Criado por: $creator"
                    val nExercicios = it.get("Number").toString().toInt()
                    if(nExercicios > 0){
                        for (i in 1..nExercicios) {
                            if(it.get("Exercicio $i").toString() != "null") {
                                val exercicio = it.get("Exercicio $i").toString()
                                val medida = it.get("Medida $i").toString()
                                val tipoMedida = it.get("Tipo Medida $i").toString()
                                val exercicioObj = Exercicio(exerciciosPlano.size+1, exercicio, medida, tipoMedida)
                                exerciciosPlano.add(exercicioObj)
                            }
                        }

                        adapter = ExerciseAdapter(exerciciosPlano,isUserPlan)
                        exercicios.adapter = adapter
                    }
                }
            }.addOnFailureListener{
                Toast.makeText(this, "Erro ao carregar plano", Toast.LENGTH_SHORT).show()
            }
        } else if(nome == "null" && !gym){
            nomePlano.visibility = View.INVISIBLE
            exist = false
            isUserPlan = true
            addEditButton.text = "CRIAR PLANO"
            editPlanName.visibility = View.VISIBLE
            addExerciseButton.visibility = View.VISIBLE
            FirebaseFirestore.getInstance().collection("users").document(uid).get().addOnSuccessListener {
                if (it != null) {
                    creator = it.get("Name").toString()
                    nomeCriador.text = "Criado por: $creator"
                }
            }
            adapter = ExerciseAdapter(exerciciosPlano,isUserPlan)
            exercicios.adapter = adapter
        } else if(gym){
            nomePlano.text =nome
            editPlanName.visibility = View.GONE
            addExerciseButton.visibility = View.GONE
            addEditButton.text = "ADICIONAR PLANO"
            exist = true

            planRef.get().addOnSuccessListener {
                if (it != null) {
                    creator = it.get("Creator").toString()
                    creatorID = it.get("Creator ID").toString()
                    nomeCriador.text = "Criado por: $creator"
                    val nExercicios = it.get("Number").toString().toInt()
                    if(nExercicios > 0){
                        for (i in 1..nExercicios) {
                            if(it.get("Exercicio $i").toString() != "null") {
                                val exercicio = it.get("Exercicio $i").toString()
                                val medida = it.get("Medida $i").toString()
                                val tipoMedida = it.get("Tipo Medida $i").toString()
                                val exercicioObj = Exercicio(exerciciosPlano.size+1, exercicio, medida, tipoMedida)
                                exerciciosPlano.add(exercicioObj)
                            }
                        }

                        adapter = ExerciseAdapter(exerciciosPlano,isUserPlan)
                        exercicios.adapter = adapter
                    }
                }
            }.addOnFailureListener{
                Toast.makeText(this, "Erro ao carregar plano", Toast.LENGTH_SHORT).show()
            }
        }


        addExerciseButton.setOnClickListener {
            availableExercises.clear()
            val numero = exerciciosPlano.size + 1
            var tipoMedida: String
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.add_exercise)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val exerciseName = dialog.findViewById<Spinner>(R.id.spinner)
            val exerciseMeasure = dialog.findViewById<EditText>(R.id.exerciseMeasure)
            val backButton = dialog.findViewById<ImageButton>(R.id.backButton)
            val addButton = dialog.findViewById<Button>(R.id.add)


            FirebaseFirestore.getInstance().collection("exercicios").get().addOnSuccessListener {
                for (document in it) {
                    val name = document.id
                    availableExercises.add(name)
                }
//                val exerciseAdapter = ArrayAdapter(
//                    this,
//                    android.R.layout.simple_spinner_item,
//                    availableExercises
//                )
//                exerciseName.adapter = exerciseAdapter
                val spinnerAdapter = ExerciseSpinnerAdapter(this, availableExercises)
                exerciseName.adapter = spinnerAdapter

                exerciseName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val exercise = parent?.getItemAtPosition(position).toString()
                        FirebaseFirestore.getInstance().collection("exercicios").document(exercise)
                            .get().addOnSuccessListener {
                            if (it != null) {
                                tipoMedida = it.get("Medida").toString()
                                exerciseMeasure.hint = "Defina -> $tipoMedida"
                            }
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        Toast.makeText(
                            this@AddEditPlan,
                            "Selecione um exercício",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
                backButton.setOnClickListener {
                    dialog.dismiss()
                }
                addButton.setOnClickListener {
                    val exercise = exerciseName.selectedItem.toString()
                    val measure = exerciseMeasure.text.toString()
                    if(measure.isEmpty() || measure.toInt()< 1 || exercise.isEmpty()){
                        Toast.makeText(this, "Defina valores válidos", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        FirebaseFirestore.getInstance().collection("exercicios").document(exercise)
                            .get().addOnSuccessListener {
                                if (it != null) {
                                    tipoMedida = it.get("Medida").toString()
                                    val exercicioObj =
                                        Exercicio(numero, exercise, measure, tipoMedida)
                                    exerciciosPlano.add(exercicioObj)
                                    adapter?.notifyDataSetChanged()
                                    dialog.dismiss()
                                }
                            }
                    }
                }
                dialog.show()
            }
        }


        addEditButton.setOnClickListener{
            val plan = hashMapOf(
                "Creator" to creator,
                "Creator ID" to creatorID,
                "Number" to exerciciosPlano.size
            )
            for (i in 1..exerciciosPlano.size) {
                plan["Exercicio $i"] = exerciciosPlano[i - 1].getNome()
                plan["Medida $i"] = exerciciosPlano[i - 1].getMedida()
                plan["Tipo Medida $i"] = exerciciosPlano[i - 1].getTipoMedida()
            }
            if(isUserPlan) {
                val nomeNovo = editPlanName.text.toString()
                if(nomeNovo.isEmpty()){
                    Toast.makeText(this, "Insira um nome para o plano", Toast.LENGTH_SHORT).show()
                } else {
                    FirebaseFirestore.getInstance().collection("planos de treino")
                        .document(nomeNovo).get().addOnSuccessListener {
                            if(it.exists()){
                                Toast.makeText(this, "Já existe um plano do ginásio com esse nome", Toast.LENGTH_SHORT).show()
                            } else {
                                userRef.get().addOnSuccessListener { documents ->
                                    if (documents != null) {
                                        for (document in documents) {
                                            if (document.id == nomeNovo && nomeNovo != nome) {
                                                Toast.makeText(
                                                    this,
                                                    "Já existe um plano do utilizador com esse nome",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                return@addOnSuccessListener
                                            }
                                        }
                                    }
                                    if(exist && nomeNovo != nome){
                                        userRef.document(nome).delete()
                                    }
                                    userRef.document(nomeNovo).set(plan)
                                        .addOnSuccessListener {
                                            if(exist){
                                                val favRef = FirebaseFirestore.getInstance().collection("favoritos/$uid/planos").document(nome)
                                                favRef.get().addOnSuccessListener {
                                                    if(it.exists()){
                                                        favRef.delete()
                                                        FirebaseFirestore.getInstance().collection("favoritos/$uid/planos").document(nomeNovo).set(
                                                            hashMapOf("Nome" to nomeNovo))
                                                    }
                                                    Toast.makeText(this, "Plano alterado com sucesso", Toast.LENGTH_SHORT).show()
                                                }
                                            } else {
                                                Toast.makeText(this, "Plano criado com sucesso", Toast.LENGTH_SHORT).show()
                                            }
                                            val intent = Intent(this, TrainingPlans::class.java)
                                            startActivity(intent)
                                        }
                                        .addOnFailureListener {
                                            if(exist){
                                                Toast.makeText(this, "Erro ao alterar plano", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(this, "Erro ao criar plano", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                }
                            }

                        }
                }
            }
            else {
                val nomeNovo = nomePlano.text.toString()
                userRef.document(nomeNovo).get().addOnSuccessListener {
                    if(it.exists()){
                        Toast.makeText(this, "Já tem este plano pré-definido nos seus planos", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, TrainingPlans::class.java)
                        startActivity(intent)
                    } else {
                        userRef.document(nomeNovo).set(plan).addOnSuccessListener {
                                Toast.makeText(this, "Plano pré-definido adicionado com sucesso", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, TrainingPlans::class.java)
                                startActivity(intent)
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Erro ao adicionar plano", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }

        }

        getMenuButton()

    }

    override fun onBackPressed() {
        if(gym){
            val intent = Intent(this, OurTrainingPlans::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, TrainingPlans::class.java)
            startActivity(intent)
        }
    }

}
