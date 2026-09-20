package com.example.aigeneratedandroid.microlearning.curation

import com.example.aigeneratedandroid.microlearning.data.ContentBank
import com.example.aigeneratedandroid.microlearning.data.FeedbackStore
import com.example.aigeneratedandroid.microlearning.model.DailyFeed
import com.example.aigeneratedandroid.microlearning.model.IdeaCard
import com.example.aigeneratedandroid.microlearning.model.LearningStyle
import com.example.aigeneratedandroid.microlearning.model.UserProfile
import java.time.LocalDate
import kotlin.random.Random

/**
 * Selects the day's cards by weighting ContentBank against the user's profile and the
 * feedback-learned weights in FeedbackStore, then samples without replacement using a
 * date-seeded RNG so the feed is stable within a day but changes the next.
 */
class ContentCurationEngine(
    private val feedbackStore: FeedbackStore,
    private val bank: List<IdeaCard> = ContentBank.all
) {

    fun generateDailyFeed(profile: UserProfile, date: LocalDate = LocalDate.now()): DailyFeed {
        val dateIso = date.toString()
        val random = Random(date.toEpochDay())

        val scored = bank.map { it to score(it, profile, dateIso) }
        val selected = weightedSampleWithoutReplacement(scored, profile.cardsPerSession, random)

        feedbackStore.markShown(selected.map { it.id }, dateIso)

        return DailyFeed(
            dateIso = dateIso,
            cards = selected,
            theme = deriveTheme(selected),
            challenge = deriveChallenge(selected)
        )
    }

    private fun score(card: IdeaCard, profile: UserProfile, dateIso: String): Double {
        val topicMatch = if (card.topic in profile.topics) 1.0 else 0.25
        val topicWeight = feedbackStore.topicWeight(card.topic)
        val styleWeight = feedbackStore.styleWeight(card.style.name)
        val styleMatch = if (card.style in profile.preferredStyles) 1.3 else 1.0
        val formatMatch = if (card.format in profile.preferredFormats) 1.2 else 1.0

        val daysSince = feedbackStore.daysSinceShown(card.id, dateIso)
        val recency = when {
            daysSince < RECENCY_COOLDOWN_DAYS -> 0.05
            else -> 1.0
        }

        return topicMatch * topicWeight * styleWeight * styleMatch * formatMatch * recency
    }

    private fun weightedSampleWithoutReplacement(
        scored: List<Pair<IdeaCard, Double>>,
        count: Int,
        random: Random
    ): List<IdeaCard> {
        val pool = scored.toMutableList()
        val result = mutableListOf<IdeaCard>()
        repeat(count.coerceAtMost(pool.size)) {
            val totalWeight = pool.sumOf { it.second }
            if (totalWeight <= 0.0) return@repeat
            var pick = random.nextDouble() * totalWeight
            val index = pool.indexOfFirst { (_, weight) ->
                pick -= weight
                pick <= 0.0
            }.let { if (it == -1) pool.lastIndex else it }
            result += pool.removeAt(index).first
        }
        return result
    }

    private fun deriveTheme(cards: List<IdeaCard>): String {
        val topicCounts = cards.groupingBy { it.topic }.eachCount()
        val (topTopic, topCount) = topicCounts.maxByOrNull { it.value } ?: return "A mix of perspectives"
        return if (topCount >= (cards.size / 2.0)) topTopic else "A mix of perspectives across your interests"
    }

    private fun deriveChallenge(cards: List<IdeaCard>): String {
        val practical = cards.firstOrNull { it.style == LearningStyle.PRACTICAL }
        return (practical ?: cards.firstOrNull())?.challenge ?: "Revisit one idea from today and tell someone about it."
    }

    companion object {
        private const val RECENCY_COOLDOWN_DAYS = 5
    }
}
