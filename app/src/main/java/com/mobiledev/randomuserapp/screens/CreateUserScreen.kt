package com.mobiledev.randomuserapp.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mobiledev.randomuserapp.data.db.AppDatabase
import com.mobiledev.randomuserapp.data.db.User
import com.mobiledev.randomuserapp.utils.generateSalt
import com.mobiledev.randomuserapp.utils.hashPassword
import com.mobiledev.randomuserapp.ui.theme.SRHOrange
import com.mobiledev.randomuserapp.utils.ImageUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUserScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    val paddingUI = 24
    val sizeUI = 120
    val borderUI = 2

    //Definitions for focussing:
    val (focusLastName, focusEmail, focusPassword, focusPhone, focusAddress, focusBirthday) = remember { FocusRequester.createRefs()}
    val birthdayInteraction = remember { MutableInteractionSource()}

    val context = LocalContext.current

    val dao = remember { AppDatabase.getInstance(context).userDao()}

    //Making sure that image gets actually saved
    var profileImageUri by remember { mutableStateOf<Uri?>(null)}
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        val imagePath = uri?.let { ImageUtils.saveImageToInternalStorage(context, it) }
        profileImageUri = Uri.fromFile(imagePath?.let { File(it) })
    }

    var firstName by remember { mutableStateOf("")}
    var lastName by remember { mutableStateOf("")}
    var email by remember { mutableStateOf("")}
    var password by remember { mutableStateOf("")}
    var phone by remember { mutableStateOf("")}
    var address by remember { mutableStateOf("")}
    var birthday by remember { mutableStateOf("")}
    var showBirthdayPicker by remember { mutableStateOf(false)}

    if(showBirthdayPicker) {
        DatePicker(
            onDismiss = {showBirthdayPicker = false},
            onConfirm = {
                birthday = it
                showBirthdayPicker = false
                focusPassword.requestFocus()
            }
        )
    }

    LaunchedEffect(birthdayInteraction) {
        birthdayInteraction.interactions.collect { interaction ->
            if (interaction is androidx.compose.foundation.interaction.PressInteraction.Release) {
                showBirthdayPicker = true
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "User anlegen",
                        fontSize = 48.sp,
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
    ) {innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)

        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingUI.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(sizeUI.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .border(borderUI.dp, SRHOrange, CircleShape)
                        .clickable {launcher.launch("image/*")},
                    contentAlignment = Alignment.Center
                ) {
                    if(profileImageUri != null) {
                        AsyncImage(
                            model = profileImageUri,
                            contentDescription = "Profile",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text("Foto", color = Color.DarkGray)
                    }
                }

                Spacer(modifier = Modifier.height(paddingUI.dp))

                OutlinedTextField(
                    value = firstName,
                    onValueChange = {firstName = it},
                    label = {Text("Vorname*")},
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = {focusLastName.requestFocus()}),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = SRHOrange,
                        unfocusedIndicatorColor = Color.Gray,
                        cursorColor = SRHOrange,
                        focusedLabelColor = SRHOrange,
                        focusedContainerColor = Color.Transparent,
                    )
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = {lastName = it},
                    label = {Text("Nachname*")},
                    modifier = Modifier
                        .focusRequester(focusLastName),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = {focusEmail.requestFocus()}),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = SRHOrange,
                        unfocusedIndicatorColor = Color.Gray,
                        cursorColor = SRHOrange,
                        focusedLabelColor = SRHOrange,
                        focusedContainerColor = Color.Transparent,
                    )
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = {email = it},
                    label = {Text("E-Mail*")},
                    modifier = Modifier
                        .focusRequester(focusEmail),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = {
                        focusBirthday.requestFocus()
                        showBirthdayPicker = true
                    }),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = SRHOrange,
                        unfocusedIndicatorColor = Color.Gray,
                        cursorColor = SRHOrange,
                        focusedLabelColor = SRHOrange,
                        focusedContainerColor = Color.Transparent,
                    )
                )

                OutlinedTextField(
                    value = birthday,
                    onValueChange = {},
                    label = {Text("Geburtstag*")},
                    readOnly = true,
                    modifier = Modifier
                        .focusRequester(focusBirthday),
                    interactionSource = birthdayInteraction,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = SRHOrange,
                        unfocusedIndicatorColor = Color.Gray,
                        cursorColor = SRHOrange,
                        focusedLabelColor = SRHOrange,
                        focusedContainerColor = Color.Transparent,
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {password = it},
                    label = {Text("Passwort*")},
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .focusRequester(focusPassword),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = {focusPhone.requestFocus()}),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = SRHOrange,
                        unfocusedIndicatorColor = Color.Gray,
                        cursorColor = SRHOrange,
                        focusedLabelColor = SRHOrange,
                        focusedContainerColor = Color.Transparent,
                    )
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = {phone = it},
                    label = {Text("Telefon")},
                    modifier = Modifier
                        .focusRequester(focusPhone),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = {focusAddress.requestFocus()}),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = SRHOrange,
                        unfocusedIndicatorColor = Color.Gray,
                        cursorColor = SRHOrange,
                        focusedLabelColor = SRHOrange,
                        focusedContainerColor = Color.Transparent,
                    )
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = {address = it},
                    label = {Text("Adresse")},
                    modifier = Modifier
                        .focusRequester(focusAddress),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = SRHOrange,
                        unfocusedIndicatorColor = Color.Gray,
                        cursorColor = SRHOrange,
                        focusedLabelColor = SRHOrange,
                        focusedContainerColor = Color.Transparent,
                    )
                )

                Spacer(modifier = Modifier.height(paddingUI.dp))

                Button(
                    onClick = {
                        val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

                        if(firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank() || birthday.isBlank()) {
                            CoroutineScope(Dispatchers.Main).launch {
                                snackbarHostState.showSnackbar("Bitte fülle alle Pflichtfelder aus.")
                            }
                        } else if (!email.matches(emailPattern)){
                            CoroutineScope(Dispatchers.Main).launch {
                                snackbarHostState.showSnackbar("Bitte gib eine gültige E-Mail-Adresse ein.")
                            }
                        } else {
                            val salt = generateSalt()
                            val user = User(
                                name = "$firstName $lastName",
                                email = email,
                                password = hashPassword(password, salt),
                                salt = salt,
                                phoneNumber = phone.takeIf {it.isNotBlank()},
                                address = address.takeIf {it.isNotBlank()},
                                birthday = birthday,
                                profilePicture = profileImageUri?.toString(),
                                qrCode = null
                            )
                            CoroutineScope(Dispatchers.IO).launch {
                                dao.insertAll(listOf(user))
                                withContext(Dispatchers.Main) {
                                    snackbarHostState.showSnackbar("Der User wurde erfolgreich angelegt.")
                                    navController.popBackStack()
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SRHOrange,
                        contentColor = Color.White
                    )
                ) {
                    Text("User erstellen")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    val formatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val formatted = formatter.format(Date(it))
                        onConfirm(formatted)
                    }
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = SRHOrange
                )
            ) {
                Text("Übernehmen")
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
        }
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                selectedDayContainerColor = SRHOrange,
                todayDateBorderColor = SRHOrange,
                dateTextFieldColors = TextFieldDefaults.colors(
                    focusedIndicatorColor = SRHOrange,
                    unfocusedIndicatorColor = Color.Gray,
                    cursorColor = SRHOrange,
                    focusedLabelColor = SRHOrange,
                    focusedContainerColor = Color.Transparent,

                    )
            )
        )
    }
}
