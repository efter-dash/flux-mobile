package com.example.flux.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.flux.data.model.ContentItem
import com.example.flux.data.model.PerformanceMetrics
import com.example.flux.data.model.StageState
import com.example.flux.data.repository.FluxRepository
import com.example.flux.domain.Derive
import com.example.flux.domain.OverallStatus
import com.example.flux.ui.components.PlatformBadge
import com.example.flux.ui.components.StatusBadge
import com.example.flux.ui.theme.BaseCanvas
import com.example.flux.ui.theme.FluxAccent
import com.example.flux.ui.theme.FluxDanger
import com.example.flux.ui.theme.FluxEmerald
import com.example.flux.ui.theme.FluxPrimary
import com.example.flux.ui.theme.FluxSecondary
import com.example.flux.ui.theme.InkDim
import com.example.flux.ui.theme.InkFaint
import com.example.flux.ui.theme.InkLight
import com.example.flux.ui.theme.LineBorder
import com.example.flux.ui.theme.PanelCard
import com.example.flux.ui.theme.RaisedCard
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentDetailScreen(
    contentId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val workspaceData by FluxRepository.instance.workspaceData.collectAsState()
    val stages = workspaceData.workspace.stages
    val members = workspaceData.members

    val item = workspaceData.content.find { it.id == contentId }

    if (item == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Content item not found", color = InkLight)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onBack) { Text("Go back") }
            }
        }
        return
    }

    var title by remember(item.id) { mutableStateOf(item.title) }
    var topic by remember(item.id) { mutableStateOf(item.topic) }
    var caption by remember(item.id) { mutableStateOf(item.caption) }
    var hashtags by remember(item.id) { mutableStateOf(item.hashtags) }
    var blocker by remember(item.id) { mutableStateOf(item.blocker) }
    var nextAction by remember(item.id) { mutableStateOf(item.nextAction) }
    var plannedDate by remember(item.id) { mutableStateOf(item.plannedPublishDate) }

    // Metrics state
    var viewsText by remember(item.id) { mutableStateOf(item.performance?.views?.toString() ?: "") }
    var reachText by remember(item.id) { mutableStateOf(item.performance?.reach?.toString() ?: "") }
    var likesText by remember(item.id) { mutableStateOf(item.performance?.likes?.toString() ?: "") }
    var commentsText by remember(item.id) { mutableStateOf(item.performance?.comments?.toString() ?: "") }
    var savesText by remember(item.id) { mutableStateOf(item.performance?.saves?.toString() ?: "") }
    var ratingText by remember(item.id) { mutableStateOf(item.performance?.rating ?: "") }
    var learningText by remember(item.id) { mutableStateOf(item.performance?.keyLearning ?: "") }

    var showPublishDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val status = Derive.overallStatus(item, stages)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(item.code, style = MaterialTheme.typography.titleMedium, color = FluxPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        PlatformBadge(platform = item.platform)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("detail_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = InkLight
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = FluxDanger)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PanelCard)
            )
        },
        bottomBar = {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(containerColor = PanelCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (!Derive.isPublished(item)) {
                        OutlinedButton(
                            onClick = { showPublishDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("mark_published_button"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = FluxEmerald)
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Publish Post")
                        }
                    }

                    Button(
                        onClick = {
                            val perf = if (viewsText.isNotBlank() || likesText.isNotBlank()) {
                                PerformanceMetrics(
                                    views = viewsText.toLongOrNull(),
                                    reach = reachText.toLongOrNull(),
                                    likes = likesText.toLongOrNull(),
                                    comments = commentsText.toLongOrNull(),
                                    saves = savesText.toLongOrNull(),
                                    rating = ratingText,
                                    keyLearning = learningText
                                )
                            } else item.performance

                            val updated = item.copy(
                                title = title,
                                topic = topic,
                                caption = caption,
                                hashtags = hashtags,
                                blocker = blocker,
                                nextAction = nextAction,
                                plannedPublishDate = plannedDate,
                                performance = perf
                            )
                            FluxRepository.instance.updateContent(updated)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("save_content_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = FluxAccent)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp), tint = BaseCanvas)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Changes", color = BaseCanvas, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        containerColor = BaseCanvas
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .testTag("content_detail_screen"),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title & Status card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = PanelCard)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "METADATA & STATUS",
                                style = MaterialTheme.typography.labelSmall,
                                color = InkFaint
                            )
                            StatusBadge(status = status)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Title") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("detail_title_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = InkLight,
                                unfocusedTextColor = InkLight
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = topic,
                            onValueChange = { topic = it },
                            label = { Text("Topic / Narrative Angle") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = InkLight,
                                unfocusedTextColor = InkLight
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = plannedDate,
                                onValueChange = { plannedDate = it },
                                label = { Text("Target Date (YYYY-MM-DD)") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = InkLight,
                                    unfocusedTextColor = InkLight
                                )
                            )
                            OutlinedTextField(
                                value = item.priority,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Priority") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = InkLight,
                                    unfocusedTextColor = InkLight
                                )
                            )
                        }
                    }
                }
            }

            // Production Pipeline Stages Checklist
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = PanelCard)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "PRODUCTION PIPELINE STAGES",
                            style = MaterialTheme.typography.labelSmall,
                            color = InkFaint
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        stages.forEach { stage ->
                            val curState = item.stageStates[stage.id] ?: StageState.PENDING
                            StageRowItem(
                                stage = stage,
                                currentState = curState,
                                onStateSelected = { newState ->
                                    FluxRepository.instance.updateStageState(item.id, stage.id, newState)
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            // Blockers & Next Action
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = PanelCard)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "OBSTACLES & NEXT STEPS",
                            style = MaterialTheme.typography.labelSmall,
                            color = InkFaint
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = blocker,
                            onValueChange = { blocker = it },
                            label = { Text("Current Blocker / Delay Reason") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = InkLight,
                                unfocusedTextColor = InkLight
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = nextAction,
                            onValueChange = { nextAction = it },
                            label = { Text("Immediate Next Action") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = InkLight,
                                unfocusedTextColor = InkLight
                            )
                        )
                    }
                }
            }

            // Copy & Social Content
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = PanelCard)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "SOCIAL COPY & HASHTAGS",
                            style = MaterialTheme.typography.labelSmall,
                            color = InkFaint
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = caption,
                            onValueChange = { caption = it },
                            label = { Text("Post Caption") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = InkLight,
                                unfocusedTextColor = InkLight
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = hashtags,
                            onValueChange = { hashtags = it },
                            label = { Text("Hashtags") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = InkLight,
                                unfocusedTextColor = InkLight
                            )
                        )
                    }
                }
            }

            // Performance Metrics
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = PanelCard)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "POST-PUBLICATION PERFORMANCE",
                            style = MaterialTheme.typography.labelSmall,
                            color = InkFaint
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = viewsText,
                                onValueChange = { viewsText = it },
                                label = { Text("Views") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = InkLight,
                                    unfocusedTextColor = InkLight
                                )
                            )
                            OutlinedTextField(
                                value = likesText,
                                onValueChange = { likesText = it },
                                label = { Text("Likes") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = InkLight,
                                    unfocusedTextColor = InkLight
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = commentsText,
                                onValueChange = { commentsText = it },
                                label = { Text("Comments") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = InkLight,
                                    unfocusedTextColor = InkLight
                                )
                            )
                            OutlinedTextField(
                                value = savesText,
                                onValueChange = { savesText = it },
                                label = { Text("Saves") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = InkLight,
                                    unfocusedTextColor = InkLight
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = learningText,
                            onValueChange = { learningText = it },
                            label = { Text("Key Learning / Takeaway") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = InkLight,
                                unfocusedTextColor = InkLight
                            )
                        )
                    }
                }
            }
        }

        // Publish Dialog
        if (showPublishDialog) {
            var liveUrl by remember { mutableStateOf("") }
            val todayStr = remember { LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) }

            AlertDialog(
                onDismissRequest = { showPublishDialog = false },
                title = { Text("Confirm Publication", color = InkLight) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Marking this item published will complete all stages and mark it as Published.", color = InkDim)
                        OutlinedTextField(
                            value = liveUrl,
                            onValueChange = { liveUrl = it },
                            label = { Text("Live Link / Post URL (optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = InkLight,
                                unfocusedTextColor = InkLight
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            FluxRepository.instance.markContentPublished(item.id, todayStr, liveUrl)
                            showPublishDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FluxEmerald)
                    ) {
                        Text("Mark Published", color = BaseCanvas)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPublishDialog = false }) { Text("Cancel", color = InkDim) }
                },
                containerColor = PanelCard
            )
        }

        // Delete Dialog
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Content Item", color = FluxDanger) },
                text = { Text("Are you sure you want to delete ${item.code} (${item.title})? This cannot be undone.", color = InkDim) },
                confirmButton = {
                    Button(
                        onClick = {
                            FluxRepository.instance.deleteContent(item.id)
                            showDeleteDialog = false
                            onBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FluxDanger)
                    ) {
                        Text("Delete", color = BaseCanvas)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel", color = InkDim) }
                },
                containerColor = PanelCard
            )
        }
    }
}

@Composable
private fun StageRowItem(
    stage: com.example.flux.data.model.Stage,
    currentState: StageState,
    onStateSelected: (StageState) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(RaisedCard)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(stage.name, style = MaterialTheme.typography.bodyMedium, color = InkLight, fontWeight = FontWeight.Medium)
            Text("Assigned: ${stage.ownerRole}", style = MaterialTheme.typography.labelSmall, color = InkFaint)
        }

        Box {
            val (chipBg, chipFg) = when (currentState) {
                StageState.COMPLETE -> FluxEmerald.copy(alpha = 0.2f) to FluxEmerald
                StageState.IN_PROGRESS -> FluxAccent.copy(alpha = 0.2f) to FluxPrimary
                StageState.BLOCKED -> FluxDanger.copy(alpha = 0.25f) to FluxDanger
                StageState.PENDING -> InkFaint.copy(alpha = 0.15f) to InkDim
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(chipBg)
                    .clickable { menuExpanded = true }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = currentState.label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = chipFg,
                    fontWeight = FontWeight.Bold
                )
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                modifier = Modifier.background(PanelCard)
            ) {
                StageState.entries.forEach { state ->
                    DropdownMenuItem(
                        text = { Text(state.label, color = InkLight) },
                        onClick = {
                            onStateSelected(state)
                            menuExpanded = false
                        }
                    )
                }
            }
        }
    }
}
