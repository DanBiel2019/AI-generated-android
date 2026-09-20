package com.example.aigeneratedandroid.microlearning.ui

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.aigeneratedandroid.microlearning.R
import com.example.aigeneratedandroid.microlearning.curation.ContentCurationEngine
import com.example.aigeneratedandroid.microlearning.data.FeedbackStore
import com.example.aigeneratedandroid.microlearning.data.ProfileStore
import com.example.aigeneratedandroid.microlearning.data.Reaction
import com.example.aigeneratedandroid.microlearning.model.IdeaCard
import com.example.aigeneratedandroid.microlearning.narration.NarrationFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Single-screen host for the daily feed: loads the profile, asks ContentCurationEngine for
 * today's cards, and renders them as swipeable pages. Feedback taps write straight back to
 * FeedbackStore so tomorrow's curation run reflects them.
 */
class DailyFeedActivity : AppCompatActivity(), FeedCardListener {

    private lateinit var feedbackStore: FeedbackStore
    private lateinit var textToSpeech: TextToSpeech
    private var ttsReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val profileStore = ProfileStore(this)
        feedbackStore = FeedbackStore(this)
        textToSpeech = TextToSpeech(this) { status -> ttsReady = status == TextToSpeech.SUCCESS }

        val profile = profileStore.load()
        val today = LocalDate.now()
        val feed = ContentCurationEngine(feedbackStore).generateDailyFeed(profile, today)

        findViewById<TextView>(R.id.feedHeader).text = getString(
            R.string.feed_header_format,
            today.format(DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault()))
        )

        findViewById<ViewPager2>(R.id.feedPager).adapter = FeedPagerAdapter(feed, this)
    }

    override fun onReaction(card: IdeaCard, reaction: Reaction) {
        feedbackStore.record(card, reaction)
        val label = when (reaction) {
            Reaction.UP -> "Noted — more like “${card.title}” coming up."
            Reaction.DOWN -> "Noted — less of this kind of thing."
            Reaction.SAVE -> "Saved."
        }
        Toast.makeText(this, label, Toast.LENGTH_SHORT).show()
    }

    override fun onListen(card: IdeaCard) {
        if (!ttsReady) {
            Toast.makeText(this, "Voice engine still warming up, try again in a second.", Toast.LENGTH_SHORT).show()
            return
        }
        textToSpeech.speak(NarrationFormatter.toSpokenScript(card), TextToSpeech.QUEUE_FLUSH, null, card.id)
    }

    override fun isSaved(card: IdeaCard): Boolean = feedbackStore.isSaved(card.id)

    override fun onDestroy() {
        textToSpeech.stop()
        textToSpeech.shutdown()
        super.onDestroy()
    }
}
