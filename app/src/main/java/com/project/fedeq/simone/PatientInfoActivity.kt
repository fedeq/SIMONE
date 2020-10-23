package com.project.fedeq.simone

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity

class PatientInfoActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_info)

        setTitle("Datos del paciente")

        val pacienteId = intent.getStringExtra("pacienteId")
        val pacientePulsera = intent.getStringExtra("pacientePulsera")
        val pacienteNombre = intent.getStringExtra("pacienteNombre")
        val pacienteEG = intent.getStringExtra("pacienteEG")
        val pacienteNacimiento = intent.getStringExtra("pacienteNacimiento")
        val pacientePeso = intent.getStringExtra("pacientePeso")
        val pacienteInternacion = intent.getStringExtra("pacienteInternacion")

        val frag = PatientInfoFragment()

        val args = Bundle()
        args.putString("pacienteId", pacienteId)
        args.putString("pacientePulsera", pacientePulsera)
        args.putString("pacienteNombre", pacienteNombre)
        args.putString("pacienteEG", pacienteEG)
        args.putString("pacienteNacimiento", pacienteNacimiento)
        args.putString("pacientePeso", pacientePeso)
        args.putString("pacienteInternacion", pacienteInternacion)
        frag.setArguments(args)

        supportFragmentManager.beginTransaction().add(R.id.fragment_patient_info, frag).commit()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

}
