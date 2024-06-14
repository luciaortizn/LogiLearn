package com.example.logilearnapp.ui.card
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.logilearnapp.ui.common.MainActivity
import com.example.logilearnapp.R
import com.example.logilearnapp.data.CardWithDifficulty
import com.example.logilearnapp.data.Difficulty
import com.example.logilearnapp.data.Label
import com.example.logilearnapp.data.UserData
import com.example.logilearnapp.util.ConfigUtils
import com.example.logilearnapp.data.TranslateRequest
import com.example.logilearnapp.database.FirebaseCallback
import com.example.logilearnapp.repository.FolderDao
import com.example.logilearnapp.data.Folder
import com.example.logilearnapp.viewmodel.EmptyEditableCardViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.properties.Delegates

class EmptyEditableCard : AppCompatActivity() {
    private lateinit var firebaseDatabase: FirebaseDatabase
    //realtime database
    private lateinit var databaseReference: DatabaseReference
   private lateinit var save_btn: ExtendedFloatingActionButton
   private lateinit var input_text: EditText
   private lateinit var result_text: EditText
   private lateinit var translationBtn: MaterialButton
    private lateinit var viewModel : EmptyEditableCardViewModel
    private lateinit var folderDao: FolderDao
    private lateinit var selectedFolder: Folder
    private lateinit var resetChip: Chip
    private lateinit var folderChip: Chip
    private var isNewFolderSelected by Delegates.notNull<Boolean>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty_editable_card)
        val topBar: MaterialToolbar = findViewById(R.id.topAppBarCard)

        //hace que el botón x se produzca un retroceso en la activity
        topBar.setNavigationOnClickListener {

            onBackPressedDispatcher.onBackPressed()
        }
        //gestión del botón de guardar y la llamada a la base de datos
        save_btn = findViewById(R.id.save_card_button)
        input_text = findViewById(R.id.Input)
        result_text = findViewById(R.id.Result)
        translationBtn = findViewById(R.id.Translate)
        folderChip = findViewById(R.id.chip_folder)
        resetChip = findViewById(R.id.chip_reset)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("user")
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("id", "")
        val databaseReference = FirebaseDatabase.getInstance().reference.child("user")
        folderDao = FolderDao()
         isNewFolderSelected = false
        //guardo esto
        selectedFolder = Folder()
        translationBtn.isEnabled = false
        folderChip.text = "Carpetas"
        folderChip.setOnClickListener{
            if(result_text.text.isEmpty() || input_text.text.isEmpty()){
                MaterialAlertDialogBuilder(this)
                    //hacer validaciones en editar perfil
                    .setTitle("Campos vacíos")
                    .setMessage("Rellena todos los campos para poder añadir una carpeta")
                    .setPositiveButton("De acuerdo") { dialog, which ->
                        dialog.dismiss()

                    }.create().apply {
                        show()
                    }

            }else {
                //guardo en la variable
                folderDao.getFoldersByUser(object : FirebaseCallback {
                    override fun onCallback(cardList: ArrayList<Card>) {
                    }
                    override fun onLabelNameCallback(cardList: ArrayList<Label>) {
                    }
                    override fun onSingleUserCallback(user: UserData) {
                    }
                    //llamo al callback y efectúo resto
                    override fun onFolderCallback(folderList: ArrayList<Folder>) {

                        showAddFolderDialog(folderList, userId!!, firebaseDatabase)

                        //add all


                    }

                    override fun onUsersCallback(userList: ArrayList<UserData>) {
                    }
                },firebaseDatabase.reference,userId!!)
            }
            //añqdir a carpeta
        }
        resetChip.setOnClickListener{
            input_text.text.clear()
            result_text.text.clear()
            translationBtn.isEnabled = false
        }

        save_btn.setOnClickListener() {
            if ( input_text.text.isNotEmpty() && result_text.text.isNotEmpty()) {

                databaseReference.orderByChild("id").equalTo(userId)
                    .addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (userSnapshot in dataSnapshot.children) {

                                    //mete el id como key:
                                    val id =
                                        databaseReference.child(userId.toString()).child("cards")
                                            .push().key

                                    // val userData = com.example.logilearnapp.data.UserData(id, email, name, surname,password)

                                    val userData = userSnapshot.getValue(UserData::class.java)
                                    if (userData != null) {
                                        val cardData = HashMap<String, Any>()
                                        cardData["id"] = id.toString()
                                        cardData["input"] = input_text.text.toString()
                                        cardData["result"] = result_text.text.toString()
                                        // Obtenemos una referencia al nodo del usuario actual
                                        val currentUserRef =
                                            databaseReference.child(userId.toString())
                                                .child("cards")

                                        // Añadimos el nodo "card" con sus campos hijos al mismo nivel que el nodo "id"
                                        // databaseReference.child(id!!).setValue(userData)
                                        currentUserRef.child(id!!).setValue(cardData)
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {

                                                    Toast.makeText(
                                                        this@EmptyEditableCard,
                                                        "Información guardada.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    //aquí añadiría el folder nuevo con la variable con texto
                                                    if(selectedFolder.dataTitle.isNotEmpty()){
                                                        //nuevo objeto folder
                                                        val currentCard =  CardWithDifficulty(id.toString(), Difficulty.EASY, 0)
                                                        selectedFolder.cardId!!.add(currentCard)
                                                        if(isNewFolderSelected){
                                                            folderDao.addFolder(firebaseDatabase.reference, userId!!, selectedFolder, this@EmptyEditableCard)
                                                          // folderDao.addNewCardIdValue(firebaseDatabase.reference,userId!!,currentCard,selectedFolder.id, this@EmptyEditableCard, )

                                                        }else{

                                                           folderDao.addNewCardIdValue(firebaseDatabase.reference,userId!!,currentCard,selectedFolder.id, this@EmptyEditableCard, )
                                                        }
                                                    }

                                                    val intent = Intent(
                                                        this@EmptyEditableCard,
                                                        MainActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                } else {
                                                    Toast.makeText(
                                                        this@EmptyEditableCard,
                                                        "Error, inténtalo más tarde.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }


                                    }
                                }


                            } else {
                                // el usuario no se encuentra se muestra alert

                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {

                        }
                    })



            } else {
                Toast.makeText(this, "No has rellenado todos los campos", Toast.LENGTH_SHORT).show()
            }

        }
        viewModel = ViewModelProvider(this)[EmptyEditableCardViewModel::class.java]
        val textInputLayout: TextInputLayout = findViewById(R.id.menu)
        val autoCompleteTextView: AutoCompleteTextView = textInputLayout.editText as AutoCompleteTextView
        autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            // Obtén el texto del elemento seleccionado
            val selectedItem = parent.getItemAtPosition(position).toString()
            if(selectedItem.isNotEmpty()){
                translationBtn.isEnabled = true
            }
            val itemCountry = getRequiredLanguageName(selectedItem)
            //aquí obtengo el id del elemento de la lista de idiomas
            //(requestBody: TranslateRequest,callback: TranslationCallback, apiKey:String
            val requestBody = TranslateRequest(listOf<String>( input_text.text.toString()), itemCountry.toString())
            if(input_text.text.isNotEmpty()){
                translationBtn.setOnClickListener(){
                    viewModel.postTranslation(requestBody, { it: String? ->
                        // Maneja la traducción recibida
                        it?.let { translation ->
                            if(translation.isEmpty() || translation.isBlank()){
                                result_text.setText("")
                                Toast.makeText(this, "No se encuentra traducción", Toast.LENGTH_SHORT).show()
                            }else {
                                result_text.setText(translation)
                            }
                        } ?: run {
                            Toast.makeText(this@EmptyEditableCard, "Error al obtener la traducción", Toast.LENGTH_SHORT).show()
                        }
                    }, ConfigUtils.getDeeplApiKey(this).toString())
                }
            }

        }

    }
    private fun getRequiredLanguageName(selectedText:String):String?{
        val regex = Regex("\\((.*?)\\)")
        val matchResult = regex.find(selectedText)
        return matchResult?.groupValues?.get(1)
    }
    fun showAddFolderDialog(folderList: ArrayList<Folder>, id: String, firebaseDatabase: FirebaseDatabase){
        val folderTitleList :ArrayList<String> = arrayListOf()
        for (folder in folderList ){
            folderTitleList.add(folder.dataTitle)
        }
        val  folders = arrayOfNulls<CharSequence>(folderTitleList.size)
        folderTitleList.forEachIndexed { index, title ->
            folders[index] = title
        }
        var selectedItem = -1 // Elemento seleccionado inicialmente
        //configurar el texto de addfolder
        val dialogView = LayoutInflater.from(this).inflate(R.layout.add_folder_layout, null)
        val textNoFolders  = dialogView.findViewById<TextView>(R.id.txtMessageNoFolders)
        val name = dialogView.findViewById<TextInputLayout>(R.id.inputLayoutAddFolder)
        var selectedFolderText = ""
        if(folderTitleList.isEmpty()){
            textNoFolders.text = this.getString(R.string.vaya_todav_a_no_tienes_ning_na_carpeta)
        }else{
            textNoFolders.text = ""
        }
        val dialog=  MaterialAlertDialogBuilder(this)
            .setTitle("Añadir a carpeta")
            .setView(dialogView)
            .setSingleChoiceItems(folders, selectedItem) { dialog, which ->
                selectedItem = which
                selectedFolderText = folders[which].toString()
            }
            .setPositiveButton("Seleccionar") { dialog, which ->
                if (selectedItem != -1) {
                  selectedFolderText = folderList[selectedItem].dataTitle
                    selectedFolder = folderList[selectedItem]
                    folderChip.text = selectedFolder.dataTitle
                    isNewFolderSelected = false

                } else if (name.editText!!.text.isNotEmpty() && !name.editText!!.isActivated && !name.editText!!.isSelected) {
                    val listToAdd = ArrayList<CardWithDifficulty>()
                    selectedFolderText = name.editText!!.text.toString()
                    selectedFolder = Folder("", "false", name.editText!!.text.toString(), listToAdd)
                    folderChip.text =  name.editText!!.text.toString()
                    isNewFolderSelected = true

                    selectedFolder = Folder("", "false", name.editText!!.text.toString(), listToAdd)
                    textNoFolders.text = ""
                } else {
                    Toast.makeText(this, "No has seleccionado una carpeta ni introducido un nombre", Toast.LENGTH_SHORT).show()
                }

                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, which ->
                dialog.dismiss()
                dialog.cancel()
                selectedFolder = Folder()
                folderChip.text = "Carpetas"
            }.create().apply {
                setOnDismissListener {

                }
                show() }
    }
}
