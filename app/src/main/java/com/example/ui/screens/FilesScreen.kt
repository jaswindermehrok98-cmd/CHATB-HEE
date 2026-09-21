package com.example.ui.screens

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.content.FilesEngine
import com.example.content.UnplugFile
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.theme.ChalkWhite
import com.example.ui.theme.ElectricChartreuse
import com.example.ui.theme.NeutralGray
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.Typography

@Composable
fun FilesScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedFile by remember { mutableStateOf<UnplugFile?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .testTag("files_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Return",
                    tint = ChalkWhite,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            if (selectedFile != null) {
                                selectedFile = null
                            } else {
                                viewModel.navigateTo(Screen.HOME)
                            }
                        }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (selectedFile != null) selectedFile!!.name else "LOCAL FILESYSTEM",
                    style = Typography.labelLarge.copy(
                        color = ChalkWhite,
                        letterSpacing = 1.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (selectedFile != null) {
                // File Viewer
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = selectedFile!!.path,
                        style = Typography.labelSmall.copy(
                            color = ElectricChartreuse,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceDark)
                            .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = selectedFile!!.content,
                            style = Typography.bodyMedium.copy(
                                color = ChalkWhite,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 22.sp
                            )
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(FilesEngine.fileTree) { file ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(SurfaceDark)
                                .border(1.dp, SurfaceBorder, RoundedCornerShape(6.dp))
                                .clickable { selectedFile = file }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = ElectricChartreuse,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = file.name,
                                        style = Typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = ChalkWhite
                                        )
                                    )
                                    Text(
                                        text = file.path,
                                        style = Typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            color = NeutralGray
                                        )
                                    )
                                }
                            }

                            Text(
                                text = file.size,
                                style = Typography.labelSmall.copy(color = NeutralGray)
                            )
                        }
                    }
                }
            }
        }
    }
}
