package com.akillifinans.app.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.akillifinans.app.data.model.StockDetail
import com.akillifinans.app.ui.components.RatioCard
import com.akillifinans.app.ui.theme.*
import com.akillifinans.app.ui.viewmodel.StockDetailUiState
import com.akillifinans.app.ui.viewmodel.StockDetailViewModel
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween

/**
 * Hisse Detay & Çarpan Analizi Ekranı.
 *
 * - Hisse başlık & fiyat bilgisi
 * - Çarpan Izgarası (F/K, PD/DD, FD/FAVÖK, Net Borç/FAVÖK, PEG)
 * - Akıllı Analiz Kartı (is_cheap_by_fk → yeşil ✅ / sarı ⚠️)
 * - Sektör kıyaslama özeti
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDetailScreen(
    symbol: String,
    onBack: () -> Unit,
    viewModel: StockDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(symbol) {
        viewModel.loadStockDetail(symbol)
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.resetState() }
    }

    val isFavorite = (uiState as? StockDetailUiState.Success)?.isFavorite ?: false
    val heartColor by animateColorAsState(
        targetValue = if (isFavorite) FavoriteRed else OnSurfaceVar,
        animationSpec = tween(300),
        label = "detailHeartColor"
    )

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = symbol,
                        style = MaterialTheme.typography.titleLarge,
                        color = OnBackground,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = OnBackground
                        )
                    }
                },
                actions = {
                    // Favori butonu — sadece Success durumunda aktif
                    if (uiState is StockDetailUiState.Success) {
                        IconButton(onClick = { viewModel.toggleFavorite(symbol) }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isFavorite) "Favoriden çıkar" else "Favorilere ekle",
                                tint = heartColor
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = uiState,
            transitionSpec = { fadeIn() + slideInVertically { it / 4 } togetherWith fadeOut() },
            label = "detailState"
        ) { state ->
            when (state) {
                is StockDetailUiState.Idle    -> {}
                is StockDetailUiState.Loading -> LoadingDetail(paddingValues)
                is StockDetailUiState.Error   -> ErrorDetail(state.message, paddingValues)
                is StockDetailUiState.Success -> StockDetailContent(
                    detail        = state.stockDetail,
                    paddingValues = paddingValues
                )
            }
        }
    }
}

// ── İçerik Bölümü ────────────────────────────────────────────────────────────

@Composable
private fun StockDetailContent(
    detail: StockDetail,
    paddingValues: PaddingValues
) {
    val isGain    = detail.dailyChangePercent >= 0
    val priceColor = if (isGain) GainGreen else LossRed
    val isCheap   = detail.analysis.isCheapByFk

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ── Fiyat & Değişim Başlık Kartı ─────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Surface)
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = detail.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVar
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "₺%.2f".format(detail.currentPrice),
                        style = MaterialTheme.typography.headlineLarge,
                        color = OnBackground,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 36.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isGain) GainGreenBg else LossRedBg)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (isGain) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = priceColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "%+.2f%%".format(detail.dailyChangePercent),
                            style = MaterialTheme.typography.labelMedium,
                            color = priceColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "🏭 ${detail.sector}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Secondary
                )
            }
        }

        // ── Çarpanlar Bölüm Başlığı ───────────────────────────────────────
        Text(
            text = "Değerleme Çarpanları",
            style = MaterialTheme.typography.titleSmall,
            color = OnSurfaceVar,
            fontWeight = FontWeight.SemiBold
        )

        // ── Çarpan Izgarası (3 sütun) ────────────────────────────────────
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RatioCard(
                    label     = "F/K",
                    value     = "%.2f".format(detail.multiples.fk),
                    subLabel  = "Sektör: %.2f".format(detail.sectorAverages.sectorFk),
                    highlight = detail.multiples.fk < detail.sectorAverages.sectorFk,
                    modifier  = Modifier.weight(1f)
                )
                RatioCard(
                    label     = "PD/DD",
                    value     = "%.2f".format(detail.multiples.pddd),
                    subLabel  = "Sektör: %.2f".format(detail.sectorAverages.sectorPddd),
                    highlight = detail.multiples.pddd < detail.sectorAverages.sectorPddd,
                    modifier  = Modifier.weight(1f)
                )
                RatioCard(
                    label    = "FD/FAVÖK",
                    value    = "%.2f".format(detail.multiples.fdFavok),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RatioCard(
                    label    = "Net Borç/FAVÖK",
                    value    = "%.2f".format(detail.multiples.netBorcFavok),
                    modifier = Modifier.weight(1f)
                )
                RatioCard(
                    label    = "PEG Oranı",
                    value    = "%.2f".format(detail.multiples.pegOrani),
                    highlight = detail.multiples.pegOrani < 1.0,
                    subLabel  = if (detail.multiples.pegOrani < 1.0) "Büyüme İskontosu" else null,
                    modifier = Modifier.weight(1f)
                )
                // Boş hücre (3'e tamamlamak için)
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        // ── Akıllı Analiz Kartı ───────────────────────────────────────────
        Text(
            text = "Akıllı Analiz",
            style = MaterialTheme.typography.titleSmall,
            color = OnSurfaceVar,
            fontWeight = FontWeight.SemiBold
        )

        SmartAnalysisCard(
            isCheapByFk       = isCheap,
            statusMessage     = detail.analysis.statusMessage,
            discountPercent   = detail.analysis.fkDiscountPercent
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ── Akıllı Analiz Kartı Bileşeni ─────────────────────────────────────────────

@Composable
private fun SmartAnalysisCard(
    isCheapByFk: Boolean,
    statusMessage: String,
    discountPercent: Double?
) {
    val bgColor     = if (isCheapByFk) GainGreenBg else BargainGoldBg
    val borderColor = if (isCheapByFk) GainGreen    else BargainGold
    val icon        = if (isCheapByFk) Icons.Default.CheckCircle else Icons.Default.Warning
    val iconColor   = if (isCheapByFk) GainGreen    else BargainGold
    val labelText   = if (isCheapByFk)
        "✅ Sektörüne Göre Uygun Çarpanlı"
    else
        "⚠️ Sektör Ortalamasının Üzerinde"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = labelText,
                    style = MaterialTheme.typography.labelLarge,
                    color = iconColor,
                    fontWeight = FontWeight.Bold
                )
            }

            if (discountPercent != null && isCheapByFk) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "F/K İskontosu: %.1f%%".format(discountPercent),
                    style = MaterialTheme.typography.headlineSmall,
                    color = GainGreen,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = borderColor.copy(alpha = 0.3f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = OnSurfaceVar,
                    modifier = Modifier.size(16.dp).padding(top = 2.dp)
                )
                Text(
                    text = statusMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurface,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

// ── Loading & Error ───────────────────────────────────────────────────────────

@Composable
private fun LoadingDetail(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Primary, strokeWidth = 3.dp)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Detaylar yükleniyor...", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVar)
        }
    }
}

@Composable
private fun ErrorDetail(message: String, paddingValues: PaddingValues) {
    Box(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Text("⚠️", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(message, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVar)
        }
    }
}
