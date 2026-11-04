package com.example.flux.data.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object SampleData {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    private fun offsetDate(days: Long): String {
        return LocalDate.now().plusDays(days).format(formatter)
    }

    private fun currentMonth(): String {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
    }

    fun createInitialData(): WorkspaceData {
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
            PlatformDef(id = "x", label = "X", color = "#E7E9EA"),
            PlatformDef(id = "threads", label = "Threads", color = "#A78BFA")
        )

        val roles = listOf(
            JobRole("lead", "Team Lead"),
            JobRole("writer", "Content Writer"),
            JobRole("designer", "Designer"),
            JobRole("videographer", "Videographer"),
            JobRole("editor", "Video Editor"),
            JobRole("social", "Social Manager")
        )

        val taxonomies = Taxonomies(
            contentTypes = listOf("Reels & Shorts (9:16)", "Carousel", "Long-Form Video (16:9)", "Graphic Post", "Social Thread"),
            categories = listOf("Product", "Brand", "Educational", "Promotional", "Customer Story", "Behind the Scenes"),
            platforms = platforms,
            roles = roles
        )

        val workspace = Workspace(
            id = "ws_default",
            name = "Flux Social Studio",
            initials = "FS",
            contentPrefix = "CN",
            taskPrefix = "T",
            ideaPrefix = "ID",
            counters = Counters(content = 10, task = 8, idea = 6),
            joinCode = "FLUX77",
            stages = stages,
            taxonomies = taxonomies,
            createdAt = offsetDate(-30),
            createdBy = "Alex Miller"
        )

        val members = listOf(
            Member("m_alex", workspace.id, "Alex Miller", "alex@fluxstudio.co", "Team Lead", "OWNER", responsibility = "Strategy & Editorial direction"),
            Member("m_sarah", workspace.id, "Sarah Chen", "sarah@fluxstudio.co", "Content Writer", "MEMBER", responsibility = "Copywriting & Storyboards"),
            Member("m_david", workspace.id, "David Kim", "david@fluxstudio.co", "Videographer", "MEMBER", responsibility = "Shooting & Camera equipment"),
            Member("m_elena", workspace.id, "Elena Rostova", "elena@fluxstudio.co", "Video Editor", "MEMBER", responsibility = "Motion Graphics & Sound design"),
            Member("m_marcus", workspace.id, "Marcus Jones", "marcus@fluxstudio.co", "Social Manager", "MEMBER", responsibility = "Publishing, Community & Analytics")
        )

        val month = currentMonth()

        val content = listOf(
            ContentItem(
                id = "c_1",
                workspaceId = workspace.id,
                code = "CN-0001",
                month = month,
                title = "Product Launch Teaser Reel",
                topic = "Spring release reveal",
                contentType = "Reels & Shorts (9:16)",
                category = "Product",
                productionFormat = ProductionFormat.VIDEO,
                platform = "Instagram",
                crossPost = listOf("TikTok"),
                ownerId = "m_alex",
                stageStates = mapOf(
                    "st_script" to StageState.COMPLETE,
                    "st_shoot" to StageState.COMPLETE,
                    "st_edit" to StageState.COMPLETE,
                    "st_review" to StageState.COMPLETE,
                    "st_publish" to StageState.COMPLETE
                ),
                plannedPublishDate = offsetDate(-10),
                actualPublishDate = offsetDate(-10),
                priority = "High",
                lifecycle = LifecycleOverride.PUBLISHED,
                performance = PerformanceMetrics(
                    views = 28400,
                    reach = 34500,
                    likes = 2150,
                    comments = 138,
                    shares = 420,
                    saves = 610,
                    completionRate = 0.68,
                    rating = "Excellent",
                    keyLearning = "Countdown hook generated 3x average saves."
                ),
                caption = "The future of creator workflow is here. Swipe to explore the new features. #creators #filmmaking #flux",
                createdAt = offsetDate(-15),
                updatedAt = offsetDate(-10)
            ),
            ContentItem(
                id = "c_2",
                workspaceId = workspace.id,
                code = "CN-0002",
                month = month,
                title = "Customer Story: 50% Time Saved",
                topic = "Case study testimonial",
                contentType = "Long-Form Video (16:9)",
                category = "Customer Story",
                productionFormat = ProductionFormat.VIDEO,
                platform = "YouTube",
                ownerId = "m_sarah",
                stageStates = mapOf(
                    "st_script" to StageState.COMPLETE,
                    "st_shoot" to StageState.COMPLETE,
                    "st_edit" to StageState.COMPLETE,
                    "st_review" to StageState.COMPLETE,
                    "st_publish" to StageState.COMPLETE
                ),
                plannedPublishDate = offsetDate(-5),
                actualPublishDate = offsetDate(-5),
                priority = "High",
                lifecycle = LifecycleOverride.PUBLISHED,
                performance = PerformanceMetrics(
                    views = 12400,
                    reach = 16800,
                    likes = 940,
                    comments = 74,
                    shares = 180,
                    saves = 210,
                    completionRate = 0.54,
                    rating = "Good",
                    keyLearning = "Named testimonials build concrete trust."
                ),
                createdAt = offsetDate(-12),
                updatedAt = offsetDate(-5)
            ),
            ContentItem(
                id = "c_3",
                workspaceId = workspace.id,
                code = "CN-0003",
                month = month,
                title = "10 Pro Lighting Tips for Vertical Video",
                topic = "Production education",
                contentType = "Carousel",
                category = "Educational",
                productionFormat = ProductionFormat.STATIC,
                platform = "LinkedIn",
                crossPost = listOf("Instagram"),
                ownerId = "m_david",
                stageStates = mapOf(
                    "st_script" to StageState.COMPLETE,
                    "st_shoot" to StageState.COMPLETE,
                    "st_edit" to StageState.IN_PROGRESS,
                    "st_review" to StageState.PENDING,
                    "st_publish" to StageState.PENDING
                ),
                stageAssignees = mapOf("st_edit" to "m_elena"),
                stageDeadlines = mapOf("st_edit" to offsetDate(1)),
                plannedPublishDate = offsetDate(3),
                priority = "Medium",
                lifecycle = LifecycleOverride.ACTIVE,
                nextAction = "Elena polishing slide 4 color grading and typography.",
                createdAt = offsetDate(-6),
                updatedAt = offsetDate(-1)
            ),
            ContentItem(
                id = "c_4",
                workspaceId = workspace.id,
                code = "CN-0004",
                month = month,
                title = "Behind the Scenes: Studio Redesign Tour",
                topic = "Culture & Workspace",
                contentType = "Reels & Shorts (9:16)",
                category = "Behind the Scenes",
                productionFormat = ProductionFormat.VIDEO,
                platform = "TikTok",
                ownerId = "m_david",
                stageStates = mapOf(
                    "st_script" to StageState.COMPLETE,
                    "st_shoot" to StageState.BLOCKED,
                    "st_edit" to StageState.PENDING,
                    "st_review" to StageState.PENDING,
                    "st_publish" to StageState.PENDING
                ),
                stageAssignees = mapOf("st_shoot" to "m_david"),
                stageDeadlines = mapOf("st_shoot" to offsetDate(-1)),
                plannedPublishDate = offsetDate(4),
                priority = "High",
                lifecycle = LifecycleOverride.ACTIVE,
                blocker = "Awaiting delivery of sound acoustic panels for b-roll.",
                nextAction = "Follow up with studio supplier delivery tracking.",
                createdAt = offsetDate(-4),
                updatedAt = offsetDate(-1)
            ),
            ContentItem(
                id = "c_5",
                workspaceId = workspace.id,
                code = "CN-0005",
                month = month,
                title = "Weekly Industry Roundup: AI in Social",
                topic = "Trend analysis",
                contentType = "Social Thread",
                category = "Educational",
                productionFormat = ProductionFormat.WRITTEN,
                platform = "X",
                crossPost = listOf("Threads"),
                ownerId = "m_sarah",
                stageStates = mapOf(
                    "st_script" to StageState.COMPLETE,
                    "st_shoot" to StageState.COMPLETE,
                    "st_edit" to StageState.COMPLETE,
                    "st_review" to StageState.COMPLETE,
                    "st_publish" to StageState.PENDING
                ),
                stageAssignees = mapOf("st_publish" to "m_marcus"),
                stageDeadlines = mapOf("st_publish" to offsetDate(1)),
                plannedPublishDate = offsetDate(1),
                priority = "Medium",
                lifecycle = LifecycleOverride.ACTIVE,
                nextAction = "Scheduled in queue for tomorrow 9 AM EST.",
                createdAt = offsetDate(-3),
                updatedAt = offsetDate(0)
            )
        )

        val tasks = listOf(
            TaskItem(
                id = "t_1",
                workspaceId = workspace.id,
                code = "T-0001",
                date = offsetDate(0),
                memberId = "m_elena",
                title = "Color grade lighting tips carousel slides",
                contentId = "c_3",
                stageId = "st_edit",
                taskType = "Design",
                priority = "High",
                deadline = offsetDate(1),
                status = TaskStatus.WORKING
            ),
            TaskItem(
                id = "t_2",
                workspaceId = workspace.id,
                code = "T-0002",
                date = offsetDate(0),
                memberId = "m_david",
                title = "Verify tracking for studio acoustic panels",
                contentId = "c_4",
                stageId = "st_shoot",
                taskType = "Production",
                priority = "High",
                deadline = offsetDate(0),
                status = TaskStatus.BLOCKED
            ),
            TaskItem(
                id = "t_3",
                workspaceId = workspace.id,
                code = "T-0003",
                date = offsetDate(-1),
                memberId = "m_marcus",
                title = "Schedule social thread on Buffer",
                contentId = "c_5",
                stageId = "st_publish",
                taskType = "Publishing",
                priority = "Medium",
                deadline = offsetDate(1),
                status = TaskStatus.WORKING
            ),
            TaskItem(
                id = "t_4",
                workspaceId = workspace.id,
                code = "T-0004",
                date = offsetDate(-5),
                memberId = "m_alex",
                title = "Review product launch teaser metrics",
                contentId = "c_1",
                stageId = "st_publish",
                taskType = "Performance Update",
                priority = "Low",
                deadline = offsetDate(-2),
                status = TaskStatus.COMPLETED
            )
        )

        val ideas = listOf(
            Idea(
                id = "id_1",
                workspaceId = workspace.id,
                code = "ID-0001",
                dateAdded = offsetDate(-4),
                topic = "Day in the Life of a Remote Video Editor",
                description = "Split-screen timelapse comparing timeline organization and sound FX layering.",
                contentType = "Reels & Shorts (9:16)",
                category = "Behind the Scenes",
                platform = "TikTok",
                priority = "High",
                potential = "High",
                status = IdeaStatus.APPROVED,
                submittedBy = "Elena Rostova"
            ),
            Idea(
                id = "id_2",
                workspaceId = workspace.id,
                code = "ID-0002",
                dateAdded = offsetDate(-2),
                topic = "The 3 Camera Angles Every Creator Should Know",
                description = "Quick tutorial demonstrating low-angle power shots vs dutch tilt narrative tension.",
                contentType = "Carousel",
                category = "Educational",
                platform = "Instagram",
                priority = "Medium",
                potential = "High",
                status = IdeaStatus.NEW,
                submittedBy = "David Kim"
            ),
            Idea(
                id = "id_3",
                workspaceId = workspace.id,
                code = "ID-0003",
                dateAdded = offsetDate(-1),
                topic = "Q&A with Founder on Bootstrapping to 100K",
                description = "AMA format answers to top 5 questions from community comments.",
                contentType = "Long-Form Video (16:9)",
                category = "Brand",
                platform = "YouTube",
                priority = "High",
                potential = "Medium",
                status = IdeaStatus.REVIEWED,
                submittedBy = "Sarah Chen"
            )
        )

        val reviews = listOf(
            WeeklyReview(
                id = "rev_1",
                workspaceId = workspace.id,
                weekStart = offsetDate(-7),
                weekEnd = offsetDate(-1),
                label = "Week 43 Performance",
                planned = "3 videos, 1 carousel",
                completed = "2 videos published on schedule",
                delayed = "1 carousel waiting on sound FX review",
                performedWell = "Launch teaser reached 34.5K reach with 68% completion",
                needsAttention = "Shooting schedule backlog caused by external equipment delay",
                keyWins = "Exceeded save benchmark by 200%",
                keyProblems = "Acoustic panel delay blocked studio tour",
                nextPriorities = "Unblock studio tour; finalize lighting tips carousel",
                status = ReviewStatus.DONE
            )
        )

        return WorkspaceData(
            workspace = workspace,
            members = members,
            content = content,
            tasks = tasks,
            ideas = ideas,
            reviews = reviews
        )
    }
}
