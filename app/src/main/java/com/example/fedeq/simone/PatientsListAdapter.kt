package com.example.fedeq.simone

import android.content.Intent
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.patient_row.view.*

class PatientListAdapter(var patients : MutableList<Paciente>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var lastClick: Long = 0
    private var patientsList = patients
    private var patientsFullList : List<Paciente>? = null

    override fun getItemCount(): Int {
        return patientsList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val patient = patientsList.get(position)
        when (holder) {
            is PatientViewHolder -> {

                val context = holder.view.context
                val patientNameView : TextView = holder.view.findViewById(R.id.patient_name)
                val patientLugar: TextView = holder.view.findViewById(R.id.lugar_internacion)
                val patientLastCheckView : TextView = holder.view.findViewById(R.id.last_check)

                patientNameView.text = patient.nombre+ " " + patient.numero_pulsera
                patientLugar.text = patient.lugar_internacion
                patientLastCheckView.text = patient.fecha_ultimo_control

                val btn = holder.view.button_check
                btn.setOnClickListener {
                    if (SystemClock.elapsedRealtime() - lastClick > 1000){
                        val intent = Intent(context, LvsActivity::class.java)
                        intent.putExtra("patientId", patient.uid)

                        context.startActivity(intent)
                    }
                    lastClick = SystemClock.elapsedRealtime()
                }

//                patientNameView.setOnClickListener {
//                    if (SystemClock.elapsedRealtime() - lastClick > 1000){
//                        val intent = Intent (context , PatientInfoActivity::class.java)
//                        context.startActivity(intent)
//                    }
//                    lastClick = SystemClock.elapsedRealtime()
//                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        val holder : RecyclerView.ViewHolder

        //viewType es el layout que obtengo de getItemViewType
        val view = LayoutInflater.from(parent.context).inflate(viewType,parent,false)
        when (viewType) {
            R.layout.patient_row -> {
                holder = PatientViewHolder(view)
            }
            else -> {
                holder = PatientViewHolder(view)
            }
        }
        return holder
    }


    override fun getItemViewType(position: Int): Int {
        return R.layout.patient_row
    }

    fun filter(name : String) {
        if(name.isEmpty()) {

        }
        else {

        }
    }

    inner class PatientViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var nameTextView : TextView? = null
        var lugarTextView : TextView? = null
        var lastCheckTextView : TextView? = null
        var cardView : CardView? = null
        init {
            nameTextView = view.findViewById(R.id.patient_name) as TextView
            lugarTextView = view.findViewById(R.id.lugar_internacion) as TextView
            lastCheckTextView = view.findViewById(R.id.last_check) as TextView
            cardView = view.findViewById(R.id.card_patient_row)
            cardView?.setOnClickListener {
                if (SystemClock.elapsedRealtime() - lastClick > 1000){
                    val intent = Intent (view.context , PatientInfoActivity::class.java)

                    intent.putExtra("pacienteId", patients.get(adapterPosition).uid)
                    intent.putExtra("pacienteNombre", patients.get(adapterPosition).nombre)
                    intent.putExtra("pacientePulsera", patients.get(adapterPosition).numero_pulsera.toString())
                    intent.putExtra("pacienteEG", patients.get(adapterPosition).edad_gestacional.toString())
                    intent.putExtra("pacienteNacimiento", patients.get(adapterPosition).fecha_nacimiento)
                    intent.putExtra("pacientePeso", patients.get(adapterPosition).peso_al_nacer.toString())
                    intent.putExtra("pacienteInternacion", patients.get(adapterPosition).lugar_internacion)
                    view.context.startActivity(intent)

                }
                lastClick = SystemClock.elapsedRealtime()
            }
        }
    }



}

