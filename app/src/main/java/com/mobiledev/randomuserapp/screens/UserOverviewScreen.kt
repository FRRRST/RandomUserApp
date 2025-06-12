package com.mobiledev.randomuserapp.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.ui.graphics.Color
import com.mobiledev.randomuserapp.data.db.User
import com.mobiledev.randomuserapp.ui.theme.SRHOrange


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserOverviewScreen(navController: NavController, users: List<User>, onUserClick: (User) -> Unit) {
    val paddingUI = 16
    val noPaddingUI = 0
    val fontSizeUI = 48

    var searchQuery by remember { mutableStateOf("")}

    var showSortDialog by remember { mutableStateOf(false)}
    var sortBy by remember { mutableStateOf("name")}
    var ascending by remember { mutableStateOf(true)}

    val filteredUsers = users
        .filter {it.name.contains(searchQuery, ignoreCase = true)}
        .sortedWith(compareBy<User> {
            when(sortBy) {
                "lastname" -> it.name.split(" ").getOrNull(1)?.lowercase() ?: ""
                else -> it.name.split(" ").getOrNull(0)?.lowercase() ?: ""
            }
        }.let {if(ascending) it else it.reversed()})

    if(showSortDialog) {
        SortDialog(
            currentSortBy = sortBy,
            ascending = ascending,
            onDismiss = {showSortDialog = false},
            onConfirm = {newSortBy, isAscending ->
                sortBy = newSortBy
                ascending = isAscending
                showSortDialog = false
            }
        )
    }

    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Übersicht",
                        fontSize = fontSizeUI.sp,
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
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .padding(start = paddingUI.dp, end = paddingUI.dp, top = noPaddingUI.dp, bottom = paddingUI.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {searchQuery = it},
                        modifier = Modifier.weight(1f),
                        placeholder = {Text("Suchen...")},
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = SRHOrange,
                            unfocusedIndicatorColor = Color.Gray,
                            cursorColor = SRHOrange,
                            focusedContainerColor = Color.Transparent
                        )
                    )

                    IconButton(onClick = {showSortDialog = true}) {
                        Icon(Icons.Default.FilterList, contentDescription = "Sortieren")
                    }
                }

                Spacer(Modifier.height(paddingUI.dp))

                LazyColumn {
                    items(filteredUsers) { user ->
                        UserListItem(user, onClick = { onUserClick(user)})
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
fun UserListItem(user: User, onClick: () -> Unit) {
    val sizeUI = 48
    val borderUI = 2
    val bigPaddingUI = 16
    val mediumPaddingUI = 12
    val smallPaddingUI = 8

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable{onClick()}
            .padding(smallPaddingUI.dp)
    ) {
        AsyncImage(
            model = user.profilePicture,
            contentDescription = null,
            modifier = Modifier
                .size(sizeUI.dp)
                .clip(CircleShape)
                .border(borderUI.dp, SRHOrange, CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(mediumPaddingUI.dp))

        Text(
            text = user.name,
            modifier = Modifier.weight(1f),
            fontSize = bigPaddingUI.sp
        )

        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Details")
    }
}

@Composable
fun SortDialog(
    currentSortBy: String,
    ascending: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, Boolean) -> Unit
) {
    val paddingUI = 8

    var selectedSortBy by remember { mutableStateOf(currentSortBy)}
    var isAscending by remember { mutableStateOf(ascending)}

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {onConfirm(selectedSortBy, isAscending)},
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
        },
        title = {Text("Sortieren nach")},
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedSortBy == "name",
                        onClick = {selectedSortBy = "name"},
                        colors = RadioButtonDefaults.colors(
                            selectedColor = SRHOrange
                        )
                    )
                    Text("Vorname")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedSortBy == "lastname",
                        onClick = {selectedSortBy = "lastname"},
                        colors = RadioButtonDefaults.colors(
                            selectedColor = SRHOrange
                        )
                    )
                    Text("Nachname")
                }

                Spacer(modifier = Modifier.height(paddingUI.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isAscending,
                        onCheckedChange = {isAscending = it},
                        colors = CheckboxDefaults.colors(
                            checkedColor = SRHOrange
                        )
                    )
                    Text("Aufsteigend")
                }
            }
        }
    )
}