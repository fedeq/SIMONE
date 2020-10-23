package com.project.fedeq.simone

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity

class LvsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lvs)
        setTitle("LVS")

        val checkId = intent.getIntExtra("checkId", -1)
        Log.d("check", checkId.toString())

        val patientId = intent.getStringExtra("patientId")

        val frag = LvsFragment()

        val args = Bundle()
        args.putInt("checkId", checkId)
        args.putString("patientId", patientId)
        frag.setArguments(args)

        supportFragmentManager.beginTransaction().add(R.id.fragment2, frag).commit()
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


        builder.setMessage("Si sale del chequeo perdera todo el progeso")
                .setTitle("¿Realmente desea salir?")


        builder.setPositiveButton("Confirmar") { dialog, which ->
            finish()
        }
        builder.setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialogInterface, i -> Log.d("Alerta", "Alerta") })


        val dialog = builder.create()


        dialog.show()
    }
}

fun Context.lvsIntent() : Intent {
    return Intent(this, LvsActivity::class.java)
}
