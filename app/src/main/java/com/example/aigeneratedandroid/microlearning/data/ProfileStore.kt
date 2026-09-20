package com.example.aigeneratedandroid.microlearning.data

import android.content.Context
import com.example.aigeneratedandroid.microlearning.model.DepthLevel
import com.example.aigeneratedandroid.microlearning.model.LearningStyle
import com.example.aigeneratedandroid.microlearning.model.SourceFormat
import com.example.aigeneratedandroid.microlearning.model.UserProfile
import org.json.JSONArray
import org.json.JSONObject

/**
 * Persists the onboarding-quiz answers. First launch seeds UserProfile.default() so the
 * feed works immediately; every field can later be overwritten by re-running onboarding.
 */
class ProfileStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(): UserProfile {
        val json = prefs.getString(KEY_PROFILE, null) ?: return UserProfile.default().also { save(it) }
        return try {
            fromJson(JSONObject(json))
        } catch (e: org.json.JSONException) {
            UserProfile.default()
        }
    }

    fun save(profile: UserProfile) {
        prefs.edit().putString(KEY_PROFILE, toJson(profile).toString()).apply()
    }

    private fun toJson(p: UserProfile): JSONObject = JSONObject().apply {
        put("lovedBooks", JSONArray(p.lovedBooks))
        put("followedAuthors", JSONArray(p.followedAuthors))
        put("topics", JSONArray(p.topics))
        put("preferredStyles", JSONArray(p.preferredStyles.map { it.name }))
        put("preferredFormats", JSONArray(p.preferredFormats.map { it.name }))
        put("sessionMinutes", p.sessionMinutes)
        put("chunkMinutes", p.chunkMinutes)
        put("depthLevel", p.depthLevel.name)
        put("consumptionMoment", p.consumptionMoment)
    }

    private fun fromJson(o: JSONObject): UserProfile = UserProfile(
        lovedBooks = o.getJSONArray("lovedBooks").toStringList(),
        followedAuthors = o.getJSONArray("followedAuthors").toStringList(),
        topics = o.getJSONArray("topics").toStringList(),
        preferredStyles = o.getJSONArray("preferredStyles").toStringList().map { LearningStyle.valueOf(it) },
        preferredFormats = o.getJSONArray("preferredFormats").toStringList().map { SourceFormat.valueOf(it) },
        sessionMinutes = o.getInt("sessionMinutes"),
        chunkMinutes = o.getInt("chunkMinutes"),
        depthLevel = DepthLevel.valueOf(o.getString("depthLevel")),
        consumptionMoment = o.getString("consumptionMoment")
    )

    private fun JSONArray.toStringList(): List<String> = (0 until length()).map { getString(it) }

    companion object {
        private const val PREFS_NAME = "microlearning_profile"
        private const val KEY_PROFILE = "profile_json"
    }
}
