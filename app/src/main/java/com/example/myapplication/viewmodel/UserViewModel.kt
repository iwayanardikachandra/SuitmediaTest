package com.example.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.api.ApiConfig
import com.example.myapplication.data.api.ApiService
import com.example.myapplication.data.model.DataItem
import com.example.myapplication.data.model.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel : ViewModel() {

    private val dataUsers = MutableLiveData<MutableList<DataItem>>().apply { value = mutableListOf() }
    val users: LiveData<MutableList<DataItem>> = dataUsers

    private val loadingStatus = MutableLiveData<Boolean>().apply { value = false }
    val isLoading: LiveData<Boolean> = loadingStatus

    private val errorMessage = MutableLiveData<String?>()
    val error: LiveData<String?> = errorMessage

    var currentPage = 1
    private var isLastPage = false
    private val perPage = 10

    fun fetchUsers(page: Int = 1, isRefresh: Boolean = false) {
        if (loadingStatus.value == true || isLastPage) return

        loadingStatus.value = true
        val apiService = ApiConfig.createService(ApiService::class.java)
        apiService.getUsers(page, perPage).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                loadingStatus.value = false
                if (response.isSuccessful) {
                    val newUsers = response.body()?.data ?: emptyList()
                    if (newUsers.isEmpty()) {
                        isLastPage = true
                    } else {
                        currentPage = page
                        if (isRefresh) {
                            dataUsers.value = newUsers.toMutableList()
                        } else {
                            dataUsers.value?.addAll(newUsers)
                            dataUsers.value = dataUsers.value
                        }
                    }
                } else {
                    errorMessage.value = "Failed to load data"
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                loadingStatus.value = false
                errorMessage.value = t.message
            }
        })
    }

    fun loadNextPage() {
        fetchUsers(currentPage + 1)
    }

    fun refresh() {
        currentPage = 1
        isLastPage = false
        fetchUsers(currentPage, isRefresh = true)
    }
}
