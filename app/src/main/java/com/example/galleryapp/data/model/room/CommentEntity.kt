package com.example.galleryapp.data.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "image_id") val imageId: Int?,
    @ColumnInfo(name = "date") val date: Int?,
    @ColumnInfo(name = "text") val text: String?
)
