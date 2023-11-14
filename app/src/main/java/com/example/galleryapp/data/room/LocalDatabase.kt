package com.example.galleryapp.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.galleryapp.data.model.room.CommentEntity
import com.example.galleryapp.data.model.room.ImageEntity

@Database(
    entities = [ImageEntity::class, CommentEntity::class],
    version = 6,
    exportSchema = false
)
abstract class LocalDatabase: RoomDatabase() {
    abstract fun localDao(): LocalDao
}