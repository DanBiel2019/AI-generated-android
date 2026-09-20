package com.example.aigeneratedandroid.microlearning.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aigeneratedandroid.microlearning.R
import com.example.aigeneratedandroid.microlearning.data.Reaction
import com.example.aigeneratedandroid.microlearning.model.DailyFeed
import com.example.aigeneratedandroid.microlearning.model.IdeaCard

private const val VIEW_TYPE_CARD = 0
private const val VIEW_TYPE_SUMMARY = 1

interface FeedCardListener {
    fun onReaction(card: IdeaCard, reaction: Reaction)
    fun onListen(card: IdeaCard)
    fun isSaved(card: IdeaCard): Boolean
}

/** One page per idea card, plus a trailing summary page with the day's theme and challenge. */
class FeedPagerAdapter(
    private val feed: DailyFeed,
    private val listener: FeedCardListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int = feed.cards.size + 1

    override fun getItemViewType(position: Int): Int =
        if (position < feed.cards.size) VIEW_TYPE_CARD else VIEW_TYPE_SUMMARY

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_CARD) {
            CardViewHolder(inflater.inflate(R.layout.page_idea_card, parent, false))
        } else {
            SummaryViewHolder(inflater.inflate(R.layout.page_summary, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CardViewHolder) {
            holder.bind(feed.cards[position], position + 1, feed.cards.size, listener)
        } else if (holder is SummaryViewHolder) {
            holder.bind(feed)
        }
    }

    private class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val progress = itemView.findViewById<android.widget.TextView>(R.id.cardProgress)
        private val title = itemView.findViewById<android.widget.TextView>(R.id.cardTitle)
        private val meta = itemView.findViewById<android.widget.TextView>(R.id.cardMeta)
        private val insight = itemView.findViewById<android.widget.TextView>(R.id.cardInsight)
        private val art = itemView.findViewById<android.widget.TextView>(R.id.cardArt)
        private val upBtn = itemView.findViewById<android.widget.Button>(R.id.btnUp)
        private val downBtn = itemView.findViewById<android.widget.Button>(R.id.btnDown)
        private val saveBtn = itemView.findViewById<android.widget.Button>(R.id.btnSave)
        private val listenBtn = itemView.findViewById<android.widget.Button>(R.id.btnListen)

        fun bind(card: IdeaCard, index: Int, total: Int, listener: FeedCardListener) {
            val ctx = itemView.context
            progress.text = ctx.getString(R.string.card_progress_format, index, total)
            title.text = "💡 ${card.title}"
            meta.text = ctx.getString(
                R.string.card_meta_format,
                card.sourceName,
                card.author,
                card.readTimeSeconds
            )
            insight.text = card.insight
            art.text = card.asciiArt
            saveBtn.text = if (listener.isSaved(card)) "🔖 Saved" else "🔖 Save"

            upBtn.setOnClickListener { listener.onReaction(card, Reaction.UP) }
            downBtn.setOnClickListener { listener.onReaction(card, Reaction.DOWN) }
            saveBtn.setOnClickListener {
                listener.onReaction(card, Reaction.SAVE)
                saveBtn.text = "🔖 Saved"
            }
            listenBtn.setOnClickListener { listener.onListen(card) }
        }
    }

    private class SummaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val theme = itemView.findViewById<android.widget.TextView>(R.id.summaryTheme)
        private val challenge = itemView.findViewById<android.widget.TextView>(R.id.summaryChallenge)
        private val count = itemView.findViewById<android.widget.TextView>(R.id.summaryCount)

        fun bind(feed: DailyFeed) {
            val ctx = itemView.context
            theme.text = ctx.getString(R.string.summary_theme_format, feed.theme)
            challenge.text = ctx.getString(R.string.summary_challenge_format, feed.challenge)
            count.text = ctx.getString(R.string.summary_count_format, feed.cards.size)
        }
    }
}
