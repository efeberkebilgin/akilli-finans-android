package com.akillifinans.app.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton Retrofit istemcisi.
 *
 * BASE_URL: Android Emülatör'den host (fiziksel) makineye erişmek için
 * 10.0.2.2 özel IP adresi kullanılır. Bu adres emülatör içinde
 * host bilgisayarın localhost'una karşılık gelir.
 *
 * Gerçek cihaz kullanıyorsanız host makinenizin yerel IP adresini
 * (örn: 192.168.1.X) kullanmanız gerekir.
 */
object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8000/"

    /** OkHttp logging interceptor — Logcat'te request/response detaylarını gösterir */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /** Uygulama genelinde kullanılacak servis örneği */
    val stockApiService: StockApiService by lazy {
        retrofit.create(StockApiService::class.java)
    }
}
