package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.content.TheReadEngine
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
fun TheReadScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val step by viewModel.readStep.collectAsState()
    val result by viewModel.readResult.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .testTag("the_read_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { viewModel.navigateTo(Screen.HOME) }
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Return Home",
                        tint = ChalkWhite,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "THE READ // CALIBRATION",
                        style = Typography.labelLarge.copy(
                            color = ChalkWhite,
                            letterSpacing = 1.5.sp
                        )
                    )
                }

                if (result != null) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart",
                        tint = ElectricChartreuse,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { viewModel.startTheRead() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (result != null) {
                // Archetype Result View
                val r = result!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "PERCEPTION PROFILE",
                        style = Typography.labelMedium.copy(
                            color = ElectricChartreuse,
                            letterSpacing = 2.0.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = r.archetype,
                        style = Typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = ChalkWhite,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = r.title,
                        style = Typography.labelLarge.copy(
                            color = NeutralGray,
                            letterSpacing = 1.5.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceDark)
                            .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                            .padding(20.dp)
                    ) {
                        Text(
                            text = r.summary,
                            style = Typography.bodyLarge.copy(
                                color = ChalkWhite,
                                lineHeight = 24.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(ElectricChartreuse)
                            .clickable { viewModel.navigateTo(Screen.HOME) }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "RETURN TO OFFLINE OS",
                            style = Typography.labelLarge.copy(
                                color = ObsidianBlack,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        )
                    }
                }
            } else {
                // Question Flow
                val currentQuestion = TheReadEngine.questions[step.coerceIn(0, TheReadEngine.questions.size - 1)]

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "STEP 0${step + 1} // 0${TheReadEngine.questions.size}",
                        style = Typography.labelMedium.copy(
                            color = ElectricChartreuse,
                            letterSpacing = 1.5.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = currentQuestion.prompt,
                        style = Typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ChalkWhite,
                            lineHeight = 32.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = currentQuestion.subtext,
                        style = Typography.bodyMedium.copy(
                            color = NeutralGray
                        )
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Option A
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceDark)
                            .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                            .clickable { viewModel.answerReadQuestion(currentQuestion.weightA) }
                            .padding(20.dp)
                            .testTag("read_option_a")
                    ) {
                        Column {
                            Text(
                                text = "A //",
                                style = Typography.labelSmall.copy(
                                    color = ElectricChartreuse,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = currentQuestion.optionA,
                                style = Typography.bodyLarge.copy(
                                    color = ChalkWhite,
                                    lineHeight = 22.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Option B
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceDark)
                            .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                            .clickable { viewModel.answerReadQuestion(currentQuestion.weightB) }
                            .padding(20.dp)
                            .testTag("read_option_b")
                    ) {
                        Column {
                            Text(
                                text = "B //",
                                style = Typography.labelSmall.copy(
                                    color = ElectricChartreuse,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = currentQuestion.optionB,
                                style = Typography.bodyLarge.copy(
                                    color = ChalkWhite,
                                    lineHeight = 22.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
