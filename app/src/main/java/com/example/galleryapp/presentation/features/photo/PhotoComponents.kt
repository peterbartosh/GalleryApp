package com.example.galleryapp.presentation.features.photo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.galleryapp.data.model.network.CommentDtoOut
import com.example.galleryapp.data.utils.Constants
import com.example.galleryapp.data.utils.dateTimeFormat
import com.example.galleryapp.data.utils.getWidthPercent
import com.example.galleryapp.data.utils.showToast
import com.example.galleryapp.presentation.components.MyAlertDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import org.w3c.dom.Comment


@Composable
fun CommentCard(
    comment: CommentDtoOut,
    onDeleteClick: (Int) -> Unit
) {

    val expandDeleteDialog = remember {
        mutableStateOf(false)
    }

    val interactionSource = remember {
        MutableInteractionSource()
    }

    val viewConfiguration = LocalViewConfiguration.current

    val haptic = LocalHapticFeedback.current

    LaunchedEffect(interactionSource) {

        interactionSource.interactions.collectLatest { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    delay(viewConfiguration.longPressTimeoutMillis)
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    expandDeleteDialog.value = true
                }
            }
        }
    }

    comment.text?.let { commentText ->
        comment.date?.let { seconds ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(start = 10.dp, end = 10.dp, bottom = 5.dp)
                    .clickable(interactionSource, null) {},
                colors = CardDefaults.cardColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp, bottomEnd = 30.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.padding(start = 20.dp),
                        fontSize = 16.sp,
                        text = commentText
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(end = 10.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            modifier = Modifier.padding(bottom = 10.dp, start = 10.dp),
                            fontSize = 12.sp,
                            text = dateTimeFormat(seconds)
                        )
                    }
                }

            }

            MyAlertDialog(text = "Delete comment?", expandDeleteDialog = expandDeleteDialog) {
                comment.id?.let { id ->
                    onDeleteClick(id)
                }
            }

        }
    }
}

@Composable
fun CommentInputField(
    height: Dp,
    input: MutableState<String>,
    onSendClick: (String) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(bottom = 5.dp, top = 5.dp),
    ) {

        Divider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {


            TextField(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(height)
                    .padding(start = 10.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    disabledContainerColor = MaterialTheme.colorScheme.background,
                ),
                value = input.value,
                onValueChange = { newInput -> input.value = newInput },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            IconButton(
                modifier = Modifier
                    .size(height)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                onClick = {
                    onSendClick(input.value)
                    input.value = ""
                }
            ) {
                Icon(
                    modifier = Modifier.size(height),
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}
