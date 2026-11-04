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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import com.example.flux.data.repository.FluxRepository
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
fun SettingsScreen(modifier: Modifier = Modifier) {
    val workspaceData by FluxRepository.instance.workspaceData.collectAsState()
    val ws = workspaceData.workspace

    var wsName by remember(ws.name) { mutableStateOf(ws.name) }
    var contentPrefix by remember(ws.contentPrefix) { mutableStateOf(ws.contentPrefix) }
    var taskPrefix by remember(ws.taskPrefix) { mutableStateOf(ws.taskPrefix) }
    var ideaPrefix by remember(ws.ideaPrefix) { mutableStateOf(ws.ideaPrefix) }
    var savedSuccess by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("settings_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "WORKSPACE SETTINGS",
                style = MaterialTheme.typography.labelSmall,
                color = FluxPrimary,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Configuration & Identifiers",
                style = MaterialTheme.typography.headlineMedium,
                color = InkLight
            )
        }

        // Workspace General Details
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = PanelCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(LineBorder.copy(alpha = 0.5f))
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "GENERAL SETTINGS",
                        style = MaterialTheme.typography.labelSmall,
                        color = InkFaint
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = wsName,
                        onValueChange = { wsName = it },
                        label = { Text("Workspace Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_ws_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = InkLight,
                            unfocusedTextColor = InkLight
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = contentPrefix,
                            onValueChange = { contentPrefix = it },
                            label = { Text("Content Prefix") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = InkLight,
                                unfocusedTextColor = InkLight
                            )
                        )
                        OutlinedTextField(
                            value = taskPrefix,
                            onValueChange = { taskPrefix = it },
                            label = { Text("Task Prefix") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = InkLight,
                                unfocusedTextColor = InkLight
                            )
                        )
                        OutlinedTextField(
                            value = ideaPrefix,
                            onValueChange = { ideaPrefix = it },
                            label = { Text("Idea Prefix") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = InkLight,
                                unfocusedTextColor = InkLight
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            FluxRepository.instance.updateWorkspace(wsName, contentPrefix, taskPrefix, ideaPrefix)
                            savedSuccess = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FluxAccent),
                        modifier = Modifier.align(Alignment.End).testTag("settings_save_button")
                    ) {
                        if (savedSuccess) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp), tint = BaseCanvas)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Saved", color = BaseCanvas)
                        } else {
                            Text("Save Changes", color = BaseCanvas)
                        }
                    }
                }
            }
        }

        // Active Pipeline Stages
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = PanelCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(LineBorder.copy(alpha = 0.5f))
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "ACTIVE PRODUCTION PIPELINE STAGES",
                        style = MaterialTheme.typography.labelSmall,
                        color = InkFaint
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    ws.stages.forEachIndexed { idx, stage ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(RaisedCard)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${idx + 1}.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = FluxPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stage.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = InkLight,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = stage.ownerRole,
                                style = MaterialTheme.typography.labelSmall,
                                color = InkDim
                            )
                        }
                    }
                }
            }
        }

        // Sample Data Management
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = PanelCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(LineBorder.copy(alpha = 0.5f))
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "DATA RESET",
                        style = MaterialTheme.typography.labelSmall,
                        color = FluxDanger
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Reset all content, tasks, ideas and metrics back to the initial production dataset.",
                        style = MaterialTheme.typography.bodySmall,
                        color = InkDim
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { FluxRepository.instance.resetToSampleData() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FluxDanger),
                        modifier = Modifier.testTag("reset_data_button")
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset to Demo State")
                    }
                }
            }
        }
    }
}
