package com.example.flux.domain

import com.example.flux.data.model.ContentItem
import com.example.flux.data.model.LifecycleOverride
import com.example.flux.data.model.Member
import com.example.flux.data.model.PerformanceMetrics
import com.example.flux.data.model.Stage
import com.example.flux.data.model.StageState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

enum class OverallStatus(val label: String) {
    IDEA("Idea"),
    ON_TRACK("On track"),
    DELAYED("Delayed"),
    BLOCKED("Blocked"),
    READY("Ready"),
    PUBLISHED("Published"),
    CANCELLED("Cancelled")
}

data class StageProgress(
    val complete: Int,
    val total: Int,
    val pct: Int
)

object Derive {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun today(): String = LocalDate.now().format(formatter)

    fun stageState(item: ContentItem, stageId: String): StageState {
        return item.stageStates[stageId] ?: StageState.PENDING
    }

    fun stageProgress(item: ContentItem, stages: List<Stage>): StageProgress {
        val total = stages.size
        val complete = stages.count { stageState(item, it.id) == StageState.COMPLETE }
        val pct = if (total == 0) 0 else (complete * 100) / total
        return StageProgress(complete, total, pct)
    }

    fun allStagesComplete(item: ContentItem, stages: List<Stage>): Boolean {
        return stages.isNotEmpty() && stages.all { stageState(item, it.id) == StageState.COMPLETE }
    }

    fun currentStage(item: ContentItem, stages: List<Stage>): Stage? {
        return stages.firstOrNull { stageState(item, it.id) != StageState.COMPLETE }
    }

    fun isPublished(item: ContentItem): Boolean {
        return item.lifecycle == LifecycleOverride.PUBLISHED || item.actualPublishDate.isNotBlank()
    }

    fun isBlocked(item: ContentItem, stages: List<Stage>): Boolean {
        if (item.blocker.isNotBlank()) return true
        val current = currentStage(item, stages) ?: return false
        return stageState(item, current.id) == StageState.BLOCKED
    }

    fun daysBetween(from: String, to: String): Long? {
        return try {
            val d1 = LocalDate.parse(from, formatter)
            val d2 = LocalDate.parse(to, formatter)
            ChronoUnit.DAYS.between(d1, d2)
        } catch (_: Exception) {
            null
        }
    }

    fun daysRemaining(item: ContentItem, now: String = today()): Long? {
        if (item.plannedPublishDate.isBlank()) return null
        return daysBetween(now, item.plannedPublishDate)
    }

    fun isOverdue(item: ContentItem, stages: List<Stage>, now: String = today()): Boolean {
        if (isPublished(item) || item.lifecycle == LifecycleOverride.CANCELLED) return false
        val current = currentStage(item, stages)
        if (current != null) {
            val stageDeadline = item.stageDeadlines[current.id]
            if (!stageDeadline.isNullOrBlank()) {
                val remaining = daysBetween(now, stageDeadline)
                if (remaining != null && remaining < 0) return true
            }
        }
        val remaining = daysRemaining(item, now)
        return remaining != null && remaining < 0
    }

    fun overallStatus(item: ContentItem, stages: List<Stage>, now: String = today()): OverallStatus {
        if (isPublished(item)) return OverallStatus.PUBLISHED
        if (item.lifecycle == LifecycleOverride.CANCELLED) return OverallStatus.CANCELLED
        if (item.lifecycle == LifecycleOverride.IDEA) return OverallStatus.IDEA
        if (isBlocked(item, stages)) return OverallStatus.BLOCKED
        if (allStagesComplete(item, stages)) return OverallStatus.READY
        if (isOverdue(item, stages, now)) return OverallStatus.DELAYED
        return OverallStatus.ON_TRACK
    }

    fun responsibleMember(item: ContentItem, stages: List<Stage>, members: List<Member>): Member? {
        val stage = currentStage(item, stages)
        val memberId = if (stage != null) {
            item.stageAssignees[stage.id] ?: item.ownerId
        } else {
            item.ownerId
        }
        return members.find { it.id == memberId }
    }

    fun engagementRate(metrics: PerformanceMetrics?): Double {
        if (metrics == null) return 0.0
        val views = metrics.views ?: metrics.reach ?: return 0.0
        if (views <= 0) return 0.0
        val interactions = (metrics.likes ?: 0) + (metrics.comments ?: 0) + (metrics.shares ?: 0) + (metrics.saves ?: 0)
        return (interactions.toDouble() / views.toDouble()) * 100.0
    }
}
