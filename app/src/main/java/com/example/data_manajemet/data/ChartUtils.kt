package com.example.data_manajemet.data

import com.github.mikephil.charting.data.*

fun createBarDataFromPreview(previewLines: List<List<String>>, columnIndex: Int): BarData {
    val barEntries = mutableListOf<BarEntry>()

    previewLines.drop(1).forEachIndexed { index, row ->
        val value = row.getOrNull(columnIndex)?.toFloatOrNull()
        if (value != null) {
            barEntries.add(BarEntry(index.toFloat(), value))
        }
    }

    val dataSet = BarDataSet(barEntries, "Bar Chart")
    dataSet.color = android.graphics.Color.rgb(61, 0, 102)

    return BarData(dataSet)
}


// Fungsi untuk buat CandleEntry (boxplot) dari data preview pada kolom tertentu
fun createCandleEntriesFromPreview(previewLines: List<List<String>>, columnIndex: Int): List<CandleEntry> {
    val values = previewLines.drop(1).mapNotNull {
        it.getOrNull(columnIndex)?.toFloatOrNull()
    }

    if (values.isEmpty()) return emptyList()

    val sorted = values.sorted()
    val q1 = sorted[sorted.size / 4]
    val q3 = sorted[3 * sorted.size / 4]
    val median = sorted[sorted.size / 2]
    val min = sorted.first()
    val max = sorted.last()

    return listOf(
        CandleEntry(
            0f,    // posisi x
            max,   // high
            min,   // low
            q3,    // open
            q1     // close
        )
    )
}

// Fungsi untuk buat CandleData dari CandleEntry (supaya mudah dipakai di chart)
fun createCandleDataFromEntries(candleEntries: List<CandleEntry>): CandleData {
    val dataSet = CandleDataSet(candleEntries, "Boxplot (Outlier Detection)")
    dataSet.color = android.graphics.Color.rgb(61, 0, 102)
    dataSet.shadowColor = android.graphics.Color.DKGRAY
    dataSet.decreasingColor = android.graphics.Color.RED
    dataSet.increasingColor = android.graphics.Color.GREEN
    dataSet.neutralColor = android.graphics.Color.BLUE
    dataSet.setDrawValues(false) // sembunyikan nilai di atas candle

    return CandleData(dataSet)
}
