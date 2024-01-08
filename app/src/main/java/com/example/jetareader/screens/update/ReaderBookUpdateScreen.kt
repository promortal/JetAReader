package com.example.jetareader.screens.update

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.jetareader.R
import com.example.jetareader.components.InputField
import com.example.jetareader.components.RatingBar
import com.example.jetareader.components.ReaderAppBar
import com.example.jetareader.components.RoundedButton
import com.example.jetareader.components.showToast
import com.example.jetareader.data.DataOrException
import com.example.jetareader.model.MBook
import com.example.jetareader.navigation.ReaderScreens
import com.example.jetareader.screens.home.HomeScreenViewModel
import com.example.jetareader.utils.formatDate
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateScreen(navController: NavController, viewModel: HomeScreenViewModel = hiltViewModel(), bookItemId: String) {

    Scaffold(
        topBar = {
            ReaderAppBar(title = "Update Book",
                icon = Icons.Default.ArrowBack,
                showProfile = false,
                navController = navController) {
                navController.popBackStack()
            }
        }
    ) { paddingValues ->

        val bookInfo = produceState<DataOrException<List<MBook>,Boolean,Exception>>(
            initialValue = DataOrException(listOf(),true,Exception(""))) {
            value = viewModel.data.value
        }.value

        Surface(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()) {
            Column(modifier = Modifier.padding(top = 3.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Log.d("INFO","BookUpdateScreen: ${viewModel.data.value.data.toString()}")
                if (bookInfo.loading == true) {
                    LinearProgressIndicator()
                    bookInfo.loading = false
                } else {
                    Text(viewModel.data.value.data?.get(0)?.title.toString())

                    Surface(modifier = Modifier
                        .padding(2.dp)
                        .fillMaxWidth(),
                        shape = CircleShape,
                        shadowElevation = 4.dp) {
                        ShowBookUpdate(bookInfo = viewModel.data.value, bookItemId)
                    }

                    ShowSimpleForm(book = viewModel.data.value.data?.first{mBook ->
                        mBook.googleBookId == bookItemId
                    }!!, navController)

                }
            }
        }
    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ShowSimpleForm(book: MBook, navController: NavController) {

    val context = LocalContext.current

    val notesText = remember {
        mutableStateOf("")
    }

    val isStartedReading = remember {
        mutableStateOf(false)
    }

    val isFinishedReading = remember {
        mutableStateOf(false)
    }

    val ratingVal = remember {
        mutableStateOf(0)
    }

    SimpleForm(defaultValue = if (book.notes.toString().isNotEmpty()) book.notes.toString() else "No thoughts available.") {
        note ->
        notesText.value = note
    }

    Row(modifier = Modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start) {

        TextButton(onClick = {
            isStartedReading.value = true
        },
            enabled  = book.startedReading == null ) {

            if (book.startedReading == null) {
                if (!isStartedReading.value) {
                    Text("Started Reading")
                } else {
                    Text("Started Reading",
                        modifier = Modifier.alpha(0.6f),
                        color = Color.Red.copy(0.5f))
                }
            } else {
                Text("Started reading on: ${formatDate(book.startedReading!!)}")
            }


        }

        Spacer(modifier = Modifier.height(4.dp))

        TextButton(onClick = { isFinishedReading.value = true },
            enabled = book.finishedReading == null) {

            if (book.finishedReading == null) {
                if (!isFinishedReading.value) {
                    Text("Mark as Read")
                } else {
                    Text("Finished Reading")
                }
            } else {
                Text("Finished on: ${formatDate(book.finishedReading!!)}")
            }


        }

    }

    Text(text = "Rating", modifier = Modifier.padding(bottom = 3.dp))

    book.rating?.toInt().let {
        RatingBar(rating = it!!) {rating ->
            ratingVal.value = rating
        }
    }

    Row(modifier = Modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly) {

        val changedNotes = book.notes != notesText.value
        val changedRating = book.rating?.toInt() != ratingVal.value
        val isFinishedTimeStamp = if (isFinishedReading.value) Timestamp.now() else book.finishedReading
        val isStartedTimeStamp = if (isStartedReading.value) Timestamp.now() else book.startedReading

        val bookUpdate = changedNotes || changedRating || isFinishedReading.value || isStartedReading.value

        val bookToUpdate = hashMapOf(
            "finished_reading_at" to isFinishedTimeStamp,
            "started_reading_at" to isStartedTimeStamp,
            "rating" to ratingVal.value,
            "notes" to notesText.value
        ).toMap()

        RoundedButton(label = "Update") {
            if (bookUpdate) {
                FirebaseFirestore.getInstance()
                    .collection("books")
                    .document(book.id!!).update(bookToUpdate)
                    .addOnCompleteListener { task ->
//                        Log.d("TAG","ShowSimpleForm: ${task.result.toString()}")
                        showToast(context,"Book Updated Successfully!")
                        navController.navigate(ReaderScreens.HomeScreen.name)
                    }
                    .addOnFailureListener {
                        Log.w("Error", "Error updating document")
                    }
            }
        }

        Spacer(modifier = Modifier.width(50.dp))


        val openDialog = remember {
            mutableStateOf(false)
        }
        if (openDialog.value) {
            ShowAlertDialog(message = stringResource(id = R.string.sure) + "/n" + stringResource(id = R.string.action), openDialog) {
                FirebaseFirestore.getInstance().collection("books").document(book.id!!).delete()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            openDialog.value = false
                            showToast(context,"Book Deleted Successfully!")
                            navController.navigate(ReaderScreens.HomeScreen.name)
                        }
                    }
            }
        }
        RoundedButton(label = "Delete", onPress = {
            openDialog.value = true
        })

    }

}

@Composable
fun ShowAlertDialog(message: String,
                    openDialog: MutableState<Boolean>,
                    onYesPressed: () -> Unit) {
    if (openDialog.value) {
        AlertDialog(onDismissRequest = { openDialog.value = false },
            title = {Text("Delete")},
            text = {Text(message)},
            confirmButton = { TextButton(onClick = { onYesPressed.invoke() }) {
                Text("Yes")
            }},
            dismissButton = { TextButton(onClick = { openDialog.value = false}) {
                Text("No")
            }})
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SimpleForm(modifier: Modifier = Modifier,
               loaing: Boolean = false,
               defaultValue: String = "Great Book!",
               onSearch: (String) -> Unit) {
        Column() {
            val textFieldValue = rememberSaveable {
                mutableStateOf(defaultValue)
            }
            val keyboardController = LocalSoftwareKeyboardController.current
            var valid = remember(textFieldValue.value) {
                textFieldValue.value.trim().isNotEmpty()
            }
            
            InputField(modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(3.dp)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp),
                valueState = textFieldValue,
                labelId = "Enter your thoughts",
                enabled = true,
                onAction = KeyboardActions{
                    if (!valid) return@KeyboardActions
                    onSearch(textFieldValue.value.trim())
                    keyboardController?.hide()
                }
            )
        }

}

@Composable
fun ShowBookUpdate(bookInfo: DataOrException<List<MBook>, Boolean, Exception>, bookItemId: String) {
    Row() {
        Spacer(modifier = Modifier.width(43.dp))
        if (bookInfo.data != null) {
            Column(modifier = Modifier.padding(4.dp),
                verticalArrangement = Arrangement.Center) {
                CardListItem(book = bookInfo.data!!.first{mBook -> mBook.googleBookId == bookItemId}, onPressDetails = {})
            }
        }
    }
}

@Composable
fun CardListItem(book: MBook, onPressDetails: () -> Unit) {
    Card(modifier = Modifier
        .padding(
            start = 4.dp,
            end = 4.dp,
            top = 4.dp,
            bottom = 8.dp
        )
        .clip(RoundedCornerShape(20.dp))
        .clickable {},
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {

        Row(horizontalArrangement = Arrangement.Start){
            Image(painter = rememberAsyncImagePainter(model = book.photoUrl),
                contentDescription = null,
                modifier = Modifier
                    .height(100.dp)
                    .width(120.dp)
                    .padding(4.dp)
                    .clip(
                        RoundedCornerShape(
                            topStartPercent = 20,
                            topEndPercent = 20,
                            bottomEndPercent = 0,
                            bottomStartPercent = 0
                        )
                    ))

            Column() {
                Text(book.title.toString(),
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis)

                Text(book.authors.toString(),
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 2.dp, bottom = 0.dp),
                    style = MaterialTheme.typography.bodyMedium)

                Text(book.publishedDate.toString(),
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 0.dp, bottom = 8.dp),
                    style = MaterialTheme.typography.bodyMedium)
            }
        }

    }
}
