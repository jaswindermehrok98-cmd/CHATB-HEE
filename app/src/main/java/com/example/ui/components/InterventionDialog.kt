package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.limits.InterventionState
import com.example.ui.theme.AlertRed
import com.example.ui.theme.ChalkWhite
import com.example.ui.theme.ElectricChartreuse
import com.example.ui.theme.NeutralGray
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.Typography

@Composable
fun InterventionDialog(
    state: InterventionState,
    onReturnHome: () -> Unit,
    onAddFiveMinutes: () -> Unit,
    onChangeLimit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack.copy(alpha = 0.96f))
            .padding(24.dp)
            .testTag("intervention_overlay"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceDark)
                .border(1.dp, AlertRed, RoundedCornerShape(12.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "TIME'S UP",
                style = Typography.displayMedium.copy(
                    color = AlertRed,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1.0).sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = state.appLabel.ifBlank { state.packageName }.uppercase(),
                style = Typography.headlineMedium.copy(
                    color = ChalkWhite,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${state.usedMinutes} / ${state.limitMinutes} MIN TODAY",
                style = Typography.titleMedium.copy(
                    color = ElectricChartreuse,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "YOU CONFIGURED THIS LIMIT.\nYOUR TIME HAS VALUE.",
                style = Typography.bodyMedium.copy(
                    color = NeutralGray,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.8.sp
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Primary action: Return Home
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(ElectricChartreuse)
                    .clickable { onReturnHome() }
                    .padding(vertical = 14.dp)
                    .testTag("intervention_return_home_btn"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "RETURN HOME",
                    style = Typography.labelLarge.copy(
                        color = ObsidianBlack,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(SurfaceDark)
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(6.dp))
                        .clickable { onAddFiveMinutes() }
                        .padding(vertical = 12.dp)
                        .testTag("intervention_add_5m_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+ 5 MIN",
                        style = Typography.labelSmall.copy(
                            color = ChalkWhite,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.0.sp
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(SurfaceDark)
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(6.dp))
                        .clickable { onChangeLimit() }
                        .padding(vertical = 12.dp)
                        .testTag("intervention_change_limit_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CHANGE LIMIT",
                        style = Typography.labelSmall.copy(
                            color = NeutralGray,
                            letterSpacing = 1.0.sp
                        )
                    )
                }
            }
        }
    }
}
