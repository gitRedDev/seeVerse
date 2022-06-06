package com.example.seeverse

import com.example.seeverse.R;
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {
    var etLoginEmail: TextInputEditText? = null
    var etLoginPassword: TextInputEditText? = null
    var btnLogin: Button? = null
    var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        etLoginEmail = findViewById(R.id.etLoginEmail)
        etLoginPassword = findViewById(R.id.etLoginPass)
        btnLogin = findViewById(R.id.btnLogin)
        mAuth = FirebaseAuth.getInstance()
        btnLogin?.setOnClickListener(View.OnClickListener { view: View? -> loginUser() })
    }

    private fun loginUser() {
        val email = etLoginEmail!!.text.toString()
        val password = etLoginPassword!!.text.toString()
        if (TextUtils.isEmpty(email)) {
            etLoginEmail!!.error = "Email cannot be empty"
            etLoginEmail!!.requestFocus()
        } else if (TextUtils.isEmpty(password)) {
            etLoginPassword!!.error = "Password cannot be empty"
            etLoginPassword!!.requestFocus()
        } else {
            mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this@LoginActivity,
                        "User logged in successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Log in Error: " + task.exception!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}