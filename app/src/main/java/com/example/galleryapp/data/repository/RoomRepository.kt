package com.example.galleryapp.data.repository

import com.example.galleryapp.data.model.room.CommentEntity
import com.example.galleryapp.data.room.LocalDao
import com.example.galleryapp.data.model.room.ImageEntity
import com.example.galleryapp.data.user.UserData
import com.example.galleryapp.data.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoomRepository @Inject constructor(private val localDao: LocalDao) {

    //user
//    suspend fun getUserCredentials() = try {
//        localDao.getUserEntity().first()
//    } catch (e : Exception){
//        null
//    }
//
//    suspend fun insertUserCredentials(userEntity: UserEntity) =
//        localDao.insertUserEntity(userEntity)

    // images
    fun getAllImages(userId: Int = UserData.userId) = localDao.getAllImages(userId =  userId).flowOn(Dispatchers.IO)

    suspend fun getImageById(imageId: Int) = withContext(Dispatchers.IO) {
        localDao.getImageById(imageId)
    }
    suspend fun addImage(imageEntity: ImageEntity) = withContext(Dispatchers.IO) {
        localDao.insertImage(imageEntity)
    }
    suspend fun addAllImages(imageEntities: List<ImageEntity>) = withContext(Dispatchers.IO){
        localDao.insertAllImages(imageEntities)
    }
    suspend fun deleteImage(imageId: Int) = withContext(Dispatchers.IO) {
        localDao.deleteImage(imageId)
    }

    suspend fun deleteAllImages() = withContext(Dispatchers.IO) {
        localDao.deleteAllImages()
    }

    // comments
    fun getImageComments(imageId: Int) =
        localDao.getImageComments(imageId).flowOn(Dispatchers.IO)

    suspend fun getImageCommentsAsList(imageId: Int) = withContext(Dispatchers.IO) {
        localDao.getImageCommentsAsList(imageId)
    }

    suspend fun addComment(commentEntity: CommentEntity) = withContext(Dispatchers.IO){
        localDao.insertComment(commentEntity)
    }
    suspend fun addAllComments(commentEntities: List<CommentEntity>) = withContext(Dispatchers.IO){
        localDao.insertAllComments(commentEntities)
    }
    suspend fun deleteComment(commentId: Int) = withContext(Dispatchers.IO){
        localDao.deleteComment(commentId)
    }
    suspend fun deleteAllComments(imageId: Int) = withContext(Dispatchers.IO){
        localDao.deleteAllComments(imageId)
    }


}