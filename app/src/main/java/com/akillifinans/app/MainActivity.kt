package com.akillifinans.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.akillifinans.app.ui.screen.PortfolioScreen
import com.akillifinans.app.ui.screen.StockDetailScreen
import com.akillifinans.app.ui.theme.AkilliFinansTheme

/** Navigasyon Rotaları */
object Routes {
    const val PORTFOLIO = "portfolio"
    const val STOCK_DETAIL = "stock_detail/{symbol}"
    fun stockDetail(symbol: String) = "stock_detail/$symbol"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AkilliFinansTheme {
                AkilliFinansApp()
            }
        }
    }
}

@Composable
fun AkilliFinansApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.PORTFOLIO
    ) {
        // ── Ana Ekran: Portföy Listesi ────────────────────────────────────
        composable(Routes.PORTFOLIO) {
            PortfolioScreen(
                onStockClick = { symbol ->
                    navController.navigate(Routes.stockDetail(symbol))
                }
            )
        }

        // ── Detay Ekranı: Çarpan & Analiz ────────────────────────────────
        composable(
            route = Routes.STOCK_DETAIL,
            arguments = listOf(navArgument("symbol") { type = NavType.StringType })
        ) { backStackEntry ->
            val symbol = backStackEntry.arguments?.getString("symbol") ?: return@composable
            StockDetailScreen(
                symbol = symbol,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
