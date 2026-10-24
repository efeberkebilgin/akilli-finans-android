package com.akillifinans.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akillifinans.app.ui.theme.*

/**
 * Çarpan değeri gösterme kartı.
 * F/K, PD/DD, FD/FAVÖK gibi rasyolar için kullanılır.
 *
 * @param label  Rasyo adı (örn: "F/K")
 * @param value  Rasyo değeri (örn: 4.15)
 * @param subLabel Alt açıklama (örn: "Sektör Ort: 8.30")
 * @param highlight Sektör ortalamasına göre iyi mi? Renk vurgusu için.
 */
@Composable
fun RatioCard(
    label: String,
    value: String,
    subLabel: String? = null,
    highlight: Boolean = false,
    modifier: Modifier = Modifier
) {
    val bgColor = if (highlight) GainGreenBg else SurfaceVar
    val valueColor = if (highlight) GainGreen else OnBackground

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = OnSurfaceVar,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = valueColor,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp
            )
            if (subLabel != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVar
                )
            }
        }
    }
}
