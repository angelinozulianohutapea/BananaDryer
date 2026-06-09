package com.bananadryer.ui.history

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bananadryer.database.AppDatabase
import com.bananadryer.databinding.FragmentHistoryBinding
import com.bananadryer.utils.PdfExportUtil
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: HistoryAdapter
    private var currentList = listOf<com.bananadryer.database.entity.ProductionHistory>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = HistoryAdapter { history ->
            val action = HistoryFragmentDirections
                .actionHistoryToDetail(history.id)
            findNavController().navigate(action)
        }

        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter

        // Swipe to delete
        val swipeCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val item = currentList[position]

                lifecycleScope.launch {
                    val db = AppDatabase.getInstance(requireContext())
                    db.historyDao().delete(item)
                    Toast.makeText(
                        requireContext(),
                        "Riwayat dihapus",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onChildDraw(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val paint = Paint()
                val iconSize = 24f
                val iconMargin = 40f

                if (dX < 0) {
                    // Swipe kiri — merah
                    paint.color = Color.parseColor("#F85149")
                    val background = RectF(
                        itemView.right + dX,
                        itemView.top.toFloat() + 10,
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat() - 10
                    )
                    canvas.drawRoundRect(background, 12f, 12f, paint)

                    // Label HAPUS
                    paint.color = Color.WHITE
                    paint.textSize = 28f
                    paint.textAlign = Paint.Align.RIGHT
                    canvas.drawText(
                        "🗑 Hapus",
                        itemView.right - iconMargin,
                        itemView.top + (itemView.height / 2f) + 10f,
                        paint
                    )
                } else if (dX > 0) {
                    // Swipe kanan — merah
                    paint.color = Color.parseColor("#F85149")
                    val background = RectF(
                        itemView.left.toFloat(),
                        itemView.top.toFloat() + 10,
                        itemView.left + dX,
                        itemView.bottom.toFloat() - 10
                    )
                    canvas.drawRoundRect(background, 12f, 12f, paint)

                    // Label HAPUS
                    paint.color = Color.WHITE
                    paint.textSize = 28f
                    paint.textAlign = Paint.Align.LEFT
                    canvas.drawText(
                        "Hapus 🗑",
                        itemView.left + iconMargin,
                        itemView.top + (itemView.height / 2f) + 10f,
                        paint
                    )
                }

                super.onChildDraw(
                    canvas, recyclerView, viewHolder,
                    dX, dY, actionState, isCurrentlyActive
                )
            }
        }

        ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.rvHistory)

        val db = AppDatabase.getInstance(requireContext())
        db.historyDao().getAllHistory().observe(viewLifecycleOwner) { list ->
            currentList = list
            adapter.updateData(list)
            binding.tvEmptyHistory.visibility =
                if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.btnExportPdf.setOnClickListener {
            if (currentList.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Tidak ada data untuk diekspor",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            try {
                PdfExportUtil.exportAndShare(requireContext(), currentList)
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Gagal export: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}