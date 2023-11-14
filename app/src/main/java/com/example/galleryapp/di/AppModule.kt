package com.example.galleryapp.di

import android.content.Context
import androidx.room.Room
import com.example.galleryapp.data.network.RestApi
import com.example.galleryapp.data.network.ServiceBuilder
import com.example.galleryapp.data.room.LocalDao
import com.example.galleryapp.data.room.LocalDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideRestApi() = ServiceBuilder.buildService(RestApi::class.java)

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): LocalDatabase =
        Room.databaseBuilder(
            context,
            LocalDatabase::class.java,
            "local_database"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideWeatherDao(localDatabase: LocalDatabase): LocalDao
            = localDatabase.localDao()
}