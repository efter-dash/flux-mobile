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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flux.data.model.TaskItem
import com.example.flux.data.model.TaskStatus
import com.example.flux.data.repository.FluxRepository
import com.example.flux.domain.Derive
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
fun TasksScreen(modifier: Modifier = Modifier) {
    val workspaceData by FluxRepository.instance.workspaceData.collectAsState()
    val tasks = workspaceData.tasks
    val members = workspaceData.members

    var selectedStatusFilter by remember { mutableStateOf<TaskStatus?>(null) }
    var showAddTaskDialog by remember { mutableStateOf(false) }

    val filteredTasks = tasks.filter {
        selectedStatusFilter == null || it.status == selectedStatusFilter
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag("tasks_screen")
        ) {
            // Filter row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedStatusFilter == null,
                        onClick = { selectedStatusFilter = null },
                        label = { Text("All (${tasks.size})") }
                    )
                }
                items(TaskStatus.entries) { status ->
                    FilterChip(
                        selected = selectedStatusFilter == status,
                        onClick = {
                            selectedStatusFilter = if (selectedStatusFilter == status) null else status
                        },
                        label = { Text(status.label) }
                    )
                }
            }

            if (filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No tasks found", color = InkDim)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredTasks) { task ->
                        val isComplete = task.status == TaskStatus.COMPLETED
                        val member = members.find { it.id == task.memberId }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("task_item_${task.code}"),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = PanelCard),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(LineBorder.copy(alpha = 0.5f))
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { FluxRepository.instance.toggleTaskStatus(task.id) }
                                ) {
                                    Icon(
                                        imageVector = if (isComplete) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = "Toggle status",
                                        tint = if (isComplete) FluxEmerald else FluxAccent
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = task.code,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = FluxPrimary
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(
                                                    when (task.status) {
                                                        TaskStatus.COMPLETED -> FluxEmerald.copy(alpha = 0.15f)
                                                        TaskStatus.BLOCKED -> FluxDanger.copy(alpha = 0.2f)
                                                        else -> FluxAccent.copy(alpha = 0.15f)
                                                    }
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = task.status.label.uppercase(),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 9.sp,
                                                color = when (task.status) {
                                                    TaskStatus.COMPLETED -> FluxEmerald
                                                    TaskStatus.BLOCKED -> FluxDanger
                                                    else -> FluxPrimary
                                                }
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            textDecoration = if (isComplete) TextDecoration.LineThrough else TextDecoration.None
                                        ),
                                        color = if (isComplete) InkFaint else InkLight,
                                        fontWeight = FontWeight.Medium
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = member?.name ?: "Unassigned",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = InkDim
                                        )
                                        if (task.deadline.isNotBlank()) {
                                            Text(
                                                text = "Due: ${task.deadline}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = InkFaint
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddTaskDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("add_task_fab"),
            containerColor = FluxAccent,
            contentColor = BaseCanvas
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
        }

        if (showAddTaskDialog) {
            var taskTitle by remember { mutableStateOf("") }
            var taskPriority by remember { mutableStateOf("Medium") }
            var taskDeadline by remember { mutableStateOf(Derive.today()) }

            AlertDialog(
                onDismissRequest = { showAddTaskDialog = false },
                title = { Text("New Action Item", color = InkLight) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = taskTitle,
                            onValueChange = { taskTitle = it },
                            label = { Text("Task Description") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("task_title_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = InkLight,
                                unfocusedTextColor = InkLight
                            )
                        )
                        OutlinedTextField(
                            value = taskDeadline,
                            onValueChange = { taskDeadline = it },
                            label = { Text("Deadline (YYYY-MM-DD)") },
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
                            if (taskTitle.isNotBlank()) {
                                val memberId = members.firstOrNull()?.id ?: ""
                                FluxRepository.instance.addTask(
                                    title = taskTitle,
                                    memberId = memberId,
                                    priority = taskPriority,
                                    deadline = taskDeadline
                                )
                                showAddTaskDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FluxAccent)
                    ) {
                        Text("Add Task", color = BaseCanvas)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddTaskDialog = false }) { Text("Cancel", color = InkDim) }
                },
                containerColor = PanelCard
            )
        }
    }
}
