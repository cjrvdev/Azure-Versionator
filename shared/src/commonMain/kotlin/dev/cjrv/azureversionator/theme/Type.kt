package dev.cjrv.azureversionator.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import azureversionator.shared.generated.resources.Res
import azureversionator.shared.generated.resources.inter_variable
import azureversionator.shared.generated.resources.inter_variable_italic
import org.jetbrains.compose.resources.Font

@Composable
fun AzureTypography(): Typography {
    val interFamily = FontFamily(
        Font(Res.font.inter_variable, weight = FontWeight.Normal),
        Font(Res.font.inter_variable, weight = FontWeight.Medium),
        Font(Res.font.inter_variable, weight = FontWeight.SemiBold),
        Font(Res.font.inter_variable, weight = FontWeight.Bold),
        Font(
            Res.font.inter_variable_italic,
            weight = FontWeight.Normal,
            style = FontStyle.Italic,
        ),
    )

    val base = Typography()
    return base.copy(
        displayLarge = base.displayLarge.withInter(interFamily, FontWeight.SemiBold),
        displayMedium = base.displayMedium.withInter(interFamily, FontWeight.SemiBold),
        displaySmall = base.displaySmall.withInter(interFamily, FontWeight.SemiBold),
        headlineLarge = base.headlineLarge.withInter(interFamily, FontWeight.SemiBold),
        headlineMedium = base.headlineMedium.withInter(interFamily, FontWeight.SemiBold),
        headlineSmall = base.headlineSmall.withInter(interFamily, FontWeight.SemiBold),
        titleLarge = base.titleLarge.withInter(interFamily, FontWeight.SemiBold),
        titleMedium = base.titleMedium.withInter(interFamily, FontWeight.Medium),
        titleSmall = base.titleSmall.withInter(interFamily, FontWeight.Medium),
        bodyLarge = base.bodyLarge.withInter(interFamily, FontWeight.Normal),
        bodyMedium = base.bodyMedium.withInter(interFamily, FontWeight.Normal),
        bodySmall = base.bodySmall.withInter(interFamily, FontWeight.Normal),
        labelLarge = base.labelLarge.withInter(interFamily, FontWeight.Medium),
        labelMedium = base.labelMedium.withInter(interFamily, FontWeight.Medium),
        labelSmall = base.labelSmall.withInter(interFamily, FontWeight.Medium),
    )
}

private fun TextStyle.withInter(fontFamily: FontFamily, defaultWeight: FontWeight): TextStyle {
    return copy(
        fontFamily = fontFamily,
        fontWeight = defaultWeight,
        // Slightly tighter spacing works well with Inter's metrics.
        letterSpacing = (letterSpacing.value * 0.95f).sp,
    )
}

