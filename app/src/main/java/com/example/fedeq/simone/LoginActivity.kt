package com.example.fedeq.simone

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    private var emailField: AutoCompleteTextView? = null
    private var passwordField: TextInputEditText? = null
    private var buttonLogin: Button? = null

    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setTitle("Login")

        buttonLogin = findViewById<View>(R.id.button_login) as Button
        emailField = findViewById<View>(R.id.email) as AutoCompleteTextView
        passwordField = findViewById<View>(R.id.password) as TextInputEditText

        Log.v("Login", "Hola")

        mAuth = FirebaseAuth.getInstance()

        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // User is signed in
                // TODO iniciar directamente la actividad inicializando usuario
                Log.d("DEBUG", "onAuthStateChanged:signed_in:" + user.uid)
            } else {
                // User is signed out
                Log.d("DEBUG", "onAuthStateChanged:signed_out")
            }
        }

        buttonLogin!!.setOnClickListener {
            emailField?.validate({s -> s.isNotEmpty()}, "Por favor ingrese un mail valido")
            passwordField?.validate({s -> s.isNotEmpty()}, "Por favor ingrese una contraseña")
            if (emailField?.error == null && passwordField?.error == null) {
                login_progress.visibility = View.VISIBLE
                startSignIn()
            }
        }


    }

    override fun onStart() {
        super.onStart();
        mAuth?.addAuthStateListener(mAuthListener!!);
    }

    override fun onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth?.removeAuthStateListener(mAuthListener!!);
        }
    }

    fun startSignIn () {
        val email: String = emailField?.text.toString().trim()
        val password: String = passwordField?.text.toString().trim()

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            mAuth!!.signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) {task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, PatientsActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.e("DEBUG", "onComplete: Failed=" + task.getException()?.message);
                        Toast.makeText(this@LoginActivity, "No se pudo loguear", Toast.LENGTH_SHORT).show()
                    }
                    login_progress.visibility = View.GONE
            }
        } else {
            Toast.makeText(this@LoginActivity, "campos vacios", Toast.LENGTH_SHORT).show()
            login_progress.visibility = View.GONE
        }
    }


}

fun Context.loginIntent() : Intent {
    return Intent(this, LoginActivity::class.java)
}
