package app.chat_m25.data.repository

import app.chat_m25.data.local.dao.CommentDao
import app.chat_m25.data.local.dao.MomentDao
import app.chat_m25.data.local.entity.CommentEntity
import app.chat_m25.data.mapper.EntityMapper.toDomain
import app.chat_m25.domain.model.Comment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepository @Inject constructor(
    private val commentDao: CommentDao,
    private val momentDao: MomentDao
) {
    fun getCommentsByMomentId(momentId: Long): Flow<List<Comment>> {
        return commentDao.getCommentsByMomentId(momentId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getCommentById(id: Long): Comment? {
        return commentDao.getCommentById(id)?.toDomain()
    }

    suspend fun addComment(
        momentId: Long,
        userId: Long,
        userName: String,
        content: String,
        replyToId: Long? = null,
        replyToUserName: String? = null
    ): Long {
        val entity = CommentEntity(
            momentId = momentId,
            userId = userId,
            userName = userName,
            content = content,
            replyToId = replyToId,
            replyToUserName = replyToUserName
        )
        val commentId = commentDao.insertComment(entity)
        momentDao.updateMoment(
            momentDao.getMomentById(momentId)?.copy(
                commentCount = commentDao.getCommentCountByMomentId(momentId)
            ) ?: return commentId
        )
        return commentId
    }

    suspend fun deleteComment(id: Long) {
        val comment = commentDao.getCommentById(id) ?: return
        commentDao.deleteCommentById(id)
        val moment = momentDao.getMomentById(comment.momentId) ?: return
        momentDao.updateMoment(
            moment.copy(commentCount = commentDao.getCommentCountByMomentId(comment.momentId))
        )
    }
}
