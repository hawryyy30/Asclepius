package com.dicoding.asclepius.apis

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
   @GET("top-headlines")
   fun fetchArticles(
       @Query("q") q: String = "cancer",
   ) : Call<Res>
}