package com.example.benergy

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import com.google.firebase.firestore.FirebaseFirestore
import java.util.jar.Manifest

private const val CAMERA_REQUEST_CODE = 101
class QRCodeScanner : ActivityWithMenuButtons() {

    private lateinit var codeScanner: CodeScanner

    private var maquinas : ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode_scanner)

        val maquinasRef = FirebaseFirestore.getInstance().collection("maquinas")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            maquinas
        )

        /*val maquinasLista = findViewById<ListView>(R.id.listMaquinas)
        maquinasLista.adapter = adapter

        maquinasRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                maquinas.add(document.id)
            }
            adapter.notifyDataSetChanged()

            maquinasLista.setOnItemClickListener { parent, _, position, _ ->
                val item = parent.getItemAtPosition(position).toString()
                val intent = Intent(this, ReceiveEnergy::class.java)
                intent.putExtra("maquina", item)
                startActivity(intent)
            }
        }*/

        setupPermissions()

        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)

        codeScanner = CodeScanner(this, findViewById(R.id.scanner_view))
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
                val intent = Intent(this, ReceiveEnergy::class.java)
                intent.putExtra("maquina", it.text)
                startActivity(intent)
            }
            codeScanner.errorCallback = ErrorCallback {
                runOnUiThread {
                    Toast.makeText(
                        this, "Camera initialization error: ${it.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

        getMenuButton()

        findViewById<ImageButton>(R.id.qrButton).visibility = View.GONE
    }

    override fun onBackPressed() {
        val intent = Intent(this, Homepage::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }
    private fun makeRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You need the camera permission to use this app.", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }
}