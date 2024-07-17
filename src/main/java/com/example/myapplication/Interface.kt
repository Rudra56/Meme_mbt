package com.example.myapplication

import retrofit2.Response
import retrofit2.http.GET


interface ApiService {
    @GET("gimme/wholesomememes")
    suspend fun getWholesomeMeme(): Response<Meme>
}






