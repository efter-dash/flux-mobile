package com.example.flux.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flux.data.model.StageState
import com.example.flux.data.repository.FluxRepository
import com.example.flux.domain.Derive
import com.example.flux.domain.OverallStatus
import com.example.flux.ui.components.ContentItemCard
import com.example.flux.ui.components.PlatformBadge
import com.example.flux.ui.components.StatusBadge
import com.example.flux.ui.theme.BaseCanvas
import com.example.flux.ui.theme.FluxAccent
import com.example.flux.ui.theme.FluxDanger
import com.example.flux.ui.theme.FluxEmerald
import com.example.flux.ui.theme.FluxPrimary
import com.example.flux.ui.theme.InkDim
import com.example.flux.ui.theme.InkFaint
import com.example.flux.ui.theme.InkLight
import com.example.flux.ui.theme.LineBorder
import com.example.flux.ui.theme.PanelCard
import com.example.flux.ui.theme.RaisedCard

@Composable
fun PipelineScreen(
    onNavigateToContentDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val workspaceData by FluxRepository.instance.workspaceData.collectAsState()
    val stages = workspaceData.workspace.stages
    val content = workspaceData.content

    var selectedStageIndex by remember { mutableIntStateOf(0) }
    val currentStage = stages.getOrNull(selectedStageIndex) ?: stages.firstOrNull()

    val stageItems = if (currentStage != null) {
        content.filter { item ->
            val active = Derive.currentStage(item, stages)
            active?.id == currentStage.id && !Derive.isPublished(item)
        }
    } else emptyList()

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("pipeline_screen")
    ) {
        // Stage Tabs
        if (stages.isNotEmpty()) {
            ScrollableTabRow(
                selectedTabIndex = selectedStageIndex,
                containerColor = PanelCard,
                contentColor = FluxAccent,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedStageIndex]),
                        color = FluxAccent
                    )
                }
            ) {
                stages.forEachIndexed { index, stage ->
                    val count = content.count {
                        val active = Derive.currentStage(it, stages)
                        active?.id == stage.id && !Derive.isPublished(it)
                    }
                    Tab(
                        selected = selectedStageIndex == index,
                        onClick = { selectedStageIndex = index },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = stage.name,
                                    fontWeight = if (selectedStageIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedStageIndex == index) InkLight else InkDim
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(if (selectedStageIndex == index) FluxAccent else RaisedCard)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "$count",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (selectedStageIndex == index) BaseCanvas else InkDim,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }

        // Selected Stage Header Info
        if (currentStage != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = RaisedCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "RESPONSIBLE ROLE",
                            style = MaterialTheme.typography.labelSmall,
                            color = InkFaint
                        )
                        Text(
                            text = currentStage.ownerRole,
                            style = MaterialTheme.typography.bodyMedium,
                            color = FluxPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(FluxAccent.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Verb: ${currentStage.verb}",
                            style = MaterialTheme.typography.labelSmall,
                            color = FluxAccent
                        )
                    }
                }
            }
        }

        // Items in this stage
        if (stageItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No content currently in this stage",
                        style = MaterialTheme.typography.titleMedium,
                        color = InkDim
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Work advances automatically as prior stages complete.",
                        style = MaterialTheme.typography.bodySmall,
                        color = InkFaint
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(stageItems) { item ->
                    if (currentStage != null) {
                        PipelineItemCard(
                            item = item,
                            stage = currentStage,
                            onItemClick = { onNavigateToContentDetail(item.id) },
                            onAdvanceStage = {
                                FluxRepository.instance.updateStageState(
                                    contentId = item.id,
                                    stageId = currentStage.id,
                                    newState = StageState.COMPLETE
                                )
                            },
                            onToggleBlocked = {
                                val curState = item.stageStates[currentStage.id] ?: StageState.PENDING
                                val next = if (curState == StageState.BLOCKED) StageState.IN_PROGRESS else StageState.BLOCKED
                                FluxRepository.instance.updateStageState(
                                    contentId = item.id,
                                    stageId = currentStage.id,
                                    newState = next
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PipelineItemCard(
    item: com.example.flux.data.model.ContentItem,
    stage: com.example.flux.data.model.Stage,
    onItemClick: () -> Unit,
    onAdvanceStage: () -> Unit,
    onToggleBlocked: () -> Unit
) {
    val stageState = item.stageStates[stage.id] ?: StageState.PENDING
    val isBlocked = stageState == StageState.BLOCKED

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = PanelCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (isBlocked) FluxDanger else LineBorder.copy(alpha = 0.6f)
            )
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.code,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = FluxPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    PlatformBadge(platform = item.platform)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isBlocked) FluxDanger.copy(alpha = 0.2f) else FluxAccent.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = stageState.label.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isBlocked) FluxDanger else FluxPrimary,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                color = InkLight
            )

            if (item.blocker.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = FluxDanger
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.blocker,
                        style = MaterialTheme.typography.bodySmall,
                        color = FluxDanger,
                        maxLines = 2
                    )
                }
            }

            if (item.nextAction.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Next: ${item.nextAction}",
                    style = MaterialTheme.typography.bodySmall,
                    color = InkDim,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onToggleBlocked,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isBlocked) FluxEmerald else FluxDanger
                    ),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = if (isBlocked) "Unblock" else "Flag Blocked",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onAdvanceStage,
                    colors = ButtonDefaults.buttonColors(containerColor = FluxAccent),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = BaseCanvas
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Complete ${stage.verb}",
                        style = MaterialTheme.typography.labelSmall,
                        color = BaseCanvas,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
