package com.example.logilearnapp.ui.study

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.logilearnapp.R
import com.example.logilearnapp.database.CardDao
import com.example.logilearnapp.database.FirebaseCallback
import com.example.logilearnapp.ui.card.Card
import com.example.logilearnapp.ui.folder.CardViewFragment
import com.example.logilearnapp.ui.folder.Folder
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.FirebaseDatabase

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
    var cardIndex = 0

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
        return inflater.inflate(R.layout.fragment_study, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val resultBtn: MaterialButton = view.findViewById(R.id.getResultBtn)
        val layoutIcons : LinearLayout = view.findViewById(R.id.layout_vote_items)
        val topBar: MaterialToolbar = view.findViewById(R.id.study_mode_toolbar)
        val cardInput: TextView = view.findViewById(R.id.front_card_input)
        val cardResult:TextView = view.findViewById(R.id.back_card_result)
        val toolbarIcons: MaterialToolbar? = layoutIcons.findViewById(R.id.toolbar_icons)
        val itemAgain = toolbarIcons!!.menu.findItem(R.id.item_again)
        val itemMeh = toolbarIcons!!.menu.findItem(R.id.item_meh)
        val itemEasy = toolbarIcons!!.menu.findItem(R.id.item_easy)
        val viewItemAgain :View  = itemAgain.actionView!!
        val viewItemMeh :View = itemMeh.actionView!!
        val viewItemEasy :View = itemEasy.actionView!!

        val folder = arguments?.getParcelable<Folder>("folder")
        var cardIdList: ArrayList<String> = arrayListOf()
        if (folder != null) {
            cardIdList = folder.cardId
        }
        val cardDao = CardDao()
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val userId = cardDao.getUserIdSharedPreferences(requireContext())
        val numCards : TextView = view.findViewById(R.id.numOfCardShowed)
        cardDao.getCardsByIdList(object : FirebaseCallback{
            @SuppressLint("SetTextI18n")
            override fun onCallback(cardList: ArrayList<Card>) {
                cardInput.text = cardList[cardIndex].input
                cardResult.text = cardList[cardIndex].result
                numCards.text = "${cardIndex + 1} / ${cardList.size}"

                viewItemAgain.setOnClickListener { v ->
                    onContextItemSelected(itemAgain)
                    val scaleAnimation = AnimationUtils.loadAnimation(context, R.anim.scale_animation)
                    setIconAnimation( scaleAnimation,cardResult,layoutIcons,resultBtn)
                    v.startAnimation(scaleAnimation)
                    if((cardIndex+1)<cardList.size){
                        cardIndex +=1
                        cardInput.text = ""
                        Handler().postDelayed({
                            cardInput.text = cardList[cardIndex].input
                        }, 309)
                        cardResult.text = cardList[cardIndex].result
                        cardResult.visibility = TextView.GONE
                        numCards.text = "${cardIndex + 1} / ${cardList.size}"
                    }else{
                        Toast.makeText(requireContext(), "Estudio finalizado", Toast.LENGTH_SHORT).show()
                        replaceFragment(requireActivity(), CardViewFragment())

                        //aquí se guardan las puntuaciones
                    }

                }
                viewItemMeh.setOnClickListener { v ->

                    onContextItemSelected(itemMeh)
                    val scaleAnimation = AnimationUtils.loadAnimation(context, R.anim.scale_animation)
                    setIconAnimation( scaleAnimation,cardResult,layoutIcons,resultBtn)
                    v.startAnimation(scaleAnimation)
                    if((cardIndex+1)<cardList.size){
                        cardIndex +=1
                        cardInput.text = ""
                        Handler().postDelayed({
                            cardInput.text = cardList[cardIndex].input
                        }, 309)
                        cardResult.text = cardList[cardIndex].result
                        cardResult.visibility = TextView.GONE
                        numCards.text = "${cardIndex + 1} / ${cardList.size}"
                    }else{
                        Toast.makeText(requireContext(), "Estudio finalizado", Toast.LENGTH_SHORT).show()
                        replaceFragment(requireActivity(), CardViewFragment())
                        //aquí se guardan las puntuaciones
                    }
                }
                viewItemEasy.setOnClickListener { v ->

                    onContextItemSelected(itemEasy)
                    val scaleAnimation = AnimationUtils.loadAnimation(context, R.anim.scale_animation)
                    setIconAnimation( scaleAnimation,cardResult,layoutIcons,resultBtn)
                    v.startAnimation(scaleAnimation)
                    if((cardIndex+1)<cardList.size){
                        cardIndex +=1
                        cardInput.text = ""
                        Handler().postDelayed({
                            cardInput.text = cardList[cardIndex].input
                        }, 309)
                        cardResult.text = cardList[cardIndex].result
                        cardResult.visibility = TextView.GONE
                        numCards.text = "${cardIndex + 1} / ${cardList.size}"

                    }else{
                        Toast.makeText(requireContext(), "Estudio finalizado", Toast.LENGTH_SHORT).show()
                        replaceFragment(requireActivity(), CardViewFragment())
                        //aquí se guardan las puntuaciones
                    }
                }

            }

            override fun onFolderCallback(folderList: ArrayList<Folder>) {
                //no hago nada
            }
        }, firebaseDatabase.reference,
            userId!!, cardIdList)

        topBar.setNavigationOnClickListener {

            replaceFragment(requireActivity(), CardViewFragment())
        }

        resultBtn.setOnClickListener{
            resultBtn.visibility = MaterialButton.GONE
            cardResult.visibility = TextView.VISIBLE
            layoutIcons.visibility = LinearLayout.VISIBLE

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
    private fun setIconAnimation(scaleAnimation: Animation, cardResult :TextView, layoutIcons :LinearLayout, resultBtn : MaterialButton){
        scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }
            override fun onAnimationEnd(animation: Animation?) {
                Handler().postDelayed({
                  changeVisibilityWhenIconPressed(cardResult, layoutIcons, resultBtn)
                }, 100)
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }
        })
    }
    private fun changeVisibilityWhenIconPressed(cardResult :TextView, layoutIcons :LinearLayout, resultBtn : MaterialButton ){
        cardResult.visibility = TextView.GONE
        layoutIcons.visibility = LinearLayout.GONE
        resultBtn.visibility = MaterialButton.VISIBLE
    }


}