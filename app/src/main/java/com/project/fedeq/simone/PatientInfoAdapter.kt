package com.project.fedeq.simone

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class PatientInfoAdapter(var context: Context?, private var checks:MutableList<Check>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.data_check_patient, parent, false)

        return PatientInfoViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return checks.size

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var check = checks[position]

        val holder = holder as PatientInfoViewHolder

        holder.txtFecha?.text = check.fecha
        holder.txtAutor?.text = check.autor
        holder.txtCantAlertasRojas?.text = check.cantAlertasRojas.toString()
        holder.txtCantAlertasAmarillas?.text = check.cantAlertasAmarillas.toString()
        holder.txtPorcentajeAlertas?.text = check.cantAlertasBlancas.toString()
        holder.updateId(position)
    }

    inner class PatientInfoViewHolder(row:View):RecyclerView.ViewHolder(row) {
        var txtFecha: TextView? = null
        var txtAutor: TextView? = null
        var txtCantAlertasRojas: TextView? = null
        var txtCantAlertasAmarillas: TextView? = null
        var txtPorcentajeAlertas: TextView? = null
        var card: CardView? = null
        var id: Int? = null

        fun updateId(id : Int) {
            this.id = id
        }

        init {
            this.txtFecha = row.findViewById(R.id.txtFecha)
            this.txtAutor = row.findViewById(R.id.txtAutor)
            this.txtCantAlertasRojas = row.findViewById(R.id.txtCantAlertasRojas)
            this.txtCantAlertasAmarillas = row.findViewById(R.id.txtCantAlertasAmarillas)
            this.txtPorcentajeAlertas = row.findViewById(R.id.txtPocentajeAlertas)
            this.card = row.findViewById(R.id.card_data_check)
            this.card?.setOnClickListener{
//                val intent = Intent(row.context, LvsActivity::class.java)
//                intent.putExtra("checkId", this.id)
//
//                row.context.startActivity(intent)
                showCommentAlert(adapterPosition)
            }
        }
    }

    fun showCommentAlert(position: Int) {

        var alertasAmarillas = mutableListOf<String>()
        var alertasRojas = mutableListOf<String>()
        var alertasBlancas = mutableListOf<String>()
        val check = checks.get(position)



        val alertsListener = object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                for (child in p0.children) {
                    var alerta = child.getValue(Alerta::class.java) as Alerta

                    when(alerta.tipo) {
                        "Roja" -> {
                            alertasRojas.add(alerta.descripcion)
                        }
                        "Amarilla" -> {
                            alertasAmarillas.add(alerta.descripcion)
                        }
                        "Blanca" -> {
                            alertasBlancas.add(alerta.descripcion)
                        }
                    }

                }

                val stringAlertasRojas = TextUtils.join("\n\t● ",alertasRojas)
                val stringAlertasAmarillas = TextUtils.join("\n\t● ",alertasAmarillas)
                val stringAlertasBlancas = TextUtils.join("\n\t● ",alertasBlancas)

                val builder = AlertDialog.Builder(context, R.style.Theme_MaterialComponents_Light_Dialog_Alert)
                var comentario = check.comentario

//                if (comentario == "") {
//                    comentario = "Ningun comentario para este chequeo"
//                }

                var mensaje = ""

                if(comentario != "") {
                    mensaje = "Comentario: $comentario \n\n"
                }
                if (alertasRojas.size > 0) {
                    mensaje+= "Alertas rojas: \n\t● $stringAlertasRojas \n\n"
                }
                if (alertasAmarillas.size > 0) {
                    mensaje+= "Alertas amarillas: \n\t● $stringAlertasAmarillas \n\n"
                }
                if (alertasBlancas.size > 0) {
                    mensaje+= "Alertas blancas: \n\t● $stringAlertasBlancas \n\n"
                }

                if(mensaje == "") {
                    mensaje+= "No hay informacion adicional para este chequeo."
                }


                builder.setMessage(mensaje)
                        .setTitle("Información adicional")


                builder.setPositiveButton("Cerrar", DialogInterface.OnClickListener { dialogInterface, i -> Log.d("Alerta", "Alerta") })


                val dialog = builder.create()


                dialog.show()

            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        mDatabase.child("alertas").orderByChild("id_chequeo").equalTo(check.uid).addListenerForSingleValueEvent(alertsListener)


    }
}