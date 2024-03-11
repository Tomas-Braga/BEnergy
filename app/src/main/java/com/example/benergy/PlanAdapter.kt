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

data class Plan(val plano: String)

class PlanAdapter(private val ArrayList: ArrayList<Plan>) : RecyclerView.Adapter<PlanAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.plan_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var itemsViewModel = ArrayList[position]
        val planName = itemsViewModel.plano
        val uid = Firebase.auth.currentUser?.uid.toString()
        val ref = FirebaseFirestore.getInstance().collection("favoritos/$uid/planos").document(planName)

        ref.get().addOnSuccessListener { document ->
            if (document.exists()) {
                holder.favorite.setImageResource(R.drawable.ic_favourite)
                holder.favorite.tag = "favourite"
            } else {
                holder.favorite.setImageResource(R.drawable.ic_non_favourite)
                holder.favorite.tag = "non_favourite"
            }

        }

        holder.plano.text = planName
        val benergyRef = FirebaseFirestore.getInstance().collection("planos de treino").document(planName)
        benergyRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                holder.planPic.setImageResource(R.drawable.logo)
            } else {
                holder.planPic.setImageResource(R.drawable.ic_profile2)
            }
        }
        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView.context, AddEditPlan::class.java)
            intent.putExtra("plano", planName)
            startActivity(holder.itemView.context, intent, null)
        }
        holder.delete.setOnClickListener {
            val documentRef = FirebaseFirestore.getInstance().collection("planos do user/$uid/planos").document(itemsViewModel.plano)
            val alert: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
            alert.setTitle("Remover Plano de Treino")
            alert.setMessage("Tem a certeza que deseja remover este plano de treino?")
            alert.setPositiveButton("Sim") { _, _ ->
                ref.delete()
                documentRef.delete()
                ArrayList.removeAt(position)
                notifyDataSetChanged()

            }
            alert.setNegativeButton("Não") { _, _ ->
            }
            alert.show()


        }
        holder.favorite.setOnClickListener {
            if (holder.favorite.tag == "favourite") {
                ref.delete()
                holder.favorite.setImageResource(R.drawable.ic_non_favourite)
                holder.favorite.tag = "non_favourite"
            } else {
                ref.set(hashMapOf("Name" to planName))
                holder.favorite.setImageResource(R.drawable.ic_favourite)
                holder.favorite.tag = "favourite"
            }
        }


    }

    override fun getItemCount(): Int {
        return ArrayList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        var plano: TextView = itemView.findViewById(R.id.plano)
        var delete: ImageButton = itemView.findViewById(R.id.deletePlan)
        var favorite: ImageButton = itemView.findViewById(R.id.favPlan)
        var planPic: CircleImageView = itemView.findViewById(R.id.planPic)
    }
}