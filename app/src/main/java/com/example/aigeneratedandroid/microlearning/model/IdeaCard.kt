package com.example.aigeneratedandroid.microlearning.model

/** Where an idea card came from, mirrors the onboarding "content format" answer. */
enum class SourceFormat { BOOK, PODCAST, ARTICLE, PAPER }

/** How the idea is delivered, mirrors the onboarding "learning style" answer. */
enum class LearningStyle { PRACTICAL, BIG_PICTURE, STORY, DATA_DRIVEN, COUNTERINTUITIVE }

/**
 * One bite-sized insight. [insight] is written to be read aloud (short sentences,
 * no parentheticals) so it can go straight into [NarrationFormatter] unchanged.
 */
data class IdeaCard(
    val id: String,
    val title: String,
    val insight: String,
    val sourceName: String,
    val author: String,
    val format: SourceFormat,
    val topic: String,
    val style: LearningStyle,
    val readTimeSeconds: Int,
    val asciiArt: String,
    val challenge: String
)
