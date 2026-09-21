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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.content.ArchiveManuscriptEngine
import com.example.content.ManuscriptChapter
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
fun ArchiveScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedChapter by remember { mutableStateOf<ManuscriptChapter?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .testTag("archive_screen")
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
                            if (selectedChapter != null) {
                                selectedChapter = null
                            } else {
                                viewModel.navigateTo(Screen.HOME)
                            }
                        }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (selectedChapter != null) "CHAPTER ${selectedChapter!!.number}" else "THE ARCHIVE // MANUSCRIPT",
                    style = Typography.labelLarge.copy(
                        color = ChalkWhite,
                        letterSpacing = 1.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (selectedChapter != null) {
                // Chapter Reader View
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = selectedChapter!!.title,
                        style = Typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = ChalkWhite
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectedChapter!!.readTime,
                        style = Typography.labelSmall.copy(
                            color = ElectricChartreuse,
                            letterSpacing = 1.0.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceDark)
                            .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                            .padding(20.dp)
                    ) {
                        Text(
                            text = selectedChapter!!.text,
                            style = Typography.bodyLarge.copy(
                                color = ChalkWhite,
                                lineHeight = 26.sp
                            )
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(ArchiveManuscriptEngine.chapters) { ch ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceDark)
                                .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedChapter = ch }
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "CHAPTER ${ch.number}",
                                    style = Typography.labelSmall.copy(
                                        color = ElectricChartreuse,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.0.sp
                                    )
                                )
                                Text(
                                    text = ch.readTime,
                                    style = Typography.labelSmall.copy(color = NeutralGray)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = ch.title,
                                style = Typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ChalkWhite
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
