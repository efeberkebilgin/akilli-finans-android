package com.akillifinans.app.data.repository

import com.akillifinans.app.data.local.StockDao
import com.akillifinans.app.data.local.StockEntity
import com.akillifinans.app.data.model.StockDetail
import com.akillifinans.app.data.network.RetrofitClient
import kotlinx.coroutines.flow.Flow

/**
 * Veri deposu — Offline-First / Single Source of Truth mimarisi.
 *
 * ┌─────────────────────────────────────────────────────────────────┐
 * │  UI (Compose)  ←── Flow ─── Room (Single Source of Truth)     │
 * │                              ↑                                  │
 * │                     refreshStocksFromNetwork()                  │
 * │                              ↑                                  │
 * │                     FastAPI Backend (Retrofit)                  │
 * └─────────────────────────────────────────────────────────────────┘
 *
 * Veri akışı:
 *  1. UI, Room'dan gelen [Flow]'u dinlemeye başlar (anlık yerel veri gösterilir).
 *  2. [refreshStocksFromNetwork] arka planda API'ye istek atar.
 *  3. Gelen veriler Room'a yazılır; favori durumu korunur.
 *  4. Room Flow otomatik güncellenir → UI yenilenir (reaktif).
 *  5. Ağ hatası olursa Room'daki son veri görünmeye devam eder.
 *
 * @param dao  Room StockDao örneği (Activity/Application context'ten inject edilir)
 */
class StockRepository(private val dao: StockDao) {

    private val api = RetrofitClient.stockApiService

    // ── Yerel Veri Akışları (Room → Flow → UI) ───────────────────────────────

    /** Tüm hisseleri yerel DB'den reaktif olarak döner. */
    fun observeAllStocks(): Flow<List<StockEntity>> = dao.getAllStocks()

    /** Sadece favori hisseleri yerel DB'den döner (Portföyüm sekmesi). */
    fun observeFavoriteStocks(): Flow<List<StockEntity>> = dao.getFavoriteStocks()

    // ── Ağ Yenileme (Network → Room, isFavorite korunur) ─────────────────────

    /**
     * FastAPI backend'den hisse listesini çekip Room'a akıllıca yazar.
     *
     * Upsert stratejisi:
     *  - Yeni sembol → INSERT (isFavorite = false varsayılan)
     *  - Mevcut sembol → sadece fiyat/rasyo alanlarını UPDATE (isFavorite değişmez)
     *
     * @throws Exception Ağ hatası, zaman aşımı veya HTTP hataları.
     *   ViewModel bu istisnayı yakalar ve SnackBar gösterir.
     */
    suspend fun refreshStocksFromNetwork() {
        val summaries = api.getStockList()

        for (summary in summaries) {
            var fk = 0.0
            var pddd = 0.0
            try {
                val detail = api.getStockDetail(summary.symbol)
                fk   = detail.multiples.fk
                pddd = detail.multiples.pddd
            } catch (e: Exception) {
                // Detay çekilemezse rasyo sıfır bırakılır
            }

            val alreadyExists = dao.exists(summary.symbol) > 0
            if (alreadyExists) {
                // Sadece fiyat/rasyo güncelle → isFavorite korunur
                dao.updateStockData(
                    symbol             = summary.symbol,
                    name               = summary.name,
                    sector             = summary.sector,
                    currentPrice       = summary.currentPrice,
                    dailyChangePercent = summary.dailyChangePercent,
                    fk                 = fk,
                    pddd               = pddd
                )
            } else {
                // İlk kez görülen hisse → yeni kayıt ekle
                dao.insertStocks(
                    listOf(
                        StockEntity(
                            symbol             = summary.symbol,
                            name               = summary.name,
                            sector             = summary.sector,
                            currentPrice       = summary.currentPrice,
                            dailyChangePercent = summary.dailyChangePercent,
                            fk                 = fk,
                            pddd               = pddd,
                            isFavorite         = false
                        )
                    )
                )
            }
        }
    }

    // ── Favori Yönetimi ───────────────────────────────────────────────────────

    /**
     * Hissenin favori durumunu ayarlar.
     * @param symbol      Hisse kodu
     * @param isFavorite  Yeni durum (true = favorile, false = favoriden çıkar)
     */
    suspend fun setFavorite(symbol: String, isFavorite: Boolean) {
        dao.updateFavoriteStatus(symbol, isFavorite)
    }

    // ── Hisse Detayı (Ağ) ────────────────────────────────────────────────────

    /** Detay ekranı için API'den tek hisse verisi çeker (sadece network). */
    suspend fun getStockDetail(symbol: String): StockDetail {
        return api.getStockDetail(symbol.uppercase())
    }
}
