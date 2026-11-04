package com.example.flux.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flux.data.model.ContentItem
import com.example.flux.data.model.ProductionFormat
import com.example.flux.data.model.Stage
import com.example.flux.domain.Derive
import com.example.flux.domain.OverallStatus
import com.example.flux.ui.theme.FluxAccent
import com.example.flux.ui.theme.FluxAmber
import com.example.flux.ui.theme.FluxDanger
import com.example.flux.ui.theme.FluxEmerald
import com.example.flux.ui.theme.FluxPrimary
import com.example.flux.ui.theme.FluxSecondary

@Composable
fun FluxStarIcon(modifier: Modifier = Modifier, tint: Color = FluxAccent) {
    Icon(
        imageVector = Icons.Default.AutoAwesome,
        contentDescription = "FLUX Star",
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun StatusBadge(status: OverallStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (status) {
        OverallStatus.PUBLISHED -> FluxEmerald.copy(alpha = 0.15f) to FluxEmerald
        OverallStatus.READY -> FluxSecondary.copy(alpha = 0.15f) to FluxSecondary
        OverallStatus.ON_TRACK -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) to MaterialTheme.colorScheme.primary
        OverallStatus.DELAYED -> FluxDanger.copy(alpha = 0.18f) to FluxDanger
        OverallStatus.BLOCKED -> FluxDanger.copy(alpha = 0.22f) to FluxDanger
        OverallStatus.IDEA -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f) to MaterialTheme.colorScheme.onSurfaceVariant
        OverallStatus.CANCELLED -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f) to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .testTag("status_badge_${status.name.lowercase()}"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status.label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PlatformBadge(platform: String, modifier: Modifier = Modifier) {
    val color = when (platform.lowercase()) {
        "instagram" -> Color(0xFFE1306C)
        "youtube" -> Color(0xFFFF3D3D)
        "tiktok" -> Color(0xFF25F4EE)
        "linkedin" -> Color(0xFF3B9AE1)
        "x" -> Color(0xFF8B98A5)
        "threads" -> Color(0xFFA78BFA)
        else -> MaterialTheme.colorScheme.primary
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.14f))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = platform,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun FormatIcon(format: ProductionFormat, modifier: Modifier = Modifier) {
    val icon = when (format) {
        ProductionFormat.VIDEO -> Icons.Default.Videocam
        ProductionFormat.STATIC -> Icons.Default.Image
        ProductionFormat.WRITTEN -> Icons.Default.Description
        ProductionFormat.BRIEF -> Icons.Default.Article
    }
    Icon(
        imageVector = icon,
        contentDescription = format.label,
        modifier = modifier.size(16.dp),
        tint = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtext: String? = null,
    icon: ImageVector? = null,
    accentColor: Color = FluxAccent,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.8.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = accentColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            if (!subtext.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtext,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ContentItemCard(
    item: ContentItem,
    stages: List<Stage>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = Derive.stageProgress(item, stages)
    val status = Derive.overallStatus(item, stages)
    val currentStage = Derive.currentStage(item, stages)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("content_card_${item.code}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Code, Format, Platform, Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.code,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    PlatformBadge(platform = item.platform)
                    Spacer(modifier = Modifier.width(6.dp))
                    FormatIcon(format = item.productionFormat)
                }
                StatusBadge(status = status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title & Topic
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2
            )
            if (item.topic.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.topic,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stage Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentStage?.let { "Stage: ${it.name}" } ?: "All Stages Complete",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (status == OverallStatus.BLOCKED) FluxDanger else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${progress.complete}/${progress.total} (${progress.pct}%)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress.pct / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = when (status) {
                    OverallStatus.PUBLISHED -> FluxEmerald
                    OverallStatus.BLOCKED -> FluxDanger
                    OverallStatus.DELAYED -> FluxAmber
                    else -> MaterialTheme.colorScheme.primary
                },
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom row: Deadline & Next action indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = if (status == OverallStatus.DELAYED) FluxDanger else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    val deadlineText = if (item.actualPublishDate.isNotBlank()) {
                        "Published: ${item.actualPublishDate}"
                    } else if (item.plannedPublishDate.isNotBlank()) {
                        "Target: ${item.plannedPublishDate}"
                    } else "No deadline"
                    Text(
                        text = deadlineText,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (status == OverallStatus.DELAYED) FluxDanger else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Details",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
