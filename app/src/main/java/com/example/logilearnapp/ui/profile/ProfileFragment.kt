package com.example.logilearnapp.ui.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.logilearnapp.R
import com.example.logilearnapp.data.UserData
import com.example.logilearnapp.data.Label
import com.example.logilearnapp.database.FirebaseCallback
import com.example.logilearnapp.repository.UserDao
import com.example.logilearnapp.ui.auth.Login
import com.example.logilearnapp.ui.card.Card
import com.example.logilearnapp.ui.common.HomeFragment
import com.example.logilearnapp.data.Folder
import com.example.logilearnapp.util.Validator
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var editName : TextInputLayout
    lateinit var editEmail : TextInputLayout
    lateinit var editSurname : TextInputLayout
    lateinit var deleteAccountBtn:MaterialButton
    lateinit var logoutBtn:MaterialButton
    lateinit var hashedPassword :String
    lateinit var currentUser: UserData
    lateinit var emailList: ArrayList<String>
    lateinit var welcomeText : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val topBar: MaterialToolbar = view.findViewById(R.id.topAppBarCard_profile)
        val sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("id", "")
        editSurname = view.findViewById(R.id.edit_surname_layout)
        editEmail = view.findViewById(R.id.edit_email_layout)
        editName= view.findViewById(R.id.edit_name_layout)
        hashedPassword = ""
        currentUser = UserData()
        emailList = arrayListOf()
        welcomeText = view.findViewById(R.id.idBienvenida)

        val databaseReference = FirebaseDatabase.getInstance().reference
        topBar.setNavigationOnClickListener {

           replaceFragment(requireActivity(), HomeFragment())
        }
        val saveOption = topBar.menu?.findItem(R.id.item_save)

        val userDao = UserDao()

        getUserData(userDao, databaseReference, userId.toString())



        saveOption?.setOnMenuItemClickListener {
            val userRepo = UserDao()
            //dialog
            if(editEmail.error.isNullOrBlank() && editName.error.isNullOrBlank() && editSurname.error.isNullOrBlank()){
                if(currentUser.name.toString() != editName.editText!!.text.toString() || currentUser.email.toString() != editEmail.editText!!.text.toString() || currentUser.surname.toString() != editSurname.editText!!.text.toString() ){
                    val user = UserData(userId,editEmail.editText!!.text.toString(),editName.editText!!.text.toString(), editSurname.editText!!.text.toString(), hashedPassword)
                    MaterialAlertDialogBuilder(requireContext())
                        //hacer validaciones en editar perfil
                        .setTitle("Actualizar perfil")
                        .setMessage("¿Deseas modificar tu información personal?")
                        .setPositiveButton("Guardar cambios") { dialog, which ->

                            userRepo.updateUser(databaseReference, userId!!, user)
                            dialog.dismiss()
                            Toast.makeText(requireContext(), "Información actualizada", Toast.LENGTH_SHORT).show()
                            replaceFragment(requireActivity(), HomeFragment())
                        }
                        .setNegativeButton("Cancelar") { dialog, which ->
                            Toast.makeText(requireContext(), "Cancelado", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            dialog.cancel()

                        }.create().apply {

                            show()
                        }
                }else{
                    replaceFragment(requireActivity(), HomeFragment())
                }


            }else{
                MaterialAlertDialogBuilder(requireContext())
                    //hacer validaciones en editar perfil
                    .setTitle("Campos no válidos")
                    .setMessage("Tienes campos incorrectos, rellénalos para poder guardar los cambios")
                    .setPositiveButton("De acuerdo") { dialog, which ->
                        dialog.dismiss()

                    }.create().apply {
                show()
            }
            }

            true
        }
        logoutBtn = view.findViewById(R.id.logout_btn)
        deleteAccountBtn = view.findViewById(R.id.delete_account_btn)
        //implementado
        deleteAccountBtn.setOnClickListener{
            MaterialAlertDialogBuilder(requireContext())
                //hacer validaciones en editar perfil
                .setTitle("Eliminar cuenta")
                .setMessage("¿Deseas eliminar la cuenta? No podrás recuperarla")
                .setPositiveButton("Continuar") { dialog, which ->
                    sharedPreferences.edit().clear().apply()
                    userDao.deleteUser(databaseReference, userId.toString())
                   //acción
                    dialog.dismiss()
                    Toast.makeText(requireContext(), "Cuenta eliminada", Toast.LENGTH_SHORT).show()
                    replaceFragment(requireActivity(), HomeFragment())
                }
                .setNegativeButton("Cancelar") { dialog, which ->
                    Toast.makeText(requireContext(), "Cancelado", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    dialog.cancel()

                }.create().apply {

                    show()
                }
        }
        logoutBtn.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                //hacer validaciones en editar perfil
                .setTitle("Cerrar sesión")
                .setMessage("¿Deseas cerrar sesión?")
                .setPositiveButton("Continuar") { dialog, which ->
                    dialog.dismiss()
                    Toast.makeText(requireContext(), "Has cerrado sesión", Toast.LENGTH_SHORT).show()
                    sharedPreferences.edit().clear().apply()
                    val activity = requireActivity()
                    val intent = Intent(activity, Login::class.java)
                    startActivity(intent)
                    activity.supportFragmentManager.popBackStack()
                    activity.finish()
                }
                .setNegativeButton("Cancelar") { dialog, which ->
                    Toast.makeText(requireContext(), "Cancelado", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    dialog.cancel()

                }.create().apply {

                    show()
                }


        }
       
        
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun replaceFragment(activity: FragmentActivity, fragment: Fragment) {
        //necesita el activity actual para poder acceder a su contenido
        val fragmentManager: FragmentManager = activity.supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.viewerFragment, fragment)
        fragmentTransaction.commit()
    }
    private fun getUserData(userDao: UserDao, databaseReference: DatabaseReference, userId:String ) {

        userDao.getUser(object : FirebaseCallback{
            override fun onCallback(cardList: ArrayList<Card>) {
            }
            override fun onLabelNameCallback(cardList: ArrayList<Label>) {

            }
            @SuppressLint("SetTextI18n")
            override fun onSingleUserCallback(user: UserData) {

                currentUser.id = user.id
                currentUser.name = user.name
                currentUser.email = user.email
                currentUser.surname = user.surname

                editName.editText!!.setText(user.name)
                editSurname.editText!!.setText(user.surname)
                editEmail.editText!!.setText(user.email)
                hashedPassword = user.password.toString()
                welcomeText.text = "¡Hola, ${user.name}!"


                val emailList: ArrayList<String> = arrayListOf()
                userDao.getAllUsers(object : FirebaseCallback {
                        override fun onCallback(cardList: ArrayList<Card>) {
                        }
                        override fun onLabelNameCallback(cardList: ArrayList<Label>) {
                        }
                        override fun onSingleUserCallback(user: UserData) {
                        }
                        override fun onFolderCallback(folderList: ArrayList<Folder>) {
                        }
                        override fun onUsersCallback(userList: ArrayList<UserData>) {
                            for(userData in userList){
                                userData.email?.let { emailList.add(it) }
                            }

                            editEmail.editText!!.addTextChangedListener(object : TextWatcher {
                                override fun afterTextChanged(s: Editable?) {
                                    val email = s.toString()
                                    var isEmailRepeated: Boolean = emailList.contains(email)
                                    if (!Validator.isValidEmail(email)) {
                                        editEmail.error = "Email incorrecto "
                                        //si el usuario actual no cambia no verifico si está porque sí lo está (registro y aquí)
                                    }else if((currentUser.email != email) && isEmailRepeated){
                                        editEmail.error = "Email en uso"
                                    } else {
                                        editEmail.error = null
                                    }
                                }
                                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                            })

                            editName.editText!!.addTextChangedListener(object : TextWatcher {
                                override fun afterTextChanged(s: Editable?) {
                                    val name = s.toString()
                                    if (!Validator.isValidName(name)) {
                                        editName.error = "Nombre no válido"
                                    }else {
                                        editName.error = null // Remueve el error si el email es válido
                                    }
                                }

                                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                            })
                            editSurname.editText!!.addTextChangedListener(object : TextWatcher {
                                override fun afterTextChanged(s: Editable?) {
                                    val surname = s.toString()
                                    if (!Validator.isValidSurname(surname)) {
                                        editSurname.error = "Apellido no válido"
                                    }else {
                                        editSurname.error = null // Remueve el error si el email es válido
                                    }
                                }

                                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                            })


                        } },databaseReference)


            }

            override fun onFolderCallback(folderList: ArrayList<Folder>) {
            }
            override fun onUsersCallback(userList: ArrayList<UserData>) {
            }

        },databaseReference, userId)

    }


    override fun onDestroyView() {
        super.onDestroyView()

    }


}