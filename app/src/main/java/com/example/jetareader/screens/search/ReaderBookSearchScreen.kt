package com.example.jetareader.screens.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.jetareader.components.InputField
import com.example.jetareader.components.ReaderAppBar
import com.example.jetareader.model.Item
import com.example.jetareader.navigation.ReaderScreens

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchScreen(navController: NavController, viewModel: BookSearchViewModel = hiltViewModel()) {

    Scaffold(topBar = {
        ReaderAppBar(
            title = "Search Books",
            icon = Icons.Default.ArrowBack,
            showProfile = false,
            navController = navController) {
            navController.popBackStack()
        }
    }) {paddingValues ->
        Surface(modifier = Modifier.padding(paddingValues)) {

            Column() {

                SearchForm(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    viewModel
                ) { query ->
                   viewModel.searchBooks(query)
                }

                Spacer(modifier = Modifier.height(10.dp))

                BookList(navController)

            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchForm(
    modifier: Modifier = Modifier,
    viewModel: BookSearchViewModel,
    loading: Boolean = false,
    hint: String = "Search",
    onSearch: (String) -> Unit = {}
) {
    Column() {
        val searchQueryState = rememberSaveable {
            mutableStateOf("")
        }
        val keyboardController = LocalSoftwareKeyboardController.current
        val valid = remember(searchQueryState.value) {
            searchQueryState.value.trim().isNotEmpty()
        }

        InputField(valueState = searchQueryState,
            labelId = "Search",
            enabled = true,
            imeAction = ImeAction.Search,
            onAction = KeyboardActions{
                if (!valid) return@KeyboardActions
                onSearch(searchQueryState.value.trim())
                searchQueryState.value = ""
                keyboardController?.hide()
            }
        )

    }
}

@Composable
fun BookList(navController: NavController, viewModel: BookSearchViewModel = hiltViewModel()) {

    val listOfBooks = viewModel.list
     if (viewModel.isLoading) {
         Row(horizontalArrangement = Arrangement.SpaceBetween) {
             LinearProgressIndicator()
             Text("Loading...")
         }

     } else {
         LazyColumn(modifier = Modifier.fillMaxSize(),
             contentPadding = PaddingValues(16.dp)
         ) { items(listOfBooks) { book ->
             BookRow(book, navController)
            }
         }
     }
}

@Composable
fun BookRow(book: Item, navController: NavController = NavController(
    LocalContext.current)) {
    Card(modifier = Modifier
        .clickable {
            navController.navigate(ReaderScreens.DetailScreen.name + "/${book.id}")
        }
        .fillMaxWidth()
        .padding(5.dp),
        elevation = CardDefaults.cardElevation(5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(horizontalArrangement = Arrangement.Start) {
            val imageUrl = book.volumeInfo.imageLinks.smallThumbnail
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
                Text(book.volumeInfo.title.toString(),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text("Author: ${book.volumeInfo.authors.toString()}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium
                )

                Text("Date: ${book.volumeInfo.publishedDate.toString()}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium
                )

                Text("Categories: ${book.volumeInfo.categories.toString()}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium
                )

            }
        }
    }
}