package com.example.galleryapp.presentation.app

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.galleryapp.data.location.CheckAndRequest
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.galleryapp.R
import com.example.galleryapp.data.user.UserData
import com.example.galleryapp.data.utils.createImageFile
import com.example.galleryapp.data.utils.toBase64
import com.example.galleryapp.presentation.features.main.mainRoute
import com.example.galleryapp.presentation.features.map.mapRoute
import com.example.galleryapp.presentation.theme.LightGreen

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun FAB(
    snackbarHostState: SnackbarHostState,
    saveImageToDatabase: (String) -> Unit
) {

    val context = LocalContext.current

    val file = context.createImageFile()

    val uri = FileProvider.getUriForFile(
        context,
        context.packageName + ".fileprovider",
        file
    )

    val requestPermissions = remember {
        mutableStateOf(false)
    }

    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            capturedImageUri = uri
        }

    CheckAndRequest(snackbarHostState, requestPermissions) {
        cameraLauncher.launch(uri)
    }

    LaunchedEffect(key1 = capturedImageUri.path){
        if (capturedImageUri.path?.isNotEmpty() == true) {
            capturedImageUri.toBase64(context)?.let { base64Str ->
                saveImageToDatabase(base64Str)
            }
        }
    }

    ExtendedFloatingActionButton(
        modifier = Modifier.size(60.dp),
        shape = CircleShape,
        onClick = { requestPermissions.value = true }
    ) {
        Icon(
            modifier = Modifier
                .size(50.dp),
                //.shadow(elevation = 0.dp, shape = CircleShape)
                //.clip(CircleShape)
            imageVector = Icons.Filled.Add,
            contentDescription = null
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    onMenuIconClick: () -> Unit,
    onBackIconClick: () -> Unit,
    showBackIcon: Boolean,
    isAuthScreen: Boolean
) {

    CenterAlignedTopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = LightGreen
        ),
        title = { Text(text = "Gallery App") },
        navigationIcon = {
            if (!isAuthScreen)
            Icon(
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        if (showBackIcon)
                            onBackIconClick()
                        else
                            onMenuIconClick()
                    },
                imageVector = if (showBackIcon) Icons.Default.ArrowBack else Icons.Default.Menu,
                contentDescription = null
            )
            else Box{}
        }
    )
}

@Composable
fun NavigationSheet(
    snackbarHostState: SnackbarHostState,
    navigateSavable: (String) -> Unit,
    hideDrawer: () -> Unit
) {
    val requestPermissions = remember {
        mutableStateOf(false)
    }
    CheckAndRequest(
        snackbarHostState = snackbarHostState,
        requestPermissions = requestPermissions,
        includeCameraPermission = false
    ) {
        navigateSavable(mapRoute)
    }

    val iconsIds = listOf(R.drawable.profile_icon, R.drawable.map_icon)
    val texts = listOf("Photos", "Map")

    ModalDrawerSheet(
        drawerContainerColor = LightGreen,
        drawerContentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.3f)
                .background(LightGreen),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                modifier = Modifier.padding(start = 10.dp, bottom = 10.dp),
                text = UserData.login,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.background
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f)
                .background(MaterialTheme.colorScheme.background)
        ) {
            //Divider()
            repeat(2){ind ->
                Row(
                    modifier = Modifier
                        .padding(top = 5.dp, bottom = 5.dp)
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clickable {
                            hideDrawer()
                            if (ind == 1)
                                requestPermissions.value = true
                            else {
                                navigateSavable(mainRoute)
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        modifier = Modifier.width(50.dp),
                        painter = painterResource(id = iconsIds[ind]),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
//                        tint = if (ind == 0)
//                            MaterialTheme.colorScheme.onBackground
//                        else
//                            MaterialTheme.colorScheme.background
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = texts[ind],
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Divider()
            }
        }
    }
}