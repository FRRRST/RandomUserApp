package com.mobiledev.randomuserapp.screens

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.mobiledev.randomuserapp.utils.qrcode.QRScannerARView
import com.mobiledev.randomuserapp.data.db.User
import com.mobiledev.randomuserapp.ui.theme.SRHOrange
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRScannerScreen(navController: NavController) {
    val noPadding = 0
    val fontSize = 48

    val context = LocalContext.current

    val overlays = remember {mutableStateMapOf<Int, UserOverlay>()}

    val cleanupInterval = 1000L
    val overlayTimeout = 1000L
    val onUserFound: (User, Rect) -> Unit = { user, rect ->
        overlays[user.id] = UserOverlay(
            user = user,
            rect = rect,
            lastSeenTime = System.currentTimeMillis()
        )
    }

    var hasPermission by remember { mutableStateOf(false)}

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {granted ->
        hasPermission = granted
    }

    LaunchedEffect(Unit) {
        while(true) {
            delay(cleanupInterval)
            val now = System.currentTimeMillis()
            overlays.entries.removeIf {now - it.value.lastSeenTime > overlayTimeout}
        }
    }

    LaunchedEffect(Unit) {
        if(ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            hasPermission = true
        } else {
            permissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    if(hasPermission) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent),
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "QR Scanner",
                            fontSize = fontSize.sp,
                            fontWeight = FontWeight.Bold,
                            color = SRHOrange
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {navController.popBackStack()},
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = Color.White,
                                containerColor = SRHOrange
                            )
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.background(Color.Transparent)
                )
            },
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(noPadding.dp)) {
                QRScannerARView(onUserFound = onUserFound)

                overlays.values.forEach {overlay ->
                    UserOverlayPanel(user = overlay.user, rect = overlay.rect, onClick = {navController.navigate("user_detail/${overlay.user.id}")})
                }


            }
            Column(modifier = Modifier.padding(innerPadding)) {} //Silence linter
        }

    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Kamerazugriff erforderlich.")
        }
    }
}

data class UserOverlay(
    val user: User,
    val rect: Rect,
    var lastSeenTime: Long
)

@Composable
fun UserOverlayPanel(user: User, rect: Rect, onClick: () -> Unit) {
    val paddingUI =  8
    val borderUI = 2
    val sizeUI = 64

    Box(
        modifier = Modifier
            .offset(
                x = rect.left.toInt().dp,
                y = rect.top.toInt().dp
            )
            .border(borderUI.dp, SRHOrange, MaterialTheme.shapes.medium)
            .background(Color.White, shape = MaterialTheme.shapes.medium)
            .clickable {
                onClick()
            }
            .padding(paddingUI.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = user.profilePicture,
                contentDescription = null,
                modifier = Modifier
                    .size(sizeUI.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(paddingUI.dp))

            Text(user.name, fontWeight = FontWeight.Bold)
            Text(user.email)
            user.phoneNumber?.let {Text(it)}
        }
    }
}