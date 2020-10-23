package com.project.fedeq.simone

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_patient_info.*

class PatientInfoFragment : Fragment() {

    private val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var chequeos: MutableList<Check> = mutableListOf()
    private var keysChequeos: MutableList<String> = mutableListOf()
    private var recyclerChequeos: RecyclerView? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_patient_info, container,false)

        val toolbar: Toolbar = v.findViewById(R.id.toolbar_info)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)


        // chequeo el tamaño de la pantalla para setear la cantidad de columnas y filas del grid
//        val config = resources.configuration
//        if (config.smallestScreenWidthDp >= 600) {
//            val gl = v.findViewById(R.id.gl_patient_info) as GridLayout
//            gl.rowCount = 2
//            gl.columnCount = 4
//
//            val p = GridLayout.LayoutParams()
//            p.columnSpec = GridLayout.spec(GridLayout.UNDEFINED,4)
//
//            val tv = v.findViewById(R.id.name_info) as TextView
//            tv.layoutParams = p
//            tv.setPaddingRelative(16,16, 0,0)
//        }


        // completo los campos de la informacion del paciente

        val pacienteId = arguments?.getString("pacienteId")

        // val pulsera: TextView = v.findViewById(R.id.editText_numero_pulsera)
        val pacientePulsera = arguments?.getString("pacientePulsera")
        //pulsera.text = numPulsera.toString()

        val nombre: TextView = v.findViewById(R.id.name_info)
        val nombreStr = arguments?.getString("pacienteNombre")
        nombre.text = nombreStr + " ( " + pacientePulsera + " )"

        val pacienteEG: TextView = v.findViewById(R.id.gestacion_info)
        val pacienteEGStr = arguments?.getString("pacienteEG")
        pacienteEG.text = pacienteEGStr+" semanas"

        val pacienteNacimiento: TextView = v.findViewById(R.id.nacimiento_info)
        val pacNacimientoStr = arguments?.getString("pacienteNacimiento")
        pacienteNacimiento.text = pacNacimientoStr

        val pacientePeso: TextView = v.findViewById(R.id.peso_info)
        val pacPesoStr = arguments?.getString("pacientePeso")
        pacientePeso.text = pacPesoStr + " gr."

        val pacienteInternacion: TextView = v.findViewById(R.id.lugar_info)
        val pacInternacionStr = arguments?.getString("pacienteInternacion")
        pacienteInternacion.text = pacInternacionStr

        // seteo el oyente del menu
        setMenuListener(toolbar, pacienteId , pacientePulsera, nombreStr, pacienteEGStr, pacNacimientoStr, pacPesoStr, pacInternacionStr)

        // consigo la informacion para los ultimos chequeos y se la paso al adapter
        obtenerChequeos(pacienteId.orEmpty())
        recyclerChequeos = v.findViewById(R.id.recycler_view_info)
        var adapter = PatientInfoAdapter(context, chequeos)
        val layoutManager = LinearLayoutManager(activity)
        recyclerChequeos?.layoutManager = layoutManager
        recyclerChequeos?.itemAnimator = DefaultItemAnimator()

        recyclerChequeos?.adapter = adapter
        adapter.notifyDataSetChanged()

        return v
    }

    private fun obtenerChequeos(pacienteId:String) {
        val pacientesListener = object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chequeo = p0.getValue(Check::class.java) as Check
                chequeos.add(0,chequeo)
                keysChequeos.add(0,chequeo.uid)

                activity?.runOnUiThread {
                    recyclerChequeos?.adapter = PatientInfoAdapter(context,chequeos)
                }
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chequeo = p0.getValue(Check::class.java) as Check
                val index = keysChequeos.indexOf(chequeo.uid)

                chequeos.removeAt(index)
                chequeos.add(index,chequeo)

                activity?.runOnUiThread {
                    recyclerChequeos?.adapter = PatientInfoAdapter(context,chequeos)
                }


            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                val paciente = p0.getValue(Check::class.java) as Check

                val index = keysChequeos.indexOf(paciente.uid)
                chequeos.removeAt(index)
                keysChequeos.removeAt(index)


                activity?.runOnUiThread {
                    recyclerChequeos?.adapter = PatientInfoAdapter(context, chequeos)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        mDatabase.child("chequeos").orderByChild("id_paciente").equalTo(pacienteId).limitToLast(5).addChildEventListener(pacientesListener)

        mDatabase.child("chequeos").orderByChild("id_paciente").equalTo(pacienteId).limitToLast(5).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                patient_checks_progress.setVisibility(View.GONE)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    private fun setMenuListener(
            toolbar: Toolbar,
            idPaciente: String? = "",
            numeroPulsera: String? = "",
            nombrePaciente:String? = "",
            pacienteEGStr:String? = "",
            pacNacimientoStr:String? = "",
            pacPesoStr:String? = "",
            pacInternacionStr:String? = ""

    ) {
        toolbar.setOnMenuItemClickListener(object : Toolbar.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                val id = item?.itemId
                Log.d("DEBUG", "id =" +id.toString())
                when (id) {
                    R.id.action_alta -> {
                        Log.d("DEBUG", "altaaaaaaaaa")
                        alertaAlta(idPaciente, nombrePaciente)
                    }
                    R.id.action_delete -> {
                        Log.d("DEBUG", "ELIMINARRRRR")
                        alertaDelete(idPaciente, nombrePaciente)
                    }
                    R.id.action_edit -> {
                        Log.d("DEBUG", "EDITAARRRRRR")
                        alertaEdit( idPaciente,
                                    numeroPulsera,
                                    nombrePaciente,
                                    pacienteEGStr,
                                    pacNacimientoStr,
                                    pacPesoStr,
                                    pacInternacionStr
                        )
                    }
                }
                return false
            }
        })
    }

    private fun alertaAlta(idPaciente: String? = "", nombrePaciente: String?="") {
        val builder = AlertDialog.Builder(context, R.style.Theme_MaterialComponents_Light_Dialog_Alert)
        val items = arrayOf<String>("Buena Evolucion", "Traslado","Fallecimiento")
        builder.setTitle("Confirmar alta de " + nombrePaciente)
                .setSingleChoiceItems(items,0, null)

        builder.setPositiveButton("Ok") {dialog, which ->
            dialog.dismiss()
            val selectedPosition = (dialog as AlertDialog).listView.checkedItemPosition
            Log.d("DEBUG", "selectedPosition: " + selectedPosition)

            when (selectedPosition) {
                0 -> {
                    // buena evolucion
                    mDatabase.child("pacientes").child(idPaciente.orEmpty()).child("estado").setValue("BUENA EVOLUCION");
                }

                1 -> {
                    // traslado
                    mDatabase.child("pacientes").child(idPaciente.orEmpty()).child("estado").setValue("TRASLADO");
                }

                2 -> {
                    // falleciento
                    mDatabase.child("pacientes").child(idPaciente.orEmpty()).child("estado").setValue("FALLECIMIENTO");
                }

            }

            Toast.makeText(context,"Paciente dado de alta", Toast.LENGTH_SHORT).show()
            activity?.finish()
        }
        builder.setNegativeButton("Cancelar") {dialog, which ->

        }

        val dialog = builder.create()


        dialog.show()

    }

    private fun alertaDelete(idPaciente: String? = "", nombrePaciente: String?="") {
        val builder = AlertDialog.Builder(context, R.style.Theme_MaterialComponents_Light_Dialog_Alert)
        builder.setMessage("Esta seguro que desea eliminar el paciente "+ nombrePaciente)
                .setTitle("Confirmar eliminacion")

        builder.setPositiveButton("Ok") {dialog, which ->
            eliminarChequeosPaciente(idPaciente.orEmpty())
            eliminarAlertasPaciente(idPaciente.orEmpty())
            mDatabase.child("pacientes").child(idPaciente.orEmpty()).removeValue()

            Toast.makeText(context,"Paciente eliminado.",Toast.LENGTH_SHORT).show()
            activity?.finish()
        }
        builder.setNegativeButton("Cancelar") {dialog, which ->

        }

        val dialog = builder.create()

        dialog.show()

    }

    private fun alertaEdit(
            idPaciente: String? = "",
            numeroPulsera: String? = "" ,
            nombrePaciente: String?="",
            pacienteEGStr:String? = "",
            pacNacimientoStr:String? = "",
            pacPesoStr:String? = "",
            pacInternacionStr:String? = ""
    ) {
        val builder = AlertDialog.Builder(context, R.style.Theme_MaterialComponents_Light_Dialog_Alert)
        val inflater = activity!!.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.fragment_patient_form, null)
        builder.setView(dialogView)

        var pulsera: TextInputEditText = dialogView.findViewById(R.id.editText_numero_pulsera)
        var nombre: TextInputEditText = dialogView.findViewById(R.id.editText_nombre)
        var edadGestacional: TextInputEditText = dialogView.findViewById(R.id.editText_edad_gestacional)
        var peso: TextInputEditText = dialogView.findViewById(R.id.editText_peso_al_nacer)
        var lugar: AppCompatSpinner = dialogView.findViewById(R.id.spinner_patient_form)
        var fecha: TextInputEditText = dialogView.findViewById(R.id.editText_fecha_nacimiento)

        builder.setPositiveButton("Ok") {dialog, which ->
            nombre.validate({s -> s.isNotEmpty()}, "Por favor ingrese un nombre")
            pulsera.validate({s -> s.isNotEmpty()}, "Por favor ingrese nro. de pulsera")
            fecha.validate({s -> s.isNotEmpty()}, "Por favor ingrese fecha de nacimiento")
            edadGestacional.validate({s -> s.isNotEmpty()}, "Por favor ingrese edad gestacional")
            peso.validate({s -> s.isNotEmpty()}, "Por favor ingrese el peso del paciente")
            // && etNro.error == null
            if (nombre.error == null && fecha.error == null && edadGestacional.error == null
                && peso.error == null) {
                editUser(
                        idPaciente,
                        pulsera.text.toString().toInt(),
                        nombre.text.toString(),
                        peso.text.toString().toInt(),
                        edadGestacional.text.toString().toInt(),
                        fecha.text.toString(),
                        lugar.selectedItem.toString()
                )
                activity?.finish()
            }
            Toast.makeText(context,"Paciente editado.",Toast.LENGTH_SHORT).show()
            activity?.finish()
        }
        builder.setNegativeButton("Cancelar") {dialog, which ->

        }
        nombre.setText(nombrePaciente)
        pulsera.setText(numeroPulsera)
        edadGestacional.setText(pacienteEGStr)
        peso.setText(pacPesoStr)

        // Dropdown de Lugar de internacion
        if (context != null){
            val adapter = ArrayAdapter.createFromResource(context,R.array.unidades_internacion, android.R.layout.simple_spinner_item)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            lugar.setAdapter(adapter)
            lugar.setSelection(adapter.getPosition(pacInternacionStr))
        }

        fecha.setText(pacNacimientoStr)

        val fabBtn: FloatingActionButton = dialogView.findViewById(R.id.fab_send_patient)
        if (fabBtn != null) {
            fabBtn.hide()
        }
        // Seteo listener del datepicker
        fecha.setOnClickListener {
            showDatePickerDialog(fecha)
        }

        val dialog = builder.create()
        dialog.show()
        dialog.window.setLayout(WindowManager.LayoutParams.MATCH_PARENT , WindowManager.LayoutParams.WRAP_CONTENT)

    }

    private fun showDatePickerDialog(element: TextInputEditText) {

        val newFragment = DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            // +1 because january is zero
            val selectedDate = day.toString() + "/" + (month + 1) + "/" + year
            element.setText(selectedDate)
        })
        newFragment.show(activity?.getSupportFragmentManager(), "datePicker")

    }

    private fun editUser(
            uid: String?,
            numero_pulsera: Int = 0,
            nombre: String = "",
            peso_al_nacer: Int = 0,
            edad_gestacional : Int = 0,
            fecha_nacimiento: String = "",
            lugar_internacion: String = ""
    ){
        //val paciente = Paciente(uid.orEmpty(),numero_pulsera, nombre , peso_al_nacer, edad_gestacional, fecha_nacimiento, lugar_internacion)
        val updates = HashMap<String, Any>()

        updates["numero_pulsera"] = numero_pulsera
        updates["nombre"] = nombre
        updates["peso_al_nacer"] = peso_al_nacer
        updates["edad_gestacional"] = edad_gestacional
        updates["fecha_nacimiento"] = fecha_nacimiento
        updates["lugar_internacion"] = lugar_internacion


        mDatabase.child("pacientes").child(uid.orEmpty()).updateChildren(updates)
                .addOnSuccessListener {
                    if(context!=null) Toast.makeText(context, "Paciente Guardado", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    if(context!=null) Toast.makeText(context, "Error al guardar el paciente", Toast.LENGTH_SHORT).show()
                }
    }

    private fun eliminarChequeosPaciente(idPaciente: String) {
        val chequeosListener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (child in p0.children){
                    child.ref.removeValue()
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        mDatabase.child("chequeos").orderByChild("id_paciente").equalTo(idPaciente).addListenerForSingleValueEvent(chequeosListener)
    }

    private fun eliminarAlertasPaciente(idPaciente: String?) {
        val alertasListener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (child in p0.children){
                    child.ref.removeValue()
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        mDatabase.child("alertas").orderByChild("id_paciente").equalTo(idPaciente).addListenerForSingleValueEvent(alertasListener)
    }


}