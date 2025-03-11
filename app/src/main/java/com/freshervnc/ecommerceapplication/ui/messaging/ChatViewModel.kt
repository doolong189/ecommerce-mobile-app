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

class ChatViewModel (private val application: Application, private val repository: ChatMessageRepository) : AndroidViewModel(application) {
//    private var chatMessageRepository = ChatMessageRepository()
    private val createMessageResult = MutableLiveData<Event<Resource<CreateChatMessageResponse>>>()
    private val _chats = MutableLiveData<MutableList<Chat>>(mutableListOf())
    val chats: LiveData<MutableList<Chat>> get() = _chats
    val chates = MutableLiveData<List<Chat>>()
    val terminateSessionObserver = MutableLiveData<Boolean>()

    fun createMessageResult() : LiveData<Event<Resource<CreateChatMessageResponse>>>{
        return createMessageResult
    }
    fun createMessage(request : CreateChatMessageRequest) : Job = viewModelScope.launch {
        createMessageResult.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = repository.createChatMessage(request)
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

//    fun addMessage(newChat : Chat) {
//        val updatedList = _chats.value ?: mutableListOf()
//        updatedList.add(newChat)
//        _chats.value = updatedList
//    }
//
//    init {
//        socket.connect()
//
//
//        chatMessageRepository.listenForEvent(socket, "message") { args ->
//            viewModelScope.launch(Dispatchers.Main) {
//                val data: JSONObject = args[0] as JSONObject
//                val chat = Chat(
//                    messageImage = "",
//                    messageText = data.getString("message_text"),
//                    senderId = data.getString("message_sender"),
//                    timestamp = System.currentTimeMillis()
//                )
//
//                if (chates.value == null) {
//                    chates.value = listOf<Chat>().toMutableList().plus(chat)
//                } else {
//                    chates.value = chates.value!!.toMutableList().plus(chat)
//                }
//            }
//        }
//
//        chatMessageRepository.listenForEvent(socket, "error") { args ->
//            viewModelScope.launch(Dispatchers.Main) {
//                val data: JSONObject = args[0] as JSONObject
//                val message = data.getString("message")
//
//                terminateSessionObserver.value = true
//                terminateSessionObserver.value = false
//            }
//        }
//    }
//
//    fun endSession() {
//        socket.disconnect()
//        socket.off("message")
//        socket.off("error")
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//        socket.disconnect()
//        socket.off("message")
//        socket.off("error")
//    }
    val messages = MutableLiveData<List<Chat>>()
    val errorMessage = MutableLiveData<String>()
    fun sendMessage(messageImage: String, senderId: String , message : String, timestamp : Long) {
        if (message.isEmpty()) {
            errorMessage.value = "Message cannot be empty"
            return
        }
        repository.sendMessage(messageImage, senderId , message , timestamp)
    }

    // Lắng nghe sự kiện tin nhắn mới từ socket
    fun listenForMessages() {
        repository.listenForMessages { args ->
            val data = args[0] as JSONObject
            val message = Chat(
                data.getString("messageImage"),
                data.getString("senderId"),
                data.getString("message"),
                data.getLong("timestamp"),
            )
            val updatedMessages = messages.value.orEmpty() + message
            messages.value = updatedMessages
        }
    }

    // Lắng nghe sự kiện lỗi
    fun listenForErrors() {
        repository.listenForError { args ->
            val data = args[0] as JSONObject
            errorMessage.value = data.getString("message")
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Đảm bảo hủy sự kiện khi ViewModel bị hủy
        repository.listenForMessages { }
        repository.listenForError { }
    }
}