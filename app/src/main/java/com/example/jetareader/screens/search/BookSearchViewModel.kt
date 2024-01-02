package com.example.jetareader.screens.search

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetareader.data.Resource
import com.example.jetareader.model.Item
import com.example.jetareader.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookSearchViewModel @Inject constructor(private val repository: BookRepository) :
    ViewModel() {

    var list: List<Item> by mutableStateOf(listOf())
    var isLoading: Boolean by mutableStateOf(true)
    
    init {
        loadBooks("android")
    }

    private fun loadBooks(query: String) {
        searchBooks(query)
    }

    fun searchBooks(query: String) {
        viewModelScope.launch {
            isLoading = true
            if (query.isEmpty()) return@launch
            when (val response = repository.getBooks(query)) {
                is Resource.Success -> {
                    list = response.data!!
                    if (list.isNotEmpty()) isLoading = false
                }
                is Resource.Error -> {
                    Log.e("Network", "searchBooks failed getting books")
                    isLoading = false
                }
                else -> { isLoading = false }
            }
        }
    }
}