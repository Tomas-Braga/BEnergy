package com.example.benergy

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

data class Plan2(val plano: String, val numero: String)

class BenergyPlanAdapter(private val ArrayList: ArrayList<Plan2>) : RecyclerView.Adapter<BenergyPlanAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.benergy_plan_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var itemsViewModel = ArrayList[position]
        val planName = itemsViewModel.plano
        val planNumber = itemsViewModel.numero

        holder.numero.text = "Exercícios: $planNumber"

        holder.plano.text = planName
        holder.planPic.setImageResource(R.drawable.logo)
        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView.context, AddEditPlan::class.java)
            intent.putExtra("plano", planName)
            intent.putExtra("gym", true)
            startActivity(holder.itemView.context, intent, null)
        }

    }

    override fun getItemCount(): Int {
        return ArrayList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        var plano: TextView = itemView.findViewById(R.id.planName)
        var numero: TextView = itemView.findViewById(R.id.planNumber)
        var planPic: CircleImageView = itemView.findViewById(R.id.planPic)
    }
}