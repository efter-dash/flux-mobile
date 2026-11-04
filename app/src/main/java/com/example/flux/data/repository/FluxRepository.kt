package com.example.flux.data.repository

import com.example.flux.data.model.ContentItem
import com.example.flux.data.model.Idea
import com.example.flux.data.model.IdeaStatus
import com.example.flux.data.model.LifecycleOverride
import com.example.flux.data.model.Member
import com.example.flux.data.model.PerformanceMetrics
import com.example.flux.data.model.ProductionFormat
import com.example.flux.data.model.SampleData
import com.example.flux.data.model.StageState
import com.example.flux.data.model.TaskItem
import com.example.flux.data.model.TaskStatus
import com.example.flux.data.model.WeeklyReview
import com.example.flux.data.model.WorkspaceData
import com.example.flux.domain.Derive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

class FluxRepository private constructor() {

    private val _workspaceData = MutableStateFlow(SampleData.createInitialData())
    val workspaceData: StateFlow<WorkspaceData> = _workspaceData.asStateFlow()

    companion object {
        val instance: FluxRepository by lazy { FluxRepository() }
    }

    private fun nextCode(prefix: String, currentCount: Int): String {
        return "$prefix-${(currentCount + 1).toString().padStart(4, '0')}"
    }

    // ---------------- Content Operations ----------------

    fun addContent(
        title: String,
        topic: String,
        contentType: String,
        category: String,
        productionFormat: ProductionFormat,
        platform: String,
        plannedPublishDate: String,
        priority: String,
        ownerId: String
    ): ContentItem {
        val current = _workspaceData.value
        val newCount = current.workspace.counters.content + 1
        val code = nextCode(current.workspace.contentPrefix, current.workspace.counters.content)
        val month = plannedPublishDate.takeIf { it.length >= 7 }?.substring(0, 7)
            ?: LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))

        val initialStates = current.workspace.stages.associate { it.id to StageState.PENDING }

        val newItem = ContentItem(
            id = "c_${UUID.randomUUID()}",
            workspaceId = current.workspace.id,
            code = code,
            month = month,
            title = title,
            topic = topic,
            contentType = contentType,
            category = category,
            productionFormat = productionFormat,
            platform = platform,
            ownerId = ownerId,
            stageStates = initialStates,
            plannedPublishDate = plannedPublishDate,
            priority = priority,
            lifecycle = LifecycleOverride.ACTIVE,
            createdAt = Derive.today(),
            updatedAt = Derive.today()
        )

        val updatedWorkspace = current.workspace.copy(
            counters = current.workspace.counters.copy(content = newCount)
        )
        _workspaceData.value = current.copy(
            workspace = updatedWorkspace,
            content = listOf(newItem) + current.content
        )
        return newItem
    }

    fun updateContent(item: ContentItem) {
        val current = _workspaceData.value
        val updated = current.content.map { if (it.id == item.id) item.copy(updatedAt = Derive.today()) else it }
        _workspaceData.value = current.copy(content = updated)
    }

    fun deleteContent(id: String) {
        val current = _workspaceData.value
        _workspaceData.value = current.copy(content = current.content.filter { it.id != id })
    }

    fun updateStageState(contentId: String, stageId: String, newState: StageState) {
        val current = _workspaceData.value
        val updatedContent = current.content.map { item ->
            if (item.id == contentId) {
                val newStates = item.stageStates.toMutableMap().apply { put(stageId, newState) }
                val allDone = current.workspace.stages.isNotEmpty() &&
                        current.workspace.stages.all { (newStates[it.id] ?: StageState.PENDING) == StageState.COMPLETE }
                val newLifecycle = if (allDone && item.lifecycle == LifecycleOverride.ACTIVE) {
                    LifecycleOverride.ACTIVE // remains active until published
                } else item.lifecycle
                item.copy(stageStates = newStates, lifecycle = newLifecycle, updatedAt = Derive.today())
            } else item
        }
        _workspaceData.value = current.copy(content = updatedContent)
    }

    fun markContentPublished(contentId: String, actualDate: String, liveUrl: String) {
        val current = _workspaceData.value
        val updated = current.content.map { item ->
            if (item.id == contentId) {
                val allCompleteStates = current.workspace.stages.associate { it.id to StageState.COMPLETE }
                item.copy(
                    lifecycle = LifecycleOverride.PUBLISHED,
                    actualPublishDate = actualDate,
                    links = item.links.copy(publishedUrl = liveUrl),
                    stageStates = allCompleteStates,
                    updatedAt = Derive.today()
                )
            } else item
        }
        _workspaceData.value = current.copy(content = updated)
    }

    fun updatePerformanceMetrics(contentId: String, metrics: PerformanceMetrics) {
        val current = _workspaceData.value
        val updated = current.content.map { item ->
            if (item.id == contentId) {
                item.copy(performance = metrics, updatedAt = Derive.today())
            } else item
        }
        _workspaceData.value = current.copy(content = updated)
    }

    // ---------------- Task Operations ----------------

    fun addTask(
        title: String,
        memberId: String,
        priority: String,
        deadline: String,
        contentId: String = "",
        taskType: String = "Task"
    ): TaskItem {
        val current = _workspaceData.value
        val newCount = current.workspace.counters.task + 1
        val code = nextCode(current.workspace.taskPrefix, current.workspace.counters.task)

        val newTask = TaskItem(
            id = "t_${UUID.randomUUID()}",
            workspaceId = current.workspace.id,
            code = code,
            date = Derive.today(),
            memberId = memberId,
            title = title,
            contentId = contentId,
            taskType = taskType,
            priority = priority,
            deadline = deadline,
            status = TaskStatus.WORKING,
            createdAt = Derive.today(),
            updatedAt = Derive.today()
        )

        val updatedWorkspace = current.workspace.copy(
            counters = current.workspace.counters.copy(task = newCount)
        )
        _workspaceData.value = current.copy(
            workspace = updatedWorkspace,
            tasks = listOf(newTask) + current.tasks
        )
        return newTask
    }

    fun updateTask(task: TaskItem) {
        val current = _workspaceData.value
        val updated = current.tasks.map { if (it.id == task.id) task.copy(updatedAt = Derive.today()) else it }
        _workspaceData.value = current.copy(tasks = updated)
    }

    fun toggleTaskStatus(taskId: String) {
        val current = _workspaceData.value
        val updated = current.tasks.map { task ->
            if (task.id == taskId) {
                val nextStatus = if (task.status == TaskStatus.COMPLETED) TaskStatus.WORKING else TaskStatus.COMPLETED
                task.copy(
                    status = nextStatus,
                    completionDate = if (nextStatus == TaskStatus.COMPLETED) Derive.today() else "",
                    updatedAt = Derive.today()
                )
            } else task
        }
        _workspaceData.value = current.copy(tasks = updated)
    }

    fun deleteTask(taskId: String) {
        val current = _workspaceData.value
        _workspaceData.value = current.copy(tasks = current.tasks.filter { it.id != taskId })
    }

    // ---------------- Idea Operations ----------------

    fun addIdea(
        topic: String,
        description: String,
        contentType: String,
        platform: String,
        priority: String,
        potential: String,
        submittedBy: String
    ): Idea {
        val current = _workspaceData.value
        val newCount = current.workspace.counters.idea + 1
        val code = nextCode(current.workspace.ideaPrefix, current.workspace.counters.idea)

        val newIdea = Idea(
            id = "id_${UUID.randomUUID()}",
            workspaceId = current.workspace.id,
            code = code,
            dateAdded = Derive.today(),
            topic = topic,
            description = description,
            contentType = contentType,
            platform = platform,
            priority = priority,
            potential = potential,
            status = IdeaStatus.NEW,
            submittedBy = submittedBy,
            createdAt = Derive.today()
        )

        val updatedWorkspace = current.workspace.copy(
            counters = current.workspace.counters.copy(idea = newCount)
        )
        _workspaceData.value = current.copy(
            workspace = updatedWorkspace,
            ideas = listOf(newIdea) + current.ideas
        )
        return newIdea
    }

    fun updateIdea(idea: Idea) {
        val current = _workspaceData.value
        val updated = current.ideas.map { if (it.id == idea.id) idea else it }
        _workspaceData.value = current.copy(ideas = updated)
    }

    fun deleteIdea(ideaId: String) {
        val current = _workspaceData.value
        _workspaceData.value = current.copy(ideas = current.ideas.filter { it.id != ideaId })
    }

    fun convertIdeaToContent(ideaId: String, format: ProductionFormat): ContentItem? {
        val current = _workspaceData.value
        val idea = current.ideas.find { it.id == ideaId } ?: return null

        val owner = current.members.firstOrNull()?.id ?: ""
        val contentItem = addContent(
            title = idea.topic,
            topic = idea.description,
            contentType = idea.contentType,
            category = idea.category,
            productionFormat = format,
            platform = idea.platform,
            plannedPublishDate = LocalDate.now().plusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE),
            priority = idea.priority,
            ownerId = owner
        )

        val updatedIdeas = _workspaceData.value.ideas.map {
            if (it.id == ideaId) it.copy(status = IdeaStatus.CONVERTED, contentId = contentItem.id) else it
        }
        _workspaceData.value = _workspaceData.value.copy(ideas = updatedIdeas)
        return contentItem
    }

    // ---------------- Member & Workspace ----------------

    fun addMember(name: String, email: String, role: String, responsibility: String) {
        val current = _workspaceData.value
        val newMember = Member(
            id = "m_${UUID.randomUUID()}",
            workspaceId = current.workspace.id,
            name = name,
            email = email,
            role = role,
            responsibility = responsibility,
            createdAt = Derive.today()
        )
        _workspaceData.value = current.copy(members = current.members + newMember)
    }

    fun updateWorkspace(name: String, contentPrefix: String, taskPrefix: String, ideaPrefix: String) {
        val current = _workspaceData.value
        val updated = current.workspace.copy(
            name = name,
            contentPrefix = contentPrefix,
            taskPrefix = taskPrefix,
            ideaPrefix = ideaPrefix
        )
        _workspaceData.value = current.copy(workspace = updated)
    }

    fun resetToSampleData() {
        _workspaceData.value = SampleData.createInitialData()
    }
}
