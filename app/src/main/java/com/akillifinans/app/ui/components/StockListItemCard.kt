package com.akillifinans.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akillifinans.app.data.local.StockEntity
import com.akillifinans.app.ui.theme.*

/**
 * Portföy listesinde gösterilen hisse satır kartı.
 *
 * Değişiklikler (Checkpoint 3):
 *  - [stock] tipi StockSummary → StockEntity olarak güncellendi (Room modeli)
 *  - Sağ üst köşeye animasyonlu Favori (kalp) ikonu eklendi
 *  - [onFavoriteClick] callback'i ile ViewModel'a favori toggle iletilir
 */
@Composable
fun StockListItemCard(
    stock: StockEntity,
    onClick: () -> Unit,
    onFavoriteClick: (StockEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val isGain = stock.dailyChangePercent >= 0.0

    val changeColor by animateColorAsState(
        targetValue = if (isGain) GainGreen else LossRed,
        animationSpec = tween(durationMillis = 400),
        label = "changeColor"
    )
    val changeBgColor by animateColorAsState(
        targetValue = if (isGain) GainGreenBg else LossRedBg,
        animationSpec = tween(durationMillis = 400),
        label = "changeBgColor"
    )

    // Kalp ikonu animasyonu: favori olunca hafifçe büyür
    val heartScale by animateFloatAsState(
        targetValue = if (stock.isFavorite) 1.2f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessLow
        ),
        label = "heartScale"
    )
    val heartColor by animateColorAsState(
        targetValue = if (stock.isFavorite) FavoriteRed else OnSurfaceVar,
        animationSpec = tween(durationMillis = 300),
        label = "heartColor"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color  = Surface,
        shape  = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ── Sol: Hisse logosu / avatar ──────────────────────────────
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = stock.symbol.take(2),
                        style      = MaterialTheme.typography.labelLarge,
                        color      = Primary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize   = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // ── Orta: Sembol + Şirket Adı ──────────────────────────────
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = stock.symbol,
                        style      = MaterialTheme.typography.titleMedium,
                        color      = OnBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text      = stock.name,
                        style     = MaterialTheme.typography.bodySmall,
                        color     = OnSurfaceVar,
                        maxLines  = 1
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // ── Sağ: Fiyat + Değişim + Favori İkonu ───────────────────
                Column(horizontalAlignment = Alignment.End) {
                    // Favori (kalp) butonu
                    IconButton(
                        onClick  = { onFavoriteClick(stock) },
                        modifier = Modifier
                            .size(32.dp)
                            .scale(heartScale)
                    ) {
                        Icon(
                            imageVector = if (stock.isFavorite)
                                Icons.Default.Favorite
                            else
                                Icons.Default.FavoriteBorder,
                            contentDescription = if (stock.isFavorite)
                                "Favorilerden çıkar"
                            else
                                "Favorilere ekle",
                            tint     = heartColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text       = "₺%.2f".format(stock.currentPrice),
                        style      = MaterialTheme.typography.titleMedium,
                        color      = OnBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Değişim Badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(changeBgColor)
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isGain) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint     = changeColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text       = "%+.2f%%".format(stock.dailyChangePercent),
                            style      = MaterialTheme.typography.labelSmall,
                            color      = changeColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
