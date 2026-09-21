package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.apps.AppInfo
import com.example.ui.theme.ChalkWhite
import com.example.ui.theme.ElectricChartreuse
import com.example.ui.theme.NeutralGray
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.Typography

@Composable
fun EssentialAppsRow(
    essentialApps: List<AppInfo>,
    onAppClick: (String, String) -> Unit,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("essential_apps_container")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ESSENTIALS",
                style = Typography.labelLarge.copy(
                    color = ChalkWhite,
                    letterSpacing = 1.5.sp
                )
            )

            Text(
                text = "ALL APPS →",
                style = Typography.labelSmall.copy(
                    color = ElectricChartreuse,
                    letterSpacing = 1.0.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .clickable { onOpenDrawer() }
                    .padding(4.dp)
                    .testTag("all_apps_button")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (essentialApps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceDark)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                    .clickable { onOpenDrawer() }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Essentials",
                        tint = NeutralGray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SELECT ESSENTIAL COMMUNICATION APPS",
                        style = Typography.labelSmall.copy(
                            color = NeutralGray,
                            letterSpacing = 1.0.sp
                        )
                    )
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                essentialApps.take(5).forEach { app ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(SurfaceDark)
                            .border(1.dp, SurfaceBorder, RoundedCornerShape(6.dp))
                            .clickable { onAppClick(app.packageName, app.label) }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                            .testTag("essential_app_row_${app.packageName}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (app.iconBitmap != null) {
                                Image(
                                    bitmap = app.iconBitmap.asImageBitmap(),
                                    contentDescription = app.label,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                            Text(
                                text = app.label.uppercase(),
                                style = Typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    color = ChalkWhite
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Text(
                            text = "OPEN",
                            style = Typography.labelSmall.copy(
                                color = NeutralGray,
                                letterSpacing = 1.2.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
