package com.example.logilearnapp.ui.profile

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.logilearnapp.R
import com.example.logilearnapp.UserData
import com.example.logilearnapp.database.FirebaseCallback
import com.example.logilearnapp.database.UserDao
import com.example.logilearnapp.ui.card.Card
import com.example.logilearnapp.ui.common.HomeFragment
import com.example.logilearnapp.ui.folder.Folder
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


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
    lateinit var editPassword : TextInputLayout
    lateinit var deleteAccountBtn:MaterialButton
    lateinit var logoutBtn:MaterialButton

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
        topBar.setNavigationOnClickListener {

           replaceFragment(requireActivity(), HomeFragment())
        }
        val sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("id", "")
        val databaseReference = FirebaseDatabase.getInstance().reference
        val userDao = UserDao()
        editPassword = view.findViewById(R.id.edit_password_layout)
        editSurname = view.findViewById(R.id.edit_surname_layout)
        editEmail = view.findViewById(R.id.edit_email_layout)
        editName= view.findViewById(R.id.edit_name_layout)
        getUserData(userDao, databaseReference, userId.toString())
        
        logoutBtn = view.findViewById(R.id.logout_btn)
        deleteAccountBtn = view.findViewById(R.id.delete_account_btn)
        logoutBtn.setOnClickListener{
            // TODO: implementar logout 
        }
        //implementado
        deleteAccountBtn.setOnClickListener{
            userDao.deleteUser(databaseReference, userId.toString())
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
    private fun getUserData(userDao:UserDao, databaseReference: DatabaseReference, userId:String ) {
      
        userDao.getUser(object : FirebaseCallback{
            override fun onCallback(cardList: ArrayList<Card>) {

            }
            override fun onSingleUserCallback(user: UserData) {
                editName.editText!!.setText(user.name)
                editSurname.editText!!.setText(user.surname)
                editEmail.editText!!.setText(user.email)
                editPassword.editText!!.setText(user.password)
            }

            override fun onFolderCallback(folderList: ArrayList<Folder>) {

            }

        },databaseReference, userId!!)

    }

    override fun onDestroyView() {
        super.onDestroyView()

    }
}