package com.smh.nxleave.design.sheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PhotoAlbum
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smh.nxleave.design.component.SheetLip
import com.smh.nxleave.design.component.modifier.bounceClick
import com.smh.nxleave.ui.theme.NXLeaveTheme
import com.smh.nxleave.ui.theme.spacing

enum class PhotoAction {
    CAMERA, LIBRARY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickPhotoActionSheet(
    onDismissRequest: () -> Unit,
    onSelected: (PhotoAction) -> Unit
) {
    val skipPartiallyExpanded by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded)
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
        windowInsets = WindowInsets(top = 0),
        dragHandle = null,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        PickPhotoActionSheet(
            onSelected = onSelected
        )
    }
}

@Composable
private fun PickPhotoActionSheet(
    onSelected: (PhotoAction) -> Unit
) {
    Column {
        SheetLip()

        Row(
            horizontalArrangement = Arrangement.spacedBy(space = spacing.space12),
            modifier = Modifier
                .padding(horizontal = spacing.horizontalSpace)
                .padding(
                    top = spacing.space12,
                    bottom = spacing.sheetBottomSpace
                )
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f / 1f)
                    .bounceClick()
                    .clickable {
                        onSelected(PhotoAction.CAMERA)
                    }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CameraAlt,
                        contentDescription = "Camera"
                    )

                    Text(
                        text = "Camera",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f / 1f)
                    .bounceClick()
                    .clickable {
                        onSelected(PhotoAction.LIBRARY)
                    }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = "Gallery"
                    )

                    Text(
                        text = "Gallery",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PickPhotoActionSheetPreview() {
    NXLeaveTheme {
        PickPhotoActionSheet(
            onSelected = {}
        )
    }
}