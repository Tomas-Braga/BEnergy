package com.example.benergy

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.SpinnerAdapter
import android.widget.TextView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ExerciseSpinnerAdapter(val context: Context, private val itemList: List<String>) : BaseAdapter() {

    private lateinit var storagaRef: StorageReference

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.custom_exercise_spinner_item, parent, false)
        val exName = view.findViewById<TextView>(R.id.exerciseNameSpinner)
        val exImage = view.findViewById<CircleImageView>(R.id.exercise_spinner_item_icon)
        exName.text = itemList[position]

        val exNamePosition = itemList[position]

        storagaRef = FirebaseStorage.getInstance().reference.child("Exercises/$exNamePosition.png")
        val noImage = FirebaseStorage.getInstance().reference.child("Exercises/NoImage.png")

        storagaRef.downloadUrl.addOnSuccessListener {
            if(it != null) {
                Picasso.get().load(it).into(exImage)
            }
        }.addOnFailureListener() {
            noImage.downloadUrl.addOnSuccessListener {
                Picasso.get().load(it).into(exImage)
            }
        }

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.custom_exercise_spinner_item, parent, false)
        val exName = view.findViewById<TextView>(R.id.exerciseNameSpinner)
        val exImage = view.findViewById<CircleImageView>(R.id.exercise_spinner_item_icon)
        exName.text = itemList[position]

        val exNamePosition = itemList[position]

        storagaRef = FirebaseStorage.getInstance().reference.child("Exercises/$exNamePosition.png")
        val noImage = FirebaseStorage.getInstance().reference.child("Exercises/NoImage.png")

        storagaRef.downloadUrl.addOnSuccessListener {
            if(it != null) {
                Picasso.get().load(it).into(exImage)
            }
        }.addOnFailureListener() {
            noImage.downloadUrl.addOnSuccessListener {
                Picasso.get().load(it).into(exImage)
            }
        }

        return view
    }

    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItem(position: Int): String? {
        return itemList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}