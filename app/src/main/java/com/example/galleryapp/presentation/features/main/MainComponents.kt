package com.example.galleryapp.presentation.features.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.galleryapp.data.model.network.ImageDtoOut
import com.example.galleryapp.data.utils.dateFormat
import com.example.galleryapp.data.utils.getWidthPercent
import com.example.galleryapp.presentation.components.MyAlertDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@ExperimentalMaterial3Api
@Composable
fun ImageBox(
    image: ImageDtoOut,
    onPhotoClicked: (Int) -> Unit,
    onDialogConfirmed: (Int) -> Unit
) {
    val context = LocalContext.current

    val wp = getWidthPercent(context = context)

    val expandDeleteDialog = remember {
        mutableStateOf(false)
    }

    val interactionSource = remember {
        MutableInteractionSource()
    }

    val viewConfiguration = LocalViewConfiguration.current

    val haptic = LocalHapticFeedback.current


    LaunchedEffect(interactionSource) {
        var isLongClick = false

        interactionSource.interactions.collectLatest { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    isLongClick = false
                    delay(viewConfiguration.longPressTimeoutMillis)
                    isLongClick = true
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    expandDeleteDialog.value = true
                }
                //context.showToast("long press")

                is PressInteraction.Release -> {
                    if (isLongClick.not()) {
                        image.id?.let { id ->
                            onPhotoClicked(id)
                        }
                        //context.showToast("simple click")
                    }
                }
            }
        }
    }


        MyAlertDialog(
            text = "Delete image?",
            expandDeleteDialog = expandDeleteDialog) {
            image.id?.let { id ->
                onDialogConfirmed(id)
            }
        }



    Card(
        modifier = Modifier
            .wrapContentHeight(unbounded = false)
            .height(wp * 60)
            .width(wp * 30)
            .padding(3.dp)
            .clickable(interactionSource, indication = null) {},
        //border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray),
        //elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentScale = ContentScale.Fit,
                model = image.url,
                contentDescription = null
            )

            Spacer(modifier = Modifier.height(10.dp))

            image.date?.let { seconds ->
                val date = dateFormat(seconds)
                Text(text = date)
            }
        }
    }
}