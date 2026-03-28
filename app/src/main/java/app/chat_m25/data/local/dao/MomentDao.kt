package app.chat_m25.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import app.chat_m25.data.local.entity.MomentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MomentDao {
    @Query("SELECT * FROM moments ORDER BY timestamp DESC")
    fun getAllMoments(): Flow<List<MomentEntity>>

    @Query("SELECT * FROM moments WHERE id = :id")
    suspend fun getMomentById(id: Long): MomentEntity?

    @Query("SELECT * FROM moments WHERE userId = :userId ORDER BY timestamp DESC")
    fun getMomentsByUser(userId: Long): Flow<List<MomentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoment(moment: MomentEntity): Long

    @Update
    suspend fun updateMoment(moment: MomentEntity)

    @Delete
    suspend fun deleteMoment(moment: MomentEntity)

    @Query("DELETE FROM moments WHERE id = :id")
    suspend fun deleteMomentById(id: Long)

    @Query("UPDATE moments SET isLiked = :isLiked, likeCount = likeCount + :delta WHERE id = :id")
    suspend fun updateLikeStatus(id: Long, isLiked: Boolean, delta: Int)
}