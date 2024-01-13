package com.smh.nxleave.design.component

import NX_BlackVariant
import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.smh.nxleave.BuildConfig
import com.smh.nxleave.R
import com.smh.nxleave.design.sheet.PhotoAction
import com.smh.nxleave.design.sheet.PickPhotoActionSheet
import com.smh.nxleave.utility.createImageFile
import com.smh.nxleave.utility.goToSetting
import com.smh.nxleave.utility.toFile
import java.io.File

@Composable
fun NXProfile(
    url: String,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    borderWidth: Dp = 4.dp,
    borderColor: Color = NX_BlackVariant,
    onUpdateProfileImage: ((File) -> Unit)? = null,
) {
    val context = LocalContext.current
    var permissionRequested by remember { mutableStateOf<String?>(null) }
    var imageFile by rememberSaveable { mutableStateOf<File?>(null) }
    var openPhotoActionBottomSheet by rememberSaveable { mutableStateOf(false) }
    var localImageFile by rememberSaveable { mutableStateOf<File?>(null) }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { result ->
            Log.i("CAMREAAA", result.toString())
            Log.i("CAMREAAA", (imageFile == null).toString())
            if (result) {
                imageFile?.let {
                    localImageFile = it
                }
            }
        }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.toFile(context = context)?.let { file ->
                localImageFile = file
            }
        }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                imageFile = context.createImageFile()
                FileProvider
                    .getUriForFile(context, "${BuildConfig.PackageId}.provider", imageFile!!)
                    .let(cameraLauncher::launch)
            } else {
                permissionRequested = "Please enable camera permission at setting."
            }
        }

    if (permissionRequested != null) {
        NXAlertDialog(
            title = "Permission required",
            body = permissionRequested!!,
            dismissButton = { permissionRequested = null },
            confirmButton = {
                permissionRequested = null
                context.goToSetting()
            }
        )
    }

    if(openPhotoActionBottomSheet) {
        PickPhotoActionSheet(
            onDismissRequest = { openPhotoActionBottomSheet = false},
            onSelected = {
                when(it) {
                    PhotoAction.CAMERA -> permissionLauncher.launch(Manifest.permission.CAMERA)
                    PhotoAction.LIBRARY -> galleryLauncher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
                openPhotoActionBottomSheet = false
            }
        )
    }

    LaunchedEffect(key1 = localImageFile) {
        localImageFile?.let {
            onUpdateProfileImage?.invoke(it)
        }
    }

    Box(modifier = modifier) {
        AsyncImage(
            model = if (localImageFile != null) localImageFile else url,
            contentDescription = "ProfileImage",
            placeholder = painterResource(id = R.drawable.placeholder_default),
            error = painterResource(id = R.drawable.placeholder_default),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size)
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = CircleShape
                )
                .clip(CircleShape),
        )

        if (onUpdateProfileImage != null) {
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                IconButton(
                    onClick = { openPhotoActionBottomSheet = true },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Yellow,),
                    modifier = Modifier.offset(x = 10.dp, y = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CameraAlt,
                        contentDescription = "Camera"
                    )
                }
            }
        }
    }
}