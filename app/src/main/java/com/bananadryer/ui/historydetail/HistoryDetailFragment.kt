package com.bananadryer.ui.historydetail

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bananadryer.database.AppDatabase
import com.bananadryer.databinding.FragmentHistoryDetailBinding
import kotlinx.coroutines.launch

class HistoryDetailFragment : Fragment() {

    private var _binding: FragmentHistoryDetailBinding? = null
    private val binding get() = _binding!!
    private val args: HistoryDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getInstance(requireContext())

        lifecycleScope.launch {
            val history = db.historyDao().getById(args.historyId)
            history?.let {
                binding.tvDetailTanggal.text = it.tanggal
                binding.tvDetailJamMulai.text = it.jamMulai
                binding.tvDetailJamSelesai.text = it.jamSelesai
                binding.tvDetailTb1.text = "${it.tb1} ms"
                binding.tvDetailTb2.text = "${it.tb2} ms"
                binding.tvDetailDc.text = "${it.dc}"
                binding.tvDetailSuhu.text = "${it.suhuAkhir}°C"
                binding.tvDetailKelembaban.text = "${it.kelembabanAkhir}%"
                binding.tvDetailStatus.text = it.status
                binding.tvDetailStatus.setTextColor(
                    when (it.status) {
                        "FINISHED" -> Color.parseColor("#3FB950")
                        "RUNNING"  -> Color.parseColor("#D29922")
                        "ERROR"    -> Color.parseColor("#F85149")
                        else       -> Color.parseColor("#8B949E")
                    }
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}