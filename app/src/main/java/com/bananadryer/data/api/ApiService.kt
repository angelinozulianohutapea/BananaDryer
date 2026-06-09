package com.bananadryer.data.api

import com.bananadryer.data.model.MonitorResponse
import com.bananadryer.data.model.StartResponse
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/monitor")
    suspend fun getMonitor(): Response<MonitorResponse>

    @POST("/start")
    suspend fun startProcess(
        @Query("tb1") tb1: Long,
        @Query("tb2") tb2: Long,
        @Query("dc") dc: Int
    ): Response<StartResponse>
}