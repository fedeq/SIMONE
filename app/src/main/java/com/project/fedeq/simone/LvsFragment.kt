package com.project.fedeq.simone

import android.app.AlertDialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class LvsFragment : Fragment() {

    private var isEditable: Boolean = true
    private val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val mapAlertaDescipcion = HashMap<Int,String>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_lvs, container, false)

        val recyclerLvs : RecyclerView = v.findViewById(R.id.recyclerView_lvs)

        val layoutManager = NpaLinearLayoutManager(activity)

        recyclerLvs.layoutManager = layoutManager
        // recyclerLvs?.itemAnimator = DefaultItemAnimator()
        val di = DividerItemDecoration(recyclerLvs.context, layoutManager.orientation)
        recyclerLvs.addItemDecoration(di)

        val checkId = arguments?.getInt("checkId")
        val patientId = arguments?.getString("patientId").orEmpty()
        val btn : MaterialButton = v.findViewById(R.id.button_lvs_save)

        Log.d("id" , patientId)

        if(checkId != -1) {
            isEditable = false
            btn.visibility = View.GONE
        }


        // Secciones
        val s1 = Pregunta(100, 10, "IDENTIFICACIÓN")
        val s2 = Pregunta(101, 10, "MACROAMBIENTE")
        val s3 = Pregunta(102, 10, "INDICACIONES MÉDICAS")
        val s4 = Pregunta(103, 10, "SOPORTE RESPIRATORIO")
        val s5 = Pregunta(104, 10, "ACCESOS VASCULARES")
        val s6 = Pregunta(105, 10, "NUTRICIÓN")
        val s7 = Pregunta(106, 10, "OTROS ACCESOS EXTERNOS")
        val s8 = Pregunta(107, 10, "OTRAS LESIONES DE PIEL")

        // Seccion 1: Identificacion
        val q1 = Pregunta(1,1,"Pulsera identificatoria en el RN",
                alertaBlanca = "NO")

        mapAlertaDescipcion.put(1,"Paciente no tenia pulsera")
        val q2 = Pregunta(2,1,"Etiqueta correcta en incubadora/cuna/servocuna",
                alertaBlanca = "NO")
        mapAlertaDescipcion.put(2,"Etiqueta incorrecta en incubadora/cuna/servocuna")


        // Seccion 2: Macroambiente

        // Hijas de P3
        val q301 = Pregunta(301,2, "Cantidad de bombas de infusión",
                opciones = ArrayList<String>(Arrays.asList("1", "2","3","4","5")))
        val q302 = Pregunta(302,1, "Rotulo correcto colocado",
                alertaRoja = "NO",
                mensajeAlerta = "Deseche la medicación NO rotulada, revise la indicación, prepare y rotule! REGISTRE")
        mapAlertaDescipcion.put(302,"Rotulo incorrecto en bombas de infusion")

        val q303 = Pregunta(303,1, "Droga coincidente con la indicación médica",
                alertaRoja = "NO",
                mensajeAlerta = "Riesgo hemodinámico/hidroelectrolito. \nVerifique con el médico a cargo del cuidado del paciente. \nREGISTRE")
        mapAlertaDescipcion.put(303,"Droga NO coincidente con la indicacion medica")

        val q304 = Pregunta(304,1, "Goteo coincidente con la indicacion medica",
                alertaRoja = "NO",
                mensajeAlerta = "Riesgo hemodinámico/hidroelectrolito. \nVerifique con el médico a cargo del cuidado del paciente. \nREGISTRE")
        mapAlertaDescipcion.put(304,"Goteo no coincidente con indicacion médica")

        val q3childs = arrayListOf(q301,q302,q303,q304)

        // P3
        val q3 = Pregunta(3,1, "¿Tiene bombas de infusión?",
                childs = q3childs)
        // P4
        val q4 = Pregunta(4,1, "Monitores con alarmas de saturación de oxígenos correctamente colocadas",
                alertaRoja = "NO",
                mensajeAlerta = "Riesgo de CEGUERA POR ROP \n¡Si tiene dudas, consulte! \nREGISTRE")
        mapAlertaDescipcion.put(4,"Monitores con alarmas de saturación de oxígenos incorrectamente colocadas")

        // P5
        val q5 = Pregunta(5,1, "Monitores con alarmas de frecuencia cardíaca correctamente colocadas",
                alertaRoja = "NO",
                mensajeAlerta = "Coloque el módulo de registro cardiográfico. \n¡Si tiene dudas, consulte! \nREGISTRE")
        mapAlertaDescipcion.put(5,"Monitores con alarmas de frecuencia cardíaca incorrectamente colocadas")
        // P6
        val q6 = Pregunta(6,1, "Temperatura del paciente adecuada (entre 36.2 y 37.5 ºC)",
                alertaRoja = "NO",
                mensajeAlerta = "Registre la temperatura actual de la  de la incubadora. \n¡Si tiene dudas, consulte!. \nREGISTRE")
        mapAlertaDescipcion.put(6,"Temperatura del paciente inadecuada")
        // Hijas P7
        val pSensor = Pregunta(701,1,"Sensor correctamente colocado",
                alertaAmarilla = "NO")
        mapAlertaDescipcion.put(701,"Sensor en servocuna incorrectamente colocado")
        val pTemp = Pregunta(702,1,"Temperatura adecuada y coincidente con la hoja de enfermeria",
                alertaBlanca = "NO")
        mapAlertaDescipcion.put(702,"Temperatura de servocuna inadecuada o no coincidente con la hoja de enfermeria")

        val pCompuertas = Pregunta(703, 1, "Compuertas correctamente selladas",
                alertaAmarilla = "NO")
        mapAlertaDescipcion.put(703,"Compuerta de incubadora incorrectamente sellada")

        val p70401 = Pregunta(70401,1, "¿La humedad está programada?",
                alertaRoja = "NO",
                mensajeAlerta = "Active la humedad de la incubadora. Riesgo de hipotermia y deshidratación. REGISTRE"
                )
        mapAlertaDescipcion.put(70401,"Humedad NO programada")


        val pHumedad = Pregunta(704, 1, "¿El paciente necesita humedad?\n(< 1000gr y <= 7 dias de vida)",
                childs = arrayListOf(p70401))

        val pSensorInc = Pregunta(705, 1, "Sensor correctamente colocado",
                alertaAmarilla = "NO")
        mapAlertaDescipcion.put(705,"Sensor de incubadora incorrectamente colocado")
        val pTempInc = Pregunta(706,1,"Temperatura adecuada y coincidente con la hoja de enfermeria",
                alertaBlanca = "NO")
        mapAlertaDescipcion.put(706,"Temperatura de incubadora inadecuada o no coincidente con la hoja de enfermeria")

        val childsCuna = ArrayList<Pregunta>()
        val childsServocuna = listOf(pSensor,pTemp)
        val childsIncubadoraa = listOf(pCompuertas,pHumedad,pSensorInc,pTempInc)

        // P7
        val q7= Pregunta(7,2, "¿Dónde se encuentra el paciente?",
                opciones = ArrayList(Arrays.asList("Cuna","Servocuna","Incubadora")),
                childsOpciones = listOf(childsCuna,childsServocuna,childsIncubadoraa))

        //Seccion 3

        // Seccion 4: Soporte respiratorio
        val q8010101 = Pregunta(80101,1,"¿Las tubuladuras estan correctamente conectadas?",
                alertaRoja = "NO",
                mensajeAlerta = "Corrija de inmediato! \nREGISTRE")
        mapAlertaDescipcion.put(80101,"Tubuladuras incorrectamente conectadas")

        val q8010102 = Pregunta(80102,1,
                "Comprobación de parámetros coincidentes con hoja de enfermería " +
                        "\n [PIM/PEEP/FR/FIO2/FLUJO/TI]",
                alertaRoja = "NO",
                mensajeAlerta = "Verifique con el médico a cargo del cuidado del paciente. REGISTRE")
        mapAlertaDescipcion.put(80102,"Parametros no coincidentes con hoja de indicacion medica")

        // hijas de q8010103
        val q801010301 = Pregunta(8010301,1,"¿El humidificador esta encendido?",
                alertaAmarilla = "NO")
        mapAlertaDescipcion.put(8010301,"Humidificador APAGADO")

        val q801010302 = Pregunta(8010302,1,"¿Tiene la cantidad de agua correspondiente?",
                alertaBlanca = "NO")
        mapAlertaDescipcion.put(8010302,"Cantidad de agua NO correspondiente en humidificador")

        val childsq8010103 = arrayListOf(q801010301,q801010302)

        val q8010103 = Pregunta(80103,1,"¿Tiene humidificador?",
                childs = childsq8010103)

        // hijas de q8010104
        val q801010401 = Pregunta(8010401,2,"¿Que nùmero de tubo tiene?",
                opciones = ArrayList(Arrays.asList( "2", "2.5", "3", "3.5", "4")))

        val q801010402 = Pregunta(8010402,1,"¿TET bien fijado?",
                alertaRoja = "NO",
                mensajeAlerta = "Corrija de inmediato. \n" +
                        "Revise medidas de confort del BB. \n" +
                        "REGISTRE")
        mapAlertaDescipcion.put(8010402,"TET mal fijado")

        val q801010403 = Pregunta(8010403,1,"¿Presenta lesiones de piel o mucosa asociada a fijacion?",
                alertaAmarilla = "SI")
        mapAlertaDescipcion.put(8010403,"Lesiones de pieol o mucosa asociada a fijacion")

        val childsq8010104 = arrayListOf(q801010401,q801010402,q801010403)

        val q8010104 = Pregunta(80104,1,"¿Tiene TET?",
                childs = childsq8010104)

        val childsARM = listOf(q8010101, q8010102, q8010103, q8010104)

        val q8010201 = Pregunta(80201,1,"¿Las tubuladuras estan correctamente conectadas?",
                alertaRoja = "NO",
                mensajeAlerta = "Corrija de inmediato. \n" +
                        "Revise medidas de confort del BB. \n" +
                        "REGISTRE")
        mapAlertaDescipcion.put(80201,"Tubuladuras incorrectamente colocadas")

        val q8010202 = Pregunta(80202,1,
                "Comprobación de parámetros coincidentes con hoja de enfermería" +
                "\n [PEEP/FIO2/FLUJO]",
                alertaRoja = "NO",
                mensajeAlerta = "Verifique con el médico a cargo del cuidado del paciente. REGISTRE")
        mapAlertaDescipcion.put(80202,"Parametros NO coincidentes con hoja de enfermeria")

        // hijas de q8010203
        val q801020301 = Pregunta(8020301,1,"¿El humidificador esta encendido?",
                alertaAmarilla = "NO")
        mapAlertaDescipcion.put(8020301,"Humidificador APAGADO")

        val q801020302 = Pregunta(8020302,1,"¿Tiene la cantidad de agua correspondiente?",
                alertaBlanca = "NO")
        mapAlertaDescipcion.put(8020302,"Cantidad de agua NO correspondiente en humidificador")

        val childsq8010203 = arrayListOf(q801020301,q801020302)

        val q8010203 = Pregunta(80203,1,"¿Tiene humidificador?",
                childs = childsq8010203)

        val q8010204 = Pregunta(80204,2,"¿Qué número de interfase tiene?",
                opciones = ArrayList(Arrays.asList("00", "0","1","2","3")))
        val q8010205 = Pregunta(80205,1,"¿Interfase bien fijada?",
                alertaRoja = "NO",
                mensajeAlerta = "Corrija de inmediato. \n" +
                        "Revise medidas de confort del BB. \n" +
                        "REGISTRE")
        mapAlertaDescipcion.put(80205,"Interfase mal fijada")

        val q8010206 = Pregunta(80206,1,"¿Presenta lesiones de piel o tabique nasal asociado a fijacion?",
                alertaAmarilla = "SI")
        mapAlertaDescipcion.put(80206, "Lesiones de piel o tabique nasal asociado a fijacion")

        val childsCPAP = listOf(q8010201, q8010202, q8010203, q8010204, q8010205, q8010206)

        val q8010301 = Pregunta(80301,2,"Tipo de canula nasal",
                opciones = ArrayList(Arrays.asList("Bajo flujo(B)", "Alto flujo(A)")))

        val q8010302 = Pregunta(80302,1,"¿Las tubuladuras estan correctamente conectadas?",
                alertaRoja = "NO",
                mensajeAlerta = "Corrija de inmediato! \nREGISTRE")
        mapAlertaDescipcion.put(80302,"Tubuladuras incorrectamente conectadas")

        val q8010303 = Pregunta(80303,1,
                "Comprobación de parámetros coincidentes con hoja de enfermería" +
                        "\n [FIO2/FLUJO]",
                alertaRoja = "NO",
                mensajeAlerta = "Verifique con el médico a cargo del cuidado del paciente. REGISTRE")
        mapAlertaDescipcion.put(80303,"Parametros no coincidentes con hoja de enfermeria")

        // hijas de q8010203
        val q801030401 = Pregunta(8030401,1,"¿El humidificador esta encendido?",
                alertaAmarilla = "NO")
        mapAlertaDescipcion.put(8030401,"Humidificador APAGADO")

        val q801030402 = Pregunta(8030402,1,"¿Tiene la cantidad de agua correspondiente?",
                alertaBlanca = "NO")
        mapAlertaDescipcion.put(8030402,"Cantidad de agua NO correspondiente en humidificador")

        val childsq8010303 = arrayListOf(q801030401,q801030402)

        val q8010304 = Pregunta(80304,1,"¿Tiene humificador?",
                childs = childsq8010303)

        val q8010305 = Pregunta(80305,1,"¿Canula bien fijada?",
                alertaRoja = "NO",
                mensajeAlerta = "Corrija de inmediato. \n" +
                        "Revise medidas de confort del BB. \n" +
                        "REGISTRE")
        mapAlertaDescipcion.put(80305,"Canula MAL fijada")

        val q8010306 = Pregunta(80306,1,"¿Presenta lesiones de piel o tabique nasal asociado a fijacion?",
                alertaAmarilla = "SI")
        mapAlertaDescipcion.put(80306,"Lesiones de piel o tabique nasal asociado a fijacion")

        val childsCN = listOf(q8010301, q8010302, q8010303, q8010304, q8010305, q8010306)


        val q8010401 = Pregunta(80401,1,"¿Las tubuladuras estan correctamente conectadas?",
                alertaRoja = "NO",
                mensajeAlerta = "Corrija de inmediato! \nREGISTRE")
        mapAlertaDescipcion.put(80401,"Tubuladuras incorrectamente colocadas")

        val q8010402 = Pregunta(80402,1,
                "Comprobación de parámetros coincidentes con hoja de enfermería" +
                        "\n [FIO2/FLUJO]",
                alertaRoja = "NO",
                mensajeAlerta = "Verifique con el médico a cargo del cuidado del paciente. REGISTRE")
        mapAlertaDescipcion.put(80402,"Parametros no coincidentes con hoja de enfermeria")

        // hijas de q8010403
        val q801040301 = Pregunta(8040301,1,"¿El humidificador esta encendido?",
                alertaAmarilla = "NO")
        mapAlertaDescipcion.put(8040301,"Humidificador APAGADO")

        val q801040302 = Pregunta(8040302,1,"¿Tiene la cantidad de agua correspondiente?",
                alertaBlanca = "NO")
        mapAlertaDescipcion.put(8040302,"Cantidad de agua NO correspondiente en humidificador")

        val childsq8010403 = arrayListOf(q801040301,q801040302)

        val q8010403 = Pregunta(80404,1,"¿Tiene humidificador?",
                childs = childsq8010403)

        val childsHalo = listOf(q8010401, q8010402, q8010403)

        val q801 = Pregunta(801,2, "Tipo de soporte respiratorio",
                opciones = ArrayList<String>(Arrays.asList("ARM", "CPAP","Canula Nasal","Halo")),
                childsOpciones = listOf(childsARM, childsCPAP, childsCN,childsHalo))
        val q8 = Pregunta(8,1, "¿Tiene soporte respiratorio?",
                childs = arrayListOf(q801))

        //Seccion 5: Accesos Vasculares
        val q901 = Pregunta(901,2, "Cantidad de accesos vasculares",
                opciones = ArrayList<String>(Arrays.asList("1", "2","3")))
        val q90201 = Pregunta(90201,1, "\t ¿Via necesaria?")
        val q90301 = Pregunta(90301,1, "\t ¿Via necesaria?")
        val q90401 = Pregunta(90401,1, "\t ¿Via necesaria?")
        val q90501 = Pregunta(90501,1, "\t ¿Via necesaria?")
        val q90601 = Pregunta(90601,1, "\t ¿Via necesaria?")

        val q902 = Pregunta(902,1, "Canalización umbilical arterial",
                childs = arrayListOf(q90201))
        val q903 = Pregunta(903,1, "Canalización umbilical venosa",
                childs = arrayListOf(q90301))
        val q904 = Pregunta(904,1, "Percutáneo ",
                childs = arrayListOf(q90401))
        val q905 = Pregunta(905,1, "Vía periférica ",
                childs = arrayListOf(q90501))
        val q906 = Pregunta(906,1, "Canalización venosa quirúrgica ",
                childs = arrayListOf(q90601))
        val q907 = Pregunta(907,1,"¿Él o los accesos vasculares están permeables?",
                alertaRoja = "NO",
                mensajeAlerta = "Permeabilice la vía y avise al médico de guardia. \nREGISTRE")
        mapAlertaDescipcion.put(907,"Acceso/s vasculares no permeable/s")

        val q908 = Pregunta(908,1,"¿La fijación del acceso vascular es adecuada?",
                alertaBlanca = "NO")
        mapAlertaDescipcion.put(908,"Fijacion de acceso vascular INADECUADA")

        val q909 = Pregunta(909,1,"¿Él paciente presenta signos de flebitis o infiltración asociadas al acceso vascular?",
                alertaAmarilla = "SI")
        mapAlertaDescipcion.put(909,"Signos de flebitis o infiltracion asociadas al acceso vasculas")

        val q9 = Pregunta(9,1, "¿Tiene accesos vasculares?",
                childs = arrayListOf(q901,q902,q903,q904,q905,q906,q907,q908,q909))

        // Seccion 6 - Nutricion
        val q10010201 = Pregunta(10010201, 1, "Ritmo de infusión coincide con indicación médica",
                alertaBlanca = "NO")
        mapAlertaDescipcion.put(10010201,"Ritmo de infusion no coincide con indicacion medica")

        val childsGc = listOf(q10010201)

        val q1001 = Pregunta(1001,2,"¿Cómo se administra?",
                opciones = ArrayList(Arrays.asList("Gavage", "Goteo Continuo")),
                childsOpciones = listOf(listOf(),childsGc))

        val q1002 = Pregunta(1002, 1, "¿Sonda bien colocada y fijada (SOG/SNG)?",
                alertaBlanca = "NO")
        mapAlertaDescipcion.put(1002,"Colocación y fijación de sonda inadecuada.")


        val q10 = Pregunta(10,1, "¿El recibe nutrición enteral por sonda?",
                childs = arrayListOf(q1001, q1002))

        val q1101 = Pregunta(1101, 1, "¿Ritmo de infusión coincide con indicación médica?",
                alertaBlanca = "NO")
        mapAlertaDescipcion.put(1101,"Ritmo de infusión no coincide con indicacion médica")

        val q11 = Pregunta(11, 1, "¿El paciente recibe nutrición parenteral?",
                childs = arrayListOf(q1101))

        // Seccion 7: Otros Accesos Externos
        val q1201 = Pregunta(1201, 1, "¿Tiene protección ocular correctamente colocada?",
                alertaBlanca = "NO")
        mapAlertaDescipcion.put(1201,"Proteccion ocular incorrectamente colocada")

        val q12 = Pregunta(12, 1, "¿El paciente recibie luminoterapia?",
                childs = arrayListOf(q1201))

        val q1301 = Pregunta(1301,1, "¿Está permeable?",
                alertaBlanca = "NO")
        mapAlertaDescipcion.put(1301,"Sonda vesical no permeable")

        val q1302 = Pregunta(1302,1, "¿Es necesaria?")
        val q13 = Pregunta(13, 1, "¿El paciente tiene sonda vesical?",
                childs = arrayListOf(q1301, q1302))

        val q1401 = Pregunta(1401, 1, "¿Drena o burbujea?")
        val q1402 = Pregunta(1402, 1, "¿Esta correctamente fijado?",
                alertaBlanca = "NO")
        mapAlertaDescipcion.put(1402,"Tubo de drenaje mal fijado")

        val q14 = Pregunta(14, 1, "¿El paciente tiene tubo de drenaje Pleural?",
                childs = arrayListOf(q1401, q1402))

        // Seccion 8: Otras lesiones de piel importante
        val q15 = Pregunta(15, 1, "Tiene hematomas?",
                alertaBlanca = "SI")
        mapAlertaDescipcion.put(15,"Paciente tiene hematomas")

        val q16 = Pregunta(16, 1, "¿Erosión o excoriación?",
                alertaBlanca = "SI")
        mapAlertaDescipcion.put(16,"Erosion o excoriación")

        val q17 = Pregunta(17, 1, "¿Ulceras por decúbito?",
                alertaBlanca = "SI")
        mapAlertaDescipcion.put(17,"Ulceras por decúbito")

        // Comentario
        val comment = Pregunta(9843,15, "comentario")

        var list = mutableListOf(s1,q1,q2,s2,q3,q4,q5,q6,q7,s4,q8,s5,q9,s6,q10,q11,s7,q12,q13, q14,
                s8,q15,q16,q17,comment)

        var lvs = LVS(1, list)
        // Fin hc lvs

        recyclerLvs.adapter = LvsAdapter(context,lvs,isEditable)

        var adap : LvsAdapter = recyclerLvs.adapter as LvsAdapter

        if(checkId== -1) {
            btn.setOnClickListener {

                var dataCheck = adap.getDataChequeo()

                if(dataCheck.containsValue("null")) {
                    mostrarAlertaFormularioIncompleto();
                }
                else {
                    var alertasRojas = adap.getRedAlerts()
                    var alertasAmarillas = adap.getYellowAlerts()
                    var alertasBlancas = adap.getWhiteAlerts()
                    var cantPreguntas = adap.getDataChequeo().size
                    var comment = adap.getComment()
                    mostrarAlertas(patientId, alertasRojas, alertasAmarillas, alertasBlancas, cantPreguntas, comment)
                }
            }
        }

        return v
    }

    fun writeNewCheck(patientId : String, redAlerts: HashSet<String>, yellowAlerts: HashSet<String>,
                      whiteAlerts: HashSet<String>, cantPreguntas: Int, comentario: String) {
        val checkKey = mDatabase.child("chequeos").push().key.orEmpty()

        // Obtengo fecha y hora actual
        val currentTime = Calendar.getInstance().time
        val df = SimpleDateFormat("dd/MM/yyy HH:mm")
        val fecha = df.format(currentTime)

        // consigo el autor
        var autor = FirebaseAuth.getInstance().currentUser?.email

        val autorSplit = autor?.split("@")
        if (autorSplit != null) {
            autor = autorSplit[0]
        }
        // Creo objeto Check
        val check = Check(checkKey, patientId, redAlerts.size, yellowAlerts.size, whiteAlerts.size, cantPreguntas, fecha, comentario=comentario, autor = autor.orEmpty())
        // Log.d("Fecha", currentTime.toString())

        //Guardo en la base de datos
        mDatabase.child("chequeos").child(checkKey).setValue(check)
                .addOnSuccessListener {
                    if(context!=null) Toast.makeText(context, "Chequeo Guardado", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    if(context!=null) Toast.makeText(context, "Error al guardar el chequeo", Toast.LENGTH_SHORT).show()
                }

        //Actualizo fecha de ultimo control en el paciente
        mDatabase.child("pacientes").child(patientId).child("fecha_ultimo_control").setValue(fecha)

        //Guardo las alertas generadas por el chequeo
        for (alertaRoja in redAlerts) {
            val alertKey = mDatabase.child("alertas").push().key.orEmpty()
            val idPregunta = alertaRoja.toInt()
            var alerta = Alerta(alertKey,alertaRoja,patientId,"Roja",checkKey,mapAlertaDescipcion.get(idPregunta).orEmpty())
            mDatabase.child("alertas").child(alertKey).setValue(alerta)
        }
        for (alertaAmarilla in yellowAlerts) {
            val alertKey = mDatabase.child("alertas").push().key.orEmpty()
            val idPregunta = alertaAmarilla.toInt()
            var alerta = Alerta(alertKey,alertaAmarilla,patientId,"Amarilla",checkKey,mapAlertaDescipcion.get(idPregunta).orEmpty())
            mDatabase.child("alertas").child(alertKey).setValue(alerta)
        }
        for(alertaBlanca in whiteAlerts) {
            val alertKey = mDatabase.child("alertas").push().key.orEmpty()
            val idPregunta = alertaBlanca.toInt()
            var alerta = Alerta(alertKey,alertaBlanca,patientId,"Blanca",checkKey,mapAlertaDescipcion.get(idPregunta).orEmpty())
            mDatabase.child("alertas").child(alertKey).setValue(alerta)
        }
    }

    fun mostrarAlertaFormularioIncompleto() {
        val builder = AlertDialog.Builder(context, R.style.Theme_MaterialComponents_Light_Dialog_Alert)


        builder.setMessage("Debe completar todo el formulario antes de guardarlo")
                .setTitle("Formulario incompleto")


        builder.setPositiveButton("Ok", DialogInterface.OnClickListener { dialogInterface, i -> Log.d("Alerta", "Alerta") })


        val dialog = builder.create()


        dialog.show()
    }

    fun mostrarAlertas(patientId: String, alertasRojas: HashSet<String>, alertasAmarillas: HashSet<String>,
                       alertasBlancas: HashSet<String>, cantPreguntas: Int, comentario: String) {
        val builder = AlertDialog.Builder(context, R.style.Theme_MaterialComponents_Light_Dialog_Alert)
        builder.setMessage("Se guardara un chequeo con: \n\n"+alertasRojas.size+" alertas rojas \n"+ alertasAmarillas.size+" alertas amarillas\n"+ alertasBlancas.size+" alertas blancas")
                .setTitle("Confirmar guardado de chequeo")

        builder.setPositiveButton("Ok") {dialog, which ->
            writeNewCheck(patientId, alertasRojas, alertasAmarillas,alertasBlancas,cantPreguntas, comentario) //TODO pasar el autor
            Toast.makeText(context,"Chequeo guardado",Toast.LENGTH_SHORT).show()
            activity?.finish()
        }
        builder.setNegativeButton("Cancelar") {dialog, which ->

        }


        val dialog = builder.create()


        dialog.show()

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }


    companion object {

        fun newInstance() = LvsFragment()
    }
}
