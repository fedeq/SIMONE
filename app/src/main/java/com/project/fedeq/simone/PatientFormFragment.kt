package com.project.fedeq.simone

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class PatientFormFragment : Fragment() {

    private val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater?.inflate(R.layout.fragment_patient_form, container,false)

        val etFechaNacimiento: TextInputEditText = v.findViewById(R.id.editText_fecha_nacimiento)
        val fab : FloatingActionButton = v.findViewById(R.id.fab_send_patient)
        val spinner: Spinner = v.findViewById(R.id.spinner_patient_form)
        val etNombre: EditText = v.findViewById(R.id.editText_nombre)
        val etNro: EditText = v.findViewById(R.id.editText_numero_pulsera)
        val etEdadGestacional : EditText = v.findViewById(R.id.editText_edad_gestacional)
        val etPesoAlNacer : EditText = v.findViewById(R.id.editText_peso_al_nacer)

        // Seteo listener del datepicker
        etFechaNacimiento.setOnClickListener {
            showDatePickerDialog(etFechaNacimiento)
        }

        // Dropdown de Lugar de internacion
        val adapter = ArrayAdapter.createFromResource(context,R.array.unidades_internacion, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.setAdapter(adapter)

        // Listener de FAB
        fab.setOnClickListener{
            etNombre.validate({s -> s.isNotEmpty()}, "Por favor ingrese un nombre")
            etNro.validate({s -> s.isNotEmpty()}, "Por favor ingrese nro. de pulsera")
            etFechaNacimiento.validate({s -> s.isNotEmpty()}, "Por favor ingrese fecha de nacimiento")
            etEdadGestacional.validate({s -> s.isNotEmpty()}, "Por favor ingrese edad gestacional")
            etPesoAlNacer.validate({s -> s.isNotEmpty()}, "Por favor ingrese el peso del paciente")

            if (etNombre.error == null && etNro.error == null && etFechaNacimiento.error == null && etEdadGestacional.error == null && etPesoAlNacer.error == null) {
                Toast.makeText(context, "Paciente Guardado", Toast.LENGTH_SHORT).show()
                writeNewUser(etNro.text.toString().toInt(),etNombre.text.toString(), etPesoAlNacer.text.toString().toInt(), etEdadGestacional.text.toString().toInt(),etFechaNacimiento.text.toString(),spinner.selectedItem.toString())
                activity?.finish()
            }
        }

        return v
    }

    private fun writeNewUser(numero_pulsera: Int = 0, nombre: String = "", peso_al_nacer: Int = 0, edad_gestacional : Int = 0, fecha_nacimiento: String = "", lugar_internacion: String = "") {
        val key = mDatabase.child("pacientes").push().key.orEmpty()
        val paciente: Paciente = Paciente(key, numero_pulsera,nombre,peso_al_nacer,edad_gestacional,fecha_nacimiento,lugar_internacion,"ACTIVO")
        mDatabase.child("pacientes").child(key).setValue(paciente)
                .addOnSuccessListener {
                    if(context!=null) Toast.makeText(context, "Paciente Guardado", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    if(context!=null) Toast.makeText(context, "Error al guardar el paciente", Toast.LENGTH_SHORT).show()
                }
    }

    private fun showDatePickerDialog(element: TextInputEditText) {
        val newFragment = DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            // +1 because january is zero
            var mes : String
            if(month+1<10) {
                mes = "0"+((month+1).toString())
            }
            else {
                mes = (month+1).toString()
            }
            val selectedDate = day.toString() + "/" + mes + "/" + year
            element.setText(selectedDate)
        })
        newFragment.show(activity?.getSupportFragmentManager(), "datePicker")
    }
}