package com.freshervnc.ecommerceapplication.ui.messaging

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.freshervnc.ecommerceapplication.R
import com.freshervnc.ecommerceapplication.app.MyApplication
import com.freshervnc.ecommerceapplication.data.enity.CreateChatMessageRequest
import com.freshervnc.ecommerceapplication.data.enity.CreateChatMessageResponse
import com.freshervnc.ecommerceapplication.data.enity.ErrorResponse
import com.freshervnc.ecommerceapplication.data.enity.GetMessageRequest
import com.freshervnc.ecommerceapplication.data.enity.GetMessageResponse
import com.freshervnc.ecommerceapplication.data.enity.GetUserInfoRequest
import com.freshervnc.ecommerceapplication.data.enity.GetUserInfoResponse
import com.freshervnc.ecommerceapplication.data.enity.PushNotificationRequest
import com.freshervnc.ecommerceapplication.data.enity.PushNotificationResponse
import com.freshervnc.ecommerceapplication.data.repository.ChatMessageRepository
import com.freshervnc.ecommerceapplication.data.repository.NotificationRepository
import com.freshervnc.ecommerceapplication.data.repository.UserRepository
import com.freshervnc.ecommerceapplication.model.Chat
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
import io.socket.client.Socket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject

class ChatViewModel (private val application: Application,
                     private val socket: Socket, private val chatMessageRepository: ChatMessageRepository) : AndroidViewModel(application) {
//    private var chatMessageRepository = ChatMessageRepository()
    private val createMessageResult = MutableLiveData<Event<Resource<CreateChatMessageResponse>>>()
    private val _chats = MutableLiveData<MutableList<Chat>>(mutableListOf())
    val chats: LiveData<MutableList<Chat>> get() = _chats
    val chates = MutableLiveData<List<Chat>>()
    val terminateSessionObserver = MutableLiveData<Boolean>()
    val messages = MutableLiveData<List<Chat>>()
    val messageObserver = MutableLiveData<Boolean>()

    fun createMessageResult() : LiveData<Event<Resource<CreateChatMessageResponse>>>{
        return createMessageResult
    }
    fun createMessage(request : CreateChatMessageRequest) : Job = viewModelScope.launch {
        createMessageResult.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = chatMessageRepository.createChatMessage(request)
                if (response.isSuccessful){
                    response.body()?.let { resultResponse ->
                        createMessageResult.postValue(Event(Resource.Success(resultResponse)))
                    }
                }else {
                    val errorResponse = response.errorBody()?.let {
                        val gson = Gson()
                        gson.fromJson(it.string(), ErrorResponse::class.java)
                    }
                    createMessageResult.postValue(Event(Resource.Error(errorResponse?.message ?: "")))
                }
            }else{
                createMessageResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                    R.string.no_internet_connection))))
            }
        }catch (t: Throwable){
            when (t) {
                is IOException -> {
                    createMessageResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.network_failure))))
                }
                else -> {
                    createMessageResult.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.conversion_error))))
                }
            }
        }
    }

    fun sendMessage(messageImage: String, messageText : String, senderId : String) {
        if (messageImage.isEmpty()) {
//            callback.onError("Type a message.")
            Log.e("zzzz","Type a message.")
            return
        }
        val data = JSONObject()
        data.put("messageImage", messageImage)
        data.put("messageText", messageText)
        data.put("senderId", senderId)
        data.put("timestamp", System.currentTimeMillis())

        chatMessageRepository.emitEvent(socket, "message", data)

        messageObserver.value = true
        messageObserver.value = false
    }

    init {
        socket.connect()

        chatMessageRepository.listenForEvent(socket, "message") { args ->
            viewModelScope.launch(Dispatchers.Main) {
                val data: JSONObject = args[0] as JSONObject
                val chats = Chat(
                    data.getString("messageImage"),
                    data.getString("messageText"),
                    data.getString("senderId"),
                    data.getLong("timestamp")
                )

                if (messages.value == null) {
                    messages.value = listOf<Chat>().toMutableList().plus(chats)
                } else {
                    messages.value = messages.value!!.toMutableList().plus(chats)
                }
            }
        }

        chatMessageRepository.listenForEvent(socket, "error") { args ->
            viewModelScope.launch(Dispatchers.Main) {
                val data: JSONObject = args[0] as JSONObject
                val message = data.getString("message")
//                callback.onError(message)
                Log.e("zzzz","${message}")
                terminateSessionObserver.value = true
                terminateSessionObserver.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        socket.disconnect()
        socket.off("message")
        socket.off("error")
    }

}