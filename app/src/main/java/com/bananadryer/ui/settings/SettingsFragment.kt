package com.bananadryer.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bananadryer.databinding.FragmentSettingsBinding
import com.bananadryer.utils.PrefsManager

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = PrefsManager(requireContext())
        binding.etEspIp.setText(prefs.espIp)

        binding.btnSimpan.setOnClickListener {
            val ip = binding.etEspIp.text.toString().trim()
            if (ip.isEmpty()) {
                binding.tvSimpanStatus.text = "❌ IP tidak boleh kosong"
                binding.tvSimpanStatus.setTextColor(android.graphics.Color.parseColor("#F85149"))
                return@setOnClickListener
            }
            prefs.espIp = ip
            binding.tvSimpanStatus.text = "✅ IP berhasil disimpan"
            binding.tvSimpanStatus.setTextColor(android.graphics.Color.parseColor("#3FB950"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}