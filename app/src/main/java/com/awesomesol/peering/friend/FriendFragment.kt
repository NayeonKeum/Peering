package com.awesomesol.peering.friend

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.awesomesol.peering.R
import com.awesomesol.peering.calendar.CalendarMainFragment
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.android.synthetic.main.fragment_friend.*

class FriendFragment(val uid:String) : Fragment() {

    val TAG="프렌드"

    val fs= Firebase.firestore
    val storage= FirebaseStorage.getInstance()

    var friendList:HashMap<String, Long> = hashMapOf()

    var userList:ArrayList<HashMap<String, Any>> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    fun userListCallback(callback:(ArrayList<HashMap<String, Any>>)->Unit){
        fs.collection("users").get().addOnSuccessListener { it->

            Log.d(TAG,"It.documents, ${it.documents}" )
            Log.d(TAG,"It.documents, ${it.documents[0].data}" )
            val documents=it.documents
            for (document in documents){
                userList.add(document.data as HashMap<String, Any>)
            }
            callback(userList)

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_friend, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.rv_FriendFragment)


        val items = ArrayList<FriendModel>()

        userListCallback{ // userList->
            fs.collection("users").document(uid).get()
                .addOnSuccessListener {
                    friendList = it.data?.get("friendList") as HashMap<String, Long>
                    Log.d(TAG, "friendList $friendList")

                    val uidList:ArrayList<String> = arrayListOf()

                    for (key in friendList.keys){
                        uidList.add(key)
                    }

                    Log.d(TAG, "uidList $uidList")



                    for (key in uidList){
                        for (data in userList){
                            if (data["uid"]==key){
                                var fm=FriendModel()
                                fm.profileImg=data["profileUrl"] as String
                                fm.nickname= data["nickName"] as String
                                fm.email= data["email"] as String

                                Log.d(TAG, "fm $fm")
                                items.add(fm)

                            }
                        }
                    }


                    rv.adapter = FriendRVAdapter(items)
                    rv.layoutManager = LinearLayoutManager(requireContext())

                }

        }


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        moreBtnClick()
    }
    // recyclerview 클릭 수정
    fun moreBtnClick(){
        iv_FriendFragment_friends.setOnClickListener {
            val diaryreadFragment = DiaryReadFragment()

            parentFragmentManager.beginTransaction()
                .replace(R.id.main_screen_panel, diaryreadFragment).commitNow()
        }
    }


}