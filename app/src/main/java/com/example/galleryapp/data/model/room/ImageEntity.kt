package com.example.galleryapp.data.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "images")
data class ImageEntity(

    @PrimaryKey()
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "user_id") val userId: Int,

    @ColumnInfo(name = "url") val url: String? = null,

    @ColumnInfo(name = "date") var date : Int? = null,

    @ColumnInfo(name = "lat") var lat : Double? = null,

    @ColumnInfo(name = "lng") var lng : Double? = null,
)