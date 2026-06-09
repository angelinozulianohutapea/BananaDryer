package com.bananadryer

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bananadryer.databinding.ActivityMainBinding
import com.bananadryer.utils.NotificationHelper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    data class NavItem(
        val container: LinearLayout,
        val icon: ImageView,
        val label: TextView,
        val destId: Int
    )

    private val navItems by lazy {
        listOf(
            NavItem(binding.navDashboard, binding.iconDashboard, binding.labelDashboard, R.id.dashboardFragment),
            NavItem(binding.navProduksi,  binding.iconProduksi,  binding.labelProduksi,  R.id.productionFragment),
            NavItem(binding.navRiwayat,   binding.iconRiwayat,   binding.labelRiwayat,   R.id.historyFragment),
            NavItem(binding.navSeting,    binding.iconSeting,    binding.labelSeting,    R.id.settingsFragment)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NotificationHelper.createChannel(this)
        requestNotifPermission()

        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController

        navItems.forEach { item ->
            item.container.setOnClickListener { navigateTo(item.destId) }
        }

        navController.addOnDestinationChangedListener { _, dest, _ ->
            updateNavSelection(dest.id)
        }
    }

    private fun navigateTo(destId: Int) {
        if (navController.currentDestination?.id == destId) return
        navController.navigate(destId)
    }

    private fun updateNavSelection(activeId: Int) {
        val accentColor = Color.parseColor("#A371F7")
        val mutedColor = Color.parseColor("#8B949E")

        navItems.forEach { item ->
            val isActive = item.destId == activeId
            item.container.background = if (isActive)
                getDrawable(R.drawable.bg_pill_active) else null
            item.icon.setColorFilter(
                if (isActive) accentColor else mutedColor,
                PorterDuff.Mode.SRC_IN
            )
            item.label.setTextColor(if (isActive) accentColor else mutedColor)
        }
    }

    private fun requestNotifPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }
    }
}