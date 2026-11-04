package com.example.flux.data.repository

import com.example.flux.data.model.ContentItem
import com.example.flux.data.model.Counters
import com.example.flux.data.model.Idea
import com.example.flux.data.model.IdeaStatus
import com.example.flux.data.model.JobRole
import com.example.flux.data.model.LifecycleOverride
import com.example.flux.data.model.Member
import com.example.flux.data.model.PerformanceMetrics
import com.example.flux.data.model.PlatformDef
import com.example.flux.data.model.ProductionFormat
import com.example.flux.data.model.SampleData
import com.example.flux.data.model.Stage
import com.example.flux.data.model.StageState
import com.example.flux.data.model.TaskItem
import com.example.flux.data.model.TaskStatus
import com.example.flux.data.model.Taxonomies
import com.example.flux.data.model.Workspace
import com.example.flux.data.model.WorkspaceData
import com.example.flux.domain.Derive
import com.example.flux.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

class FluxRepository private constructor() {

    private val scope = CoroutineScope(Dispatchers.Default)

    // Workspaces List
    private val _workspaces = MutableStateFlow<List<WorkspaceData>>(
        listOf(SampleData.createInitialData())
    )
    val workspaces: StateFlow<List<WorkspaceData>> = _workspaces.asStateFlow()

    // Currently Active Workspace ID (null on fresh app open to prompt selection/creation)
    private val _activeWorkspaceId = MutableStateFlow<String?>(null)
    val activeWorkspaceId: StateFlow<String?> = _activeWorkspaceId.asStateFlow()

    // Theme Mode
    private val _themeMode = MutableStateFlow(AppThemeMode.DARK)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    // Current Active WorkspaceData (falls back to first workspace if selected, or null)
    val workspaceData: StateFlow<WorkspaceData> = combine(_workspaces, _activeWorkspaceId) { list, activeId ->
        list.find { it.workspace.id == activeId } ?: list.firstOrNull() ?: SampleData.createInitialData()
    }.stateIn(scope, SharingStarted.Eagerly, SampleData.createInitialData())

    companion object {
        val instance: FluxRepository by lazy { FluxRepository() }
    }

    private fun nextCode(prefix: String, currentCount: Int): String {
        return "$prefix-${(currentCount + 1).toString().padStart(4, '0')}"
    }

    // ---------------- Theme Operations ----------------

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
    }

    fun toggleTheme() {
        _themeMode.value = if (_themeMode.value == AppThemeMode.LIGHT) AppThemeMode.DARK else AppThemeMode.LIGHT
    }

    // ---------------- Workspace Operations ----------------

    fun selectWorkspace(workspaceId: String) {
        _activeWorkspaceId.value = workspaceId
    }

    fun clearActiveWorkspace() {
        _activeWorkspaceId.value = null
    }

    fun createWorkspace(
        name: String,
        contentPrefix: String = "CN",
        taskPrefix: String = "T",
        ideaPrefix: String = "ID",
        userName: String = "Workspace Owner"
    ): WorkspaceData {
        val wsId = "ws_${UUID.randomUUID().toString().take(8)}"
        val initials = name.trim().split("\\s+".toRegex())
            .mapNotNull { it.firstOrNull() }
            .take(2)
            .joinToString("")
            .uppercase()
            .ifBlank { "WS" }

        val stages = listOf(
            Stage(id = "st_script", name = "Script & Brief", ownerRole = "Content Writer", verb = "Write"),
            Stage(id = "st_shoot", name = "Production & Shoot", ownerRole = "Videographer", verb = "Shoot"),
            Stage(id = "st_edit", name = "Editing & Design", ownerRole = "Video Editor", verb = "Edit"),
            Stage(id = "st_review", name = "Review & Signoff", ownerRole = "Team Lead", verb = "Review"),
            Stage(id = "st_publish", name = "Publish & Promote", ownerRole = "Social Manager", verb = "Publish")
        )

        val platforms = listOf(
            PlatformDef(id = "instagram", label = "Instagram", color = "#E1306C"),
            PlatformDef(id = "youtube", label = "YouTube", color = "#FF3D3D"),
            PlatformDef(id = "tiktok", label = "TikTok", color = "#25F4EE"),
            PlatformDef(id = "linkedin", label = "LinkedIn", color = "#3B9AE1"),
            PlatformDef(id = "x", label = "X", color = "#E7E9EA")
        )

        val newWorkspace = Workspace(
            id = wsId,
            name = name.trim(),
            initials = initials,
            contentPrefix = contentPrefix.uppercase().trim().ifBlank { "CN" },
            taskPrefix = taskPrefix.uppercase().trim().ifBlank { "T" },
            ideaPrefix = ideaPrefix.uppercase().trim().ifBlank { "ID" },
            counters = Counters(0, 0, 0),
            stages = stages,
            taxonomies = Taxonomies(
                contentTypes = listOf("Reels & Shorts", "Carousel", "Video (16:9)", "Graphic Post", "Thread"),
                categories = listOf("Product", "Brand", "Educational", "Announcement"),
                platforms = platforms
            ),
            createdAt = Derive.today(),
            createdBy = userName
        )

        val defaultMember = Member(
            id = "m_${UUID.randomUUID().toString().take(6)}",
            workspaceId = wsId,
            name = userName,
            email = "creator@flux.app",
            role = "Team Lead",
            access = "OWNER",
            responsibility = "Content Strategy & Approvals",
            createdAt = Derive.today()
        )

        val newWorkspaceData = WorkspaceData(
            workspace = newWorkspace,
            members = listOf(defaultMember),
            content = emptyList(),
            tasks = emptyList(),
            ideas = emptyList(),
            reviews = emptyList()
        )

        _workspaces.value = _workspaces.value + newWorkspaceData
        _activeWorkspaceId.value = wsId
        return newWorkspaceData
    }

    fun updateWorkspace(name: String, contentPrefix: String, taskPrefix: String, ideaPrefix: String) {
        val currentId = _activeWorkspaceId.value ?: _workspaces.value.firstOrNull()?.workspace?.id ?: return
        _workspaces.value = _workspaces.value.map { wsData ->
            if (wsData.workspace.id == currentId) {
                val updatedWs = wsData.workspace.copy(
                    name = name,
                    contentPrefix = contentPrefix,
                    taskPrefix = taskPrefix,
                    ideaPrefix = ideaPrefix
                )
                wsData.copy(workspace = updatedWs)
            } else wsData
        }
    }

    private fun updateCurrentWorkspaceData(transform: (WorkspaceData) -> WorkspaceData) {
        val currentId = _activeWorkspaceId.value ?: _workspaces.value.firstOrNull()?.workspace?.id ?: return
        _workspaces.value = _workspaces.value.map { wsData ->
            if (wsData.workspace.id == currentId) {
                transform(wsData)
            } else wsData
        }
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
        val current = workspaceData.value
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

        updateCurrentWorkspaceData { wsData ->
            val updatedWs = wsData.workspace.copy(
                counters = wsData.workspace.counters.copy(content = newCount)
            )
            wsData.copy(
                workspace = updatedWs,
                content = listOf(newItem) + wsData.content
            )
        }
        return newItem
    }

    fun updateContent(item: ContentItem) {
        updateCurrentWorkspaceData { wsData ->
            val updated = wsData.content.map { if (it.id == item.id) item.copy(updatedAt = Derive.today()) else it }
            wsData.copy(content = updated)
        }
    }

    fun deleteContent(id: String) {
        updateCurrentWorkspaceData { wsData ->
            wsData.copy(content = wsData.content.filter { it.id != id })
        }
    }

    fun updateStageState(contentId: String, stageId: String, newState: StageState) {
        updateCurrentWorkspaceData { wsData ->
            val updatedContent = wsData.content.map { item ->
                if (item.id == contentId) {
                    val newStates = item.stageStates.toMutableMap().apply { put(stageId, newState) }
                    val allDone = wsData.workspace.stages.isNotEmpty() &&
                            wsData.workspace.stages.all { (newStates[it.id] ?: StageState.PENDING) == StageState.COMPLETE }
                    val newLifecycle = if (allDone && item.lifecycle == LifecycleOverride.ACTIVE) {
                        LifecycleOverride.ACTIVE
                    } else item.lifecycle
                    item.copy(stageStates = newStates, lifecycle = newLifecycle, updatedAt = Derive.today())
                } else item
            }
            wsData.copy(content = updatedContent)
        }
    }

    fun markContentPublished(contentId: String, actualDate: String, liveUrl: String) {
        updateCurrentWorkspaceData { wsData ->
            val allCompleteStates = wsData.workspace.stages.associate { it.id to StageState.COMPLETE }
            val updated = wsData.content.map { item ->
                if (item.id == contentId) {
                    item.copy(
                        lifecycle = LifecycleOverride.PUBLISHED,
                        actualPublishDate = actualDate,
                        links = item.links.copy(publishedUrl = liveUrl),
                        stageStates = allCompleteStates,
                        updatedAt = Derive.today()
                    )
                } else item
            }
            wsData.copy(content = updated)
        }
    }

    fun updatePerformanceMetrics(contentId: String, metrics: PerformanceMetrics) {
        updateCurrentWorkspaceData { wsData ->
            val updated = wsData.content.map { item ->
                if (item.id == contentId) {
                    item.copy(performance = metrics, updatedAt = Derive.today())
                } else item
            }
            wsData.copy(content = updated)
        }
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
        val current = workspaceData.value
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

        updateCurrentWorkspaceData { wsData ->
            val updatedWs = wsData.workspace.copy(
                counters = wsData.workspace.counters.copy(task = newCount)
            )
            wsData.copy(
                workspace = updatedWs,
                tasks = listOf(newTask) + wsData.tasks
            )
        }
        return newTask
    }

    fun updateTask(task: TaskItem) {
        updateCurrentWorkspaceData { wsData ->
            val updated = wsData.tasks.map { if (it.id == task.id) task.copy(updatedAt = Derive.today()) else it }
            wsData.copy(tasks = updated)
        }
    }

    fun toggleTaskStatus(taskId: String) {
        updateCurrentWorkspaceData { wsData ->
            val updated = wsData.tasks.map { task ->
                if (task.id == taskId) {
                    val nextStatus = if (task.status == TaskStatus.COMPLETED) TaskStatus.WORKING else TaskStatus.COMPLETED
                    task.copy(
                        status = nextStatus,
                        completionDate = if (nextStatus == TaskStatus.COMPLETED) Derive.today() else "",
                        updatedAt = Derive.today()
                    )
                } else task
            }
            wsData.copy(tasks = updated)
        }
    }

    fun deleteTask(taskId: String) {
        updateCurrentWorkspaceData { wsData ->
            wsData.copy(tasks = wsData.tasks.filter { it.id != taskId })
        }
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
        val current = workspaceData.value
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

        updateCurrentWorkspaceData { wsData ->
            val updatedWs = wsData.workspace.copy(
                counters = wsData.workspace.counters.copy(idea = newCount)
            )
            wsData.copy(
                workspace = updatedWs,
                ideas = listOf(newIdea) + wsData.ideas
            )
        }
        return newIdea
    }

    fun updateIdea(idea: Idea) {
        updateCurrentWorkspaceData { wsData ->
            val updated = wsData.ideas.map { if (it.id == idea.id) idea else it }
            wsData.copy(ideas = updated)
        }
    }

    fun deleteIdea(ideaId: String) {
        updateCurrentWorkspaceData { wsData ->
            wsData.copy(ideas = wsData.ideas.filter { it.id != ideaId })
        }
    }

    fun convertIdeaToContent(ideaId: String, format: ProductionFormat): ContentItem? {
        val current = workspaceData.value
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

        updateCurrentWorkspaceData { wsData ->
            val updatedIdeas = wsData.ideas.map {
                if (it.id == ideaId) it.copy(status = IdeaStatus.CONVERTED, contentId = contentItem.id) else it
            }
            wsData.copy(ideas = updatedIdeas)
        }
        return contentItem
    }

    // ---------------- Member Operations ----------------

    fun addMember(name: String, email: String, role: String, responsibility: String) {
        val current = workspaceData.value
        val newMember = Member(
            id = "m_${UUID.randomUUID()}",
            workspaceId = current.workspace.id,
            name = name,
            email = email,
            role = role,
            responsibility = responsibility,
            createdAt = Derive.today()
        )
        updateCurrentWorkspaceData { wsData ->
            wsData.copy(members = wsData.members + newMember)
        }
    }

    fun resetToSampleData() {
        _workspaces.value = listOf(SampleData.createInitialData())
        _activeWorkspaceId.value = "ws_default"
    }
}
