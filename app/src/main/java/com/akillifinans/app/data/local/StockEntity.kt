package com.akillifinans.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room SQLite entity — yerel veritabanında hisse verilerini saklar.
 *
 * @param symbol       Borsa kodu (PrimaryKey) — örn: "THYAO"
 * @param name         Şirket unvanı
 * @param sector       Faaliyet sektörü
 * @param currentPrice Anlık fiyat (TL)
 * @param dailyChangePercent Günlük yüzde değişim
 * @param fk           Fiyat/Kazanç oranı (P/E)
 * @param pddd         Piyasa Değeri / Defter Değeri (P/B)
 * @param isFavorite   Kullanıcı favorisi mi? (Portföy filtresi için)
 */
@Entity(tableName = "stocks")
data class StockEntity(
    @PrimaryKey
    val symbol: String,
    val name: String,
    val sector: String,
    val currentPrice: Double,
    val dailyChangePercent: Double,
    val fk: Double,
    val pddd: Double,
    val isFavorite: Boolean = false
)
