package com.mobiledev.randomuserapp.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.material3.*
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.mobiledev.randomuserapp.utils.globalflags.AdminState.isAdmin
import com.mobiledev.randomuserapp.ui.theme.SRHOrange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.net.toUri
import com.mobiledev.randomuserapp.data.db.AppDatabase
import com.mobiledev.randomuserapp.data.db.User
import com.mobiledev.randomuserapp.utils.generateSalt
import com.mobiledev.randomuserapp.utils.hashPassword
import com.mobiledev.randomuserapp.utils.ImageUtils
import com.mobiledev.randomuserapp.utils.qrcode.QRGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailsScreen(navController: NavController, userId: Int) {
    val sizeBigUI = 300
    val sizeSmall = 200
    val fontSize = 48
    val paddingBigUI = 24
    val paddingMediumUI = 16
    val paddingSmallUI = 8
    val noPaddingUI = 0
    val borderUI = 2

    var showImageDialog by remember { mutableStateOf(false)}
    var showQRCodeDialog by remember { mutableStateOf(false)}
    var showEditDialog by remember { mutableStateOf(false)}

    var user by remember { mutableStateOf<User?>(null)}

    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(userId) {
        user = AppDatabase.getInstance(context).userDao().getUserById(userId)
    }

    if(showImageDialog) {
        Dialog(onDismissRequest = {showImageDialog = false}) {
            Box(
                modifier = Modifier
                    .background(Color.Black, shape = MaterialTheme.shapes.medium)
                    .padding(paddingSmallUI.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    IconButton(onClick = { showImageDialog = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }

                    AsyncImage(
                        model = user?.profilePicture,
                        contentDescription = "Full Image",
                        modifier = Modifier
                            .size(sizeBigUI.dp)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }

    if(showQRCodeDialog) {
        val qrBitmap = QRGenerator.generateQRCode(user?.id.toString())
        Dialog(onDismissRequest = {showQRCodeDialog = false}) {
            Box(
                modifier = Modifier
                    .background(Color.Black, shape = MaterialTheme.shapes.medium)
                    .padding(paddingSmallUI.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    IconButton(onClick = {showQRCodeDialog = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }

                    qrBitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "QR-Code-Image",
                            modifier = Modifier
                                .size(sizeBigUI.dp)
                                .padding(top = paddingSmallUI.dp)
                        )
                    }
                }
            }
        }
    }

    if(showEditDialog) {
        user?.let {
            EditUserDialog(
                user = it,
                onDismiss = {showEditDialog = false},
                onSave = {updatedUser ->
                    CoroutineScope(Dispatchers.IO).launch {
                        AppDatabase.getInstance(context).userDao().update(updatedUser)
                        val refreshedUser = AppDatabase.getInstance(context).userDao().getUserById(userId)
                        withContext(Dispatchers.Main) {
                            showEditDialog = false
                            user = refreshedUser
                            snackbarHostState.showSnackbar("Der User wurde erfolgreich aktualisiert.")
                        }
                    }
                }
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState)},
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "User Details",
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
    ) {innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)

        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .padding(start = paddingMediumUI.dp, end = paddingMediumUI.dp, top = noPaddingUI.dp, bottom = paddingMediumUI.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = user?.profilePicture,
                    contentDescription = "Profilbild",
                    modifier = Modifier
                        .size(sizeSmall.dp)
                        .clip(CircleShape)
                        .border(borderUI.dp, SRHOrange, CircleShape)
                        .clickable {showImageDialog = true},
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(paddingBigUI.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(borderUI.dp, SRHOrange, MaterialTheme.shapes.medium)
                        .padding(paddingMediumUI.dp)
                        .clickable {if (isAdmin) showEditDialog = true}
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Row {
                            Text(
                                text = "Name: ",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            user?.let {
                                Text(
                                    text = it.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            }
                        }
                        Row {
                            Text(
                                text = "E-Mail: ",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            user?.let {
                                Text(
                                    text = it.email,
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            }
                        }
                        Row {
                            Text(
                                text = "Geburtstag: ",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            user?.let {
                                Text(
                                    text = it.birthday,
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            }
                        }
                        Row {
                            Text(
                                text = "Telefon: ",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            user?.phoneNumber?.let {
                                Text(it, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(paddingBigUI.dp))

                IconButton(
                    onClick = {showQRCodeDialog = true},
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = Color.White,
                        containerColor = SRHOrange
                    )
                ) {
                    Icon(Icons.Default.QrCode2, contentDescription = "QR-Code")
                }

                if(isAdmin) {
                    Spacer(modifier = Modifier.height(paddingBigUI.dp))
                    Text(
                        text = "Du bist im Admin Modus",
                        color = SRHOrange,
                        fontWeight = FontWeight.ExtraBold,
                        fontStyle = FontStyle.Italic
                    )
                }

            }
        }
    }
}

@Composable
fun EditUserDialog(
    user: User,
    onDismiss: () -> Unit,
    onSave: (User) -> Unit
) {
    val paddingBigUI = 24
    val paddingMediumUI = 16
    val paddingSmallUI = 8
    val sizeUI = 120
    val borderUI = 2

    val birthdayInteraction = remember { MutableInteractionSource() }

    val (focusLastName, focusEmail, focusPassword, focusPhone, focusAddress, focusBirthday) = remember { FocusRequester.createRefs()}

    val localSnackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    var profileImageUri by remember {mutableStateOf(user.profilePicture?.toUri())}
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {uri -> profileImageUri = uri}

    var firstName by remember { mutableStateOf(user.name.split(" ").firstOrNull() ?: "")}
    var lastName by remember {mutableStateOf(user.name.split(" ").drop(1).joinToString(" "))}
    var email by remember { mutableStateOf(user.email)}
    var birthday by remember { mutableStateOf(user.birthday)}
    var password by remember {mutableStateOf("")}
    var phone by remember { mutableStateOf(user.phoneNumber ?: "")}
    var address by remember { mutableStateOf(user.address ?: "")}

    var showBirthdayPicker by remember { mutableStateOf(false)}

    if(showBirthdayPicker) {
        DatePicker(
            onConfirm = {
                birthday = it
                showBirthdayPicker = false
            },
            onDismiss = { showBirthdayPicker = false }
        )
    }

    LaunchedEffect(birthdayInteraction) {
        birthdayInteraction.interactions.collect { interaction ->
            if (interaction is androidx.compose.foundation.interaction.PressInteraction.Release) {
                showBirthdayPicker = true
            }
        }
    }

        Dialog(onDismissRequest = onDismiss) {
            Scaffold(
                snackbarHost = {SnackbarHost(localSnackbarHostState)},
                containerColor = Color.Transparent
            ) {innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {} //Silent Linter
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = paddingSmallUI.dp,
                    modifier = Modifier.padding(paddingMediumUI.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(paddingBigUI.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(sizeUI.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                                .border(borderUI.dp, SRHOrange, CircleShape)
                                .clickable{launcher.launch("image/*")},
                            contentAlignment = Alignment.Center
                        ) {
                            if(profileImageUri != null) {
                                AsyncImage(
                                    model = profileImageUri,
                                    contentDescription = "Profile Picture",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Text("Foto", color = Color.DarkGray)
                            }
                        }

                        Spacer(modifier = Modifier.height(paddingMediumUI.dp))

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
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = SRHOrange,
                                unfocusedIndicatorColor = Color.Gray,
                                cursorColor = SRHOrange,
                                focusedLabelColor = SRHOrange,
                                focusedContainerColor = Color.Transparent,
                            ),
                            modifier = Modifier
                                .focusRequester(focusEmail),
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = {
                                focusBirthday.requestFocus()
                                showBirthdayPicker = true
                            })
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
                            label = {Text("Neues Passwort")},
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
                            ),
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

                        Spacer(modifier = Modifier.height(paddingMediumUI.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TextButton(
                                onClick = onDismiss,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = SRHOrange
                                )
                            ) {
                                Text("Abbrechen")
                            }
                            Button(onClick = {
                                val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

                                if(firstName.isBlank() || lastName.isBlank() || email.isBlank() || birthday.isBlank()) {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        localSnackbarHostState.showSnackbar("Bitte fülle alle Pflichtfelder aus.")
                                    }
                                } else if (!email.matches(emailPattern)){
                                    CoroutineScope(Dispatchers.Main).launch {
                                        localSnackbarHostState.showSnackbar("Bitte gib eine gültige E-Mail-Adresse ein.")
                                    }
                                } else {
                                    val salt = generateSalt()
                                    CoroutineScope(Dispatchers.IO).launch {
                                        val savedImageUri = profileImageUri?.let { ImageUtils.saveImageToInternalStorage(context, it)}

                                        val updatedUser = user.copy(
                                            name = "$firstName $lastName",
                                            email = email,
                                            birthday = birthday,
                                            phoneNumber = phone.takeIf{it.isNotBlank()},
                                            address = address.takeIf{it.isNotBlank()},
                                            profilePicture = savedImageUri ?: user.profilePicture,
                                            password = if(password.isNotBlank()) hashPassword(password, salt) else user.password,
                                            salt = if(password.isNotBlank()) salt else user.salt
                                        )
                                        withContext(Dispatchers.Main) {
                                            onSave(updatedUser)
                                        }
                                    }
                                }
                            },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = SRHOrange
                                )
                            ) {
                                Text("Speichern")
                            }
                        }
                    }
                }
            }
    }
}
