package com.project.fedeq.simone

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity

class PatientsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patients)
        setTitle("Pacientes")

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                alertSalir()
                return true
            }
            else -> {
                return super.onKeyDown(keyCode, event)
            }
        }
    }



    fun alertSalir() {
        val builder = AlertDialog.Builder(this, R.style.Theme_MaterialComponents_Light_Dialog_Alert)


        builder.setMessage("Esta a punto de salir de la aplicacion")
                .setTitle("Confirmar")


        builder.setPositiveButton("Ok") { dialog, which ->
            finish()
        }
        builder.setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialogInterface, i -> Log.d("Alerta", "Alerta") })


        val dialog = builder.create()


        dialog.show()
    }
}

fun Context.patientsIntent() : Intent {
    return Intent(this, PatientsActivity::class.java)
}

