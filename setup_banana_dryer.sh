#!/bin/bash

# ================================================
# Banana Dryer Controller - Android Project Setup
# Jalankan script ini di root folder project
# (tempat folder app/ berada)
# Usage: bash setup_banana_dryer.sh
# ================================================

BASE="app/src/main/java/com/bananadryer"
RES="app/src/main/res"

echo "🍌 Membuat struktur project Banana Dryer Controller..."

# ── Buat semua direktori ──────────────────────────────────────
mkdir -p "$BASE/data/api"
mkdir -p "$BASE/data/model"
mkdir -p "$BASE/data/repository"
mkdir -p "$BASE/database/entity"
mkdir -p "$BASE/database/dao"
mkdir -p "$BASE/ui/dashboard"
mkdir -p "$BASE/ui/production"
mkdir -p "$BASE/ui/history"
mkdir -p "$BASE/ui/historydetail"
mkdir -p "$BASE/ui/settings"
mkdir -p "$BASE/utils"
mkdir -p "$RES/navigation"

echo "✅ Direktori berhasil dibuat"

# ── data/api ─────────────────────────────────────────────────
cat > "$BASE/data/api/ApiService.kt" << 'EOF'
package com.bananadryer.data.api

import retrofit2.http.GET

interface ApiService {
    // TODO: Tambahkan endpoint API di sini
}
EOF

cat > "$BASE/data/api/RetrofitClient.kt" << 'EOF'
package com.bananadryer.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://your-esp32-ip/" // TODO: ganti dengan IP ESP32

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
EOF

# ── data/model ────────────────────────────────────────────────
cat > "$BASE/data/model/MonitorResponse.kt" << 'EOF'
package com.bananadryer.data.model

data class MonitorResponse(
    // TODO: Sesuaikan field dengan response JSON dari ESP32
    val temperature: Float = 0f,
    val humidity: Float = 0f,
    val status: String = ""
)
EOF

cat > "$BASE/data/model/StartResponse.kt" << 'EOF'
package com.bananadryer.data.model

data class StartResponse(
    // TODO: Sesuaikan field dengan response JSON dari ESP32
    val success: Boolean = false,
    val message: String = ""
)
EOF

# ── data/repository ───────────────────────────────────────────
cat > "$BASE/data/repository/BananaDryerRepository.kt" << 'EOF'
package com.bananadryer.data.repository

import com.bananadryer.data.api.RetrofitClient

class BananaDryerRepository {
    private val api = RetrofitClient.instance

    // TODO: Tambahkan fungsi repository di sini
}
EOF

# ── database/entity ───────────────────────────────────────────
cat > "$BASE/database/entity/ProductionHistory.kt" << 'EOF'
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
EOF

# ── database/dao ──────────────────────────────────────────────
cat > "$BASE/database/dao/HistoryDao.kt" << 'EOF'
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
EOF

# ── database/AppDatabase ──────────────────────────────────────
cat > "$BASE/database/AppDatabase.kt" << 'EOF'
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
EOF

# ── ui/dashboard ──────────────────────────────────────────────
cat > "$BASE/ui/dashboard/DashboardViewModel.kt" << 'EOF'
package com.bananadryer.ui.dashboard

import androidx.lifecycle.ViewModel
import com.bananadryer.data.repository.BananaDryerRepository

class DashboardViewModel : ViewModel() {
    private val repository = BananaDryerRepository()

    // TODO: Tambahkan LiveData / StateFlow di sini
}
EOF

cat > "$BASE/ui/dashboard/DashboardFragment.kt" << 'EOF'
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
        // TODO: Inflate layout
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}
EOF

# ── ui/production ─────────────────────────────────────────────
cat > "$BASE/ui/production/ProductionViewModel.kt" << 'EOF'
package com.bananadryer.ui.production

import androidx.lifecycle.ViewModel
import com.bananadryer.data.repository.BananaDryerRepository

class ProductionViewModel : ViewModel() {
    private val repository = BananaDryerRepository()

    // TODO: Tambahkan LiveData / StateFlow di sini
}
EOF

cat > "$BASE/ui/production/ProductionFragment.kt" << 'EOF'
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
        // TODO: Inflate layout
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}
EOF

# ── ui/history ────────────────────────────────────────────────
cat > "$BASE/ui/history/HistoryFragment.kt" << 'EOF'
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
        // TODO: Inflate layout
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}
EOF

cat > "$BASE/ui/history/HistoryAdapter.kt" << 'EOF'
package com.bananadryer.ui.history

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bananadryer.database.entity.ProductionHistory

class HistoryAdapter(
    private var items: List<ProductionHistory> = emptyList()
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // TODO: Inflate item layout
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
EOF

# ── ui/historydetail ──────────────────────────────────────────
cat > "$BASE/ui/historydetail/HistoryDetailFragment.kt" << 'EOF'
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
        // TODO: Inflate layout
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}
EOF

# ── ui/settings ───────────────────────────────────────────────
cat > "$BASE/ui/settings/SettingsFragment.kt" << 'EOF'
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
        // TODO: Inflate layout
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}
EOF

# ── utils ─────────────────────────────────────────────────────
cat > "$BASE/utils/PrefsManager.kt" << 'EOF'
package com.bananadryer.utils

import android.content.Context
import android.content.SharedPreferences

class PrefsManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("banana_dryer_prefs", Context.MODE_PRIVATE)

    // TODO: Tambahkan getter/setter preferences di sini
    var espIpAddress: String
        get() = prefs.getString("esp_ip", "192.168.1.100") ?: "192.168.1.100"
        set(value) = prefs.edit().putString("esp_ip", value).apply()
}
EOF

cat > "$BASE/utils/PdfExportUtil.kt" << 'EOF'
package com.bananadryer.utils

import android.content.Context
import com.bananadryer.database.entity.ProductionHistory

object PdfExportUtil {
    fun exportHistory(context: Context, historyList: List<ProductionHistory>): Boolean {
        // TODO: Implementasi export PDF menggunakan iText atau PdfDocument
        return false
    }
}
EOF

# ── MainActivity ──────────────────────────────────────────────
cat > "$BASE/MainActivity.kt" << 'EOF'
package com.bananadryer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: setContentView(R.layout.activity_main)
        // TODO: Setup Navigation Component
    }
}
EOF

# ── res/navigation/nav_graph.xml ──────────────────────────────
cat > "$RES/navigation/nav_graph.xml" << 'EOF'
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
EOF

echo ""
echo "✅ Semua file berhasil dibuat!"
echo ""
echo "📁 Struktur yang dibuat:"
find "$BASE" -name "*.kt" | sort
echo "$RES/navigation/nav_graph.xml"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📌 LANGKAH SELANJUTNYA:"
echo "  1. Pastikan package name di build.gradle = com.bananadryer"
echo "  2. Tambahkan dependencies di build.gradle (app):"
echo "     - Retrofit + Gson Converter"
echo "     - Room"
echo "     - Navigation Component"
echo "     - Lifecycle ViewModel + LiveData"
echo "  3. Sync Gradle → Build → Run"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
