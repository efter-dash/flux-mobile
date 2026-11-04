package com.example.flux.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.flux.ui.components.FluxStarIcon
import com.example.flux.ui.components.MetricCard
import com.example.flux.ui.theme.FluxAccent
import com.example.flux.ui.theme.FluxAmber
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

@Composable
fun DashboardScreen(
    onNavigateToContentDetail: (String) -> Unit,
    onNavigateToCreateContent: () -> Unit,
    onNavigateToPipeline: () -> Unit,
    modifier: Modifier = Modifier
) {
    val workspaceData by FluxRepository.instance.workspaceData.collectAsState()
    val stages = workspaceData.workspace.stages
    val content = workspaceData.content

    val totalPlanned = content.size
    val published = content.count { Derive.isPublished(it) }
    val delayed = content.count { Derive.overallStatus(it, stages) == OverallStatus.DELAYED }
    val blocked = content.count { Derive.overallStatus(it, stages) == OverallStatus.BLOCKED }
    val ready = content.count { Derive.overallStatus(it, stages) == OverallStatus.READY }
    val inProgress = content.count { Derive.overallStatus(it, stages) == OverallStatus.ON_TRACK }

    val completionRate = if (totalPlanned > 0) ((published * 100) / totalPlanned) else 0

    val attentionItems = content.filter {
        val s = Derive.overallStatus(it, stages)
        s == OverallStatus.BLOCKED || s == OverallStatus.DELAYED
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .testTag("dashboard_screen"),
            contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp)
        ) {
            // Header: Workspace info & month
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FluxStarIcon(modifier = Modifier.size(20.dp), tint = FluxAccent)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "FLUX COMMAND",
                                style = MaterialTheme.typography.labelSmall,
                                color = FluxPrimary,
                                letterSpacing = 1.2.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = workspaceData.workspace.name,
                            style = MaterialTheme.typography.headlineMedium,
                            color = InkLight
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(RaisedCard)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Active Cycle",
                            style = MaterialTheme.typography.labelSmall,
                            color = FluxEmerald
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
            }

            // High-Level KPI Metric Cards Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Published",
                        value = "$published",
                        subtext = "$completionRate% completed",
                        icon = Icons.Default.CheckCircleOutline,
                        accentColor = FluxEmerald,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "In Flight",
                        value = "$inProgress",
                        subtext = "$ready ready to post",
                        icon = Icons.Default.HourglassTop,
                        accentColor = FluxAccent,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Attention",
                        value = "${delayed + blocked}",
                        subtext = "$blocked blocked, $delayed late",
                        icon = Icons.Default.WarningAmber,
                        accentColor = FluxDanger,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Pipeline Stage Health Breakdown
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onNavigateToPipeline),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = PanelCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(LineBorder.copy(alpha = 0.5f)))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PIPELINE BREAKDOWN",
                                style = MaterialTheme.typography.labelSmall,
                                color = InkFaint,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = "View Pipeline →",
                                style = MaterialTheme.typography.labelSmall,
                                color = FluxAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            stages.forEach { stage ->
                                val count = content.count {
                                    val current = Derive.currentStage(it, stages)
                                    current?.id == stage.id && !Derive.isPublished(it)
                                }
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(if (count > 0) FluxAccent.copy(alpha = 0.2f) else RaisedCard),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$count",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = if (count > 0) FluxPrimary else InkFaint
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = stage.name.split(" ").firstOrNull() ?: stage.name,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = InkDim,
                                        fontSize = 10.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Attention / Blockers Section
            if (attentionItems.isNotEmpty()) {
                item {
                    Text(
                        text = "NEEDS ATTENTION (${attentionItems.size})",
                        style = MaterialTheme.typography.labelSmall,
                        color = FluxDanger,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(attentionItems) { item ->
                    ContentItemCard(
                        item = item,
                        stages = stages,
                        onClick = { onNavigateToContentDetail(item.id) },
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            // Recent Active Content
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECENT PRODUCTION QUEUE",
                        style = MaterialTheme.typography.labelSmall,
                        color = InkFaint,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "${content.size} items",
                        style = MaterialTheme.typography.labelSmall,
                        color = InkDim
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(content.take(5)) { item ->
                ContentItemCard(
                    item = item,
                    stages = stages,
                    onClick = { onNavigateToContentDetail(item.id) },
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = onNavigateToCreateContent,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("create_content_fab"),
            containerColor = FluxAccent,
            contentColor = BaseCanvas
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Content")
        }
    }
}
