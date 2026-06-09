package com.bananadryer.ui.production

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bananadryer.databinding.FragmentProductionBinding

class ProductionFragment : Fragment() {

    private var _binding: FragmentProductionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cek koneksi saat fragment dibuka
        viewModel.checkKoneksi()

        viewModel.isConnected.observe(viewLifecycleOwner) { connected ->
            connected ?: return@observe
            if (connected) {
                binding.tvStartStatus.text = "✅ ESP32 terhubung, siap mengirim perintah"
                binding.tvStartStatus.setTextColor(Color.parseColor("#3FB950"))
                binding.btnStart.isEnabled = true
            } else {
                binding.tvStartStatus.text =
                    "❌ ESP32 tidak terhubung!\nCek IP di menu Seting."
                binding.tvStartStatus.setTextColor(Color.parseColor("#F85149"))
                binding.btnStart.isEnabled = false
            }
        }

        viewModel.startStatus.observe(viewLifecycleOwner) { status ->
            binding.tvStartStatus.text = status
            binding.tvStartStatus.setTextColor(
                when {
                    status.startsWith("✅") -> Color.parseColor("#3FB950")
                    status.startsWith("❌") -> Color.parseColor("#F85149")
                    status.startsWith("⚠️") -> Color.parseColor("#D29922")
                    else -> Color.parseColor("#8B949E")
                }
            )
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.btnStart.isEnabled = !loading
            binding.btnStart.text = if (loading) "Memproses..." else "START MESIN"
        }

        binding.btnStart.setOnClickListener {
            val tb1 = binding.etTb1.text.toString().toLongOrNull() ?: 0L
            val tb2 = binding.etTb2.text.toString().toLongOrNull() ?: 0L
            val dc = binding.etDc.text.toString().toIntOrNull() ?: 0
            viewModel.startProses(tb1, tb2, dc)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}