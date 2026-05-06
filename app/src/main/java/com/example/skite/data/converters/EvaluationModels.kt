package com.example.skite.data.converters

import kotlinx.serialization.Serializable

//TODO Refactor how result are stored.
/**
 * Mapped to the [ResultType.data] JSON field to determine the tools and metrics available.
 */
@Serializable
data class SkillConfiguration(
    val name: String,
    val ratio: Float
)

@Serializable
data class ResultTypeData(
    val skills: List<SkillConfiguration> = emptyList()
)

/**
 * Mapped to the [StudentSessionResult.jsonData] field reflecting individual metric scores.
 */
@Serializable
data class StudentSkillResult(
    val skillName: String,
    val score: Float,
    val ratio: Float
)

@Serializable
data class StudentResultData(
    val skills: List<StudentSkillResult> = emptyList(),
    val manualOverride: Boolean = false
)
