package com.akillifinans.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO — StockEntity CRUD operasyonları.
 *
 * Tüm liste sorgular Flow döndürür; bu sayede Room verisi değiştiğinde
 * UI otomatik olarak güncellenir (reaktif mimari).
 */
@Dao
interface StockDao {

    /**
     * Hisse listesini yerel veritabanına yazar.
     * Aynı sembol varsa üzerine yazar (REPLACE) — sunucudan gelen güncel
     * veriyi önbelleğe almak için kullanılır.
     * NOT: isFavorite korunması için insertOrIgnore + updateFields tercih edilebilir.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStocks(stocks: List<StockEntity>)

    /**
     * Tüm hisseleri reaktif Flow olarak döner.
     * Favori durumu ne olursa olsun tüm kayıtları içerir.
     */
    @Query("SELECT * FROM stocks ORDER BY symbol ASC")
    fun getAllStocks(): Flow<List<StockEntity>>

    /**
     * Sadece isFavorite = true olan hisseleri döner (Portföyüm sekmesi).
     */
    @Query("SELECT * FROM stocks WHERE isFavorite = 1 ORDER BY symbol ASC")
    fun getFavoriteStocks(): Flow<List<StockEntity>>

    /**
     * Belirtilen hissenin favori durumunu günceller.
     * @param symbol      Hisse kodu
     * @param isFavorite  Yeni favori durumu
     */
    @Query("UPDATE stocks SET isFavorite = :isFavorite WHERE symbol = :symbol")
    suspend fun updateFavoriteStatus(symbol: String, isFavorite: Boolean)

    /**
     * Ağ yenilemesi sırasında fiyat/rasyo bilgilerini günceller,
     * ancak isFavorite alanına dokunmaz (favori durumu korunur).
     */
    @Query("""
        UPDATE stocks 
        SET name = :name,
            sector = :sector,
            currentPrice = :currentPrice,
            dailyChangePercent = :dailyChangePercent,
            fk = :fk,
            pddd = :pddd
        WHERE symbol = :symbol
    """)
    suspend fun updateStockData(
        symbol: String,
        name: String,
        sector: String,
        currentPrice: Double,
        dailyChangePercent: Double,
        fk: Double,
        pddd: Double
    )

    /**
     * Verilen sembolün DB'de var olup olmadığını kontrol eder.
     * Yeni kayıt eklenecek mi, mevcut güncellenecek mi kararı için.
     */
    @Query("SELECT COUNT(*) FROM stocks WHERE symbol = :symbol")
    suspend fun exists(symbol: String): Int

    /**
     * Belirtilen sembolün favori durumunu döner.
     */
    @Query("SELECT isFavorite FROM stocks WHERE symbol = :symbol LIMIT 1")
    suspend fun isFavorite(symbol: String): Boolean?

    /**
     * Belirtilen sembolün entity'sini döner.
     */
    @Query("SELECT * FROM stocks WHERE symbol = :symbol LIMIT 1")
    suspend fun getStockBySymbol(symbol: String): StockEntity?
}
