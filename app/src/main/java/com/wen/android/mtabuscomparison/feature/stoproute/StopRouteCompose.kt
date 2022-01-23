package com.wen.android.mtabuscomparison.feature.stoproute

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun StopRouteCard(
    stopName: String,
    stopCode: String,
    @DrawableRes rulerDrawable: Int,
    @DrawableRes circleDrawable: Int,
    hideFirstLine: Boolean,
    hideLastLine: Boolean,
    onClickCard: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClickCard(stopCode) }) {
        val icons = Icons.Outlined
        Column(
            Modifier
                .width(24.dp)
                .height(48.dp)
        ) {
            if (!hideFirstLine) {
                Icon(
                    painter = painterResource(id = rulerDrawable),
                    contentDescription = "route icon",
                    modifier = Modifier
                        .size(16.dp)
                        .padding(0.dp)
                )
            } else {
                Text(
                    text = "",
                    modifier = Modifier.height(16.dp)
                )
            }
            Icon(
                painter = painterResource(id = circleDrawable),
                contentDescription = "route icon",
                modifier = Modifier
                    .size(16.dp)
                    .padding(0.dp)
            )
            if (!hideLastLine) {
                Icon(
                    painter = painterResource(id = rulerDrawable),
                    contentDescription = "route icon",
                    modifier = Modifier
                        .padding(0.dp)
                        .size(16.dp)
                )
            }

        }
        Text(text = stopName, modifier = Modifier.padding(start = 32.dp))
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun SnackbarScreen(message: String) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    scope.launch {
        snackbarHostState.showSnackbar(message)
    }
    
    SnackbarHost(hostState = snackbarHostState)

}