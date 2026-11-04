package com.example.flux.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.flux.data.repository.FluxRepository
import com.example.flux.domain.Derive
import com.example.flux.domain.OverallStatus
import com.example.flux.ui.components.PlatformBadge
import com.example.flux.ui.theme.BaseCanvas
import com.example.flux.ui.theme.FluxAccent
import com.example.flux.ui.theme.FluxEmerald
import com.example.flux.ui.theme.FluxPrimary
import com.example.flux.ui.theme.InkDim
import com.example.flux.ui.theme.InkFaint
import com.example.flux.ui.theme.InkLight
import com.example.flux.ui.theme.LineBorder
import com.example.flux.ui.theme.PanelCard
import com.example.flux.ui.theme.RaisedCard

@Composable
fun PublishingScreen(
    onNavigateToContentDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val workspaceData by FluxRepository.instance.workspaceData.collectAsState()
    val stages = workspaceData.workspace.stages
    val content = workspaceData.content

    val readyToPublish = content.filter {
        !Derive.isPublished(it) && Derive.overallStatus(it, stages) == OverallStatus.READY
    }

    val recentlyPublished = content.filter { Derive.isPublished(it) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("publishing_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Ready for Release section
        item {
            Text(
                text = "READY FOR RELEASE (${readyToPublish.size})",
                style = MaterialTheme.typography.labelSmall,
                color = FluxEmerald,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        if (readyToPublish.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = PanelCard)
                ) {
                    Box(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "No items currently pending final publication signoff.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = InkDim
                        )
                    }
                }
            }
        } else {
            items(readyToPublish) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = PanelCard),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(FluxEmerald.copy(alpha = 0.4f))
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(item.code, style = MaterialTheme.typography.labelSmall, color = FluxPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                PlatformBadge(platform = item.platform)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(FluxEmerald.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("ALL STAGES DONE", style = MaterialTheme.typography.labelSmall, color = FluxEmerald, fontSize = 9.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(item.title, style = MaterialTheme.typography.titleMedium, color = InkLight)
                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { onNavigateToContentDetail(item.id) },
                            modifier = Modifier.fillMaxWidth().height(36.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FluxEmerald)
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp), tint = BaseCanvas)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Launch / Mark Published", color = BaseCanvas, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Live Content history
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "LIVE PUBLISHED ARCHIVE (${recentlyPublished.size})",
                style = MaterialTheme.typography.labelSmall,
                color = InkFaint,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(recentlyPublished) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = PanelCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(LineBorder.copy(alpha = 0.4f))
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(item.code, style = MaterialTheme.typography.labelSmall, color = FluxPrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            PlatformBadge(platform = item.platform)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(item.title, style = MaterialTheme.typography.bodyMedium, color = InkLight, fontWeight = FontWeight.Medium)
                        if (item.actualPublishDate.isNotBlank()) {
                            Text("Published ${item.actualPublishDate}", style = MaterialTheme.typography.labelSmall, color = InkFaint)
                        }
                    }

                    Button(
                        onClick = { onNavigateToContentDetail(item.id) },
                        modifier = Modifier.height(32.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RaisedCard)
                    ) {
                        Text("View Metrics", style = MaterialTheme.typography.labelSmall, color = FluxPrimary)
                    }
                }
            }
        }
    }
}
