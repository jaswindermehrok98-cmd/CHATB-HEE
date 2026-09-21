package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.message.DailyMessage
import com.example.ui.theme.ChalkWhite
import com.example.ui.theme.ElectricChartreuse
import com.example.ui.theme.NeutralGray
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.Typography

@Composable
fun TodayMessageCard(
    message: DailyMessage,
    onCycleMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceDark)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
            .clickable { onCycleMessage() }
            .padding(16.dp)
            .testTag("today_message_card")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "TODAY",
                    style = Typography.labelLarge.copy(
                        color = ElectricChartreuse,
                        letterSpacing = 1.5.sp
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "// ${message.category}",
                    style = Typography.labelSmall.copy(
                        color = NeutralGray,
                        letterSpacing = 1.0.sp
                    )
                )
            }

            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Cycle Message",
                tint = NeutralGray,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "\"${message.text}\"",
            style = Typography.bodyLarge.copy(
                fontSize = 17.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Medium,
                color = ChalkWhite
            ),
            modifier = Modifier.testTag("today_message_text")
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message.contextNote.uppercase(),
            style = Typography.labelSmall.copy(
                color = NeutralGray,
                letterSpacing = 0.8.sp
            )
        )
    }
}
