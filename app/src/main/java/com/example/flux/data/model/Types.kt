package com.example.flux.data.model

import kotlinx.serialization.Serializable

enum class AccessLevel(val label: String, val blurb: String) {
    OWNER("Owner", "Full control, including deleting the workspace"),
    ADMIN("Admin", "Manage members, pipeline, taxonomies and all content"),
    MEMBER("Member", "Create and edit content, tasks and ideas"),
    VIEWER("Viewer", "Read-only access to everything")
}

@Serializable
data class AuthUser(
    val uid: String,
    val name: String,
    val email: String,
    val photoUrl: String? = null
)

@Serializable
data class Stage(
    val id: String,
    val name: String,
    val ownerRole: String,
    val verb: String = "Proceed",
    val pipelineId: String? = null
)

@Serializable
data class JobRole(
    val id: String,
    val label: String
)

@Serializable
data class PlatformDef(
    val id: String,
    val label: String,
    val color: String
)

@Serializable
data class Taxonomies(
    val contentTypes: List<String> = emptyList(),
    val categories: List<String> = emptyList(),
    val platforms: List<PlatformDef> = emptyList(),
    val priorities: List<String> = listOf("High", "Medium", "Low"),
    val taskTypes: List<String> = emptyList(),
    val objectives: List<String> = emptyList(),
    val audiences: List<String> = emptyList(),
    val roles: List<JobRole> = emptyList(),
    val performanceRatings: List<String> = listOf("Excellent", "Good", "Average", "Poor"),
    val repurposeLevels: List<String> = listOf("High", "Medium", "Low"),
    val ideaPotentials: List<String> = listOf("High", "Medium", "Low")
)

@Serializable
data class Counters(
    val content: Int = 0,
    val task: Int = 0,
    val idea: Int = 0
)

@Serializable
data class Workspace(
    val id: String,
    val name: String,
    val initials: String,
    val contentPrefix: String = "CN",
    val taskPrefix: String = "T",
    val ideaPrefix: String = "ID",
    val counters: Counters = Counters(),
    val joinCode: String = "FLUX01",
    val joinEnabled: Boolean = true,
    val joinAccess: String = "MEMBER",
    val weekStartsOn: Int = 1,
    val stages: List<Stage> = emptyList(),
    val taxonomies: Taxonomies = Taxonomies(),
    val createdAt: String = "",
    val createdBy: String = ""
)

@Serializable
data class Member(
    val id: String,
    val workspaceId: String,
    val name: String,
    val email: String,
    val role: String,
    val access: String = "MEMBER",
    val active: Boolean = true,
    val responsibility: String = "",
    val contact: String = "",
    val notes: String = "",
    val userId: String? = null,
    val createdAt: String = ""
)

enum class StageState(val label: String) {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    COMPLETE("Complete"),
    BLOCKED("Blocked")
}

enum class LifecycleOverride(val label: String) {
    IDEA("Idea"),
    ACTIVE("Active"),
    REVISION("Revision"),
    PUBLISHED("Published"),
    CANCELLED("Cancelled")
}

@Serializable
data class PerformanceMetrics(
    val views: Long? = null,
    val reach: Long? = null,
    val likes: Long? = null,
    val comments: Long? = null,
    val shares: Long? = null,
    val saves: Long? = null,
    val watchTimeMin: Long? = null,
    val avgWatchTimeSec: Long? = null,
    val completionRate: Double? = null,
    val leads: Long? = null,
    val conversions: Long? = null,
    val response: String = "",
    val rating: String = "",
    val keyLearning: String = "",
    val repurposePotential: String = "",
    val remarks: String = ""
)

@Serializable
data class ContentLinks(
    val brief: String = "",
    val raw: String = "",
    val finalUrl: String = "",
    val publishedUrl: String = ""
)

enum class ProductionFormat(val label: String) {
    VIDEO("Video"),
    STATIC("Static Design"),
    WRITTEN("Written Content"),
    BRIEF("Strategic Brief")
}

@Serializable
data class ProductionMetadata(
    val aspectRatio: String = "9:16",
    val duration: String = "",
    val editingNotes: String = "",
    val videoHook: String = "",
    val soundNotes: String = "",
    val draftUrl: String = "",
    val figmaUrl: String = "",
    val clientName: String = "",
    val deliverables: String = ""
)

@Serializable
data class ContentItem(
    val id: String,
    val workspaceId: String,
    val code: String,
    val month: String,
    val title: String,
    val topic: String = "",
    val contentType: String = "Reel",
    val category: String = "Product",
    val productionFormat: ProductionFormat = ProductionFormat.VIDEO,
    val productionMeta: ProductionMetadata = ProductionMetadata(),
    val platform: String = "Instagram",
    val crossPost: List<String> = emptyList(),
    val objective: String = "Engagement",
    val audience: String = "Followers",
    val ownerId: String = "",
    val pipelineId: String? = null,
    val stageAssignees: Map<String, String> = emptyMap(),
    val stageDeadlines: Map<String, String> = emptyMap(),
    val stageStates: Map<String, StageState> = emptyMap(),
    val stageEnteredAt: Map<String, String> = emptyMap(),
    val plannedPublishDate: String = "",
    val actualPublishDate: String = "",
    val priority: String = "Medium",
    val lifecycle: LifecycleOverride = LifecycleOverride.ACTIVE,
    val blocker: String = "",
    val nextAction: String = "",
    val caption: String = "",
    val hashtags: String = "",
    val links: ContentLinks = ContentLinks(),
    val remarks: String = "",
    val performance: PerformanceMetrics? = null,
    val createdAt: String = "",
    val updatedAt: String = "",
    val updatedBy: String = "",
    val fromIdeaId: String? = null
)

enum class TaskStatus(val label: String) {
    NOT_STARTED("Not started"),
    WORKING("Working"),
    COMPLETED("Completed"),
    BLOCKED("Blocked"),
    CANCELLED("Cancelled")
}

@Serializable
data class TaskItem(
    val id: String,
    val workspaceId: String,
    val code: String,
    val date: String,
    val memberId: String,
    val title: String,
    val contentId: String = "",
    val stageId: String = "",
    val taskType: String = "Task",
    val priority: String = "Medium",
    val deadline: String = "",
    val status: TaskStatus = TaskStatus.NOT_STARTED,
    val completionDate: String = "",
    val outputLink: String = "",
    val remarks: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
)

enum class IdeaStatus(val label: String) {
    NEW("New"),
    REVIEWED("Reviewed"),
    APPROVED("Approved"),
    CONVERTED("Converted"),
    REJECTED("Rejected")
}

@Serializable
data class Idea(
    val id: String,
    val workspaceId: String,
    val code: String,
    val dateAdded: String,
    val topic: String,
    val description: String = "",
    val contentType: String = "Video",
    val category: String = "General",
    val platform: String = "Instagram",
    val audience: String = "",
    val objective: String = "",
    val reference: String = "",
    val priority: String = "Medium",
    val potential: String = "High",
    val status: IdeaStatus = IdeaStatus.NEW,
    val contentId: String = "",
    val submittedBy: String = "",
    val remarks: String = "",
    val createdAt: String = ""
)

enum class ReviewStatus(val label: String) {
    NOT_STARTED("Not started"),
    OPEN("Open"),
    DONE("Done")
}

@Serializable
data class WeeklyReview(
    val id: String,
    val workspaceId: String,
    val weekStart: String,
    val weekEnd: String,
    val label: String,
    val planned: String = "",
    val completed: String = "",
    val delayed: String = "",
    val performedWell: String = "",
    val needsAttention: String = "",
    val keyWins: String = "",
    val keyProblems: String = "",
    val nextPriorities: String = "",
    val actionOwnerId: String = "",
    val actionDeadline: String = "",
    val status: ReviewStatus = ReviewStatus.NOT_STARTED,
    val updatedAt: String = ""
)

@Serializable
data class WorkspaceData(
    val workspace: Workspace,
    val members: List<Member> = emptyList(),
    val content: List<ContentItem> = emptyList(),
    val tasks: List<TaskItem> = emptyList(),
    val ideas: List<Idea> = emptyList(),
    val reviews: List<WeeklyReview> = emptyList()
)
