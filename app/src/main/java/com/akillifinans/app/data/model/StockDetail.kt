package com.akillifinans.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * Değerleme çarpanları — /api/stocks/{symbol} yanıtının "multiples" alanına karşılık gelir.
 * Backend: ValuationMultiples Pydantic modeli
 */
data class ValuationMultiples(
    @SerializedName("fk")
    val fk: Double,

    @SerializedName("pddd")
    val pddd: Double,

    @SerializedName("fd_favok")
    val fdFavok: Double,

    @SerializedName("net_borc_favok")
    val netBorcFavok: Double,

    @SerializedName("peg_orani")
    val pegOrani: Double
)

/**
 * Sektör ortalama çarpanları — "sector_averages" alanı.
 * Backend: SectorAverages Pydantic modeli
 */
data class SectorAverages(
    @SerializedName("sector_fk")
    val sectorFk: Double,

    @SerializedName("sector_pddd")
    val sectorPddd: Double
)

/**
 * Akıllı analiz sonucu — "analysis" alanı.
 * Backend: StockAnalysis Pydantic modeli
 */
data class StockAnalysis(
    @SerializedName("is_cheap_by_fk")
    val isCheapByFk: Boolean,

    @SerializedName("status_message")
    val statusMessage: String,

    @SerializedName("fk_discount_percent")
    val fkDiscountPercent: Double?
)

/**
 * /api/stocks/{symbol} tam detay yanıtı.
 * Backend: StockDetail Pydantic modeli
 */
data class StockDetail(
    @SerializedName("symbol")
    val symbol: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("sector")
    val sector: String,

    @SerializedName("current_price")
    val currentPrice: Double,

    @SerializedName("daily_change_percent")
    val dailyChangePercent: Double,

    @SerializedName("multiples")
    val multiples: ValuationMultiples,

    @SerializedName("sector_averages")
    val sectorAverages: SectorAverages,

    @SerializedName("analysis")
    val analysis: StockAnalysis
)
