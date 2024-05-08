package com.example.logilearnapp

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.set
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.logilearnapp.ui.common.HomeFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
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
    private fun getUserData(name_input: TextInputEditText,surname_input: TextInputEditText, email_input: TextInputEditText,password_input: TextInputEditText ) {
        val sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("id", "")

        val userRef = FirebaseDatabase.getInstance().reference.child("user").child(userId!!)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue(String::class.java)
                var surname = snapshot.child("surname").getValue(String::class.java)
                var email = snapshot.child("email").getValue(String::class.java)
                var password = snapshot.child("password").getValue(String::class.java)

                if (name != null && surname != null) {
                    var new_editable  = Editable.Factory.getInstance().newEditable(name)
                    name_input.text = new_editable
                    new_editable = Editable.Factory.getInstance().newEditable(surname)
                    surname_input.text = new_editable
                    new_editable = Editable.Factory.getInstance().newEditable(email)
                    email_input.text = new_editable
                    new_editable = Editable.Factory.getInstance().newEditable(password)
                    password_input.text = new_editable

                    //aquí muestro los datos que ya están
                  //  binding.textFirstName.text = firstName
                  //  binding.textUsername.text = formattedUsername
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DatabaseError", "Error: ${error.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }
}