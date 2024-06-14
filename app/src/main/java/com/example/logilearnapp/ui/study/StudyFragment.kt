package com.example.logilearnapp.ui.study

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
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
import androidx.lifecycle.lifecycleScope
import com.example.logilearnapp.R
import com.example.logilearnapp.data.UserData
import com.example.logilearnapp.data.CardWithDifficulty
import com.example.logilearnapp.data.Difficulty
import com.example.logilearnapp.data.Label
import com.example.logilearnapp.repository.CardDao
import com.example.logilearnapp.database.FirebaseCallback
import com.example.logilearnapp.repository.FolderDao
import com.example.logilearnapp.ui.card.Card
import com.example.logilearnapp.ui.folder.CardViewFragment
import com.example.logilearnapp.data.Folder
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

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
    private var repeatIndex by Delegates.notNull<Int>()
    private var repeatAuxIndex by Delegates.notNull<Int>()

    var cardIndex = 0
    private lateinit var numCards: TextView
    private lateinit var cardResult: TextView
    private lateinit var currentCardWithDifficulty: CardWithDifficulty
    private lateinit var  viewItemAgain :View
    private lateinit var viewItemMeh :View
    private lateinit var viewItemEasy :View
    private lateinit var folderDao: FolderDao
    private lateinit var cardDao: CardDao
    private lateinit var userId: String
    private lateinit var firebaseDatabase: FirebaseDatabase


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
        cardResult = view.findViewById(R.id.back_card_result)
        val toolbarIcons: MaterialToolbar? = layoutIcons.findViewById(R.id.toolbar_icons)
        val itemAgain = toolbarIcons!!.menu.findItem(R.id.item_again)
        val itemMeh = toolbarIcons.menu.findItem(R.id.item_meh)
        val itemEasy = toolbarIcons.menu.findItem(R.id.item_easy)
         viewItemAgain   = itemAgain.actionView!!
         viewItemMeh= itemMeh.actionView!!
         viewItemEasy = itemEasy.actionView!!

        val folder = arguments?.getParcelable<Folder>("folder")

        var cardIdList: ArrayList<CardWithDifficulty>? = arrayListOf()
        if (folder != null) {

            cardIdList = folder.cardId!!
            cardDao = CardDao()
            firebaseDatabase = FirebaseDatabase.getInstance()
            userId = cardDao.getUserIdSharedPreferences(requireContext()).toString()
            numCards = view.findViewById(R.id.numOfCardShowed)
            folderDao =  FolderDao()
            for(cards in folder.cardId!!){
                cards.repeated =0
            }
            folderDao.updateFolder(firebaseDatabase.reference, userId, folder!!, requireContext())
        }

        //modo estudio:
        /* obtengo el botón que se le da y el folder en el que se está
        * */
        resultBtn.setOnClickListener{
            resultBtn.visibility = MaterialButton.GONE
            cardResult.visibility = TextView.VISIBLE
            layoutIcons.visibility = LinearLayout.VISIBLE

        }
        cardDao.getCardsByIdList(object : FirebaseCallback{

            @SuppressLint("SetTextI18n")
            override fun onCallback(cardList: ArrayList<Card>) {
                val auxList= cardList
                var repeatList: ArrayList<Int> = arrayListOf()
                var repeatAuxList:ArrayList<Int> = arrayListOf()
                var roundMeh: Int =0
                var roundBad: Int =0
                repeatIndex  =0
                repeatAuxIndex =0

                cardInput.text = auxList[cardIndex].input
                cardResult.text = auxList[cardIndex].result
                numCards.text = "Tarjeta ${cardIndex + 1} de ${auxList.size}"
                //0+1 de 5
                 currentCardWithDifficulty =  CardWithDifficulty()
                //repito hasta que la lista no se vacíe

                viewItemAgain.setOnClickListener { v ->

                    onContextItemSelected(itemAgain)
                     val scaleAnimation = AnimationUtils.loadAnimation(context, R.anim.scale_animation)
                    setIconAnimation( scaleAnimation,cardResult,layoutIcons,resultBtn)
                    v.startAnimation(scaleAnimation)
                    if((cardIndex+1)<auxList.size){
                          cardIndex +=1
                          cardInput.text = ""
                          Handler().postDelayed({
                              cardInput.text = auxList[cardIndex].input
                          }, 309)
                          cardResult.text = auxList[cardIndex].result
                          cardResult.visibility = TextView.GONE
                          numCards.text = "Tarjeta $cardIndex de ${auxList.size}"
                          //modo estudio
                          //encuentro el elemento current
                          for(idList in folder!!.cardId.orEmpty()){
                              if(idList.cardId == auxList[cardIndex].id) {
                                  currentCardWithDifficulty = idList

                              }
                          }
                          //algoritmon que suma y cambia de estado
                          studyAlgorithm(viewItemAgain,currentCardWithDifficulty,folderDao,
                              userId.toString(), firebaseDatabase.reference, folder.id )
                        //si ya lo ha repetido 3 veces paro
                        if(!repeatList.contains(cardIndex-1)){
                            repeatList.add(cardIndex-1)
                        }
                        if(currentCardWithDifficulty.repeated>=4){
                            //ya se verá
                        }
                          //si ya lo ha repetido 3 veces paro

                    }else if(((cardIndex+1) == auxList.size)&& repeatList.isEmpty() ){
                        Toast.makeText(requireContext(), "Estudio finalizado", Toast.LENGTH_SHORT).show()
                        replaceFragment(requireActivity(), CardViewFragment())

                    }else if(repeatList.isNotEmpty() && ((cardIndex+1) == auxList.size) ){
                        //posiciono el próximo elemento en -1 de la posicion de repeat
                        cardIndex = repeatList[0]
                    }else if((cardIndex+1)>=auxList.size){
                        roundBad ++
                    }
                    if(roundBad >=1){
                        optionManager(repeatList, repeatAuxList, cardInput, auxList,folder!!, false)
                    }

                }
                  viewItemMeh.setOnClickListener { v ->

                      onContextItemSelected(itemMeh)
                      val scaleAnimation = AnimationUtils.loadAnimation(context, R.anim.scale_animation)
                      setIconAnimation( scaleAnimation,cardResult,layoutIcons,resultBtn)
                      v.startAnimation(scaleAnimation)
                      if((cardIndex+1)<auxList.size){
                          cardIndex +=1
                          cardInput.text = ""
                          Handler().postDelayed({
                              cardInput.text = auxList[cardIndex].input
                          }, 309)
                          cardResult.text = auxList[cardIndex].result
                          cardResult.visibility = TextView.GONE
                          numCards.text = "Tarjeta ${cardIndex+1} de ${auxList.size}"
                          for(idList in folder!!.cardId.orEmpty() ){
                              if(idList.cardId == auxList[cardIndex].id) {
                                  currentCardWithDifficulty = idList
                              }
                          }
                          Log.d("indiceCard", cardIndex.toString())
                          //si se ha acertado la tarjeta que habia en repeat se quita

                          studyAlgorithm(viewItemMeh,currentCardWithDifficulty,folderDao,
                              userId.toString(), firebaseDatabase.reference, folder.id )
                          //si ya lo ha repetido 3 veces paro
                         if(!repeatList.contains(cardIndex-1)){
                             repeatList.add(cardIndex-1)
                         }
                          if(currentCardWithDifficulty.repeated>=2){
                             //ya se verá
                          }
                      }else if(((cardIndex+1) == auxList.size)&& repeatList.isEmpty() ){
                          Toast.makeText(requireContext(), "Estudio finalizado", Toast.LENGTH_SHORT).show()
                          replaceFragment(requireActivity(), CardViewFragment())

                      }else if(repeatList.isNotEmpty() && ((cardIndex+1) == auxList.size) ){
                          //posiciono el próximo elemento en -1 de la posicion de repeat
                          cardIndex = repeatList[0]
                      }else if((cardIndex+1)>=auxList.size){
                          roundMeh ++
                      }

                    if(roundMeh >=1){
                        optionManager(repeatList, repeatAuxList, cardInput, auxList,folder!!, false)
                    }

                  }

                  viewItemEasy.setOnClickListener { v ->
                      onContextItemSelected(itemEasy)
                      val scaleAnimation =
                          AnimationUtils.loadAnimation(context, R.anim.scale_animation)
                      setIconAnimation(scaleAnimation, cardResult, layoutIcons, resultBtn)
                      v.startAnimation(scaleAnimation)

                      //si hay cartas en la baraja hago esto
                      if ((cardIndex + 1) < auxList.size) {
                          //comprobar si hay repeatlist
                          cardIndex += 1
                          cardInput.text = ""
                          Handler().postDelayed({
                              cardInput.text = auxList[cardIndex].input
                          }, 309)
                          cardResult.text = auxList[cardIndex].result
                          cardResult.visibility = TextView.GONE
                          numCards.text = "Tarjeta ${cardIndex + 1} de ${auxList.size}"
                          //encuentro el elemento current
                          for (idList in folder!!.cardId.orEmpty()) {
                              if (idList.cardId == auxList[cardIndex - 1].id) {
                                  currentCardWithDifficulty = idList
                              }
                          }
                          Handler().postDelayed({
                              studyAlgorithm(viewItemEasy,currentCardWithDifficulty,folderDao, userId.toString(), firebaseDatabase.reference, folder!!.id )
                          },409)
                          //aquí iría unos condicionales para repetir
                          if(currentCardWithDifficulty.repeated>=1 || currentCardWithDifficulty.difficulty.name == "EASY"){
                              Log.d("repetida", "he accedido con este size ${auxList.size.toString()} ")
                          }
                          //si no hay cartas en la bajara y la lista no está vacía
                      } else if((repeatList.isNotEmpty() && repeatIndex <repeatList.size) || (repeatAuxList.isNotEmpty() && repeatAuxIndex<repeatAuxList.size)){
                          if(repeatAuxList.isEmpty()){
                              repeatIndex++
                              cardIndex = auxList.size
                              cardInput.text = ""
                              Handler().postDelayed({


                                  cardInput.text = auxList[repeatList[repeatIndex-1]].input
                              }, 309)
                              cardResult.text = auxList[repeatList[repeatIndex-1]].result
                              cardResult.visibility = TextView.GONE
                              numCards.text = "Tarjeta ${repeatList[repeatIndex-1]} de ${repeatList.size}"
                              for (idList in folder!!.cardId.orEmpty()) {
                                  if (idList.cardId == auxList[repeatList[repeatIndex-1]].id) {
                                      currentCardWithDifficulty = idList
                                  }
                              }
                              Handler().postDelayed({
                                  if(cardResult.visibility == TextView.VISIBLE){
                                      studyAlgorithm(viewItemEasy,currentCardWithDifficulty,folderDao, userId.toString(), firebaseDatabase.reference, folder!!.id )
                                      val iterator = repeatList.iterator()
                                      while (iterator.hasNext()) {
                                          val number = iterator.next()
                                          if (number == repeatIndex-1) {
                                              Log.d("vaciar", "${(repeatIndex-1)} y $number, coincide")
                                          }
                                      }
                                      checkCompletion(auxList, repeatList, cardResult, repeatIndex, folder)
                                  }
                              }, 709)
                          }else{
                              repeatAuxIndex++
                              cardIndex = auxList.size
                              cardInput.text = ""
                              Handler().postDelayed({
                                  //1
                                  Log.d("vaciar", "repeatList ${repeatAuxList.size}")
                                  cardInput.text = auxList[repeatAuxList[repeatAuxIndex-1]].input
                              }, 309)
                              //pequeño parche
                              if((repeatAuxIndex-1)==repeatAuxList.size){
                                  repeatAuxIndex--
                              }
                              cardResult.text = auxList[repeatAuxList[repeatAuxIndex-1]].result
                              cardResult.visibility = TextView.GONE
                              numCards.text = "Tarjeta ${repeatAuxList[repeatAuxIndex-1]} de ${repeatAuxList.size}"
                              for (idList in folder!!.cardId.orEmpty()) {
                                  if (idList.cardId == auxList[repeatAuxList[repeatAuxIndex-1]].id) {
                                      currentCardWithDifficulty = idList
                                  }
                              }
                              Handler().postDelayed({
                                  if(cardResult.visibility == TextView.VISIBLE){
                                      studyAlgorithm(viewItemEasy,currentCardWithDifficulty,folderDao, userId.toString(), firebaseDatabase.reference, folder!!.id )
                                      val iterator = repeatAuxList.iterator()
                                      while (iterator.hasNext()) {
                                          val number = iterator.next()
                                          if (number == repeatAuxIndex-1) {
                                              Log.d("vaciar", "${(repeatAuxIndex-1)} y $number, coincide")
                                          }
                                      }
                                      checkCompletion(auxList, repeatAuxList, cardResult, repeatAuxIndex, folder)
                                  }
                              }, 709)
                          }
                      } else{
                          if(repeatAuxList.isEmpty()){
                              checkCompletion(auxList, repeatList, cardResult, repeatIndex, folder!!)

                          }else{
                              checkCompletion(auxList, repeatAuxList, cardResult, repeatAuxIndex, folder!!)

                          }

                      }
                  }
            }
            override fun onLabelNameCallback(cardList: ArrayList<Label>) {
            }
            override fun onSingleUserCallback(user: UserData) {
            }
            override fun onFolderCallback(folderList: ArrayList<Folder>) {
            }
            override fun onUsersCallback(userList: ArrayList<UserData>) {
            }
        }, firebaseDatabase.reference,
            userId!!, cardIdList!!
        )
        topBar.setNavigationOnClickListener {

            replaceFragment(requireActivity(), CardViewFragment())
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
    fun checkCompletion(auxList: ArrayList<Card>, repeatList: ArrayList<Int>, cardResult: TextView, repeatIndex: Int, folder: Folder) {

        if ((cardIndex -1) == auxList.size && repeatList.isEmpty()) {
            for(cards in folder.cardId!!){
                cards.repeated = 0

            }
            folderDao.updateFolder(firebaseDatabase.reference, userId, folder!!, requireContext())
            finishedStudying()
        } else if (repeatList.isNotEmpty() && (cardIndex + 1) == auxList.size) {
            cardIndex = repeatList[0]

        } else if ( (repeatIndex == repeatList.size)) {
            for(cards in folder.cardId!!){
                cards.repeated = 0

            }
            folderDao.updateFolder(firebaseDatabase.reference, userId, folder!!, requireContext())
           finishedStudying()
        }
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
    fun studyAlgorithm(item: View, currentCardWithDifficulty: CardWithDifficulty, folderDao: FolderDao, userId :String, databaseReference: DatabaseReference, folderId :String){
        Log.d("problema", "ID: ${item.id}?")
        when (item.id) {
            2131362301 -> {
                currentCardWithDifficulty.difficulty  = Difficulty.HARD
                currentCardWithDifficulty.repeated++

            }
            2131362175 -> {
                currentCardWithDifficulty.difficulty  = Difficulty.REGULAR
                currentCardWithDifficulty.repeated++


            }
            2131362008-> {

                currentCardWithDifficulty.difficulty  = Difficulty.EASY
                currentCardWithDifficulty.repeated++

            }
            else -> {}
        }
        folderDao.updateCardDifficulty(databaseReference, userId, folderId,currentCardWithDifficulty, requireContext())

    }
    fun optionManager(repeatList: ArrayList<Int>, repeatAuxList:ArrayList<Int>, cardInput:TextView, auxList: ArrayList<Card>, folder: Folder, isBad : Boolean){
        if(repeatAuxList.isEmpty()){

            if (repeatList.isNotEmpty() && (repeatIndex < repeatList.size)) {
                //uso repeat
                repeatIndex++
                if(!repeatAuxList.contains(repeatAuxIndex)){
                    repeatAuxList.add(repeatIndex)
                }

                cardInput.text = ""

                Handler().postDelayed({

                    cardInput.text =  auxList[repeatList[repeatIndex-1]].input
                }, 309)

                cardResult.text = auxList[repeatList[repeatIndex-1]].result
                cardResult.visibility = TextView.GONE
                numCards.text = "Tarjeta ${repeatList[repeatIndex-1]} de ${repeatList.size}"
                for (idList in folder!!.cardId.orEmpty()) {
                    if (idList.cardId == auxList[repeatList[repeatIndex-1]].id) {
                        currentCardWithDifficulty = idList
                    }
                }
                //post click
                Handler().postDelayed({
                    if(cardResult.visibility == TextView.VISIBLE){
                        if(!isBad){
                            studyAlgorithm(viewItemMeh,currentCardWithDifficulty,folderDao, userId.toString(), firebaseDatabase.reference, folder!!.id )

                        }else{
                            studyAlgorithm(viewItemAgain,currentCardWithDifficulty,folderDao, userId.toString(), firebaseDatabase.reference, folder!!.id )
                        }

                    }


                }, 709)
                val repeatedCardValue = currentCardWithDifficulty.repeated
                //manejo de opciones
                if(!isBad){
                    checkRegularCompletion(repeatedCardValue,repeatList, repeatIndex, folder)
                }else{
                    checkBadCompletion(repeatedCardValue,repeatList, repeatIndex)
                }
            }else if( repeatIndex >=repeatList.size || currentCardWithDifficulty.repeated>1 ){

                //valida si queda algo
                val easyCards = folder.cardId?.filter {
                    it.difficulty.name == "EASY"
                } ?: emptyList()
                if (easyCards.size > repeatList.size - 1) {
                    lifecycleScope.launch {
                        for(cards in folder.cardId!!){
                            cards.repeated =0
                        }
                        folderDao.updateFolder(firebaseDatabase.reference, userId, folder, requireContext())
                        repeatList.clear()
                        finishedStudying()
                    }
                }else{
                    repeatList.clear()
                }
            }

        }else{
            if (repeatAuxList.isNotEmpty() && repeatAuxIndex <repeatAuxList.size) {
                //uso repeat-aux
                repeatAuxIndex++
                if(!repeatList.contains(repeatAuxIndex)){
                    repeatList.add(repeatAuxIndex)
                }
                cardInput.text = ""
                Handler().postDelayed({

                    cardInput.text = auxList[repeatAuxList[repeatAuxIndex-1]].input
                }, 309)

                cardResult.text = auxList[repeatAuxList[repeatAuxIndex-1]].result
                cardResult.visibility = TextView.GONE
                numCards.text = "Tarjeta ${repeatAuxList[repeatAuxIndex-1]} de ${repeatAuxList.size}"
                for (idList in folder!!.cardId.orEmpty()) {
                    if (idList.cardId == auxList[repeatAuxList[repeatAuxIndex-1]].id) {
                        currentCardWithDifficulty = idList
                    }
                }
                val repeatedCardValue = currentCardWithDifficulty.repeated
                //post click
                if(!isBad){

                    checkRegularCompletion(repeatedCardValue,repeatAuxList, repeatAuxIndex, folder)
                }else{

                    checkBadCompletion(repeatedCardValue,repeatAuxList, repeatAuxIndex)
                }
            }else if( repeatAuxIndex >=repeatAuxList.size){
                repeatAuxList.clear()

            }
        }
    }
  private fun checkRegularCompletion(repetido: Int, lista: ArrayList<Int>, index: Int, folder: Folder){
      val easyCards = folder.cardId?.filter {
          it.difficulty.name == "EASY"
      } ?: emptyList()
      if (easyCards.size > lista.size - 1 && repetido>1 && index+1 == lista.size) {
          lifecycleScope.launch {
              for(cards in folder.cardId!!){
                  cards.repeated =0
              }
              folderDao.updateFolder(firebaseDatabase.reference, userId, folder, requireContext())
              finishedStudying()
          }
      }else if(repetido <1){
          studyAlgorithm(viewItemAgain,currentCardWithDifficulty,folderDao, userId.toString(), firebaseDatabase.reference, folder!!.id )
         if(!lista.contains(index)){
             lista.add(index)
         }
          Log.d("acciones", "repito")
      }
  }
    private fun checkBadCompletion(repetido: Int, lista: ArrayList<Int>, index: Int){
        if(repetido >5){

        }else if(repetido <5){
            lista.add(index)
        }
    }
    private fun finishedStudying(){
        Toast.makeText(requireContext(), "Estudio finalizado", Toast.LENGTH_SHORT).show()
        replaceFragment(requireActivity(), CardViewFragment())
    }
}