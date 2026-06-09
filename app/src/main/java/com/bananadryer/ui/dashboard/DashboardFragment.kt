package com.bananadryer.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bananadryer.R
import com.bananadryer.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var prefs: com.bananadryer.utils.PrefsManager

    // DIHAPUS: private var runningStartTime: Long = 0L
    // Sekarang pakai viewModel.runningStartTime

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = com.bananadryer.utils.PrefsManager(requireContext())

        val ip = viewModel.espIp
        binding.tvEspIp.text = if (ip.isNotEmpty()) ip else "--"

        viewModel.monitor.observe(viewLifecycleOwner) { data ->
            binding.tvSuhu.text = String.format("%.1f", data.temperature)
            binding.tvKelembaban.text = String.format("%.0f", data.humidity)
            binding.tvChartNow.text = "${String.format("%.1f", data.temperature)}°C"

            val statusColor = when (data.status) {
                "RUNNING"  -> Color.parseColor("#D29922")
                "FINISHED" -> Color.parseColor("#3FB950")
                "ERROR"    -> Color.parseColor("#F85149")
                else       -> Color.parseColor("#A371F7")
            }
            binding.tvStatus.text = data.status
            binding.tvStatus.setTextColor(statusColor)

            if (data.status == "RUNNING") {
                // Pakai ViewModel, tidak reset saat navigasi
                viewModel.setRunningStart()
                binding.layoutProgress.visibility = View.VISIBLE
                binding.layoutTimer.visibility = View.VISIBLE
                updateProgress()
            } else {
                binding.layoutProgress.visibility = View.GONE
                binding.layoutTimer.visibility = View.GONE
                viewModel.resetRunningStart()
                resetPhases()
            }

            binding.tvLastStatus.text = data.status
            binding.tvLastStatus.setTextColor(statusColor)
        }

        viewModel.chartPoint.observe(viewLifecycleOwner) { temp ->
            binding.tempChart.addPoint(temp)
        }

        viewModel.prevTemp.observe(viewLifecycleOwner) { prev ->
            val curr = viewModel.monitor.value?.temperature ?: return@observe
            if (prev == null || prev == 0f) return@observe
            val delta = curr - prev
            val sign = if (delta >= 0) "▲" else "▼"
            binding.tvSuhuDelta.text = "$sign ${String.format("%.1f", Math.abs(delta))}"
            binding.tvSuhuDelta.setTextColor(
                if (delta >= 0) Color.parseColor("#D29922")
                else Color.parseColor("#3FB950")
            )
        }

        viewModel.prevHumid.observe(viewLifecycleOwner) { prev ->
            val curr = viewModel.monitor.value?.humidity ?: return@observe
            if (prev == null || prev == 0f) return@observe
            val delta = curr - prev
            val sign = if (delta >= 0) "▲" else "▼"
            binding.tvKelembabanDelta.text = "$sign ${String.format("%.1f", Math.abs(delta))}"
        }

        viewModel.isConnected.observe(viewLifecycleOwner) { connected ->
            if (connected) {
                binding.tvEspIp.setTextColor(Color.parseColor("#3FB950"))
                binding.viewConnectionDot.setBackgroundResource(R.drawable.bg_dot_green)
            } else {
                binding.tvEspIp.setTextColor(Color.parseColor("#8B949E"))
                binding.viewConnectionDot.setBackgroundResource(R.drawable.bg_dot_red)
            }
        }

        viewModel.lastParams.observe(viewLifecycleOwner) { params ->
            binding.tvParamTb1.text = "${params.first} ms"
            binding.tvParamTb2.text = "${params.second} ms"
            binding.tvParamDc.text  = "${params.third}"
        }

        viewModel.totalSesi.observe(viewLifecycleOwner) { total ->
            binding.tvTotalSesi.text = "$total sesi"
        }

        viewModel.statusChanged.observe(viewLifecycleOwner) { status ->
            val pesan = when (status) {
                "FINISHED" -> "✅ Mesin selesai! Riwayat telah diperbarui."
                "ERROR"    -> "❌ Mesin error! Periksa perangkat."
                else -> return@observe
            }
            Toast.makeText(requireContext(), pesan, Toast.LENGTH_LONG).show()
        }

        binding.btnKeProduksi.setOnClickListener {
            findNavController().navigate(R.id.productionFragment)
        }
        binding.btnKeRiwayat.setOnClickListener {
            findNavController().navigate(R.id.historyFragment)
        }

        viewModel.startPolling()
    }

    private fun updateProgress() {
        // Pakai viewModel.runningStartTime, bukan variabel lokal
        val elapsed = System.currentTimeMillis() - viewModel.runningStartTime
        val estimated = viewModel.totalDurasiMs.takeIf { it > 0 } ?: (5 * 60 * 1000L)
        val pct = ((elapsed.toFloat() / estimated) * 100).coerceIn(0f, 100f).toInt()
        val sisa = ((estimated - elapsed) / 1000).coerceAtLeast(0)

        binding.tvProgressPct.text = "$pct%"
        binding.tvTimer.text = String.format("%02d:%02d", sisa / 60, sisa % 60)

        binding.viewProgressFill.post {
            val parent = (binding.viewProgressFill.parent as View)
            val targetWidth = (parent.width * pct / 100f).toInt()
            binding.viewProgressFill.layoutParams =
                binding.viewProgressFill.layoutParams.apply { width = targetWidth }
            binding.viewProgressFill.requestLayout()
        }

        val tb1Ms = prefs.lastTb1
        if (elapsed < tb1Ms) {
            binding.phaseIris.background =
                requireContext().getDrawable(R.drawable.bg_phase_active)
            binding.tvPhaseIrisLabel.setTextColor(Color.parseColor("#A371F7"))
            binding.tvPhaseIrisVal.text = "Berjalan"
            binding.tvPhaseIrisVal.setTextColor(Color.parseColor("#A371F7"))
            binding.phaseKering.background =
                requireContext().getDrawable(R.drawable.bg_phase_idle)
            binding.tvPhaseKeringLabel.setTextColor(Color.parseColor("#8B949E"))
            binding.tvPhaseKeringVal.text = "Menunggu"
            binding.tvPhaseKeringVal.setTextColor(Color.parseColor("#8B949E"))
        } else {
            binding.phaseIris.background =
                requireContext().getDrawable(R.drawable.bg_phase_done)
            binding.tvPhaseIrisLabel.setTextColor(Color.parseColor("#3FB950"))
            binding.tvPhaseIrisVal.text = "Selesai"
            binding.tvPhaseIrisVal.setTextColor(Color.parseColor("#3FB950"))
            binding.phaseKering.background =
                requireContext().getDrawable(R.drawable.bg_phase_active)
            binding.tvPhaseKeringLabel.setTextColor(Color.parseColor("#A371F7"))
            binding.tvPhaseKeringVal.text = "Berjalan"
            binding.tvPhaseKeringVal.setTextColor(Color.parseColor("#A371F7"))
        }
    }

    private fun resetPhases() {
        listOf(binding.phaseIris, binding.phaseKering).forEach {
            it.background = requireContext().getDrawable(R.drawable.bg_phase_idle)
        }
        listOf(
            binding.tvPhaseIrisLabel, binding.tvPhaseIrisVal,
            binding.tvPhaseKeringLabel, binding.tvPhaseKeringVal
        ).forEach { it.setTextColor(Color.parseColor("#8B949E")) }
        binding.tvPhaseIrisVal.text = "Menunggu"
        binding.tvPhaseKeringVal.text = "Menunggu"
    }

    override fun onResume() {
        super.onResume()
        binding.tvEspIp.text = viewModel.espIp
        viewModel.startPolling()
    }
    override fun onPause() { super.onPause(); viewModel.stopPolling() }
    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}