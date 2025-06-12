package com.mobiledev.randomuserapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.withStyle
import com.mobiledev.randomuserapp.ui.theme.SRHOrange

@Composable
fun HomeScreen(navController: NavController) {
    val paddingTopAppBarUI = 64
    val paddingUI = 16
    val noPaddingUI = 0
    val fontSizeBig = 48
    val fontSizeMedium = 16
    val fontSizeSmall = 14

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = paddingTopAppBarUI.dp, bottom = paddingUI.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Willkommen",
                    fontSize = fontSizeBig.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Random User App",
                    fontSize = fontSizeMedium.sp,
                    fontStyle = FontStyle.Italic,

                    color = Color.Gray
                )
                Text(
                    text = "von Csongor Olah",
                    fontSize = fontSizeMedium.sp,
                    fontStyle = FontStyle.Italic,

                    color = Color.Gray
                )
            }

        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)

        ) {
           Column(
               modifier = Modifier
                   .align(Alignment.Center)
                   .padding(start = paddingUI.dp, end = paddingUI.dp, top = noPaddingUI.dp, bottom = paddingUI.dp),
               horizontalAlignment = Alignment.CenterHorizontally
           ) {
               Button(
                   onClick = {navController.navigate("qr_scanner")},
                   colors = ButtonDefaults.buttonColors(
                       containerColor = SRHOrange,
                       contentColor = Color.White
                   ),
                   modifier = Modifier.fillMaxWidth()
               ) {
                   Text("QR-Code lesen")
               }

               Spacer(modifier = Modifier.height(paddingUI.dp))

               Button(
                   onClick = {navController.navigate("create_user")},
                   colors = ButtonDefaults.buttonColors(
                       containerColor = SRHOrange,
                       contentColor = Color.White
                   ),
                   modifier = Modifier.fillMaxWidth()
               ) {
                   Text("User anlegen")
               }

               Spacer(modifier = Modifier.height(paddingUI.dp))

               Button(
                   onClick = {navController.navigate("user_overview")},
                   colors = ButtonDefaults.buttonColors(
                       containerColor = SRHOrange,
                       contentColor = Color.White
                   ),
                   modifier = Modifier.fillMaxWidth()
               ) {
                   Text("Übersicht")
               }
           }

            val annotatedText = buildAnnotatedString {
                pushStringAnnotation(tag = "SETTINGS", annotation = "settings")
                withStyle(
                    style = SpanStyle(
                        color = SRHOrange,
                        fontSize = fontSizeSmall.sp,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("Einstellungen")
                }
                pop()
            }

            Text(
                text = annotatedText,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(paddingUI.dp)
                    .clickable {
                        navController.navigate("settings")
                    },
                style = TextStyle.Default
            )
        }
    }
}