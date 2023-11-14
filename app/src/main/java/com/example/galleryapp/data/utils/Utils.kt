package com.example.galleryapp.data.utils

import android.R.attr.bitmap
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.galleryapp.R
import com.example.galleryapp.data.model.doe.DataOrException
import com.example.galleryapp.data.model.network.ApiResponseData
import com.example.galleryapp.presentation.app.TAG
import okio.IOException
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date


@Composable
fun getWidthPercent(context: Context): Dp {
    val displayMetrics = context.resources.displayMetrics
    return ((displayMetrics.widthPixels / displayMetrics.density) / 100).dp
}

@Composable
fun getHeightPercent(context: Context): Dp {
    val displayMetrics = context.resources.displayMetrics
    return ((displayMetrics.heightPixels / displayMetrics.density) / 100).dp
}

fun Context.showToast(message: String){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

//fun Context.findActivity(): Activity? = when (this) {
//    is Activity -> this
//    is ContextWrapper -> baseContext.findActivity()
//    else -> null
//}

@RequiresApi(Build.VERSION_CODES.P)
fun Uri.compressImage(context: Context) = try {

    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true

    val contentResolver = context.contentResolver
    val source = ImageDecoder.createSource(contentResolver, this)

    var bitmap = ImageDecoder.decodeBitmap(source)

//    var bitmap = context.contentResolver.openInputStream(this).use { inpStream ->
//        BitmapFactory.decodeStream(inpStream, null, options)
//    }

    val k = 0.9
    while (
        //bitmap != null &&
        (bitmap.byteCount >= 2_000_000 ||
                bitmap.height >= Constants.MAX_SIZE ||
                bitmap.width >= Constants.MAX_SIZE)
    ) {

        bitmap = Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.width * k).toInt(),
            (bitmap.height * k).toInt(),
            false
        )

    }

    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
    val byteArr = baos.toByteArray()
    System.gc()
    Base64.encodeToString(byteArr, Base64.NO_WRAP).toString()

} catch (e : Exception){
    Log.d(TAG, "compressImage: $e")
    ""
}

@RequiresApi(Build.VERSION_CODES.P)
fun Uri.toBase64(context: Context) =
    try {
        context.contentResolver.openInputStream(this)?.use {
            return this.compressImage(context)
        }
    } catch (error: IOException) {
        error.printStackTrace()
        ""
    }

fun dateFormat(seconds: Int) = SimpleDateFormat("dd.MM.yyyy").format(Date(seconds.toLong() * 1000L)).toString()
fun dateTimeFormat(seconds: Int) = SimpleDateFormat("dd.MM.yyyy hh:mm").format(Date(seconds.toLong() * 1000L)).toString()


fun Context.createImageFile(): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName,
        ".jpg",
        externalCacheDir
    )
}

inline fun <T : ApiResponseData> checkResponse(
    onSuccessNecessary: () -> Unit = {},
    dataOrException: DataOrException<T>,
    onError: (String) -> Unit,
    onSuccess: (T) -> Unit
) = when (dataOrException.data?.status) {
        200 -> {
            try {
                dataOrException.data?.data?.let { data ->
                    onSuccess(data)
                }
                onSuccessNecessary()
                UiState.Success()
            } catch (e: Exception){
                val message = Constants.NOT_FOUND_MESSAGE
                onError(message)
                UiState.Failure()
            }
        }
        else -> {
            val message = try {
                throw dataOrException.exception ?: Exception(Constants.NULL_MESSAGE)
            } catch (ioE: IOException) {
                Constants.CONNECTION_LOST_MESSAGE
            } catch (hE: HttpException){
                hE.response()?.errorBody()?.string() ?: Constants.NULL_MESSAGE    // todo consider all statuses
            } catch (e : Exception){
                e.message.toString()
            }
            onError(message)
            UiState.Failure()
        }
    }

