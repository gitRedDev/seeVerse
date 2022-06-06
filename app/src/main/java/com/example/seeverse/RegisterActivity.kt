package com.example.seeverse


import com.example.seeverse.R;
import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class RegisterActivity : AppCompatActivity() {
    var etName: EditText? = null
    var etAge: EditText? = null
    var etEmail: EditText? = null
    var etLoginPass: EditText? = null
    var btnCreate: Button? = null
    var pd: ProgressDialog? = null
    var db: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        etName = findViewById(R.id.etName)
        etAge = findViewById(R.id.etAge)
        etEmail = findViewById(R.id.etEmail)
        etLoginPass = findViewById(R.id.etLoginPass)
        btnCreate = findViewById(R.id.btnCreate)
        pd = ProgressDialog(this)
        db = FirebaseFirestore.getInstance()
        btnCreate?.setOnClickListener(View.OnClickListener {
            val name = etName?.getText().toString().trim { it <= ' ' }
            val age = etAge?.getText().toString().toInt()
            val email = etEmail?.getText().toString().trim { it <= ' ' }
            val password = etLoginPass?.getText().toString().trim { it <= ' ' }
            uploadData(name, age, email, password)
        })
    }

    private fun uploadData(name: String, age: Int, email: String, password: String) {
        pd!!.setTitle("adding data to firestore")
        pd!!.show()
        val id = UUID.randomUUID().toString()
        val doc: MutableMap<String, Any> = HashMap()
        doc["id"] = id
        doc["name"] = name
        doc["age"] = age
        doc["emil"] = email
        doc["password"] = password
        db!!.collection("users").document(id).set(doc)
            .addOnCompleteListener {
                pd!!.dismiss()
                Toast.makeText(this@RegisterActivity, "Uploaded ...", Toast.LENGTH_SHORT)
            }
            .addOnFailureListener { e ->
                pd!!.dismiss()
                Toast.makeText(this@RegisterActivity, e.message, Toast.LENGTH_SHORT)
            }
    }
}