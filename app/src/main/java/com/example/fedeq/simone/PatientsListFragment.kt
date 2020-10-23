package com.example.fedeq.simone

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_patients_list.*

class PatientsListFragment : Fragment() {
    private val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var pacientes: MutableList<Paciente> = mutableListOf()
    private var keysPacientes: MutableList<String> = mutableListOf()
    private var recyclerPatients: RecyclerView? = null
    private var mAuth: FirebaseAuth? = null

    // TODO ver si se usa
    companion object {
        fun newInstance(): PatientsListFragment {
            return PatientsListFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_patients_list, container,false)

        recyclerPatients = v.findViewById(R.id.recyclerView_patients)

        recyclerPatients?.layoutManager = LinearLayoutManager(activity)

        inicializarPacientes()

        recyclerPatients?.adapter = PatientListAdapter(pacientes)
        mAuth = FirebaseAuth.getInstance()

        val addPatientFab : FloatingActionButton = v.findViewById(R.id.fab_add_patient)
        addPatientFab.setOnClickListener{
            val intent = Intent(activity, PatientFormActivity::class.java)
            this.startActivity(intent)
        }

        // consigo el usuario para mostrar en el toolbar
        var usuario = FirebaseAuth.getInstance().currentUser?.email
        val usuarioSplit = usuario?.split("@")
        if (usuarioSplit != null) {
            usuario = usuarioSplit[0]
        }
        val textUsuario: TextView = v.findViewById(R.id.label_usuario)
        textUsuario.setText(usuario)

        // seteo el clickListener para el logout
        val logout: ImageView = v.findViewById(R.id.logout_icon)
        logout.setOnClickListener{
            Log.d("DEBUG", "logOut")
            mAuth?.signOut()
            Toast.makeText(context, "Sesion cerrada", Toast.LENGTH_LONG).show();
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            this.startActivity(intent)
        }
        return v
    }

    private fun inicializarPacientes() {
        val pacientesListener = object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val paciente = p0.getValue(Paciente::class.java) as Paciente

                if(paciente.lugar_internacion == "UCE") {
                    pacientes.add(0, paciente)
                    keysPacientes.add(0, paciente.uid.orEmpty())
                } else {
                    pacientes.add(pacientes.size, paciente)
                    keysPacientes.add(keysPacientes.size, paciente.uid.orEmpty())
                }

                activity?.runOnUiThread {
                    recyclerPatients?.adapter = PatientListAdapter(pacientes)
                }
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val paciente = p0.getValue(Paciente::class.java) as Paciente
                val index = keysPacientes.indexOf(paciente.uid)

                pacientes.removeAt(index)
                keysPacientes.removeAt(index)
//              pacientes.add(index,paciente)

                if(paciente.lugar_internacion == "UCE") {
                    pacientes.add(0, paciente)
                    keysPacientes.add(0, paciente.uid.orEmpty())
                } else {
                    pacientes.add(pacientes.size, paciente)
                    keysPacientes.add(keysPacientes.size, paciente.uid.orEmpty())
                }
                activity?.runOnUiThread {
                    recyclerPatients?.adapter = PatientListAdapter(pacientes)
                }
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                val paciente = p0.getValue(Paciente::class.java) as Paciente

                val index = keysPacientes.indexOf(paciente.uid)
                pacientes.removeAt(index)
                keysPacientes.removeAt(index)


                activity?.runOnUiThread {
                    recyclerPatients?.adapter = PatientListAdapter(pacientes)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        //TODO ordenar pacientes por fecha de ultimo control (mas arriba los que mas tiempo hace que no se controlan)
        mDatabase.child("pacientes").orderByChild("estado").equalTo("ACTIVO").addChildEventListener(pacientesListener)

        // Este listener se usa solo para esconder el progressBar, ya que el otro queda escuchando por nuevos cambios
        mDatabase.child("pacientes").orderByChild("estado").equalTo("ACTIVO").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                patient_list_progress.setVisibility(View.GONE)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

}