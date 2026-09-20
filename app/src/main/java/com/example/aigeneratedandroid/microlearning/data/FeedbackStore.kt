package com.example.aigeneratedandroid.microlearning.data

import android.content.Context
import com.example.aigeneratedandroid.microlearning.model.IdeaCard
import org.json.JSONObject

enum class Reaction { UP, DOWN, SAVE }

/**
 * The personalization engine's memory: per-topic and per-style weights that nudge which
 * cards ContentCurationEngine favors, plus a shown-history so cards don't repeat too soon.
 */
class FeedbackStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun topicWeight(topic: String): Double = weights(KEY_TOPIC_WEIGHTS).optDouble(topic, 1.0)

    fun styleWeight(style: String): Double = weights(KEY_STYLE_WEIGHTS).optDouble(style, 1.0)

    fun isSaved(cardId: String): Boolean = savedIds().contains(cardId)

    /** Records a reaction and nudges the relevant topic/style weight. Thumbs down never
     *  drives a weight below a small floor, so a disliked topic still occasionally reappears
     *  rather than vanishing outright. */
    fun record(card: IdeaCard, reaction: Reaction) {
        val delta = when (reaction) {
            Reaction.UP -> WEIGHT_STEP
            Reaction.DOWN -> -WEIGHT_STEP
            Reaction.SAVE -> WEIGHT_STEP / 2
        }
        adjustWeight(KEY_TOPIC_WEIGHTS, card.topic, delta)
        adjustWeight(KEY_STYLE_WEIGHTS, card.style.name, delta)

        if (reaction == Reaction.SAVE) {
            val saved = savedIds().apply { put(card.id, true) }
            prefs.edit().putString(KEY_SAVED, saved.toString()).apply()
        }
    }

    fun markShown(cardIds: List<String>, dateIso: String) {
        val history = shownHistory()
        cardIds.forEach { history.put(it, dateIso) }
        prefs.edit().putString(KEY_SHOWN_HISTORY, history.toString()).apply()
    }

    /** Days since a card last appeared in the feed; large if it's never been shown. */
    fun daysSinceShown(cardId: String, todayIso: String): Int {
        val lastShown = shownHistory().optString(cardId, null) ?: return Int.MAX_VALUE
        return try {
            val last = java.time.LocalDate.parse(lastShown)
            val today = java.time.LocalDate.parse(todayIso)
            java.time.temporal.ChronoUnit.DAYS.between(last, today).toInt()
        } catch (e: java.time.format.DateTimeParseException) {
            Int.MAX_VALUE
        }
    }

    private fun adjustWeight(key: String, name: String, delta: Double) {
        val w = weights(key)
        val newValue = (w.optDouble(name, 1.0) + delta).coerceIn(MIN_WEIGHT, MAX_WEIGHT)
        w.put(name, newValue)
        prefs.edit().putString(key, w.toString()).apply()
    }

    private fun weights(key: String): JSONObject = JSONObject(prefs.getString(key, "{}") ?: "{}")
    private fun shownHistory(): JSONObject = JSONObject(prefs.getString(KEY_SHOWN_HISTORY, "{}") ?: "{}")
    private fun savedIds(): JSONObject = JSONObject(prefs.getString(KEY_SAVED, "{}") ?: "{}")

    companion object {
        private const val PREFS_NAME = "microlearning_feedback"
        private const val KEY_TOPIC_WEIGHTS = "topic_weights"
        private const val KEY_STYLE_WEIGHTS = "style_weights"
        private const val KEY_SHOWN_HISTORY = "shown_history"
        private const val KEY_SAVED = "saved_ids"
        private const val WEIGHT_STEP = 0.2
        private const val MIN_WEIGHT = 0.2
        private const val MAX_WEIGHT = 3.0
    }
}
