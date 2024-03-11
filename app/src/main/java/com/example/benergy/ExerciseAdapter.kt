package com.example.benergy

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ExerciseAdapter(private val ArrayList: ArrayList<Exercicio>, private var isUserPlan : Boolean ) : RecyclerView.Adapter<ExerciseAdapter.ViewHolder>() {

    private lateinit var storagaRef: StorageReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.exercise_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var itemsViewModel = ArrayList[position]
        val exName = itemsViewModel.getNome()
        val exMedida = itemsViewModel.getMedida()
        val exTipoMedida = itemsViewModel.getTipoMedida()
        val exNumber = itemsViewModel.getNumber()

        storagaRef = FirebaseStorage.getInstance().reference.child("Exercises/$exName.png")
        val noImage = FirebaseStorage.getInstance().reference.child("Exercises/NoImage.png")

        holder.exercicio.text = exName
        holder.medida.text = "$exMedida $exTipoMedida"

        storagaRef.downloadUrl.addOnSuccessListener {
            if(it != null) {
                Picasso.get().load(it).into(holder.exPic)
            }
        }.addOnFailureListener() {
            noImage.downloadUrl.addOnSuccessListener {
                Picasso.get().load(it).into(holder.exPic)
            }
        }

        if(isUserPlan) {
        holder.itemView.setOnClickListener{
            val dialog = Dialog(holder.itemView.context)
            dialog.setContentView(R.layout.edit_exercise)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val exerciseName = dialog.findViewById<TextView>(R.id.textView7)
            val exerciseMeasure = dialog.findViewById<EditText>(R.id.exerciseMeasureChange)
            val backButton = dialog.findViewById<ImageButton>(R.id.backButton2)
            val change = dialog.findViewById<Button>(R.id.change)
            val number = exNumber

            exerciseMeasure.hint = "Defina -> $exTipoMedida"
            exerciseMeasure.setText(exMedida)
            exerciseName.text = exName

            backButton.setOnClickListener {
                dialog.dismiss()
            }

            change.setOnClickListener{
                val measure = exerciseMeasure.text.toString()
                if(measure.isEmpty() || measure.toInt()< 1){
                    Toast.makeText(holder.itemView.context, "Defina valores válidos", Toast.LENGTH_SHORT).show()
                }
                else {
                    itemsViewModel.setMedida(measure)
                    notifyDataSetChanged()
                    dialog.dismiss()
                }
            }
            dialog.show()
        }

        holder.delete.setOnClickListener {
            val alert: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
            alert.setTitle("Remover Plano de Treino")
            alert.setMessage("Tem a certeza que deseja remover este exercício?")
            alert.setPositiveButton("Sim") { _, _ ->
                ArrayList.removeAt(position)
                for(i in exNumber..ArrayList.size){
                    ArrayList[i - 1].setNumber(i)
                }
                notifyDataSetChanged()
            }
            alert.setNegativeButton("Não") { _, _ ->
            }
            alert.show()
        }
        } else {
            holder.delete.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return ArrayList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        var exercicio: TextView = itemView.findViewById(R.id.exName)
        var medida: TextView = itemView.findViewById(R.id.exMeasure)
        var delete: ImageButton = itemView.findViewById(R.id.deleteExercise)
        var exPic : CircleImageView = itemView.findViewById(R.id.exPic)
    }
}