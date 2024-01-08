package com.example.jetareader.screens.stats

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.sharp.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.jetareader.components.ReaderAppBar
import com.example.jetareader.model.MBook
import com.example.jetareader.navigation.ReaderScreens
import com.example.jetareader.screens.home.HomeScreenViewModel
import com.example.jetareader.utils.formatDate
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderStatsScreen(navController: NavController, viewModel:HomeScreenViewModel = hiltViewModel()) {
    var books: List<MBook>
    val currentUser = FirebaseAuth.getInstance().currentUser

    Scaffold(topBar = {
        ReaderAppBar(title = "Book Stats",
            icon = Icons.Default.ArrowBack,
            showProfile = false,
            navController = navController) {
            navController.navigate(ReaderScreens.HomeScreen.name)
        }
    }) { paddingValues ->
        Surface(modifier = Modifier
            .padding(paddingValues)
            .padding(8.dp)) {
            books = if (!viewModel.data.value.data.isNullOrEmpty()) {
                viewModel.data.value.data!!.filter{mBook -> mBook.userId == currentUser?.uid}
            } else {
                emptyList()
            }
            Column() {
                Row(modifier = Modifier
                    .height(45.dp)
                    .padding(2.dp)) {
                    Icon(imageVector = Icons.Sharp.Person,contentDescription = "icon")
                    Text(text = "Hi, ${currentUser?.email.toString().split("@")[0].uppercase()}")
                }

                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                    shape = CircleShape,
                    elevation = CardDefaults.cardElevation(5.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    val readBooksList: List<MBook> = if (!viewModel.data.value.data.isNullOrEmpty()) {
                        books.filter { mBook ->
                            (mBook.userId == currentUser?.uid) && (mBook.finishedReading != null)
                        }

                    }else {
                        emptyList()
                    }

                    val readingBooks = books.filter { mBook ->
                        (mBook.startedReading != null && mBook.finishedReading == null)
                    }

                    Column(modifier = Modifier.padding(start = 25.dp, top = 4.dp, bottom = 4.dp),
                        horizontalAlignment = Alignment.Start) {
                        Text(text = "Your Stats", style = MaterialTheme.typography.headlineMedium)
                        Divider()
                        Text(text = "You're reading: ${readingBooks.size} books")
                        Text(text = "You've read: ${readBooksList.size} books")

                    }
                }

                if (viewModel.data.value.loading == true) {
                    LinearProgressIndicator()
                }else {
                    Divider()

                    LazyColumn(modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        val readBooks: List<MBook> =
                            if (!viewModel.data.value.data.isNullOrEmpty()) {
                                viewModel.data.value.data!!.filter { mBook -> (mBook.userId == currentUser?.uid) && (mBook.finishedReading != null) }
                            } else {
                                emptyList()
                            }
                        items(items = readBooks) {book ->
                            BookRowStats(book = book)
                        }

                    }
                }

            }
        }

    }
}

@Composable
fun BookRowStats(book: MBook) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(5.dp),
        elevation = CardDefaults.cardElevation(5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(horizontalArrangement = Arrangement.Start) {
            val imageUrl = book.photoUrl.toString()
            if (imageUrl.isNotEmpty()) {
                Image(painter = rememberAsyncImagePainter(model = imageUrl), contentDescription = "Book Cover",
                    modifier = Modifier
                        .padding(4.dp)
                        .height(140.dp)
                        .width(100.dp))
            } else {
                Text("image missing", modifier = Modifier
                    .padding(4.dp)
                    .height(140.dp)
                    .width(100.dp))
            }
            Column(modifier = Modifier.padding(4.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(book.title.toString(),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    if (book.rating!! >= 4) {
                        Spacer(modifier = Modifier.fillMaxWidth(0.8f))
                        Icon(imageVector = Icons.Filled.ThumbUp,
                            tint = Color.Red.copy(0.5f),
                            contentDescription = "thumbs up")
                    } else {
                        Box{}
                    }
                }

                Text("Author: ${book.authors.toString()}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium
                )

                Text("Started: ${formatDate(book.startedReading!!)}",
                    softWrap = true,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium
                )

                Text("Finished: ${formatDate(book.finishedReading!!)}",
                    softWrap = true,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium
                )

            }
        }
    }
}