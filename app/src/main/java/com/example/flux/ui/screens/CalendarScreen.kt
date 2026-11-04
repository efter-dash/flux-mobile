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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flux.data.repository.FluxRepository
import com.example.flux.ui.components.ContentItemCard
import com.example.flux.ui.theme.FluxAccent
import com.example.flux.ui.theme.FluxEmerald
import com.example.flux.ui.theme.FluxPrimary
import com.example.flux.ui.theme.InkDim
import com.example.flux.ui.theme.InkFaint
import com.example.flux.ui.theme.InkLight
import com.example.flux.ui.theme.LineBorder
import com.example.flux.ui.theme.PanelCard
import com.example.flux.ui.theme.RaisedCard
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun CalendarScreen(
    onNavigateToContentDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val workspaceData by FluxRepository.instance.workspaceData.collectAsState()
    val stages = workspaceData.workspace.stages
    val content = workspaceData.content

    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    val daysInMonth = currentYearMonth.lengthOfMonth()
    val firstDayOfWeek = currentYearMonth.atDay(1).dayOfWeek.value // 1=Mon .. 7=Sun

    val selectedDateString = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
    val itemsForSelectedDate = content.filter {
        it.plannedPublishDate == selectedDateString || it.actualPublishDate == selectedDateString
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("calendar_screen")
    ) {
        // Month Navigation Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = PanelCard)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { currentYearMonth = currentYearMonth.minusMonths(1) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev Month", tint = InkLight)
                    }

                    Text(
                        text = currentYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                        style = MaterialTheme.typography.titleMedium,
                        color = InkLight,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = { currentYearMonth = currentYearMonth.plusMonths(1) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month", tint = InkLight)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Days of week header (Mon - Sun)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                        Text(
                            text = day,
                            style = MaterialTheme.typography.labelSmall,
                            color = InkFaint,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Calendar Day Grid
                val totalSlots = ((firstDayOfWeek - 1) + daysInMonth + 6) / 7 * 7
                val weeks = totalSlots / 7

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (w in 0 until weeks) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                            for (d in 1..7) {
                                val dayNumber = (w * 7 + d) - (firstDayOfWeek - 1)
                                if (dayNumber in 1..daysInMonth) {
                                    val date = currentYearMonth.atDay(dayNumber)
                                    val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                                    val hasItems = content.any { it.plannedPublishDate == dateStr || it.actualPublishDate == dateStr }
                                    val isSelected = date == selectedDate
                                    val isToday = date == LocalDate.now()

                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    isSelected -> FluxAccent
                                                    isToday -> RaisedCard
                                                    else -> Color.Transparent
                                                }
                                            )
                                            .clickable { selectedDate = date },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "$dayNumber",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (isSelected) Color.Black else InkLight,
                                                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                            if (hasItems) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(4.dp)
                                                        .clip(CircleShape)
                                                        .background(if (isSelected) Color.Black else FluxEmerald)
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.size(38.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Selected Date Schedule Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SCHEDULE FOR ${selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")).uppercase()}",
                style = MaterialTheme.typography.labelSmall,
                color = InkFaint
            )
            Text(
                text = "${itemsForSelectedDate.size} posts",
                style = MaterialTheme.typography.labelSmall,
                color = FluxPrimary
            )
        }

        if (itemsForSelectedDate.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No content planned for this date.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkDim
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(itemsForSelectedDate) { item ->
                    ContentItemCard(
                        item = item,
                        stages = stages,
                        onClick = { onNavigateToContentDetail(item.id) }
                    )
                }
            }
        }
    }
}
