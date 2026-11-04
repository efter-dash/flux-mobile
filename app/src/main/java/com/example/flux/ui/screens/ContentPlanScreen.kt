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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flux.data.model.ProductionFormat
import com.example.flux.data.repository.FluxRepository
import com.example.flux.domain.Derive
import com.example.flux.domain.OverallStatus
import com.example.flux.ui.components.ContentItemCard
import com.example.flux.ui.theme.BaseCanvas
import com.example.flux.ui.theme.FluxAccent
import com.example.flux.ui.theme.FluxPrimary
import com.example.flux.ui.theme.InkDim
import com.example.flux.ui.theme.InkFaint
import com.example.flux.ui.theme.InkLight
import com.example.flux.ui.theme.LineBorder
import com.example.flux.ui.theme.PanelCard
import com.example.flux.ui.theme.RaisedCard
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ContentPlanScreen(
    onNavigateToContentDetail: (String) -> Unit,
    showCreateInitialDialog: Boolean = false,
    modifier: Modifier = Modifier
) {
    val workspaceData by FluxRepository.instance.workspaceData.collectAsState()
    val stages = workspaceData.workspace.stages
    val content = workspaceData.content

    var searchQuery by remember { mutableStateOf("") }
    var selectedPlatformFilter by remember { mutableStateOf<String?>(null) }
    var selectedFormatFilter by remember { mutableStateOf<ProductionFormat?>(null) }
    var showCreateDialog by remember { mutableStateOf(showCreateInitialDialog) }

    val filteredContent = content.filter { item ->
        val matchesSearch = searchQuery.isBlank() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.code.contains(searchQuery, ignoreCase = true) ||
                item.topic.contains(searchQuery, ignoreCase = true)

        val matchesPlatform = selectedPlatformFilter == null || item.platform.equals(selectedPlatformFilter, ignoreCase = true)
        val matchesFormat = selectedFormatFilter == null || item.productionFormat == selectedFormatFilter

        matchesSearch && matchesPlatform && matchesFormat
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag("content_plan_screen")
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .testTag("content_search_input"),
                placeholder = { Text("Search by code, title or topic...", color = InkFaint) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = InkDim)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = InkDim)
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FluxAccent,
                    unfocusedBorderColor = LineBorder,
                    focusedTextColor = InkLight,
                    unfocusedTextColor = InkLight,
                    focusedContainerColor = PanelCard,
                    unfocusedContainerColor = PanelCard
                ),
                shape = RoundedCornerShape(8.dp)
            )

            // Platform Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedPlatformFilter == null && selectedFormatFilter == null,
                        onClick = {
                            selectedPlatformFilter = null
                            selectedFormatFilter = null
                        },
                        label = { Text("All (${content.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FluxAccent.copy(alpha = 0.2f),
                            selectedLabelColor = FluxPrimary,
                            containerColor = RaisedCard,
                            labelColor = InkDim
                        )
                    )
                }

                // Formats
                items(ProductionFormat.entries) { format ->
                    FilterChip(
                        selected = selectedFormatFilter == format,
                        onClick = {
                            selectedFormatFilter = if (selectedFormatFilter == format) null else format
                        },
                        label = { Text(format.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FluxAccent.copy(alpha = 0.2f),
                            selectedLabelColor = FluxPrimary,
                            containerColor = RaisedCard,
                            labelColor = InkDim
                        )
                    )
                }

                // Platforms
                workspaceData.workspace.taxonomies.platforms.forEach { platform ->
                    item {
                        FilterChip(
                            selected = selectedPlatformFilter == platform.label,
                            onClick = {
                                selectedPlatformFilter = if (selectedPlatformFilter == platform.label) null else platform.label
                            },
                            label = { Text(platform.label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FluxAccent.copy(alpha = 0.2f),
                                selectedLabelColor = FluxPrimary,
                                containerColor = RaisedCard,
                                labelColor = InkDim
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // List of items
            if (filteredContent.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No content matches the selected filters",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkDim
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredContent) { item ->
                        ContentItemCard(
                            item = item,
                            stages = stages,
                            onClick = { onNavigateToContentDetail(item.id) }
                        )
                    }
                }
            }
        }

        // FAB to add content
        FloatingActionButton(
            onClick = { showCreateDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("content_plan_add_fab"),
            containerColor = FluxAccent,
            contentColor = BaseCanvas
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Content")
        }

        if (showCreateDialog) {
            CreateContentDialog(
                onDismiss = { showCreateDialog = false },
                onConfirm = { title, topic, format, platform, plannedDate, priority ->
                    val ownerId = workspaceData.members.firstOrNull()?.id ?: ""
                    val created = FluxRepository.instance.addContent(
                        title = title,
                        topic = topic,
                        contentType = "Reel",
                        category = "Product",
                        productionFormat = format,
                        platform = platform,
                        plannedPublishDate = plannedDate,
                        priority = priority,
                        ownerId = ownerId
                    )
                    showCreateDialog = false
                    onNavigateToContentDetail(created.id)
                }
            )
        }
    }
}

@Composable
fun CreateContentDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, topic: String, format: ProductionFormat, platform: String, plannedDate: String, priority: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("") }
    var selectedFormat by remember { mutableStateOf(ProductionFormat.VIDEO) }
    var selectedPlatform by remember { mutableStateOf("Instagram") }
    var plannedDate by remember {
        mutableStateOf(LocalDate.now().plusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE))
    }
    var priority by remember { mutableStateOf("High") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Content Item", color = InkLight) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title / Working Headline") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_content_title_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = InkLight,
                        unfocusedTextColor = InkLight
                    )
                )

                OutlinedTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    label = { Text("Core Topic or Angle") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = InkLight,
                        unfocusedTextColor = InkLight
                    )
                )

                // Format Picker
                Text(text = "Production Format", style = MaterialTheme.typography.labelSmall, color = InkFaint)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(ProductionFormat.entries) { fmt ->
                        FilterChip(
                            selected = selectedFormat == fmt,
                            onClick = { selectedFormat = fmt },
                            label = { Text(fmt.label) }
                        )
                    }
                }

                // Platform Picker
                Text(text = "Primary Platform", style = MaterialTheme.typography.labelSmall, color = InkFaint)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val plats = listOf("Instagram", "YouTube", "TikTok", "LinkedIn", "X", "Threads")
                    items(plats) { plat ->
                        FilterChip(
                            selected = selectedPlatform == plat,
                            onClick = { selectedPlatform = plat },
                            label = { Text(plat) }
                        )
                    }
                }

                OutlinedTextField(
                    value = plannedDate,
                    onValueChange = { plannedDate = it },
                    label = { Text("Planned Publish Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = InkLight,
                        unfocusedTextColor = InkLight
                    )
                )

                // Priority
                Text(text = "Priority", style = MaterialTheme.typography.labelSmall, color = InkFaint)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("High", "Medium", "Low").forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, topic, selectedFormat, selectedPlatform, plannedDate, priority)
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = FluxAccent)
            ) {
                Text("Create Item", color = BaseCanvas)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = InkDim)
            }
        },
        containerColor = PanelCard
    )
}
