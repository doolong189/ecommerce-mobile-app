package com.freshervnc.ecommerceapplication.ui.message

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.app.MyApplication
import com.freshervnc.ecommerceapplication.data.enity.ErrorResponse
import com.freshervnc.ecommerceapplication.data.enity.GetAllUserRequest
import com.freshervnc.ecommerceapplication.data.enity.GetAllUserResponse
import com.freshervnc.ecommerceapplication.data.enity.GetMessageRequest
import com.freshervnc.ecommerceapplication.data.enity.LoginRequest
import com.freshervnc.ecommerceapplication.data.repository.UserRepository
import com.freshervnc.ecommerceapplication.model.MessageModel
import com.freshervnc.ecommerceapplication.model.UserInfo
import com.freshervnc.ecommerceapplication.ui.launch.login.LoginViewModel
import com.freshervnc.ecommerceapplication.utils.Contacts
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.PreferencesUtils
import com.freshervnc.ecommerceapplication.utils.Resource
import com.freshervnc.ecommerceapplication.utils.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

class MessageViewModel (private val application: Application)  : AndroidViewModel(application) {
    private var repository : UserRepository = UserRepository()
    private val getAllUsersResult = MutableLiveData<Event<Resource<GetAllUserResponse>>>()
    private var mySharedPreferences: PreferencesUtils = PreferencesUtils(application)
    private val _messagesList = MutableLiveData<List<MessageModel>>()
    val messagesList: LiveData<List<MessageModel>> get() = _messagesList
    fun getUsersResult(): LiveData<Event<Resource<GetAllUserResponse>>> {
        return getAllUsersResult
    }
    private val getMessageResult = MutableLiveData<List<MessageModel>>()

    fun getMessageResult(): LiveData<List<MessageModel>>{
        return getMessageResult
    }

    fun getUsers(request : GetAllUserRequest) : Job = viewModelScope.launch{
        getAllUsersResult.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = repository.getAllUser(request)
                if (response.isSuccessful){
                    response.body()?.let { resultResponse ->
                        getAllUsersResult.postValue(Event(Resource.Success(resultResponse)))
                    }
                }else {
                    val errorResponse = response.errorBody()?.let {
                        val gson = Gson()
                        gson.fromJson(it.string(), ErrorResponse::class.java)
                    }
                        getAllUsersResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                }
            }else{
                getAllUsersResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                    R.string.no_internet_connection))))
            }
        }catch (t: Throwable){
            when (t) {
                is IOException -> {
                    getAllUsersResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.network_failure))))
                }
                else -> {
                    getAllUsersResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.conversion_error))))
                }
            }
        }
    }


//    fun fetchHistoryMessage(request : GetMessageRequest){
//        var senderRoom = ""
//        for (item in request.users!!){
//            senderRoom = request.senderId + item._id
//            FirebaseDatabase.getInstance().reference
//                .child("Chats")
//                .child(senderRoom)
//                .orderByChild("lastMsgTime")
//                .addValueEventListener(object : ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        val messagesList = mutableListOf<MessageModel>()
//                        if (snapshot.exists()) {
//                            for (messageSnapshot in snapshot.children) {
//                                val lastMsg = messageSnapshot.child("lastMsg").getValue(String::class.java)
//                                val time = messageSnapshot.child("lastMsgTime").getValue(Long::class.java) ?: 0L
//                                if (lastMsg != null) {
//                                    messagesList.add(
//                                        MessageModel(
//                                            messageText = lastMsg,
//                                            timestamp = time)
//                                    )
//                                }
//                            }
//                            messagesList.sortByDescending { it.timestamp }
//                            Log.e(Contacts.TAG,"${messagesList.size}")
//                            getMessageResult.postValue(messagesList)
//                        }
//                    }
//                    override fun onCancelled(error: DatabaseError) {}
//                })
//        }
//    }
        fun fetchHistoryMessage(users : List<UserInfo>){
            val senderId = mySharedPreferences.userId!!
            var senderRoom = ""
            for (item in users){
                senderRoom = senderId + item._id
                FirebaseDatabase.getInstance().reference
                    .child("Chats")
                    .child(senderRoom)
                    .orderByChild("lastMsgTime")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val messagesList = mutableListOf<MessageModel>()
                            if (snapshot.exists()) {
                                for (messageSnapshot in snapshot.children) {
                                    val lastMsg = messageSnapshot.child("lastMsg").getValue(String::class.java)
                                    val time = messageSnapshot.child("lastMsgTime").getValue(Long::class.java) ?: 0L
                                    if (lastMsg != null) {
                                        messagesList.add(
                                            MessageModel(
                                                messageText = lastMsg,
                                                timestamp = time
                                            )
                                        )
                                        println("log123 ${messagesList}")
                                    }
                                }
                                messagesList.sortByDescending { it.timestamp }
                                _messagesList.postValue(messagesList)
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
            }
        }


    class MessageViewModelFactory(val application : Application) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(MessageViewModel::class.java)){
                MessageViewModel(application) as T
            }else{
                throw IllegalArgumentException("viewmodel not found")
            }
        }
    }
}