package com.akillifinans.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room veritabanı tanımı.
 *
 * Singleton örüntüsüyle tek bir veritabanı örneği yönetilir.
 * Uygulama genelinde [AppDatabase.getInstance] ile erişilir.
 *
 * version = 1: İlk şema sürümü.
 * exportSchema = false: CI ortamında şema JSON'ı oluşturulmasın.
 */
@Database(
    entities = [StockEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun stockDao(): StockDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Thread-safe singleton fabrika metodu.
         * @param context Application context
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "akilli_finans_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
