package com.bananadryer.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.bananadryer.database.entity.ProductionHistory
import com.itextpdf.text.Document
import com.itextpdf.text.Font
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Phrase
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Element
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExportUtil {

    fun exportAndShare(context: Context, historyList: List<ProductionHistory>) {
        val file = buildPdf(context, historyList)
        sharePdf(context, file)
    }

    private fun buildPdf(
        context: Context,
        historyList: List<ProductionHistory>
    ): File {
        val document = Document()
        val fileName = "riwayat_produksi_${
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        }.pdf"
        val file = File(context.getExternalFilesDir(null), fileName)
        PdfWriter.getInstance(document, FileOutputStream(file))
        document.open()

        // Font
        val titleFont = Font(Font.FontFamily.HELVETICA, 16f, Font.BOLD)
        val headerFont = Font(Font.FontFamily.HELVETICA, 10f, Font.BOLD, BaseColor.WHITE)
        val bodyFont = Font(Font.FontFamily.HELVETICA, 10f)
        val subFont = Font(Font.FontFamily.HELVETICA, 11f, Font.BOLD)

        // Judul
        val title = Paragraph("Laporan Riwayat Produksi\nBanana Dryer Controller", titleFont)
        title.alignment = Element.ALIGN_CENTER
        title.spacingAfter = 6f
        document.add(title)

        val tanggalCetak = Paragraph(
            "Dicetak: ${
                SimpleDateFormat("dd MMMM yyyy HH:mm", Locale("id")).format(Date())
            }",
            bodyFont
        )
        tanggalCetak.alignment = Element.ALIGN_CENTER
        tanggalCetak.spacingAfter = 16f
        document.add(tanggalCetak)

        if (historyList.isEmpty()) {
            document.add(Paragraph("Tidak ada data riwayat.", bodyFont))
            document.close()
            return file
        }

        // Tabel
        val table = PdfPTable(6)
        table.widthPercentage = 100f
        table.setWidths(floatArrayOf(1.5f, 1.5f, 1.5f, 1.2f, 1.2f, 1.2f))

        // Header tabel
        val headers = listOf("Tanggal", "Mulai", "Selesai", "TB1 (ms)", "TB2 (ms)", "Status")
        headers.forEach { h ->
            val cell = PdfPCell(Phrase(h, headerFont))
            cell.backgroundColor = BaseColor(31, 111, 235) // #1F6FEB
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.paddingTop = 6f
            cell.paddingBottom = 6f
            table.addCell(cell)
        }

        // Isi tabel
        historyList.forEach { item ->
            val rowData = listOf(
                item.tanggal,
                item.jamMulai,
                item.jamSelesai,
                "${item.tb1}",
                "${item.tb2}",
                item.status
            )
            rowData.forEachIndexed { index, value ->
                val cell = PdfPCell(Phrase(value, bodyFont))
                cell.horizontalAlignment = if (index == 5)
                    Element.ALIGN_CENTER else Element.ALIGN_LEFT
                cell.paddingTop = 4f
                cell.paddingBottom = 4f
                cell.paddingLeft = 4f

                // Warna status
                if (index == 5) {
                    cell.backgroundColor = when (value) {
                        "FINISHED" -> BaseColor(63, 185, 80)   // hijau
                        "RUNNING"  -> BaseColor(210, 153, 34)  // kuning
                        "ERROR"    -> BaseColor(248, 81, 73)   // merah
                        else       -> BaseColor(139, 148, 158) // abu
                    }
                    cell.phrase = Phrase(value, headerFont)
                }
                table.addCell(cell)
            }
        }
        document.add(table)

        // Ringkasan
        document.add(Paragraph(" "))
        document.add(Paragraph("Ringkasan", subFont))

        val total = historyList.size
        val selesai = historyList.count { it.status == "FINISHED" }
        val error = historyList.count { it.status == "ERROR" }
        val running = historyList.count { it.status == "RUNNING" }

        document.add(Paragraph("Total Produksi  : $total", bodyFont))
        document.add(Paragraph("Selesai         : $selesai", bodyFont))
        document.add(Paragraph("Error           : $error", bodyFont))
        document.add(Paragraph("Masih Berjalan  : $running", bodyFont))

        document.close()
        return file
    }

    private fun sharePdf(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Bagikan PDF via"))
    }
}