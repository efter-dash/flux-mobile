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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.flux.data.model.IdeaStatus
import com.example.flux.data.model.ProductionFormat
import com.example.flux.data.repository.FluxRepository
import com.example.flux.ui.components.PlatformBadge
import com.example.flux.ui.theme.BaseCanvas
import com.example.flux.ui.theme.FluxAccent
import com.example.flux.ui.theme.FluxEmerald
import com.example.flux.ui.theme.FluxPrimary
import com.example.flux.ui.theme.FluxSecondary
import com.example.flux.ui.theme.InkDim
import com.example.flux.ui.theme.InkFaint
import com.example.flux.ui.theme.InkLight
import com.example.flux.ui.theme.LineBorder
import com.example.flux.ui.theme.PanelCard

@Composable
fun IdeasScreen(
    onNavigateToContentDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val workspaceData by FluxRepository.instance.workspaceData.collectAsState()
    val ideas = workspaceData.ideas

    var showAddIdeaDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag("ideas_screen")
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "IDEA BACKLOG (${ideas.size})",
                        style = MaterialTheme.typography.labelSmall,
                        color = InkFaint,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                items(ideas) { idea ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("idea_card_${idea.code}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = PanelCard),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(LineBorder.copy(alpha = 0.5f))
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
                                        text = idea.code,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = FluxPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    PlatformBadge(platform = idea.platform)
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(FluxSecondary.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${idea.potential} Potential",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = FluxSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = idea.topic,
                                style = MaterialTheme.typography.titleMedium,
                                color = InkLight
                            )

                            if (idea.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = idea.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = InkDim
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "By: ${idea.submittedBy}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = InkFaint
                                )

                                if (idea.status != IdeaStatus.CONVERTED) {
                                    Button(
                                        onClick = {
                                            val created = FluxRepository.instance.convertIdeaToContent(
                                                ideaId = idea.id,
                                                format = ProductionFormat.VIDEO
                                            )
                                            if (created != null) {
                                                onNavigateToContentDetail(created.id)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = FluxAccent),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = BaseCanvas
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Promote to Plan",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = BaseCanvas,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(FluxEmerald.copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("CONVERTED", style = MaterialTheme.typography.labelSmall, color = FluxEmerald)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddIdeaDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("add_idea_fab"),
            containerColor = FluxAccent,
            contentColor = BaseCanvas
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Idea")
        }

        if (showAddIdeaDialog) {
            var ideaTopic by remember { mutableStateOf("") }
            var ideaDesc by remember { mutableStateOf("") }
            var ideaPlatform by remember { mutableStateOf("Instagram") }
            var ideaSubmitter by remember { mutableStateOf(workspaceData.members.firstOrNull()?.name ?: "Creator") }

            AlertDialog(
                onDismissRequest = { showAddIdeaDialog = false },
                title = { Text("Log New Idea", color = InkLight) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = ideaTopic,
                            onValueChange = { ideaTopic = it },
                            label = { Text("Idea Hook / Concept") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("idea_topic_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = InkLight,
                                unfocusedTextColor = InkLight
                            )
                        )
                        OutlinedTextField(
                            value = ideaDesc,
                            onValueChange = { ideaDesc = it },
                            label = { Text("Notes & References") },
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
                            if (ideaTopic.isNotBlank()) {
                                FluxRepository.instance.addIdea(
                                    topic = ideaTopic,
                                    description = ideaDesc,
                                    contentType = "Reels",
                                    platform = ideaPlatform,
                                    priority = "Medium",
                                    potential = "High",
                                    submittedBy = ideaSubmitter
                                )
                                showAddIdeaDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FluxAccent)
                    ) {
                        Text("Add to Backlog", color = BaseCanvas)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddIdeaDialog = false }) { Text("Cancel", color = InkDim) }
                },
                containerColor = PanelCard
            )
        }
    }
}
