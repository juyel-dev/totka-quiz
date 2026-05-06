package com.juyel.totka.data.model

data class UserProfile(
    val userId:      String = "",
    val gmail:       String = "",
    val fullName:    String = "",
    val username:    String = "",
    val bio:         String = "",
    val board:       String = "",
    val classVal:    String = "",
    val phone:       String = "",
    val favSubject:  String = "",
    val socialLinks: SocialLinks = SocialLinks(),
    val joinDate:    String = "",
)

data class SocialLinks(
    val facebook:  String = "",
    val instagram: String = "",
    val youtube:   String = "",
    val telegram:  String = "",
)

data class MasterRow(
    val sheetid:   String = "",
    val sheetname: String = "",
    val board:     String = "",
    val classVal:  String = "",
    val subject:   String = "",
    val chapter:   String = "",
    val csvlink:   String = "",
)

data class Question(
    val id:          String = "",
    val chapter:     String = "",
    val difficulty:  String = "",
    val question:    String = "",
    val optA:        String = "",
    val optB:        String = "",
    val optC:        String = "",
    val optD:        String = "",
    val correct:     String = "",
    val explanation: String = "",
    var userAnswer:  String = "",
    var bookmarked:  Boolean = false,
)

data class QuizResult(
    val totalQ:   Int    = 0,
    val correct:  Int    = 0,
    val wrong:    Int    = 0,
    val skipped:  Int    = 0,
    val timeSec:  Long   = 0L,
    val subject:  String = "",
    val chapter:  String = "",
    val board:    String = "",
    val classVal: String = "",
    val date:     String = "",
)
