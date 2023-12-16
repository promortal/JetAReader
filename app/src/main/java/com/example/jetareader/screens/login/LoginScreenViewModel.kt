package com.example.jetareader.screens.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetareader.model.MUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoginScreenViewModel : ViewModel() {
    // val loadingState = MutableStateFlow(LoadingState.IDLE)
    private val auth:FirebaseAuth = Firebase.auth

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    fun signInWithEmailAndPassword(email: String, password: String, home: () -> Unit) = viewModelScope.launch {
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FB","signInWithEmailAndPassword: SUCCESS ${task.result}")
                        home()
                    } else {
                        Log.d("FB", "signInWithEmailAndPassword: ${task.result}")
                    }
                }
        } catch (ex: Exception) {
            Log.d("FB","signInWithEmailAndPassword: ${ex.message}")
        }
    }

    fun createUserWithEmailAndPassword(email: String, password: String, home: () -> Unit) = viewModelScope.launch{
        try {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FB","createUserWithEmailAndPassword: SUCCESS ${task.result}")
                        val displayName = email.split('@').get(0)
                        createUser(displayName)
                        home()
                    } else {
                        Log.d("FB","createUserWithEmailAndPassword: ${task.result}")
                    }
                }
        } catch (ex: Exception) {
            Log.d("FB","createUserWithEmailAndPassword: ${ex.message}")
        }

    }

    private fun createUser(displayName: String) {
        var userId = auth.currentUser?.uid
        val user = MUser(userId = userId.toString(),
            displayName = displayName,
            quote = "Life is great",
            profession = "Android Developer",
            avatarUrl = "",
            id = null).toMap()

        FirebaseFirestore.getInstance().collection("users").add(user)

    }


}