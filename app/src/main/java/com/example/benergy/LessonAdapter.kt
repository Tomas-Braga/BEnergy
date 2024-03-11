package com.example.benergy

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

data class Lesson(val lesson: String)

class LessonAdapter(private val ArrayList: ArrayList<Lesson>) : RecyclerView.Adapter<LessonAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.aulas_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var itemsViewModel = ArrayList[position]

        holder.but.text = itemsViewModel.lesson
        holder.but.setOnClickListener {
            if (holder.but.text == "Não existem aulas para o dia selecionado!") return@setOnClickListener
            val intent = Intent(holder.but.context, GroupClassInfo::class.java)
            intent.putExtra("aula", itemsViewModel.lesson)
            startActivity(holder.but.context, intent, null)
        }
    }

    override fun getItemCount(): Int {
        return ArrayList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        var but: Button = itemView.findViewById(R.id.aul)
    }
}
