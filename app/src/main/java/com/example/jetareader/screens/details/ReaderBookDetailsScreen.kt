package com.example.jetareader.screens.details

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.jetareader.components.ReaderAppBar
import com.example.jetareader.components.RoundedButton
import com.example.jetareader.data.Resource
import com.example.jetareader.model.Item
import com.example.jetareader.model.MBook
import com.example.jetareader.navigation.ReaderScreens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController: NavController, bookId: String, viewModel: DetailsViewModel = hiltViewModel()) {

    Scaffold(topBar = {
        ReaderAppBar(title = "Book Details",
            icon = Icons.Default.ArrowBack,
            showProfile = false,
            navController = navController)
        {
            navController.popBackStack()
        }
    }) { paddingValues ->
        Surface(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()) {
            Column(modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally) {

                val bookInfo = produceState<Resource<Item>>(initialValue = Resource.Loading()) {
                    value = viewModel.getBookInfo(bookId = bookId)
                }.value

                if (bookInfo.data == null) {
                    Row {
                        LinearProgressIndicator()
                        Text("Loading...")
                    }
                } else {
                    ShowBookDetails(bookInfo, navController)
                }
            }
        }
    }


}

@Composable
fun ShowBookDetails(bookInfo: Resource<Item>, navController: NavController) {
    val bookData = bookInfo.data!!.volumeInfo
    val googleBookId = bookInfo.data!!.id


    val imageUrl = bookData.imageLinks.smallThumbnail
    if (imageUrl.isNotEmpty()) {
        Image(modifier = Modifier
            .clip(shape = CircleShape)
            .height(90.dp)
            .width(90.dp)
            .border(width = 1.dp, color = Color.LightGray, shape = CircleShape),
            painter = rememberAsyncImagePainter(model = imageUrl), contentDescription = "thumbnail")
    }
    
    Spacer(modifier = Modifier.height(25.dp))

    Text(bookData.title, style = MaterialTheme.typography.headlineMedium)

    Text("Authors: ${bookData.authors}")

    Text("Page Count: ${bookData.pageCount}")

    Text("Categories: ${bookData.categories}",
        maxLines = 3,
        overflow = TextOverflow.Ellipsis)

    Text("Published: ${bookData.publishedDate}")

    val localDims = LocalContext.current.resources.displayMetrics

    Box(modifier = Modifier
        .padding(15.dp)
        .border(width = 1.dp, color = Color.DarkGray).height(localDims.heightPixels.dp.times(0.09f))) {
        LazyColumn() {
            item {
                Text(
                    HtmlCompat.fromHtml(bookData.description, HtmlCompat.FROM_HTML_MODE_COMPACT).toString(),
                    modifier = Modifier.padding(10.dp))
            }
        }
    }

    Row(modifier = Modifier.padding(top = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround) {
        RoundedButton("Save") {

            val book = MBook(
                title = bookData.title,
                authors = bookData.authors.toString(),
                description = bookData.description,
                categories = bookData.categories.toString(),
                notes = "",
                photoUrl = bookData.imageLinks.thumbnail,
                publishedDate = bookData.publishedDate,
                rating = 0.0,
                googleBookId = googleBookId,
                userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
            )
            SaveToFirebase(book, navController)
        }

        Spacer(modifier = Modifier.width(25.dp))

        RoundedButton("Cancel") {
            navController.popBackStack()
        }
    }
}

fun SaveToFirebase(book: MBook, navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val dbCollection = db.collection("books")

    if (book.toString().isNotEmpty()) {
        dbCollection.add(book)
            .addOnSuccessListener { documentRef ->
                val docId = documentRef.id
                dbCollection.document(docId)
                    .update(hashMapOf("id" to docId) as Map<String, Any>)
                    .addOnCompleteListener {
                        task ->
                        if (task.isSuccessful) {
                            navController.navigate(ReaderScreens.HomeScreen.name)
                        }
                    }
            }
            .addOnFailureListener {
                Log.w("TAG","SaveToFirebase: Error updating doc",it)
            }
    } else {

    }
}
