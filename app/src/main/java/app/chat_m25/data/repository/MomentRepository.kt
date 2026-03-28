package app.chat_m25.data.repository

import app.chat_m25.data.local.dao.MomentDao
import app.chat_m25.data.local.entity.MomentEntity
import app.chat_m25.data.mapper.EntityMapper.toDomain
import app.chat_m25.data.mapper.EntityMapper.toEntity
import app.chat_m25.domain.model.Moment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MomentRepository @Inject constructor(
    private val momentDao: MomentDao
) {
    fun getAllMoments(): Flow<List<Moment>> {
        return momentDao.getAllMoments().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getMomentById(id: Long): Moment? {
        return momentDao.getMomentById(id)?.toDomain()
    }

    suspend fun createMoment(userId: Long, userName: String, content: String, images: List<String>): Long {
        val entity = MomentEntity(
            userId = userId,
            userName = userName,
            content = content,
            images = JSONArray(images).toString()
        )
        return momentDao.insertMoment(entity)
    }

    suspend fun deleteMoment(id: Long) {
        momentDao.deleteMomentById(id)
    }

    suspend fun toggleLike(id: Long) {
        val moment = momentDao.getMomentById(id) ?: return
        val newLiked = !moment.isLiked
        val delta = if (newLiked) 1 else -1
        momentDao.updateLikeStatus(id, newLiked, delta)
    }
}
