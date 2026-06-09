package com.bananadryer.ui.history

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bananadryer.database.entity.ProductionHistory
import com.bananadryer.databinding.ItemHistoryBinding

class HistoryAdapter(
    private var items: List<ProductionHistory> = emptyList(),
    private val onClick: (ProductionHistory) -> Unit = {}
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvTanggal.text = item.tanggal
            tvWaktu.text = "${item.jamMulai} → ${item.jamSelesai}"
            tvSuhuKelembaban.text = "${item.suhuAkhir}°C / ${item.kelembabanAkhir}%"
            tvStatusBadge.text = item.status
            tvStatusBadge.setTextColor(
                when (item.status) {
                    "FINISHED" -> Color.parseColor("#3FB950")
                    "RUNNING"  -> Color.parseColor("#D29922")
                    "ERROR"    -> Color.parseColor("#F85149")
                    else       -> Color.parseColor("#8B949E")
                }
            )
            root.setOnClickListener { onClick(item) }
        }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<ProductionHistory>) {
        items = newItems
        notifyDataSetChanged()
    }
}