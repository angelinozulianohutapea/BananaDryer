package com.bananadryer.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bananadryer.data.model.MonitorResponse
import com.bananadryer.data.repository.BananaDryerRepository
import com.bananadryer.database.AppDatabase
import com.bananadryer.utils.NotificationHelper
import com.bananadryer.utils.PrefsManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Triple<A, B, C>(val first: A, val second: B, val third: C)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PrefsManager(application)
    private val db = AppDatabase.getInstance(application)

    private val _monitor = MutableLiveData<MonitorResponse>()
    val monitor: LiveData<MonitorResponse> = _monitor

    private val _isConnected = MutableLiveData<Boolean>(false)
    val isConnected: LiveData<Boolean> = _isConnected

    private val _statusChanged = MutableLiveData<String>()
    val statusChanged: LiveData<String> = _statusChanged

    private val _chartPoint = MutableLiveData<Float>()
    val chartPoint: LiveData<Float> = _chartPoint

    private val _prevTemp = MutableLiveData<Float?>(null)
    val prevTemp: LiveData<Float?> = _prevTemp

    private val _prevHumid = MutableLiveData<Float?>(null)
    val prevHumid: LiveData<Float?> = _prevHumid

    private val _lastParams = MutableLiveData<Triple<Long, Long, Int>>(Triple(0L, 0L, 0))
    val lastParams: LiveData<Triple<Long, Long, Int>> = _lastParams

    private val _totalSesi = MutableLiveData<Int>(0)
    val totalSesi: LiveData<Int> = _totalSesi

    // Total durasi akurat dari TB1 + TB2
    val totalDurasiMs: Long
        get() = prefs.lastTb1 + prefs.lastTb2

    val espIp: String get() = prefs.espIp

    private var pollingJob: Job? = null
    private var lastStatus: String = "IDLE"
    private var lastTemp: Float = 0f
    private var lastHumid: Float = 0f

    // =====================================================================
    // RUNNING START TIME — disimpan di ViewModel agar tidak reset saat
    // user navigasi ke fragment lain lalu kembali ke Dashboard
    // =====================================================================

    var runningStartTime: Long = 0L
        private set

    fun setRunningStart() {
        if (runningStartTime == 0L) {
            runningStartTime = System.currentTimeMillis()
        }
    }

    fun resetRunningStart() {
        runningStartTime = 0L
    }

    init {
        loadSummary()
    }

    private fun loadSummary() {
        viewModelScope.launch {
            val last = db.historyDao().getLatestHistory()
            last?.let {
                _lastParams.postValue(Triple(it.tb1, it.tb2, it.dc))
            }
        }
        db.historyDao().getAllHistory().observeForever { list ->
            _totalSesi.postValue(list.size)
        }
    }

    fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                fetchMonitor()
                delay(2000)
            }
        }
    }

    fun stopPolling() { pollingJob?.cancel() }

    private suspend fun fetchMonitor() {
        val repo = BananaDryerRepository(prefs.espIp)
        val result = repo.getMonitor()
        if (result.isSuccess) {
            val data = result.getOrNull() ?: return
            _prevTemp.postValue(lastTemp)
            _prevHumid.postValue(lastHumid)
            _monitor.postValue(data)
            _isConnected.postValue(true)
            _chartPoint.postValue(data.temperature)

            if (lastStatus == "RUNNING" &&
                (data.status == "FINISHED" || data.status == "ERROR")) {
                updateLatestHistory(data)
                _statusChanged.postValue(data.status)
                resetRunningStart() // reset timer saat FINISHED/ERROR
            }

            lastStatus = data.status
            lastTemp = data.temperature
            lastHumid = data.humidity
        } else {
            _isConnected.postValue(false)
        }
    }

    private suspend fun updateLatestHistory(data: MonitorResponse) {
        val history = db.historyDao().getLatestRunning() ?: return
        val jam = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        db.historyDao().update(
            history.copy(
                jamSelesai = jam,
                suhuAkhir = data.temperature,
                kelembabanAkhir = data.humidity,
                status = data.status
            )
        )
        NotificationHelper.sendNotification(getApplication(), data.status)
    }
}