package com.bananadryer.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var instance: ApiService? = null
    private var currentBaseUrl: String = ""

    fun getInstance(baseUrl: String): ApiService {
        if (instance == null || currentBaseUrl != baseUrl) {
            currentBaseUrl = baseUrl
            instance = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
        return instance!!
    }
}