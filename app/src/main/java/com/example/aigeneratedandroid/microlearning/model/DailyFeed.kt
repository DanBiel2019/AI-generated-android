package com.example.aigeneratedandroid.microlearning.model

/** The result of one curation run: today's cards plus the connecting thread across them. */
data class DailyFeed(
    val dateIso: String,
    val cards: List<IdeaCard>,
    val theme: String,
    val challenge: String
)
