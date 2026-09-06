package dev.jvqtil.cuber.desktop.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SolveDao {

    @Insert
    suspend fun insert(solve: SolveEntity)

    @Delete
    suspend fun delete(solve: SolveEntity)

    @Query("SELECT * FROM solves ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<SolveEntity>>

    @Query("UPDATE solves SET penalty = :penalty WHERE id = :id")
    suspend fun updatePenalty(
        id: Long,
        penalty: String
    )

    @Query("UPDATE solves SET comment = :comment WHERE id = :id")
    suspend fun updateComment(
        id: Long,
        comment: String?
    )
}