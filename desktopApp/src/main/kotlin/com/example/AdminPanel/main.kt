package com.example.AdminPanel

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.window.singleWindowApplication
import com.example.AdminPanel.ui.components.AppDialog
import java.awt.Dimension


fun main() = application {
    var closeTheApp by remember { mutableStateOf(false) }
    val state = rememberWindowState(
        width = 1200.dp,
        height = 800.dp
    )

    val customIcon = try {
        useResource("sejong_logo.png") { inputStream ->
            BitmapPainter(loadImageBitmap(inputStream))
        }
    } catch (e: Exception) {
        null
    }
    Window(
        onCloseRequest = {closeTheApp = true},
        title = "Sejong Admin Panel",
        state = state,
        icon = customIcon
    ) {

        LaunchedEffect(Unit) {
            window.minimumSize = Dimension(1400, 800)
        }

        App()

        if (closeTheApp){
            AppDialog(
                title = "Close the app?",
                message = "Do you really want to close the application?",
                onClose = {closeTheApp=false},
                confirmText = "Exit" ,
                dismissText =  "Cancel",
                onOkClick = {exitApplication() }
            )
        }


    }
}