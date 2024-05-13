package com.example.logilearnapp.ui.study

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.logilearnapp.R
import com.example.logilearnapp.ui.common.HomeFragment
import com.example.logilearnapp.ui.folder.CardViewFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StudyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StudyFragment : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_study, container, false)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val resultBtn: MaterialButton = view.findViewById(R.id.getResultBtn)
        val layoutIcons : LinearLayout = view.findViewById(R.id.layout_vote_items)
        val topBar: MaterialToolbar = view.findViewById(R.id.study_mode_toolbar)
        val cardResult:TextView = view.findViewById(R.id.back_card_result)
        val toolbarIcons: MaterialToolbar? = layoutIcons.findViewById(R.id.toolbar_icons)
        topBar.setNavigationOnClickListener {

            replaceFragment(requireActivity(), CardViewFragment())
        }
        resultBtn.setOnClickListener{
            resultBtn.visibility = MaterialButton.GONE
            cardResult.visibility = TextView.VISIBLE
            layoutIcons.visibility = LinearLayout.VISIBLE

        }
        toolbarIcons?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_again -> {
                    cardResult.visibility = TextView.GONE
                    layoutIcons.visibility = LinearLayout.GONE
                    resultBtn.visibility = MaterialButton.VISIBLE
                    // Acción cuando se hace clic en el primer ítem del menú
                    true
                }

                R.id.item_meh -> {
                    cardResult.visibility = TextView.GONE
                    layoutIcons.visibility = LinearLayout.GONE
                    resultBtn.visibility = MaterialButton.VISIBLE


                    true
                }

                R.id.item_easy -> {
                    cardResult.visibility = TextView.GONE
                    layoutIcons.visibility = LinearLayout.GONE
                    resultBtn.visibility = MaterialButton.VISIBLE

                    // Acción cuando se hace clic en el segundo ítem del menú
                    true
                }
                // Agrega más casos para otros elementos del menú si es necesario
                else -> false
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
         * @return A new instance of fragment StudyFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StudyFragment().apply {
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
}