package com.akillifinans.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.akillifinans.app.data.local.AppDatabase
import com.akillifinans.app.data.local.StockEntity
import com.akillifinans.app.data.repository.StockRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ── Tab Durumu ────────────────────────────────────────────────────────────────

/** Ana ekrandaki iki sekme seçeneği */
enum class StockTab { ALL, FAVORITES }

// ── UI Durumu ─────────────────────────────────────────────────────────────────

/**
 * Ana ekran UI durumu.
 * [isOffline] = true → Room verileri gösteriliyor, network bağlantısı yok.
 */
data class PortfolioUiState(
    val stocks: List<StockEntity> = emptyList(),
    val isLoading: Boolean = true,
    val isOffline: Boolean = false,
    val selectedTab: StockTab = StockTab.ALL,
    val snackbarMessage: String? = null
)

/**
 * Ana portföy ekranı ViewModel — Offline-First + Tab filtresi.
 *
 * AndroidViewModel kullanımı: AppDatabase için Application context gereklidir.
 *
 * Akış:
 *  1. init'te Room Flow'u dinlemeye başlar → anlık DB verisi UI'a gelir.
 *  2. refreshStocks() arka planda ağdan çeker; başarısız olursa SnackBar gösterir.
 *  3. selectedTab değiştiğinde doğru Flow kaynağı seçilir (ALL / FAVORITES).
 */
class PortfolioViewModel(application: Application) : AndroidViewModel(application) {

    private val dao        = AppDatabase.getInstance(application).stockDao()
    private val repository = StockRepository(dao)

    private val _uiState = MutableStateFlow(PortfolioUiState())
    val uiState: StateFlow<PortfolioUiState> = _uiState.asStateFlow()

    init {
        observeStocks()
        refreshStocks()
    }

    // ── Room Gözlem ───────────────────────────────────────────────────────────

    /**
     * Seçili sekmeye göre doğru Room Flow'una abone olur.
     * Tab değiştikçe flatMapLatest ile Flow kaynağı dinamik olarak değişir.
     */
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private fun observeStocks() {
        viewModelScope.launch {
            _uiState
                .map { it.selectedTab }
                .distinctUntilChanged()
                .flatMapLatest { tab ->
                    when (tab) {
                        StockTab.ALL       -> repository.observeAllStocks()
                        StockTab.FAVORITES -> repository.observeFavoriteStocks()
                    }
                }
                .collect { stocks ->
                    _uiState.update { it.copy(stocks = stocks, isLoading = false) }
                }
        }
    }

    // ── Ağ Yenileme ──────────────────────────────────────────────────────────

    /**
     * FastAPI'den verileri çekip Room'a yazar.
     * Hata durumunda SnackBar mesajı ayarlanır; Room verileri kaybolmaz.
     */
    fun refreshStocks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, snackbarMessage = null) }
            try {
                repository.refreshStocksFromNetwork()
                _uiState.update { it.copy(isLoading = false, isOffline = false) }
            } catch (e: Exception) {
                // Ağ hatası: Room'daki son verilerle devam et
                _uiState.update {
                    it.copy(
                        isLoading      = false,
                        isOffline      = true,
                        snackbarMessage = "Çevrimdışı moddasınız — son kaydedilen veriler gösteriliyor."
                    )
                }
            }
        }
    }

    // ── Tab Seçimi ────────────────────────────────────────────────────────────

    fun selectTab(tab: StockTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    // ── Favori Toggle ─────────────────────────────────────────────────────────

    /**
     * Hissenin favori durumunu tersine çevirir.
     * @param stock Mevcut entity (isFavorite durumu okunur)
     */
    fun toggleFavorite(stock: StockEntity) {
        viewModelScope.launch {
            repository.setFavorite(stock.symbol, !stock.isFavorite)
        }
    }

    // ── SnackBar Temizle ──────────────────────────────────────────────────────

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    // ── Özet Hesaplamaları ────────────────────────────────────────────────────

    fun getTotalPortfolioValue(stocks: List<StockEntity>): Double =
        stocks.sumOf { it.currentPrice }

    fun getDailyPnL(stocks: List<StockEntity>): Double =
        stocks.sumOf { it.currentPrice * (it.dailyChangePercent / 100.0) }
}
