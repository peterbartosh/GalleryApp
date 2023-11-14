package com.example.galleryapp.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.galleryapp.data.model.room.CommentEntity
import com.example.galleryapp.data.model.room.ImageEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface LocalDao {

    // user
//    @Query("Select * From user")
//    suspend fun getUserEntity(): List<UserEntity>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertUserEntity(userEntity: UserEntity)

    // images
    @Query("Select * From images Where user_id = :userId Order By id")
    fun getAllImages(userId: Int) : Flow<List<ImageEntity>>

    @Query("Select * From images Where id = :imageId")
    suspend fun getImageById(imageId: Int) : ImageEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(imageEntity: ImageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllImages(imageEntities: List<ImageEntity>)

    @Query("Delete From images Where id = :imageId")
    suspend fun deleteImage(imageId: Int)

    @Query("Delete From images")
    suspend fun deleteAllImages()

    // comments
    @Query("Select * From comments Where image_id = :imageId Order By id")
    fun getImageComments(imageId: Int): Flow<List<CommentEntity>>

    @Query("Select * From comments Where image_id = :imageId Order By id")
    fun getImageCommentsAsList(imageId: Int): List<CommentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(commentEntity: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllComments(commentEntities: List<CommentEntity>)

    @Query("Delete From comments Where id = :commentId")
    suspend fun deleteComment(commentId: Int)

    @Query("Delete From comments Where image_id = :imageId")
    suspend fun deleteAllComments(imageId: Int)


}