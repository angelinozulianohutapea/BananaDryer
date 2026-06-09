package com.bananadryer.ui.production

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bananadryer.data.repository.BananaDryerRepository
import com.bananadryer.database.AppDatabase
import com.bananadryer.database.entity.ProductionHistory
import com.bananadryer.utils.PrefsManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProductionViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PrefsManager(application)
    private val db = AppDatabase.getInstance(application)

    private val _startStatus = MutableLiveData<String>()
    val startStatus: LiveData<String> = _startStatus

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isConnected = MutableLiveData<Boolean?>(null)
    val isConnected: LiveData<Boolean?> = _isConnected

    fun checkKoneksi() {
        viewModelScope.launch {
            val repo = BananaDryerRepository(prefs.espIp)
            val result = repo.getMonitor()
            _isConnected.postValue(result.isSuccess)
        }
    }

    fun startProses(tb1: Long, tb2: Long, dc: Int) {
        if (tb1 <= 0 || tb2 <= 0 || dc <= 0) {
            _startStatus.value = "❌ Semua parameter harus diisi dan > 0"
            return
        }
        if (dc > 255) {
            _startStatus.value = "❌ DC Speed maksimal 255"
            return
        }

        viewModelScope.launch {
            _isLoading.postValue(true)
            _startStatus.postValue("🔍 Mengecek koneksi ESP32...")

            val repo = BananaDryerRepository(prefs.espIp)
            val koneksi = repo.getMonitor()

            if (koneksi.isFailure) {
                _startStatus.postValue(
                    "❌ ESP32 tidak terhubung!\n" +
                            "Pastikan HP dan ESP32 di WiFi yang sama,\n" +
                            "lalu cek IP di menu Seting."
                )
                _isLoading.postValue(false)
                return@launch
            }

            val currentStatus = koneksi.getOrNull()?.status
            if (currentStatus == "RUNNING") {
                _startStatus.postValue("⚠️ Mesin sedang berjalan, tunggu hingga selesai.")
                _isLoading.postValue(false)
                return@launch
            }

            _startStatus.postValue("📡 Mengirim perintah ke ESP32...")

            val result = repo.startProcess(tb1, tb2, dc)

            if (result.isSuccess) {
                // Simpan TB1 + TB2 untuk timer akurat di Dashboard
                prefs.lastTb1 = tb1
                prefs.lastTb2 = tb2

                val now = SimpleDateFormat(
                    "yyyy-MM-dd", Locale.getDefault()
                ).format(Date())
                val jam = SimpleDateFormat(
                    "HH:mm:ss", Locale.getDefault()
                ).format(Date())

                val history = ProductionHistory(
                    tanggal = now,
                    jamMulai = jam,
                    jamSelesai = "--",
                    tb1 = tb1,
                    tb2 = tb2,
                    dc = dc,
                    status = "RUNNING"
                )
                db.historyDao().insert(history)
                _startStatus.postValue("✅ Mesin berhasil dijalankan!")
            } else {
                _startStatus.postValue(
                    "❌ Gagal mengirim perintah.\n${result.exceptionOrNull()?.message}"
                )
            }

            _isLoading.postValue(false)
        }
    }
}