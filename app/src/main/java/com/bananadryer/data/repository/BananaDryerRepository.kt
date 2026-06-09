package com.bananadryer.data.repository

import com.bananadryer.data.api.RetrofitClient
import com.bananadryer.data.model.MonitorResponse
import com.bananadryer.data.model.StartResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BananaDryerRepository(private val espIp: String) {

    private val api get() = RetrofitClient.getInstance("http://$espIp/")

    suspend fun getMonitor(): Result<MonitorResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.getMonitor()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun startProcess(tb1: Long, tb2: Long, dc: Int): Result<StartResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.startProcess(tb1, tb2, dc)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}