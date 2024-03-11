package com.example.benergy


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

@Suppress("DEPRECATION")
class Profile : ActivityWithMenuButtons() {

    private lateinit var imageUri: Uri
    private lateinit var storagaRef: StorageReference
    private var imageChoosed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val uid = Firebase.auth.currentUser?.uid.toString()
        val documentRef = FirebaseFirestore.getInstance().collection("users").document(uid)
        storagaRef = FirebaseStorage.getInstance().reference.child("$uid.png")
        val noImage = FirebaseStorage.getInstance().reference.child("noImage.png")

        getMenuButton()
        findViewById<Button>(R.id.buttonProfile).isEnabled = false
        findViewById<Button>(R.id.buttonProfile).setBackgroundColor(resources.getColor(R.color.menubuttonpressed))

        val editProfileName = findViewById<EditText>(R.id.editProfileName)
        val editProfileEmail = findViewById<EditText>(R.id.editProfileEmail)
        val editProfilePhone = findViewById<EditText>(R.id.editProfilePhone)
        val editProfilePassword = findViewById<EditText>(R.id.editProfilePassword)

        val nameText = findViewById<TextView>(R.id.nameText)
        val emailText = findViewById<TextView>(R.id.emailText)
        val phoneText = findViewById<TextView>(R.id.phoneText)
        val passwordText = findViewById<TextView>(R.id.passwordText)

        val saveButton = findViewById<Button>(R.id.saveButton)

        val profilePic = findViewById<CircleImageView>(R.id.profilePic)
        val changeProfilePic = findViewById<ImageButton>(R.id.changeProfilePictureButton)

        documentRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                editProfileEmail.setText(Firebase.auth.currentUser?.email.toString())
                editProfileName.setText(document?.get("Name").toString())
                editProfilePhone.setText(document?.get("Phone Number").toString())
            }
                else {
                    Toast.makeText(this, "Error, Signing Out", Toast.LENGTH_SHORT).show()
                    Firebase.auth.signOut()
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                }
            }

        storagaRef.downloadUrl.addOnSuccessListener {
            if(it != null) {
                Picasso.get().load(it).into(profilePic)
            }

        }.addOnFailureListener() {
            noImage.downloadUrl.addOnSuccessListener {
                Picasso.get().load(it).into(profilePic)
            }
        }


        editProfileName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                nameText.setTextColor(resources.getColor(R.color.verde_titulo))
            } else {
                nameText.setTextColor(resources.getColor(R.color.cinza_escuro))
            }
        }

        editProfileEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                emailText.setTextColor(resources.getColor(R.color.verde_titulo))
            } else {
                emailText.setTextColor(resources.getColor(R.color.cinza_escuro))
            }
        }

        editProfilePhone.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                phoneText.setTextColor(resources.getColor(R.color.verde_titulo))
            } else {
                phoneText.setTextColor(resources.getColor(R.color.cinza_escuro))
            }
        }

        editProfilePassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                passwordText.setTextColor(resources.getColor(R.color.verde_titulo))
            } else {
                passwordText.setTextColor(resources.getColor(R.color.cinza_escuro))
            }
        }


        saveButton.setOnClickListener {
            saveChanges()
            uploadImage()
        }

        changeProfilePic.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1000)
        }


    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1000){
            if(resultCode == RESULT_OK){
                imageUri = data?.data!!
                val profilePic = findViewById<CircleImageView>(R.id.profilePic)
                profilePic.setImageURI(imageUri)
                imageChoosed = true
            }
        }
    }

    private fun saveChanges(){
        val uid = Firebase.auth.currentUser?.uid.toString()
        val editProfileName = findViewById<EditText>(R.id.editProfileName)
        val editProfileEmail = findViewById<EditText>(R.id.editProfileEmail)
        val editProfilePhone = findViewById<EditText>(R.id.editProfilePhone)
        val editProfilePassword = findViewById<EditText>(R.id.editProfilePassword)

        if(editProfileEmail.text.isEmpty() || editProfileName.text.isEmpty() || editProfilePhone.text.isEmpty()){
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if(editProfilePhone.text.length != 9 || editProfilePhone.text[0] != '9') {
            Toast.makeText(baseContext, "Número de telemóvel inválido", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseFirestore.getInstance().collection("users").document(uid).update(
            "Name", editProfileName.text.toString(),
            "Phone Number", editProfilePhone.text.toString(),
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if(editProfilePassword.text.isNotEmpty()){
                    Firebase.auth.currentUser?.updatePassword(editProfilePassword.text.toString())
                }
                Firebase.auth.currentUser?.updateEmail(editProfileEmail.text.toString())

                Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error Updating Information", Toast.LENGTH_SHORT).show()
            }
        }

        val intent = Intent(this, Homepage::class.java)
        startActivity(intent)

    }

    private fun uploadImage() {
        if(imageChoosed) {
            storagaRef.putFile(imageUri).addOnSuccessListener {
                Toast.makeText(this, "Image Uploaded", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, Homepage::class.java)
        startActivity(intent)
    }
}

