package com.juyel.totka.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
data class User(
    val userId:      String = "",
    val gmail:       String = "",
    val fullName:    String = "",
    val username:    String = "",
    val bio:         String = "",
    val board:       String = "",
    val classVal:    String = "",
    val phone:       String = "",
    val favSubject:  String = "",
    val facebook:    String = "",
    val instagram:   String = "",
    val youtube:     String = "",
    val telegram:    String = "",
    val joinDate:    String = ""
) : Parcelable {

    fun toJson(): JSONObject = JSONObject().apply {
        put("userId",     userId)
        put("gmail",      gmail)
        put("fullName",   fullName)
        put("username",   username)
        put("bio",        bio)
        put("board",      board)
        put("classVal",   classVal)
        put("phone",      phone)
        put("favSubject", favSubject)
        put("facebook",   facebook)
        put("instagram",  instagram)
        put("youtube",    youtube)
        put("telegram",   telegram)
        put("joinDate",   joinDate)
    }

    fun toChangesJson(updated: User): JSONObject {
        val changes = JSONObject()
        if (fullName   != updated.fullName)   changes.put("fullName",   updated.fullName)
        if (username   != updated.username)   changes.put("username",   updated.username)
        if (bio        != updated.bio)        changes.put("bio",        updated.bio)
        if (board      != updated.board)      changes.put("board",      updated.board)
        if (classVal   != updated.classVal)   changes.put("classVal",   updated.classVal)
        if (phone      != updated.phone)      changes.put("phone",      updated.phone)
        if (favSubject != updated.favSubject) changes.put("favSubject", updated.favSubject)
        val socialOld = JSONObject().apply {
            put("facebook", facebook); put("instagram", instagram)
            put("youtube", youtube);   put("telegram", telegram)
        }
        val socialNew = JSONObject().apply {
            put("facebook", updated.facebook); put("instagram", updated.instagram)
            put("youtube", updated.youtube);   put("telegram", updated.telegram)
        }
        if (socialOld.toString() != socialNew.toString()) changes.put("socialLinks", socialNew)
        return changes
    }

    companion object {
        fun fromJson(j: JSONObject): User {
            // socialLinks can be a nested object or flat fields
            val social = try { j.optJSONObject("socialLinks") } catch (e: Exception) { null }
            return User(
                userId      = j.optString("userId"),
                gmail       = j.optString("gmail"),
                fullName    = j.optString("fullName"),
                username    = j.optString("username"),
                bio         = j.optString("bio"),
                board       = j.optString("board"),
                classVal    = j.optString("classVal"),
                phone       = j.optString("phone"),
                favSubject  = j.optString("favSubject"),
                facebook    = social?.optString("facebook") ?: j.optString("facebook"),
                instagram   = social?.optString("instagram") ?: j.optString("instagram"),
                youtube     = social?.optString("youtube") ?: j.optString("youtube"),
                telegram    = social?.optString("telegram") ?: j.optString("telegram"),
                joinDate    = j.optString("joinDate")
            )
        }
    }
}
