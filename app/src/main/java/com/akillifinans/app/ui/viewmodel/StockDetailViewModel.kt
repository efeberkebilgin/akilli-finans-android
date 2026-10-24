package com.akillifinans.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.akillifinans.app.data.local.AppDatabase
import com.akillifinans.app.data.model.StockDetail
import com.akillifinans.app.data.repository.StockRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Hisse detay ekranı UI durumu */
sealed class StockDetailUiState {
    data object Idle    : StockDetailUiState()
    data object Loading : StockDetailUiState()
    data class Success(val stockDetail: StockDetail, val isFavorite: Boolean = false) : StockDetailUiState()
    data class Error(val message: String) : StockDetailUiState()
}

/**
 * Hisse detay ve çarpan analizi ViewModel.
 * Seçilen sembol değiştiğinde yeni API isteği yapılır.
 * Room'dan isFavorite durumu okunur ve detay ekranında kalp ikonu için kullanılır.
 */
class StockDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val dao        = AppDatabase.getInstance(application).stockDao()
    private val repository = StockRepository(dao)

    private val _uiState = MutableStateFlow<StockDetailUiState>(StockDetailUiState.Idle)
    val uiState: StateFlow<StockDetailUiState> = _uiState.asStateFlow()

    fun loadStockDetail(symbol: String) {
        viewModelScope.launch {
            _uiState.value = StockDetailUiState.Loading
            try {
                val detail     = repository.getStockDetail(symbol)
                val isFavorite = dao.isFavorite(symbol) ?: false
                _uiState.value = StockDetailUiState.Success(detail, isFavorite)
            } catch (e: Exception) {
                _uiState.value = StockDetailUiState.Error(
                    message = e.localizedMessage ?: "Detay yüklenemedi."
                )
            }
        }
    }

    fun toggleFavorite(symbol: String) {
        viewModelScope.launch {
            val current = _uiState.value
            if (current is StockDetailUiState.Success) {
                val newFav = !current.isFavorite
                repository.setFavorite(symbol, newFav)
                _uiState.value = current.copy(isFavorite = newFav)
            }
        }
    }

    fun resetState() {
        _uiState.value = StockDetailUiState.Idle
    }
}
