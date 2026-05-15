package com.kaushalya.karnataka.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val KaushalyaColorScheme = lightColorScheme(
    primary          = RoyalBlue,
    onPrimary        = PureWhite,
    primaryContainer = ElectricBlue,
    secondary        = SaffronOrange,
    onSecondary      = PureWhite,
    tertiary         = WarmGold,
    background       = OffWhite,
    surface          = PureWhite,
    onBackground     = NearBlack,
    onSurface        = NearBlack,
    error            = CrimsonRed,
    onError          = PureWhite,
)

val KaushalyaTypography = Typography(
    headlineLarge  = TextStyle(fontWeight = FontWeight.Bold,   fontSize = 28.sp, color = NearBlack),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold,   fontSize = 22.sp, color = NearBlack),
    titleLarge     = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = NearBlack),
    titleMedium    = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = NearBlack),
    bodyLarge      = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, color = NearBlack),
    bodyMedium     = TextStyle(fontWeight = FontWeight.Normal, fontSize = 13.sp, color = DarkGrey),
    labelSmall     = TextStyle(fontWeight = FontWeight.Medium, fontSize = 10.sp, color = DarkGrey),
)

@Composable
fun KaushalyaKarnatakaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KaushalyaColorScheme,
        typography  = KaushalyaTypography,
        content     = content,
    )
}
