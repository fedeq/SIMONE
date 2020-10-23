package com.example.fedeq.simone

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.comment_row.view.*
import kotlinx.android.synthetic.main.question_num_row.view.*
import kotlinx.android.synthetic.main.question_radio_row.view.*
import kotlinx.android.synthetic.main.question_spinner_row.view.*
import kotlinx.android.synthetic.main.section_row.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet


class LvsAdapter(var context: Context?, var lvs: LVS, val isEditable: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var checkMap: Chequeo = Chequeo()
    private var dataChequeo : HashMap<Int,String> = HashMap()
    private var onBind: Boolean = false
    private var redAlerts: HashSet<String> = HashSet()
    private var yellowAlerts: HashSet<String> = HashSet()
    private var whiteAlerts: HashSet<String> = HashSet()
    private var comment: String = ""

    init {
        checkMap.answers = ArrayList()
        var ans = checkMap.answers
        for (i in 0..lvs.questions.size-1) {
            ans?.add(i,"")
        }
        setHasStableIds(true)
    }

    fun getDataChequeo(): HashMap<Int,String> {
        return dataChequeo
    }

    fun getRedAlerts() : HashSet<String> {
        return redAlerts
    }

    fun getYellowAlerts() : HashSet<String> {
        return yellowAlerts
    }

    fun getWhiteAlerts() : HashSet<String> {
        return whiteAlerts
    }

    fun getComment(): String {
        return comment
    }

    override fun getItemId(position: Int): Long {
        val question = lvs.questions.get(position)
        return question.uid.toLong()
    }


    override fun getItemCount(): Int {
        return lvs.questions.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val question = lvs.questions.get(position)
        onBind = true
        when (holder) {
            is QuestionNumViewHolder -> {
                holder.questionTextView?.text = question.texto
                holder.myCustomEditTextListener.updateId(holder.adapterPosition)

                val ans = checkMap.answers?.get(holder.adapterPosition)
                if ((ans != null) && (ans != "null")) {
                    holder.answerEditText?.setText(checkMap.answers?.get(holder.adapterPosition))
                }
                else {
                    holder.answerEditText?.setText("")
                }
                holder.answerEditText?.setImeOptions(EditorInfo.IME_ACTION_DONE)
                holder.answerEditText?.isFocusable = isEditable
            }
            is QuestionRadioViewHolder -> {

                //Seteo texto de la pregunta
                holder.questionTextView?.text = question.texto

                //Actualizo id
                holder.updateId(holder.adapterPosition)

                //Busco respuestra
                val ans = checkMap.answers?.get(holder.adapterPosition)

                Log.d("print",checkMap.answers?.toString())
                Log.d("size",checkMap.answers?.size.toString())

                //Si no esta la respuestra muestro los radio sin checkear
                if (ans !== null && ans!== "" && ans!=="null") {
                    Log.d("sarasa","Seteando check en pregunta con id: "+ question.uid)
                    holder.radioGroup?.check(ans.toInt())
                }
                else {
                    holder.radioGroup?.clearCheck()
                }

                // Pintado de pregunta para indicar alertas
                if(dataChequeo.get(question.uid) == question.alertaAmarilla) {
                    holder.itemView.setBackgroundColor(Color.parseColor("#ffee58"))
                }
                else {
                    if(dataChequeo.get(question.uid) == question.alertaRoja) {
                        holder.itemView.setBackgroundColor(Color.parseColor("#ff7043"))
                    }
                    else {
                        holder.itemView.setBackgroundColor(Color.WHITE)
                    }
                }

                if(!isEditable) {
                    for (i in 0 until 2) {
                        (holder.radioGroup?.getChildAt(i) as RadioButton).isEnabled = false
                    }
                }
            }

            is QuestionSpinnerViewHolder -> {

                holder.questionTextView?.text = question.texto

                holder.updateId(holder.adapterPosition)

                val options: ArrayList<String>? = question.opciones

                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, options)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                holder.spinnerView?.setAdapter(adapter)

                val ans: String? = checkMap.answers?.get(holder.adapterPosition)
                if((ans != null) && (ans != "") && (ans!= "null")) {
                    holder.spinnerView?.setSelection(ans.toInt())
                }

                holder.itemView.setBackgroundColor(Color.WHITE)


                holder.spinnerView?.isEnabled = isEditable
            }

            is SectionViewHolder -> {
                holder.section?.text = question.texto
            }

            is CommentViewHolder -> {
                holder.itemView.setBackgroundColor(Color.WHITE)
            }


        }
        onBind = false

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val holder: RecyclerView.ViewHolder

        //viewType es el layout que obtengo de getItemViewType
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        when (viewType) {
            R.layout.question_radio_row -> {
                holder = QuestionRadioViewHolder(view)
            }
            R.layout.question_num_row -> {
                holder = QuestionNumViewHolder(view, MyCustomEditTextListener())
            }
            R.layout.question_spinner_row -> {
                holder = QuestionSpinnerViewHolder(view)
            }
            R.layout.section_row -> {
                holder = SectionViewHolder(view)
            }
            R.layout.comment_row -> {
                holder = CommentViewHolder(view)
            }

            else -> {
                holder = QuestionRadioViewHolder(view)
            }
        }
        return holder
    }


    override fun getItemViewType(position: Int): Int {
        val qType = lvs.questions.get(position).tipo
        // Buena practica!: Retornar los layouts!
        when (qType) {
            1 -> {
                return R.layout.question_radio_row
            }
            2 -> {
                return R.layout.question_spinner_row
            }
            3 -> {
                return R.layout.question_num_row
            }
            5 -> {
                return R.layout.send_row
            }
            10 -> {
                return R.layout.section_row
            }
            15 -> {
                return R.layout.comment_row
            }
            else -> {
                return R.layout.question_radio_row
            }
        }

    }



    class QuestionNumViewHolder(val view: View, val myCustomEditTextListener: MyCustomEditTextListener) : RecyclerView.ViewHolder(view) {
        val answerEditText: EditText? = view.editText2
        val questionTextView: TextView? = view.textView_question2

        init {
            answerEditText?.addTextChangedListener(myCustomEditTextListener)
        }

    }

    inner class MyCustomEditTextListener : TextWatcher {

        var id: Int = 0

        fun updateId(id: Int) {
            this.id = id
        }

        override fun beforeTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
            checkMap.answers?.set(id, charSequence.toString())
            dataChequeo.put(lvs.questions.get(id).uid,charSequence.toString())
        }

        override fun afterTextChanged(p0: Editable?) {

        }
    }

    inner class QuestionRadioViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var questionTextView: TextView? = view.textView_question
        var radioGroup: RadioGroup? = view.radioGroup

        var id = 0

        fun updateId(id: Int) {
            this.id = id
        }

        init {
            radioGroup?.setOnCheckedChangeListener { rGroup, i ->
                val radioButton : RadioButton? = rGroup.findViewById<View>(i) as? RadioButton
                val textAns = radioButton?.text.toString()
                Log.d("sarasa",textAns)
                val currentQuestion = lvs.questions.get(adapterPosition)
                if (!onBind) {
                    if(textAns == "SI") {
                        if(!currentQuestion.isExpanded) {
                            lvs.questions.addAll(adapterPosition + 1, currentQuestion.childs)
                            notifyItemRangeInserted(adapterPosition + 1, currentQuestion.childs.size.or(0))
                            updateDataMapInsert(currentQuestion.childs)
                            updateCheckMapInsert(adapterPosition + 1,currentQuestion.childs.size.or(0))
                            currentQuestion.isExpanded=true
                        }
                    }
                    else {
                        if(textAns == "NO") {
                            if(currentQuestion.isExpanded) {
                                val cCount = childsCount(currentQuestion)
                                contractQuestion(currentQuestion)
                                notifyItemRangeRemoved(adapterPosition + 1, cCount)
                                updateDataMapDelete(currentQuestion.childs)
                                updateCheckMapDelete(adapterPosition + 1, cCount)
                                currentQuestion.isExpanded=false
                            }
                        }
                    }
                    if(currentQuestion.alertaRoja == textAns) {
                        mostrarAlertaRoja(currentQuestion.mensajeAlerta)
                        redAlerts.add(currentQuestion.uid.toString())
                        itemView.setBackgroundColor(Color.parseColor("#ff7043"))
                    }
                    else if(currentQuestion.alertaAmarilla == textAns) {
                        yellowAlerts.add(currentQuestion.uid.toString())
                        itemView.setBackgroundColor(Color.parseColor("#ffee58"))
                    }
                    else if(currentQuestion.alertaBlanca == textAns) {
                        whiteAlerts.add(currentQuestion.uid.toString())
                    }
                    else {
                        redAlerts.remove(currentQuestion.uid.toString())
                        yellowAlerts.remove(currentQuestion.uid.toString())
                        whiteAlerts.remove(currentQuestion.uid.toString())
                        itemView.setBackgroundColor(Color.WHITE)
                    }
                }
                Log.d("Radio", "ID: " + id + " Option " + radioButton?.text + " Selected")
                checkMap.answers?.set(adapterPosition, radioButton?.id.toString())
                dataChequeo.put(currentQuestion.uid,textAns)
                Log.d("Respuestas", dataChequeo.toString()+" Size: "+dataChequeo.size)
                Log.d("Contiene null: " , dataChequeo.containsValue("null").toString())
            }
        }

    }

    fun updateDataMapInsert(childs : ArrayList<Pregunta>) {
        for (c in childs) {
            dataChequeo.put(c.uid,"null")
        }
    }

    fun updateDataMapDelete(childs : ArrayList<Pregunta>) {
        for (c in childs) {
            dataChequeo.remove(c.uid)
        }
    }

    fun contractQuestion(question : Pregunta) {
        if(question.tipo == 1) {
            if (question.childs.size > 0) {
                for (child in question.childs) {
                    //Llamada recursiva
                    contractQuestion(child)
                    //Remuevo la pregunta de mi listado de respuestas
                    dataChequeo.remove(child.uid)
                    //La eliminio de las alertas si es que estaba
                    redAlerts.remove(child.uid.toString())
                    yellowAlerts.remove(child.uid.toString())
                    whiteAlerts.remove(child.uid.toString())
                }
                lvs.questions.removeAll(question.childs)
                question.isExpanded = false
            }
        }
        else {
            if(question.childsOpciones.size>0 && question.childsOpciones.get(question.selectedOption).size > 0) {
                for (child in question.childsOpciones.get(question.selectedOption)) {
                    //Llamada recursiva
                    contractQuestion(child)
                    //Remuevo la pregunta de mi listado de respuestas
                    dataChequeo.remove(child.uid)
                    //La eliminio de las alertas si es que estaba
                    redAlerts.remove(child.uid.toString())
                    yellowAlerts.remove(child.uid.toString())
                    whiteAlerts.remove(child.uid.toString())
                }
                lvs.questions.removeAll(question.childsOpciones.get(question.selectedOption))
                question.selectedOption = -1
            }
        }
    }

    fun childsCount(question: Pregunta) : Int {
        var cant = 0
        if(question.tipo == 1) {
            cant = question.childs.size
            if (cant == 0) {
                return 0
            } else {
                for (child in question.childs) {
                    if (child.isExpanded || child.tipo == 2)
                        cant += childsCount(child)
                }
            }
        }
        else {
            if(question.childsOpciones.size>0) {
                cant = question.childsOpciones.get(question.selectedOption).size
                if (cant == 0) {
                    return 0
                } else {
                    for (child in question.childsOpciones.get(question.selectedOption)) {
                        if (child.isExpanded || child.tipo == 2)
                            cant += childsCount(child)
                    }
                }
            }
        }
        return cant
    }

    fun updateCheckMapInsert(position: Int, count: Int) {
        Log.d("Update","Agregando "+count+" elementos desde la posicion "+position)
        for (i in 0..count-1) {
            checkMap.answers?.add(position,"")
        }
    }

    fun updateCheckMapDelete(position: Int, count: Int) {
        Log.d("Update","Eliminando "+count+" elementos desde la posicion "+position)
        for (i in 0..count-1) {
            checkMap.answers?.removeAt(position)
        }
    }

    fun mostrarAlertaRoja(mensaje:String = "Informar a medic@ de guardia") {
        val builder = AlertDialog.Builder(context, R.style.Theme_MaterialComponents_Light_Dialog_Alert)


        builder.setMessage(mensaje)
                .setTitle("Alerta Roja")


        builder.setPositiveButton("Ok", DialogInterface.OnClickListener { dialogInterface, i -> Log.d("Alerta", "Alerta") })


        val dialog = builder.create()


        dialog.show()


    }

    inner class QuestionSpinnerViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val questionTextView: TextView? = view.textView_spinner_question
        val spinnerView: Spinner? = view.spinner

        var questionId = 0

        fun updateId(id: Int) {
            this.questionId = id
        }

        init {
            spinnerView?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val currentQuestion = lvs.questions.get(adapterPosition)
                    val currentOption = currentQuestion.selectedOption
                    if(currentQuestion.childsOpciones.size > 0) {
                        if (!onBind) {
                            if (currentOption != position) {
                                // Escondo las preguntas de la seleccion anterior
                                if (currentOption != -1) {
                                    val cCount = childsCount(currentQuestion)
                                    contractQuestion(currentQuestion)
                                    notifyItemRangeRemoved(adapterPosition + 1, cCount)
                                    updateDataMapDelete(currentQuestion.childs)
                                    updateCheckMapDelete(adapterPosition + 1, cCount)
                                }


                                //Agrego preguntas de nueva seleccion
                                val hijos = currentQuestion.childsOpciones.get(position)

                                lvs.questions.addAll(adapterPosition + 1, hijos)
                                notifyItemRangeInserted(adapterPosition + 1, hijos.size)
                                updateDataMapInsert(ArrayList(hijos))
                                updateCheckMapInsert(adapterPosition + 1, hijos.size)
                            }
                            currentQuestion.selectedOption = position
                        }
                    }
                    checkMap.answers?.set(adapterPosition,position.toString())
                    dataChequeo.put(currentQuestion.uid,spinnerView?.selectedItem.toString())
                    Log.d("Respuestas", dataChequeo.toString())
                    Log.d("CheckMap", checkMap.answers.toString())
                }

            }
        }

    }




    inner class SectionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val section: TextView? = view.textView_section
    }

    inner class CommentViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val etComentario: EditText = view.edit_text_comentario

        init {
            etComentario.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(p0: Editable?) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    comment = p0.toString()
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }
            })
        }
    }
}








