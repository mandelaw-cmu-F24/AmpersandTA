package com.ampersand.ampersandtechnicalassessment.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ampersand.ampersandtechnicalassessment.data.ObjectData
import com.ampersand.ampersandtechnicalassessment.network.ApiService
import com.ampersand.ampersandtechnicalassessment.network.ApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainViewModel : ViewModel() {
    private val _objects = MutableStateFlow<List<ObjectData>>(emptyList())
    val objects: StateFlow<List<ObjectData>> = _objects

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val apiService: ApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.restful-api.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
        fetchObjects()
    }

    private fun fetchObjects() {
        viewModelScope.launch {
            try {
                val response = apiService.getObjects()
                _objects.value = response.map { item ->
                    ObjectData(
                        id = item.id,
                        name = item.name,
                        data = item.data ?: emptyMap()
                    )
                }
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to fetch data: ${e.localizedMessage}"
                Log.e("MainViewModel", "Error fetching objects", e)
            }
        }
    }
}

// data class
data class ApiResponse(
    val id: String,
    val name: String,
    val data: Map<String, Any>?
)