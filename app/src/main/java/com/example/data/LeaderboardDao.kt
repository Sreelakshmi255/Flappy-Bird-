package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LeaderboardDao {
    @Query("SELECT * FROM leaderboard ORDER BY score DESC LIMIT 100")
    fun getAllLeaderboardEntries(): Flow<List<LeaderboardEntryEntity>>

    @Query("SELECT * FROM leaderboard WHERE isFriend = 1 ORDER BY score DESC LIMIT 50")
    fun getFriendLeaderboardEntries(): Flow<List<LeaderboardEntryEntity>>

    @Query("SELECT * FROM leaderboard WHERE difficulty = :difficulty ORDER BY score DESC LIMIT 50")
    fun getLeaderboardByDifficulty(difficulty: String): Flow<List<LeaderboardEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntries(entries: List<LeaderboardEntryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: LeaderboardEntryEntity)

    @Query("DELETE FROM leaderboard WHERE isUser = 1")
    suspend fun deleteUserEntries()

    @Query("SELECT COUNT(*) FROM leaderboard")
    suspend fun getCount(): Int
}
