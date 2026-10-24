package com.akillifinans.app.data.network

import com.akillifinans.app.data.model.StockDetail
import com.akillifinans.app.data.model.StockSummary
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit API arayüzü — FastAPI backend endpoint'lerine karşılık gelir.
 * Base URL: http://10.0.2.2:8000/ (emülatörden host makine erişimi)
 */
interface StockApiService {

    /**
     * GET /api/stocks
     * Tüm hisselerin özet listesini döner.
     */
    @GET("api/stocks")
    suspend fun getStockList(): List<StockSummary>

    /**
     * GET /api/stocks/{symbol}
     * Belirli bir hissenin detaylı bilgilerini ve akıllı analizini döner.
     * @param symbol Hisse sembolü (örn: THYAO, LINK)
     */
    @GET("api/stocks/{symbol}")
    suspend fun getStockDetail(@Path("symbol") symbol: String): StockDetail
}
