package com.freshervnc.ecommerceapplication.ui.messaging

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.app.MyApplication
import com.freshervnc.ecommerceapplication.data.enity.ErrorResponse
import com.freshervnc.ecommerceapplication.data.enity.GetMessageRequest
import com.freshervnc.ecommerceapplication.data.enity.GetMessageResponse
import com.freshervnc.ecommerceapplication.data.enity.GetUserInfoRequest
import com.freshervnc.ecommerceapplication.data.enity.GetUserInfoResponse
import com.freshervnc.ecommerceapplication.data.enity.PushNotificationRequest
import com.freshervnc.ecommerceapplication.data.enity.PushNotificationResponse
import com.freshervnc.ecommerceapplication.data.repository.NotificationRepository
import com.freshervnc.ecommerceapplication.data.repository.UserRepository
import com.freshervnc.ecommerceapplication.model.Message
import com.freshervnc.ecommerceapplication.utils.Event
import com.freshervnc.ecommerceapplication.utils.Resource
import com.freshervnc.ecommerceapplication.utils.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

class ChatViewModel(private val application: Application) : AndroidViewModel(application) {
    private var repository = UserRepository()
    private var notificationRepository : NotificationRepository = NotificationRepository()

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val getUserInfoResult = MutableLiveData<Event<Resource<GetUserInfoResponse>>>()
    private val pushNotificationResult = MutableLiveData<Event<Resource<PushNotificationResponse>>>()
    private val getMessageResult = MutableLiveData<GetMessageResponse>()
    private val getErrorMessageResult = MutableLiveData<String>()

    val isSuccessful = MutableLiveData<Boolean>()

    fun getUserInfoResult(): LiveData<Event<Resource<GetUserInfoResponse>>> {
        return getUserInfoResult
    }

    fun getUserInfo(request : GetUserInfoRequest) : Job = viewModelScope.launch {
        getUserInfoResult.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = repository.getUserInfo(request)
                if (response.isSuccessful) {
                    response.body()?.let { resultResponse ->
                        getUserInfoResult.postValue(Event(Resource.Success(resultResponse)))
                    }
                } else {
                    val errorResponse = response.errorBody()?.let {
                        val gson = Gson()
                        gson.fromJson(it.string(), ErrorResponse::class.java)
                    }
                    getUserInfoResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                }
            } else {
                getUserInfoResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                    R.string.no_internet_connection))))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    getUserInfoResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.network_failure))))
                }
                else -> {
                    getUserInfoResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.conversion_error))))
                }
            }
        }
    }

    fun getSuccessful() : LiveData<Boolean>{
        return isSuccessful
    }

    fun pushNotificationResult(): LiveData<Event<Resource<PushNotificationResponse>>> {
        return pushNotificationResult
    }

    fun getMessageResult() : LiveData<GetMessageResponse>{
        return getMessageResult
    }

    fun getErrorMessageResult() : LiveData<String> {
        return getErrorMessageResult
    }

    fun sendMessage(
        messageTxt: String,
        senderUid: String,
        date: Long,
        senderRoom: String,
        receiverRoom: String,
    ) {
        val message = Message(
            messageText = messageTxt,
            senderId = senderUid,
            timestamp = date
        )
        val randomKey: String = database.reference.push().key!!
        val lastMgsObj = HashMap<String, Any>()
        lastMgsObj["lastMsg"] = message.messageText
        lastMgsObj["lastMsgTime"] = date
        database.reference.child("Chats").child(senderRoom).updateChildren(lastMgsObj)
        database.reference.child("Chats").child(receiverRoom).updateChildren(lastMgsObj)
        database.reference.child("Chats")
            .child(senderRoom)
            .child("Messages")
            .child(randomKey)
            .setValue(message)
            .addOnSuccessListener {
                database.reference.child("Chats")
                    .child(receiverRoom)
                    .child("Messages")
                    .child(randomKey)
                    .setValue(message)
                    .addOnSuccessListener {
                        isSuccessful.value = true
                    }
            }
            .addOnFailureListener {
                isSuccessful.value = false
            }
    }

    fun pushNotification(request : PushNotificationRequest) : Job = viewModelScope.launch{
        pushNotificationResult.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = notificationRepository.pushNotification(request)
                if (response.isSuccessful){
                    response.body()?.let { resultResponse ->
                        pushNotificationResult.postValue(Event(Resource.Success(resultResponse)))
                    }
                }else {
                    val errorResponse = response.errorBody()?.let {
                        val gson = Gson()
                        gson.fromJson(it.string(), ErrorResponse::class.java)
                    }
                    pushNotificationResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                }
            }else{
                pushNotificationResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                    R.string.no_internet_connection))))
            }
        }catch (t: Throwable){
            when (t) {
                is IOException -> {
                    pushNotificationResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.network_failure))))
                }
                else -> {
                    pushNotificationResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.conversion_error))))
                }
            }
        }
    }


    fun getMessages(request : GetMessageRequest): Job = viewModelScope.launch {
        Log.e("zzzz","${request.senderRoom.toString()}")
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                database.getReference("Chats")
                    .child(request.senderRoom.toString())
                    .child("Messages").addValueEventListener(object : ValueEventListener {
                        @SuppressLint("SuspiciousIndentation")
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val messageList = mutableListOf<Message>()
                            snapshot.children.forEach { child ->
                                val message = child.getValue(Message::class.java)
                                message?.let { messageList.add(it) }
                            }
                            getMessageResult.postValue(GetMessageResponse(messageList))
                            Log.e("zzzz123","${GetMessageResponse(messageList)} \n ${messageList}")

                        }

                        override fun onCancelled(error: DatabaseError) {
                            getErrorMessageResult.postValue(ErrorResponse(error.message).toString())
                        }
                    })
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    getErrorMessageResult.postValue(ErrorResponse(getApplication<MyApplication>().getString(R.string.network_failure)).toString())
                }

                else -> {
                    getErrorMessageResult.postValue(ErrorResponse(getApplication<MyApplication>().getString(R.string.conversion_error)).toString())
                }
            }
        }
    }
}