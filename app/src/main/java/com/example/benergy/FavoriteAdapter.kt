package com.example.benergy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

data class Fav(val exercicio: String)

class FavoriteAdapter(private val ArrayList: ArrayList<Fav>, private val type: String) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.favorite_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var itemsViewModel = ArrayList[position]

        holder.exetext.text = itemsViewModel.exercicio
        holder.delete.setOnClickListener {
            val uid = Firebase.auth.currentUser?.uid.toString()
            val documentRef = FirebaseFirestore.getInstance().collection("favoritos/$uid/$type").document(itemsViewModel.exercicio)
            documentRef.delete()
            ArrayList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return ArrayList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        var exetext: TextView = itemView.findViewById(R.id.exe)
        var delete: ImageButton = itemView.findViewById(R.id.trash)
    }
}
