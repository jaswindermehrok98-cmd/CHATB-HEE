package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.apps.AppInfo
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.theme.ChalkWhite
import com.example.ui.theme.ElectricChartreuse
import com.example.ui.theme.NeutralGray
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceDarkHover
import com.example.ui.theme.Typography

@Composable
fun AppDrawerScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val allApps by viewModel.allApps.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val appLimits by viewModel.appLimits.collectAsState()
    val appFilterCategory by viewModel.appFilterCategory.collectAsState()
    val appViewMode by viewModel.appViewMode.collectAsState()
    val vaultHiddenPackages by viewModel.vaultHiddenPackages.collectAsState()
    val isPremiumUnlocked by viewModel.isPremiumUnlocked.collectAsState()

    var selectedAppForLimit by remember { mutableStateOf<AppInfo?>(null) }
    var limitMinutesSlider by remember { mutableFloatStateOf(15f) }
    var selectedAlphabetFilter by remember { mutableStateOf<Char?>(null) }

    val visibleApps = remember(allApps, vaultHiddenPackages) {
        allApps.filter { !vaultHiddenPackages.contains(it.packageName) }
    }

    val filteredApps = remember(visibleApps, searchQuery, appFilterCategory, selectedAlphabetFilter) {
        var list = visibleApps

        // Tab Filter
        list = when (appFilterCategory) {
            "DOWNLOADED" -> list.filter { it.isDownloaded }
            "SYSTEM" -> list.filter { !it.isDownloaded }
            "COMMUNICATION" -> list.filter { it.category == "COMMUNICATION" }
            "SOCIAL" -> list.filter { it.category == "SOCIAL" }
            "TOOLS" -> list.filter { it.category == "TOOLS" }
            "MEDIA" -> list.filter { it.category == "MEDIA" }
            "WORK" -> list.filter { it.category == "WORK" }
            else -> list
        }

        // Alphabet quick filter
        if (selectedAlphabetFilter != null) {
            list = list.filter { it.label.trim().startsWith(selectedAlphabetFilter!!, ignoreCase = true) }
        }

        // Search Query
        if (searchQuery.isNotBlank()) {
            val query = searchQuery.trim().lowercase()
            list = list.filter {
                it.label.lowercase().contains(query) || it.packageName.lowercase().contains(query)
            }.sortedBy {
                if (it.label.lowercase().startsWith(query)) 0 else 1
            }
        }

        list
    }

    val filterTabs = listOf(
        "ALL" to "ALL APPS",
        "DOWNLOADED" to "DOWNLOADED",
        "SYSTEM" to "SYSTEM",
        "COMMUNICATION" to "COMMUNICATION",
        "TOOLS" to "TOOLS",
        "SOCIAL" to "SOCIAL",
        "MEDIA" to "MEDIA",
        "WORK" to "WORK"
    )

    val alphabetList = ('A'..'Z').toList()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .testTag("app_drawer_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // Header / Back & Pro Access
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
                        text = "DOWNLOADED APPS",
                        style = Typography.labelLarge.copy(
                            color = ChalkWhite,
                            letterSpacing = 1.5.sp
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // View Mode Toggle (List vs Grid)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(SurfaceDark)
                            .border(1.dp, SurfaceBorder, RoundedCornerShape(4.dp))
                            .clickable { viewModel.toggleAppViewMode() }
                            .padding(6.dp)
                    ) {
                        Icon(
                            imageVector = if (appViewMode == "LIST") Icons.Default.GridView else Icons.Default.ViewList,
                            contentDescription = "Toggle Grid/List View",
                            tint = ChalkWhite,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Pro Sovereign Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isPremiumUnlocked) ElectricChartreuse else SurfaceDark)
                            .border(1.dp, if (isPremiumUnlocked) ElectricChartreuse else SurfaceBorder, RoundedCornerShape(4.dp))
                            .clickable { viewModel.navigateTo(Screen.PREMIUM) }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Pro Suite",
                                tint = if (isPremiumUnlocked) ObsidianBlack else ElectricChartreuse,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (isPremiumUnlocked) "PRO" else "PRO (2013)",
                                style = Typography.labelSmall.copy(
                                    color = if (isPremiumUnlocked) ObsidianBlack else ElectricChartreuse,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar + Command parsing
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = {
                    Text(
                        "Search apps or type command (e.g. 'focus 25')",
                        style = Typography.bodyMedium.copy(color = NeutralGray, fontSize = 13.sp)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = if (searchQuery.isNotBlank()) ElectricChartreuse else NeutralGray
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = NeutralGray,
                            modifier = Modifier.clickable { viewModel.setSearchQuery("") }
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricChartreuse,
                    unfocusedBorderColor = SurfaceBorder,
                    focusedTextColor = ChalkWhite,
                    unfocusedTextColor = ChalkWhite,
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    viewModel.executeSearchOrCommand(searchQuery)
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("app_search_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Tabs (ALL, DOWNLOADED, SYSTEM, CATEGORIES)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterTabs.forEach { (key, label) ->
                    val isSelected = appFilterCategory == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) ElectricChartreuse else SurfaceDark)
                            .border(
                                1.dp,
                                if (isSelected) ElectricChartreuse else SurfaceBorder,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable {
                                viewModel.setAppFilter(key)
                                selectedAlphabetFilter = null
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("tab_filter_$key")
                    ) {
                        Text(
                            text = label,
                            style = Typography.labelSmall.copy(
                                color = if (isSelected) ObsidianBlack else NeutralGray,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Alphabetical Jump Index Ribbon (A-Z)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(if (selectedAlphabetFilter == null) SurfaceDarkHover else SurfaceDark)
                        .border(1.dp, if (selectedAlphabetFilter == null) ElectricChartreuse else SurfaceBorder, CircleShape)
                        .clickable { selectedAlphabetFilter = null },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "•",
                        style = Typography.labelSmall.copy(
                            color = if (selectedAlphabetFilter == null) ElectricChartreuse else NeutralGray,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                alphabetList.forEach { letter ->
                    val isLetterSelected = selectedAlphabetFilter == letter
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(if (isLetterSelected) ElectricChartreuse else SurfaceDark)
                            .border(1.dp, if (isLetterSelected) ElectricChartreuse else SurfaceBorder, CircleShape)
                            .clickable {
                                selectedAlphabetFilter = if (isLetterSelected) null else letter
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = letter.toString(),
                            style = Typography.labelSmall.copy(
                                color = if (isLetterSelected) ObsidianBlack else ChalkWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // App Counter & Count Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SHOWING ${filteredApps.size} APPS",
                    style = Typography.labelSmall.copy(
                        color = NeutralGray,
                        letterSpacing = 1.0.sp,
                        fontSize = 10.sp
                    )
                )

                if (selectedAlphabetFilter != null) {
                    Text(
                        text = "LETTER '$selectedAlphabetFilter' FILTER ACTIVE",
                        style = Typography.labelSmall.copy(
                            color = ElectricChartreuse,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredApps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No applications match your filter",
                            style = Typography.bodyMedium.copy(color = NeutralGray)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(SurfaceDark)
                                .clickable {
                                    viewModel.setSearchQuery("")
                                    viewModel.setAppFilter("ALL")
                                    selectedAlphabetFilter = null
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "RESET FILTERS",
                                style = Typography.labelSmall.copy(color = ElectricChartreuse)
                            )
                        }
                    }
                }
            } else {
                if (appViewMode == "LIST") {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredApps, key = { it.packageName }) { app ->
                            val limitEntity = appLimits.firstOrNull { it.packageName == app.packageName }

                            AppRowItem(
                                app = app,
                                dailyLimitMinutes = limitEntity?.dailyLimitMinutes,
                                onLaunch = { viewModel.launchApp(app.packageName, app.label) },
                                onConfigureLimit = {
                                    selectedAppForLimit = app
                                    limitMinutesSlider = (limitEntity?.dailyLimitMinutes ?: 15).toFloat()
                                }
                            )
                        }
                    }
                } else {
                    // Grid View
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredApps, key = { it.packageName }) { app ->
                            AppGridItem(
                                app = app,
                                onLaunch = { viewModel.launchApp(app.packageName, app.label) }
                            )
                        }
                    }
                }
            }
        }

        // Limit Configuration Dialog
        if (selectedAppForLimit != null) {
            val app = selectedAppForLimit!!
            AlertDialog(
                onDismissRequest = { selectedAppForLimit = null },
                containerColor = SurfaceDark,
                title = {
                    Text(
                        text = "DAILY LIMIT // ${app.label.uppercase()}",
                        style = Typography.titleMedium.copy(
                            color = ChalkWhite,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Set the maximum conscious minutes per day you intend to spend inside this app.",
                            style = Typography.bodyMedium.copy(color = NeutralGray)
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = "${limitMinutesSlider.toInt()} MINUTES / DAY",
                            style = Typography.headlineMedium.copy(
                                color = ElectricChartreuse,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Slider(
                            value = limitMinutesSlider,
                            onValueChange = { limitMinutesSlider = it },
                            valueRange = 5f..120f,
                            steps = 22,
                            colors = SliderDefaults.colors(
                                thumbColor = ElectricChartreuse,
                                activeTrackColor = ElectricChartreuse,
                                inactiveTrackColor = SurfaceBorder
                            )
                        )
                    }
                },
                confirmButton = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(ElectricChartreuse)
                            .clickable {
                                viewModel.setAppLimit(app.packageName, limitMinutesSlider.toInt())
                                selectedAppForLimit = null
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("save_limit_btn")
                    ) {
                        Text(
                            text = "SAVE LIMIT",
                            style = Typography.labelSmall.copy(
                                color = ObsidianBlack,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                },
                dismissButton = {
                    Text(
                        text = "CANCEL",
                        style = Typography.labelSmall.copy(color = NeutralGray),
                        modifier = Modifier
                            .clickable { selectedAppForLimit = null }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            )
        }
    }
}

@Composable
private fun AppRowItem(
    app: AppInfo,
    dailyLimitMinutes: Int?,
    onLaunch: () -> Unit,
    onConfigureLimit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(SurfaceDark)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(6.dp))
            .clickable { onLaunch() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("app_row_${app.packageName}"),
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
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
            } else {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(SurfaceDarkHover),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = app.label.take(1).uppercase(),
                        style = Typography.bodyMedium.copy(color = ChalkWhite, fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = app.label,
                        style = Typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = ChalkWhite
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (app.isDownloaded) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(2.dp))
                                .background(SurfaceDarkHover)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "USER",
                                style = Typography.labelSmall.copy(
                                    fontSize = 8.sp,
                                    color = ElectricChartreuse,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
                Text(
                    text = "${app.category} • ${app.packageName}",
                    style = Typography.labelSmall.copy(
                        fontSize = 9.sp,
                        color = NeutralGray
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (dailyLimitMinutes != null && dailyLimitMinutes > 0) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(SurfaceDarkHover)
                        .clickable { onConfigureLimit() }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${dailyLimitMinutes}M",
                        style = Typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = ElectricChartreuse,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = "Set Limit",
                    tint = NeutralGray,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { onConfigureLimit() }
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(ElectricChartreuse)
                    .clickable { onLaunch() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("open_btn_${app.packageName}")
            ) {
                Text(
                    text = "OPEN",
                    style = Typography.labelSmall.copy(
                        color = ObsidianBlack,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun AppGridItem(
    app: AppInfo,
    onLaunch: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceDark)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
            .clickable { onLaunch() }
            .padding(12.dp)
            .testTag("app_grid_${app.packageName}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (app.iconBitmap != null) {
                Image(
                    bitmap = app.iconBitmap.asImageBitmap(),
                    contentDescription = app.label,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceDarkHover),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = app.label.take(1).uppercase(),
                        style = Typography.titleMedium.copy(color = ChalkWhite, fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = app.label,
                style = Typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = ChalkWhite,
                    fontSize = 11.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = if (app.isDownloaded) "USER APP" else "SYSTEM",
                style = Typography.labelSmall.copy(
                    fontSize = 8.sp,
                    color = if (app.isDownloaded) ElectricChartreuse else NeutralGray
                )
            )
        }
    }
}
