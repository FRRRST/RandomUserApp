package com.mobiledev.randomuserapp.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mobiledev.randomuserapp.ui.theme.SRHOrange
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import com.mobiledev.randomuserapp.utils.globalflags.AdminState.isAdmin
import com.mobiledev.randomuserapp.data.db.AppDatabase
import com.mobiledev.randomuserapp.data.remote.clearDatabase
import com.mobiledev.randomuserapp.data.remote.fillDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val fontSize = 48
    val paddingUI = 16
    val noPaddingUI = 0

    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }

    var showDialog by remember { mutableStateOf(false)}
    var deleteUserDialog by remember { mutableStateOf(false)}

    if(deleteUserDialog) {
        ClearDatabasePopup(
            onConfirm = {
                deleteUserDialog = false

                CoroutineScope(Dispatchers.IO).launch {
                     try {
                         val db = AppDatabase.getInstance(context)
                         val dao = db.userDao()

                         clearDatabase(dao)
                         withContext(Dispatchers.Main) {
                             snackbarHostState.showSnackbar("Datenbank wurde erfolgreich geleert.")
                         }
                     } catch (e: Exception) {
                         withContext(Dispatchers.Main) {
                             snackbarHostState.showSnackbar("Fehler beim Leeren der Datenbank.")
                         }
                     }
                }
            },
            onDismiss = {deleteUserDialog = false}
        )
    }

    if(showDialog) {
        FillDatabasePopup(
            onConfirm = {count ->
                showDialog = false

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val db = AppDatabase.getInstance(context)
                        val dao = db.userDao()

                        fillDatabase(count, dao)
                        withContext(Dispatchers.Main) {
                            snackbarHostState.showSnackbar("Datenbank wurde erfolgreich mit $count Usern befüllt.")
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            snackbarHostState.showSnackbar("Fehler beim Befüllen der Datenbank: ${e.message}")
                        }
                    }
                }

            },
            onDismiss = {showDialog = false}
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState)},
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Einstellungen",
                        fontSize = fontSize.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {navController.popBackStack()}) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                }
            )
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
                    onClick = {showDialog = true},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SRHOrange,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Datenbank füllen")
                }

                Spacer(modifier = Modifier.height(paddingUI.dp))

                Button(
                    onClick = {deleteUserDialog = true},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SRHOrange,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Datenbank leeren")
                }

                Spacer(modifier = Modifier.height(paddingUI.dp))

                Button(
                    onClick = {
                        isAdmin = !isAdmin
                        CoroutineScope(Dispatchers.Main).launch {
                            snackbarHostState.showSnackbar(if (isAdmin) "Du bist jetzt im Admin Modus" else "Du bist jetzt im User Modus")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SRHOrange,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isAdmin)"User werden" else "Admin werden")
                }
            }
        }
    }
}

@Composable
fun ClearDatabasePopup(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                colors = ButtonDefaults.textButtonColors(
                    contentColor = SRHOrange
                ),
                onClick = {
                    onConfirm()
                }) {
                Text("Fortfahren")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = SRHOrange
                )
            ) {
                Text("Abbrechen")
            }
        },
        title = {Text("Möchtest du wirklich die Datenbank leeren?")},
    )
}

@Composable
fun FillDatabasePopup(
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var input by remember {mutableStateOf("")}

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                colors = ButtonDefaults.textButtonColors(
                    contentColor = SRHOrange
                ),
                onClick = {
                val number = input.toIntOrNull()
                if (number != null && number > 0) {
                    onConfirm(number)
                }
            }) {
                Text("Los")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = SRHOrange
                )
            ) {
                Text("Abbrechen")
            }
        },
        title = {Text("Datenbank befüllen")},
        text = {
            Column {
                Text("Anzahl der zu erstellenden User:")
                OutlinedTextField(
                    value = input,
                    onValueChange = {input = it},
                    placeholder = {Text("z.B. 10")},
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = SRHOrange,
                        unfocusedIndicatorColor = Color.Gray,
                        cursorColor = SRHOrange,
                        focusedContainerColor = Color.Transparent
                    ),
                    keyboardOptions =  KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
                )
            }
        }
    )
}
