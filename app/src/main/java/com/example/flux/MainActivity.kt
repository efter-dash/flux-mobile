package com.example.flux

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ViewKanban
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flux.data.repository.FluxRepository
import com.example.flux.ui.components.FluxStarIcon
import com.example.flux.ui.screens.CalendarScreen
import com.example.flux.ui.screens.ContentDetailScreen
import com.example.flux.ui.screens.ContentPlanScreen
import com.example.flux.ui.screens.DashboardScreen
import com.example.flux.ui.screens.IdeasScreen
import com.example.flux.ui.screens.PipelineScreen
import com.example.flux.ui.screens.PublishingScreen
import com.example.flux.ui.screens.ReportsScreen
import com.example.flux.ui.screens.SettingsScreen
import com.example.flux.ui.screens.TasksScreen
import com.example.flux.ui.screens.TeamScreen
import com.example.flux.ui.theme.BaseCanvas
import com.example.flux.ui.theme.FluxAccent
import com.example.flux.ui.theme.FluxPrimary
import com.example.flux.ui.theme.FluxTheme
import com.example.flux.ui.theme.InkDim
import com.example.flux.ui.theme.InkFaint
import com.example.flux.ui.theme.InkLight
import com.example.flux.ui.theme.LineBorder
import com.example.flux.ui.theme.PanelCard
import com.example.flux.ui.theme.RaisedCard
import kotlinx.coroutines.launch

import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.IconButton
import com.example.flux.ui.screens.WorkspacePickerScreen
import com.example.flux.ui.theme.AppThemeMode

enum class AppDestination(val label: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard),
    PIPELINE("Pipeline", Icons.Default.ViewKanban),
    CONTENT("Content", Icons.Default.Folder),
    CALENDAR("Calendar", Icons.Default.CalendarMonth),
    TASKS("Tasks", Icons.Default.Checklist),
    IDEAS("Ideas", Icons.Default.Lightbulb),
    PUBLISHING("Publishing", Icons.Default.Publish),
    REPORTS("Reports", Icons.Default.BarChart),
    TEAM("Team", Icons.Default.People),
    SETTINGS("Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by FluxRepository.instance.themeMode.collectAsState()
            FluxTheme(themeMode = themeMode) {
                FluxAppMain()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FluxAppMain() {
    val activeWorkspaceId by FluxRepository.instance.activeWorkspaceId.collectAsState()
    val workspaceData by FluxRepository.instance.workspaceData.collectAsState()
    val themeMode by FluxRepository.instance.themeMode.collectAsState()

    // Prompt user to pick or create a workspace on app open if none is selected
    if (activeWorkspaceId == null) {
        WorkspacePickerScreen(
            onWorkspaceSelected = { id ->
                FluxRepository.instance.selectWorkspace(id)
            }
        )
        return
    }

    var currentDestination by remember { mutableStateOf(AppDestination.DASHBOARD) }
    var viewingContentId by remember { mutableStateOf<String?>(null) }
    var showMoreSheet by remember { mutableStateOf(false) }
    var triggerCreateInContentPlan by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    BackHandler(enabled = currentDestination != AppDestination.DASHBOARD && viewingContentId == null) {
        currentDestination = AppDestination.DASHBOARD
    }

    if (viewingContentId != null) {
        ContentDetailScreen(
            contentId = viewingContentId!!,
            onBack = { viewingContentId = null }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 4.dp)
                        ) {
                            // Logo and Title
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f, fill = false)
                            ) {
                                FluxStarIcon(modifier = Modifier.size(22.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "FLUX",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                // Active Workspace Switcher Pill
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { FluxRepository.instance.clearActiveWorkspace() }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .testTag("top_bar_switch_workspace")
                                ) {
                                    Text(
                                        text = workspaceData.workspace.initials,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = workspaceData.workspace.name,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Icon(
                                        imageVector = Icons.Default.SwapHoriz,
                                        contentDescription = "Switch Workspace",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            // Theme Toggle Button
                            IconButton(
                                onClick = { FluxRepository.instance.toggleTheme() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("top_bar_theme_toggle")
                            ) {
                                Icon(
                                    imageVector = if (themeMode == AppThemeMode.LIGHT) Icons.Default.DarkMode else Icons.Default.LightMode,
                                    contentDescription = "Toggle Theme",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    val mainTabs = listOf(
                        AppDestination.DASHBOARD,
                        AppDestination.PIPELINE,
                        AppDestination.CONTENT,
                        AppDestination.CALENDAR
                    )

                    mainTabs.forEach { destination ->
                        NavigationBarItem(
                            selected = currentDestination == destination,
                            onClick = { currentDestination = destination },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.label
                                )
                            },
                            label = { Text(destination.label, fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("nav_tab_${destination.name.lowercase()}")
                        )
                    }

                    // More tab
                    val isMoreSelected = currentDestination !in mainTabs
                    NavigationBarItem(
                        selected = isMoreSelected,
                        onClick = { showMoreSheet = true },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.MoreHoriz,
                                contentDescription = "More"
                            )
                        },
                        label = { Text("More", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_tab_more")
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentDestination) {
                    AppDestination.DASHBOARD -> DashboardScreen(
                        onNavigateToContentDetail = { viewingContentId = it },
                        onNavigateToCreateContent = {
                            triggerCreateInContentPlan = true
                            currentDestination = AppDestination.CONTENT
                        },
                        onNavigateToPipeline = { currentDestination = AppDestination.PIPELINE }
                    )
                    AppDestination.PIPELINE -> PipelineScreen(
                        onNavigateToContentDetail = { viewingContentId = it }
                    )
                    AppDestination.CONTENT -> ContentPlanScreen(
                        onNavigateToContentDetail = { viewingContentId = it },
                        showCreateInitialDialog = triggerCreateInContentPlan
                    )
                    AppDestination.CALENDAR -> CalendarScreen(
                        onNavigateToContentDetail = { viewingContentId = it }
                    )
                    AppDestination.TASKS -> TasksScreen()
                    AppDestination.IDEAS -> IdeasScreen(
                        onNavigateToContentDetail = { viewingContentId = it }
                    )
                    AppDestination.PUBLISHING -> PublishingScreen(
                        onNavigateToContentDetail = { viewingContentId = it }
                    )
                    AppDestination.REPORTS -> ReportsScreen(
                        onNavigateToContentDetail = { viewingContentId = it }
                    )
                    AppDestination.TEAM -> TeamScreen()
                    AppDestination.SETTINGS -> SettingsScreen(
                        onSwitchWorkspace = { FluxRepository.instance.clearActiveWorkspace() }
                    )
                }
            }

            if (showMoreSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showMoreSheet = false },
                    sheetState = sheetState,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .padding(bottom = 24.dp)
                    ) {
                        Text(
                            text = "WORKSPACES & TOOLS",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        val moreItems = listOf(
                            AppDestination.TASKS,
                            AppDestination.IDEAS,
                            AppDestination.PUBLISHING,
                            AppDestination.REPORTS,
                            AppDestination.TEAM,
                            AppDestination.SETTINGS
                        )

                        moreItems.forEach { dest ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        currentDestination = dest
                                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                                            showMoreSheet = false
                                        }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = dest.icon,
                                    contentDescription = dest.label,
                                    modifier = Modifier.size(20.dp),
                                    tint = if (currentDestination == dest) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = dest.label,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (currentDestination == dest) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (currentDestination == dest) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
