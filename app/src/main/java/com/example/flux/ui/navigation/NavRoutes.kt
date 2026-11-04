package com.example.flux.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Dashboard : Screen

    @Serializable
    data object Pipeline : Screen

    @Serializable
    data object ContentPlan : Screen

    @Serializable
    data object Calendar : Screen

    @Serializable
    data class ContentDetail(val id: String) : Screen

    @Serializable
    data object Tasks : Screen

    @Serializable
    data object Ideas : Screen

    @Serializable
    data object Publishing : Screen

    @Serializable
    data object Reports : Screen

    @Serializable
    data object Team : Screen

    @Serializable
    data object Settings : Screen
}
