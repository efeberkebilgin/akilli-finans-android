package com.akillifinans.app.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.akillifinans.app.data.local.StockEntity
import com.akillifinans.app.ui.components.StockListItemCard
import com.akillifinans.app.ui.theme.*
import com.akillifinans.app.ui.viewmodel.PortfolioViewModel
import com.akillifinans.app.ui.viewmodel.StockTab
import kotlinx.coroutines.launch

/**
 * Ana Portföy Ekranı — Checkpoint 3 güncellemesi.
 *
 * Yeni özellikler:
 *  - Room + Offline-First: veriler Room'dan Flow ile gelir
 *  - "Çevrimdışı moddasınız" SnackBar uyarısı
 *  - Tab Row: "Tüm Hisseler" / "Portföyüm ❤️"
 *  - Her hisse kartında animasyonlu favori (kalp) butonu
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    onStockClick: (String) -> Unit,
    viewModel: PortfolioViewModel = viewModel()
) {
    val uiState        by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope             = rememberCoroutineScope()

    // SnackBar tetikle
    LaunchedEffect(uiState.snackbarMessage) {
        val msg = uiState.snackbarMessage
        if (!msg.isNullOrEmpty()) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message  = msg,
                    duration = SnackbarDuration.Long
                )
                viewModel.clearSnackbar()
            }
        }
    }

    Scaffold(
        containerColor    = Background,
        snackbarHost      = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData   = data,
                    containerColor = SurfaceVar,
                    contentColor   = OnBackground,
                    actionColor    = Primary,
                    shape          = RoundedCornerShape(12.dp)
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text       = "📈 Akıllı Finans",
                            style      = MaterialTheme.typography.titleLarge,
                            color      = OnBackground,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text  = "Portföy & Takip Listesi",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVar
                        )
                    }
                },
                colors  = TopAppBarDefaults.topAppBarColors(containerColor = Background),
                actions = {
                    // Çevrimdışı ikonu
                    if (uiState.isOffline) {
                        Icon(
                            imageVector        = Icons.Default.WifiOff,
                            contentDescription = "Çevrimdışı",
                            tint               = LossRed,
                            modifier           = Modifier
                                .padding(end = 4.dp)
                                .size(20.dp)
                        )
                    }
                    IconButton(onClick = { viewModel.refreshStocks() }) {
                        Icon(
                            imageVector        = Icons.Default.Refresh,
                            contentDescription = "Yenile",
                            tint               = Primary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ── Tab Row ────────────────────────────────────────────────────
            StockTabRow(
                selectedTab   = uiState.selectedTab,
                onTabSelected = viewModel::selectTab
            )

            // ── İçerik ────────────────────────────────────────────────────
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading && uiState.stocks.isEmpty() -> LoadingView()
                    uiState.stocks.isEmpty() && !uiState.isLoading -> EmptyView(uiState.selectedTab)
                    else -> PortfolioContent(
                        stocks         = uiState.stocks,
                        viewModel      = viewModel,
                        onStockClick   = onStockClick,
                        onFavoriteClick = viewModel::toggleFavorite
                    )
                }

                // Yenileme spinner (veri zaten gösterilirken)
                if (uiState.isLoading && uiState.stocks.isNotEmpty()) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter),
                        color           = Primary,
                        trackColor      = PrimaryContainer
                    )
                }
            }
        }
    }
}

// ── Tab Satırı ────────────────────────────────────────────────────────────────

@Composable
private fun StockTabRow(
    selectedTab: StockTab,
    onTabSelected: (StockTab) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        containerColor   = Background,
        contentColor     = Primary,
        indicator        = { tabPositions ->
            if (selectedTab.ordinal < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier  = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                    color     = Primary,
                    height    = 3.dp
                )
            }
        }
    ) {
        Tab(
            selected = selectedTab == StockTab.ALL,
            onClick  = { onTabSelected(StockTab.ALL) },
            text = {
                Text(
                    "Tüm Hisseler",
                    fontWeight = if (selectedTab == StockTab.ALL) FontWeight.Bold else FontWeight.Normal,
                    color      = if (selectedTab == StockTab.ALL) Primary else OnSurfaceVar
                )
            }
        )
        Tab(
            selected = selectedTab == StockTab.FAVORITES,
            onClick  = { onTabSelected(StockTab.FAVORITES) },
            text = {
                Text(
                    "❤️ Portföyüm",
                    fontWeight = if (selectedTab == StockTab.FAVORITES) FontWeight.Bold else FontWeight.Normal,
                    color      = if (selectedTab == StockTab.FAVORITES) Primary else OnSurfaceVar
                )
            }
        )
    }
}

// ── Portföy İçerik Bölümü ────────────────────────────────────────────────────

@Composable
private fun PortfolioContent(
    stocks: List<StockEntity>,
    viewModel: PortfolioViewModel,
    onStockClick: (String) -> Unit,
    onFavoriteClick: (StockEntity) -> Unit
) {
    val totalValue = viewModel.getTotalPortfolioValue(stocks)
    val dailyPnL   = viewModel.getDailyPnL(stocks)
    val isPnLGain  = dailyPnL >= 0

    LazyColumn(
        modifier       = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Portföy Özet Kartı
        item {
            PortfolioSummaryCard(
                totalValue = totalValue,
                dailyPnL   = dailyPnL,
                isPnLGain  = isPnLGain
            )
        }

        // Bölüm Başlığı
        item {
            Text(
                text      = "Takip Listesi",
                style     = MaterialTheme.typography.titleSmall,
                color     = OnSurfaceVar,
                modifier  = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }

        // Hisse Listesi
        items(stocks, key = { it.symbol }) { stock ->
            AnimatedVisibility(
                visible  = true,
                enter    = fadeIn(tween(300)) + slideInVertically(tween(300)),
            ) {
                StockListItemCard(
                    stock           = stock,
                    onClick         = { onStockClick(stock.symbol) },
                    onFavoriteClick = onFavoriteClick
                )
            }
        }
    }
}

// ── Özet Kart ────────────────────────────────────────────────────────────────

@Composable
private fun PortfolioSummaryCard(
    totalValue: Double,
    dailyPnL: Double,
    isPnLGain: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Surface)
            .padding(24.dp)
    ) {
        Column {
            Text(
                text  = "Toplam Portföy Değeri",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVar
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text       = "₺%,.2f".format(totalValue),
                style      = MaterialTheme.typography.headlineLarge,
                color      = OnBackground,
                fontWeight = FontWeight.ExtraBold,
                fontSize   = 32.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = OutlineVar, thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment    = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (isPnLGain) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    contentDescription = null,
                    tint     = if (isPnLGain) GainGreen else LossRed,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text  = "Günlük Kar/Zarar:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVar
                )
                Text(
                    text       = "%+.2f ₺".format(dailyPnL),
                    style      = MaterialTheme.typography.bodyMedium,
                    color      = if (isPnLGain) GainGreen else LossRed,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ── Boş Durum ────────────────────────────────────────────────────────────────

@Composable
private fun EmptyView(tab: StockTab) {
    Box(
        modifier           = Modifier.fillMaxSize(),
        contentAlignment   = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier            = Modifier.padding(32.dp)
        ) {
            Text(
                text     = if (tab == StockTab.FAVORITES) "❤️" else "📋",
                fontSize = 56.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text  = if (tab == StockTab.FAVORITES)
                    "Henüz favori hisseniz yok"
                else
                    "Hisse bulunamadı",
                style      = MaterialTheme.typography.titleMedium,
                color      = OnSurfaceVar,
                fontWeight = FontWeight.SemiBold
            )
            if (tab == StockTab.FAVORITES) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text  = "Ana listede ❤️ ikonuna dokunarak\nhisseleri portföyünüze ekleyin.",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVar
                )
            }
        }
    }
}

// ── Yükleniyor ────────────────────────────────────────────────────────────────

@Composable
private fun LoadingView() {
    Box(
        modifier         = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Primary, strokeWidth = 3.dp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text  = "Hisseler yükleniyor...",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVar
            )
        }
    }
}
