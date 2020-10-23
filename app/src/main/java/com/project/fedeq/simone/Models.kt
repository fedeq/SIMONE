package com.project.fedeq.simone

import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

class Pregunta(
        val uid: Int,
        val tipo: Int,
        val texto: String,
        var opciones: ArrayList<String>? = ArrayList(),
        var childs: ArrayList<Pregunta> = ArrayList(),
        var childsOpciones: List<List<Pregunta>> = ArrayList(ArrayList()),
        var isExpanded: Boolean = false,
        var alertaRoja : String = "",
        var alertaAmarilla: String = "",
        var alertaBlanca: String = "",
        var selectedOption: Int = -1,
        var mensajeAlerta: String = "",
        var descripcionAlerta: String = ""
)

//class Paciente(val uid: String = "", val numero_pulsera: Int = 0, val nombre: String = "", val peso_al_nacer: Int = 0, val edad_gestacional : Int = 0, val fecha_nacimiento: String = "", val lugar_internacion: String = "")
@IgnoreExtraProperties
class Paciente {
    var uid: String? = ""
    var numero_pulsera: Int? = 0
    var nombre : String? = ""
    var peso_al_nacer: Int? = 0
    var edad_gestacional: Int? = 0
    var fecha_nacimiento: String? = ""
    var lugar_internacion: String? = ""
    var fecha_ultimo_control: String? = ""
    var estado: String? = "ACTIVO"

    constructor() {

    }

    constructor(
            uid: String = "",
            numero_pulsera: Int = 0,
            nombre: String = "",
            peso_al_nacer: Int = 0,
            edad_gestacional : Int = 0,
            fecha_nacimiento: String = "",
            lugar_internacion: String = "",
            estado: String = "ACTIVO",
            fecha_ultimo_control: String = ""
    ) {
        this.uid = uid
        this.numero_pulsera = numero_pulsera
        this.nombre = nombre
        this.peso_al_nacer = peso_al_nacer
        this.edad_gestacional = edad_gestacional
        this.fecha_nacimiento = fecha_nacimiento
        this.lugar_internacion = lugar_internacion
        this.estado = estado
        this.fecha_ultimo_control = fecha_ultimo_control
    }
}

class homeFeed(val pacientes: List<Paciente>)



class Chequeo(
    val uid: Int? = null,
    var answers : ArrayList<String>? = ArrayList()
)

class Check {
    var uid: String = ""
    var id_paciente: String = ""
    var cantAlertasRojas: Int = 0
    var cantAlertasAmarillas: Int = 0
    var cantAlertasBlancas: Int = 0
    var cantPreguntas: Int = 0
    var fecha: String = ""
    var autor: String = "Fede"
    var comentario: String = ""

    constructor()

    constructor( uid: String = "", id_paciente: String, cantAlertasRojas: Int = 0, cantAlertasAmarillas: Int = 0, cantAlertasBlancas: Int = 0, cantPreguntas: Int = 0, fecha: String = "", autor: String = "", comentario: String = ""){
        this.uid = uid
        this.id_paciente = id_paciente
        this.cantAlertasAmarillas = cantAlertasAmarillas
        this.cantAlertasRojas = cantAlertasRojas
        this.cantAlertasBlancas = cantAlertasBlancas
        this.cantPreguntas = cantPreguntas
        this.fecha = fecha
        this.autor = autor
        this.comentario = comentario
    }
}


//class Alerta(val uid: String, val id_pregunta: String, val id_paciente: String, val id_chequeo: String, val tipo: Int)

class Answer(val questionId: Int, val value: String)

class LVS(val id: Int, val questions: MutableList<Pregunta>)

class EncabezadoChequeo {
    var fecha: String = ""
    var autor: String = ""
    var cantAlertas: Int = 0

    constructor() {}

    constructor(fecha: String, autor: String, cantAlertas:Int) {
        this.fecha = fecha
        this.autor = autor
        this.cantAlertas = cantAlertas
    }
}

class Alerta {
    var uid: String = ""
    var id_pregunta: String = ""
    var id_paciente: String = ""
    var tipo: String = ""
    var id_chequeo: String = ""
    var descripcion: String = ""

    constructor() {}

    constructor(uid: String, id_pregunta: String, id_paciente: String, tipo: String, id_chequeo: String, descripcion: String) {
        this.uid = uid
        this.id_pregunta = id_pregunta
        this.id_paciente = id_paciente
        this.tipo = tipo
        this.id_chequeo = id_chequeo
        this.descripcion = descripcion
    }

}