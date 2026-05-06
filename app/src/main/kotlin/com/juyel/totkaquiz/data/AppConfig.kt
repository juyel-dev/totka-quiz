package com.juyel.totkaquiz.data

object AppConfig {
    const val GAS_URL = "https://script.google.com/macros/s/AKfycbzrSAsUNxfsW2mtV7yQrOiPB6IloQ2XhMAgVhOmFXsQXk8KZSvD2UXYra-LnB0Id8vo1Q/exec"

    const val MASTER_CSV = "https://docs.google.com/spreadsheets/d/1dJtuu61H_i1q_xL4--b4xsFtCVxzIi301bIDIAz1qdw/export?format=csv"

    // ⚠️ PLACEHOLDER — replace with real bot token before release
    const val TG_BOT_TOKEN = "YOUR_BOT_TOKEN_HERE"
    const val TG_CHAT_ID   = "7929275539"
    const val TG_API_URL   = "https://api.telegram.org/bot${TG_BOT_TOKEN}/sendMessage"

    // Boards list
    val BOARDS = listOf(
        "SSC (NCTB)", "HSC (NCTB)", "JSC (NCTB)",
        "Primary (NCTB)", "CBSE", "ICSE", "Madrasah",
        "Alim", "Fazil", "Other"
    )

    // Classes list
    val CLASSES = listOf(
        "Class 1","Class 2","Class 3","Class 4","Class 5",
        "Class 6","Class 7","Class 8","Class 9","Class 10",
        "Class 11","Class 12"
    )

    // Social platforms
    val SOCIAL_PLATFORMS = listOf(
        "facebook","instagram","youtube","twitter","linkedin","telegram","tiktok"
    )
}
