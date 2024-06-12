package com.dicoding.asclepius.view.result

import ApiConfiguration
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.apis.ArticleData
import com.dicoding.asclepius.apis.Res
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArticlesViewModel : ViewModel() {

    private val articles = MutableLiveData<List<ArticleData>>()
    val articlesLiveData: LiveData<List<ArticleData>> = articles

    private val isLoading = MutableLiveData<Boolean>()
    val isLoadingLiveData: LiveData<Boolean> = isLoading

    private val errorMsg = MutableLiveData<String>()
    val errorMsgLiveData: LiveData<String> = errorMsg

    init {
        fetchArticles()
    }

    private fun fetchArticles() {
        isLoading.value = true
        val req = ApiConfiguration.createApi().fetchArticles()
        req.enqueue(object : Callback<Res> {
            override fun onResponse(call: Call<Res>, response: Response<Res>) {
                isLoading.value = false
                if (response.isSuccessful) {
                    val res = response.body()

                    errorMsg.value = ""
                    res?.let {
                        articles.value = it.res ?: emptyList()
                    }
                } else {
                    errorMsg.value = response.message()
                }
            }

            override fun onFailure(call: Call<Res>, t: Throwable) {
                isLoading.value = false
                errorMsg.value = t.message
            }
        })
    }
}
