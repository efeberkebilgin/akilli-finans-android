package com.akillifinans.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * /api/stocks endpoint'inden dönen liste öğesi.
 * Backend StockSummary Pydantic modeline karşılık gelir.
 */
data class StockSummary(
    @SerializedName("symbol")
    val symbol: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("sector")
    val sector: String,

    @SerializedName("current_price")
    val currentPrice: Double,

    @SerializedName("daily_change_percent")
    val dailyChangePercent: Double
)
