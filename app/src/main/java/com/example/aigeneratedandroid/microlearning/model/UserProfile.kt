package com.example.aigeneratedandroid.microlearning.model

enum class DepthLevel { BEGINNER, INTERMEDIATE, ADVANCED }

/**
 * Answers from the onboarding quiz. [topics] and [preferredStyles] drive selection weight
 * in ContentCurationEngine; [chunkMinutes]/[sessionMinutes] decide how many cards ship per day.
 */
data class UserProfile(
    val lovedBooks: List<String>,
    val followedAuthors: List<String>,
    val topics: List<String>,
    val preferredStyles: List<LearningStyle>,
    val preferredFormats: List<SourceFormat>,
    val sessionMinutes: Int,
    val chunkMinutes: Int,
    val depthLevel: DepthLevel,
    val consumptionMoment: String
) {
    val cardsPerSession: Int
        get() = (sessionMinutes / chunkMinutes).coerceIn(5, 10)

    companion object {
        /** Seeded from the user's own onboarding answers, editable later from the app. */
        fun default(): UserProfile = UserProfile(
            lovedBooks = listOf(
                "Crucial Conversations",
                "Crucial Accountability",
                "The Phoenix Project",
                "The 7 Habits of Highly Effective People",
                "A More Beautiful Question"
            ),
            followedAuthors = listOf(
                "Kerry Patterson",
                "Gene Kim",
                "Stephen R. Covey",
                "Warren Berger",
                "John C. Maxwell"
            ),
            topics = listOf(
                "Business & Entrepreneurship",
                "Technology & AI",
                "Leadership",
                "Creativity",
                "Systems & Measurement"
            ),
            preferredStyles = listOf(LearningStyle.STORY, LearningStyle.COUNTERINTUITIVE),
            preferredFormats = listOf(SourceFormat.PODCAST),
            sessionMinutes = 10,
            chunkMinutes = 2,
            depthLevel = DepthLevel.ADVANCED,
            consumptionMoment = "Morning routine (coffee / breakfast)"
        )
    }
}
