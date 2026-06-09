# ================================================
# Banana Dryer Controller - Android Project Setup
# Jalankan di root folder project (tempat folder app/ berada)
# Usage: .\setup_banana_dryer.ps1
# ================================================

$BASE = "app\src\main\java\com\bananadryer"
$RES  = "app\src\main\res"

Write-Host "Membuat struktur project Banana Dryer Controller..." -ForegroundColor Yellow

# ── Buat semua direktori ──────────────────────────────────────
$dirs = @(
    "$BASE\data\api",
    "$BASE\data\model",
    "$BASE\data\repository",
    "$BASE\database\entity",
    "$BASE\database\dao",
    "$BASE\ui\dashboard",
    "$BASE\ui\production",
    "$BASE\ui\history",
    "$BASE\ui\historydetail",
    "$BASE\ui\settings",
    "$BASE\utils",
    "$RES\navigation"
)
foreach ($dir in $dirs) {
    New-Item -ItemType Directory -Force -Path $dir | Out-Null
}
Write-Host "Direktori berhasil dibuat" -ForegroundColor Green

# ── data/api/ApiService.kt ────────────────────────────────────
@'
package com.bananadryer.data.api

import retrofit2.http.GET

interface ApiService {
    // TODO: Tambahkan endpoint API di sini
}
'@ | Set-Content "$BASE\data\api\ApiService.kt" -Encoding UTF8

# ── data/api/RetrofitClient.kt ────────────────────────────────
@'
package com.bananadryer.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.1.100/" // TODO: ganti dengan IP ESP32

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
'@ | Set-Content "$BASE\data\api\RetrofitClient.kt" -Encoding UTF8

# ── data/model/MonitorResponse.kt ────────────────────────────
@'
package com.bananadryer.data.model

data class MonitorResponse(
    val temperature: Float = 0f,
    val humidity: Float = 0f,
    val status: String = ""
)
'@ | Set-Content "$BASE\data\model\MonitorResponse.kt" -Encoding UTF8

# ── data/model/StartResponse.kt ──────────────────────────────
@'
package com.bananadryer.data.model

data class StartResponse(
    val success: Boolean = false,
    val message: String = ""
)
'@ | Set-Content "$BASE\data\model\StartResponse.kt" -Encoding UTF8

# ── data/repository/BananaDryerRepository.kt ─────────────────
@'
package com.bananadryer.data.repository

import com.bananadryer.data.api.RetrofitClient

class BananaDryerRepository {
    private val api = RetrofitClient.instance

    // TODO: Tambahkan fungsi repository di sini
}
'@ | Set-Content "$BASE\data\repository\BananaDryerRepository.kt" -Encoding UTF8

# ── database/entity/ProductionHistory.kt ─────────────────────
@'
package com.bananadryer.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "production_history")
data class ProductionHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String = "",
    val duration: Long = 0L,
    val avgTemperature: Float = 0f,
    val avgHumidity: Float = 0f
)
'@ | Set-Content "$BASE\database\entity\ProductionHistory.kt" -Encoding UTF8

# ── database/dao/HistoryDao.kt ────────────────────────────────
@'
package com.bananadryer.database.dao

import androidx.room.*
import com.bananadryer.database.entity.ProductionHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM production_history ORDER BY id DESC")
    fun getAllHistory(): Flow<List<ProductionHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: ProductionHistory)

    @Delete
    suspend fun delete(history: ProductionHistory)
}
'@ | Set-Content "$BASE\database\dao\HistoryDao.kt" -Encoding UTF8

# ── database/AppDatabase.kt ───────────────────────────────────
@'
package com.bananadryer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bananadryer.database.dao.HistoryDao
import com.bananadryer.database.entity.ProductionHistory

@Database(entities = [ProductionHistory::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "banana_dryer.db"
                ).build().also { INSTANCE = it }
            }
    }
}
'@ | Set-Content "$BASE\database\AppDatabase.kt" -Encoding UTF8

# ── ui/dashboard/DashboardViewModel.kt ───────────────────────
@'
package com.bananadryer.ui.dashboard

import androidx.lifecycle.ViewModel
import com.bananadryer.data.repository.BananaDryerRepository

class DashboardViewModel : ViewModel() {
    private val repository = BananaDryerRepository()

    // TODO: Tambahkan LiveData / StateFlow di sini
}
'@ | Set-Content "$BASE\ui\dashboard\DashboardViewModel.kt" -Encoding UTF8

# ── ui/dashboard/DashboardFragment.kt ────────────────────────
@'
package com.bananadryer.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

class DashboardFragment : Fragment() {
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}
'@ | Set-Content "$BASE\ui\dashboard\DashboardFragment.kt" -Encoding UTF8

# ── ui/production/ProductionViewModel.kt ─────────────────────
@'
package com.bananadryer.ui.production

import androidx.lifecycle.ViewModel
import com.bananadryer.data.repository.BananaDryerRepository

class ProductionViewModel : ViewModel() {
    private val repository = BananaDryerRepository()

    // TODO: Tambahkan LiveData / StateFlow di sini
}
'@ | Set-Content "$BASE\ui\production\ProductionViewModel.kt" -Encoding UTF8

# ── ui/production/ProductionFragment.kt ──────────────────────
@'
package com.bananadryer.ui.production

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

class ProductionFragment : Fragment() {
    private val viewModel: ProductionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}
'@ | Set-Content "$BASE\ui\production\ProductionFragment.kt" -Encoding UTF8

# ── ui/history/HistoryFragment.kt ────────────────────────────
@'
package com.bananadryer.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class HistoryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}
'@ | Set-Content "$BASE\ui\history\HistoryFragment.kt" -Encoding UTF8

# ── ui/history/HistoryAdapter.kt ─────────────────────────────
@'
package com.bananadryer.ui.history

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bananadryer.database.entity.ProductionHistory

class HistoryAdapter(
    private var items: List<ProductionHistory> = emptyList()
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(android.view.View(parent.context))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // TODO: Bind data
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<ProductionHistory>) {
        items = newItems
        notifyDataSetChanged()
    }
}
'@ | Set-Content "$BASE\ui\history\HistoryAdapter.kt" -Encoding UTF8

# ── ui/historydetail/HistoryDetailFragment.kt ────────────────
@'
package com.bananadryer.ui.historydetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class HistoryDetailFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}
'@ | Set-Content "$BASE\ui\historydetail\HistoryDetailFragment.kt" -Encoding UTF8

# ── ui/settings/SettingsFragment.kt ──────────────────────────
@'
package com.bananadryer.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}
'@ | Set-Content "$BASE\ui\settings\SettingsFragment.kt" -Encoding UTF8

# ── utils/PrefsManager.kt ─────────────────────────────────────
@'
package com.bananadryer.utils

import android.content.Context
import android.content.SharedPreferences

class PrefsManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("banana_dryer_prefs", Context.MODE_PRIVATE)

    var espIpAddress: String
        get() = prefs.getString("esp_ip", "192.168.1.100") ?: "192.168.1.100"
        set(value) = prefs.edit().putString("esp_ip", value).apply()
}
'@ | Set-Content "$BASE\utils\PrefsManager.kt" -Encoding UTF8

# ── utils/PdfExportUtil.kt ────────────────────────────────────
@'
package com.bananadryer.utils

import android.content.Context
import com.bananadryer.database.entity.ProductionHistory

object PdfExportUtil {
    fun exportHistory(context: Context, historyList: List<ProductionHistory>): Boolean {
        // TODO: Implementasi export PDF
        return false
    }
}
'@ | Set-Content "$BASE\utils\PdfExportUtil.kt" -Encoding UTF8

# ── MainActivity.kt ───────────────────────────────────────────
@'
package com.bananadryer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: setContentView(R.layout.activity_main)
    }
}
'@ | Set-Content "$BASE\MainActivity.kt" -Encoding UTF8

# ── res/navigation/nav_graph.xml ──────────────────────────────
@'
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/nav_graph"
    app:startDestination="@id/dashboardFragment">

    <fragment
        android:id="@+id/dashboardFragment"
        android:name="com.bananadryer.ui.dashboard.DashboardFragment"
        android:label="Dashboard" />

    <fragment
        android:id="@+id/productionFragment"
        android:name="com.bananadryer.ui.production.ProductionFragment"
        android:label="Produksi" />

    <fragment
        android:id="@+id/historyFragment"
        android:name="com.bananadryer.ui.history.HistoryFragment"
        android:label="Riwayat" />

    <fragment
        android:id="@+id/historyDetailFragment"
        android:name="com.bananadryer.ui.historydetail.HistoryDetailFragment"
        android:label="Detail Riwayat" />

    <fragment
        android:id="@+id/settingsFragment"
        android:name="com.bananadryer.ui.settings.SettingsFragment"
        android:label="Seting" />

</navigation>
'@ | Set-Content "$RES\navigation\nav_graph.xml" -Encoding UTF8

# ── Ringkasan ─────────────────────────────────────────────────
Write-Host ""
Write-Host "Semua file berhasil dibuat!" -ForegroundColor Green
Write-Host ""
Write-Host "File yang dibuat:" -ForegroundColor Cyan
Get-ChildItem -Path $BASE -Recurse -Filter "*.kt" | ForEach-Object { Write-Host "  $($_.FullName)" }
Write-Host "  $RES\navigation\nav_graph.xml"
Write-Host ""
Write-Host "=================================================" -ForegroundColor Yellow
Write-Host "LANGKAH SELANJUTNYA:" -ForegroundColor Yellow
Write-Host "  1. Package name di build.gradle = com.bananadryer"
Write-Host "  2. Tambahkan dependencies (Retrofit, Room, Navigation, ViewModel)"
Write-Host "  3. Sync Gradle -> Build -> Run"
Write-Host "=================================================" -ForegroundColor Yellow
