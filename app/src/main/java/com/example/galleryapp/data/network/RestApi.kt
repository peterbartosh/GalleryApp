package com.example.galleryapp.data.network

import com.example.galleryapp.data.model.network.CommentDtoIn
import com.example.galleryapp.data.model.network.CommentDtoOut
import com.example.galleryapp.data.model.network.ImageDtoIn
import com.example.galleryapp.data.model.network.ImageDtoOut
import com.example.galleryapp.data.model.network.ResponseDto
import com.example.galleryapp.data.model.network.ResponseDtoComment
import com.example.galleryapp.data.model.network.ResponseDtoImage
import com.example.galleryapp.data.model.network.SignUserDtoIn
import com.example.galleryapp.data.model.network.SignUserOutDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Singleton

@Singleton
interface RestApi {

    // users
    @POST("api/account/signin")
    suspend fun signIn(@Body userDto: SignUserDtoIn): ResponseDto<SignUserOutDto>

    @POST("api/account/signup")
    suspend fun signUp(@Body userDto: SignUserDtoIn) : ResponseDto<SignUserOutDto>

    // images
    @GET("/api/image")
    suspend fun getImages(
        @HeaderMap token: Map<String, String>,
        @Query("page") page: Int
    ): ResponseDtoImage

    @POST("/api/image")
    suspend fun postImage(
        @HeaderMap token: Map<String, String>,
        @Body imageDtoIn: ImageDtoIn
    ): ResponseDto<ImageDtoOut>

    @DELETE("/api/image/{id}")
    suspend fun deleteImage(
        @HeaderMap token: Map<String, String>,
        @Path("id") id: Int
    ): ResponseDto<ImageDtoOut>

    // comments
    @GET("/api/image/{imageId}/comment")
    suspend fun getComments(
        @HeaderMap token: Map<String, String>,
        @Path("imageId") imageId: Int,
        @Query("page") page: Int
    ): ResponseDtoComment

    @POST("/api/image/{imageId}/comment")
    suspend fun postComment(
        @HeaderMap token: Map<String, String>,
        @Body commentDtoIn: CommentDtoIn,
        @Path("imageId") imageId: Int,
    ): ResponseDto<CommentDtoOut>

    @DELETE("/api/image/{imageId}/comment/{commentId}")
    suspend fun deleteComment(
        @HeaderMap token: Map<String, String>,
        @Path("commentId") commentID: Int,
        @Path("imageId") imageId: Int,
    ): ResponseDto<CommentDtoOut>


}