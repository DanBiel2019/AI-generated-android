package com.example.aigeneratedandroid.microlearning.narration

import com.example.aigeneratedandroid.microlearning.model.IdeaCard

/**
 * Prepares an idea card for text-to-speech. Card copy in ContentBank is already written
 * short-sentence and parenthetical-free; this only adds the pacing a TTS engine can't infer
 * from punctuation alone (a beat after the title, a beat before the attribution).
 */
object NarrationFormatter {

    fun toSpokenScript(card: IdeaCard): String {
        val sourceLine = "From ${card.sourceName}, by ${card.author}."
        return listOf(card.title + ".", card.insight, sourceLine).joinToString(separator = "  ")
    }
}
