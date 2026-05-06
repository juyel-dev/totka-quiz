package com.juyel.totka.data.db

import androidx.room.*

@Entity(tableName = "quiz_history")
data class QuizHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val board:    String,
    val classVal: String,
    val subject:  String,
    val chapter:  String,
    val total:    Int,
    val correct:  Int,
    val wrong:    Int,
    val skipped:  Int,
    val timeSec:  Long,
    val percent:  Int,
    val date:     String,
)

@Dao
interface QuizHistoryDao {
    @Insert
    suspend fun insert(h: QuizHistoryEntity)

    @Query("SELECT * FROM quiz_history ORDER BY id DESC")
    suspend fun getAll(): List<QuizHistoryEntity>

    @Query("SELECT * FROM quiz_history ORDER BY id DESC LIMIT 10")
    suspend fun getRecent(): List<QuizHistoryEntity>

    @Query("DELETE FROM quiz_history")
    suspend fun clearAll()
}

@Database(entities = [QuizHistoryEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun quizHistoryDao(): QuizHistoryDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun get(ctx: android.content.Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    ctx.applicationContext, AppDatabase::class.java, "totka_db"
                ).build().also { INSTANCE = it }
            }
    }
}
