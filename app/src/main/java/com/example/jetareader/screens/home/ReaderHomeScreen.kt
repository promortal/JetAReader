package com.example.jetareader.screens.home

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.jetareader.components.FABContent
import com.example.jetareader.components.ListCard
import com.example.jetareader.components.ReaderAppBar
import com.example.jetareader.components.TitleSection
import com.example.jetareader.model.MBook
import com.example.jetareader.navigation.ReaderScreens
import com.google.firebase.auth.FirebaseAuth


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController = NavController(LocalContext.current)) {
    Scaffold (
        topBar = { ReaderAppBar(title = "A.Reader", navController = navController) },
        floatingActionButton = {
            FABContent{
                navController.navigate(ReaderScreens.SearchScreen.name)
            }
        }
    ) { paddingValues ->
        Surface(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()) {
            HomeContent(navController)
        }

    }
}

@Composable
fun HomeContent(navController: NavController ) {

    val readingNow = listOf(
        MBook(id = "awsasx", title = "This Book", authors = "ALl of Us", notes = null)
    )

    val listOfBooks = listOf(
        MBook(id = "awsasx", title = "My Book", authors = "ALl of Us", notes = null),
        MBook(id = "bwsasx", title = "Your Book", authors = "ALl of Us", notes = null),
        MBook(id = "cwsasx", title = "Our Book", authors = "ALl of Us", notes = null),
        MBook(id = "dwsasx", title = "Their Book", authors = "ALl of Us", notes = null),
        MBook(id = "ewsasx", title = "Nobody's Book", authors = "ALl of Us", notes = null)
    )

    val email = FirebaseAuth.getInstance().currentUser?.email
    val currentUserName = if (!email.isNullOrEmpty())
        email.split("@")?.get(0) else "N/A"
    Column(modifier = Modifier.padding(2.dp),
        verticalArrangement = Arrangement.Top) {
        Row(modifier = Modifier.align(alignment = Alignment.Start)) {
            TitleSection(label = "Your reading\n activity right now...")
            Spacer(modifier = Modifier.fillMaxWidth(0.7f))
            Column {
                Icon(modifier = Modifier
                    .clickable {
                        navController.navigate(ReaderScreens.ReaderStatsScreen.name)
                    }
                    .size(45.dp),
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.secondary)
                Text(text = currentUserName,
                    modifier = Modifier.padding(2.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Red,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Clip)
                Divider()
            }
        }

        ReadingRightNow(readingNow, navController)

        TitleSection(label = "Reading List")

        BoolListArea(listOfBooks, navController)
    }
}

@Composable
fun ReadingRightNow(books: List<MBook>, navController: NavController) {
    BoolListArea(books, navController)
}

@Composable
fun BoolListArea(listOfBooks: List<MBook>, navController: NavController) {
    LazyRow {
        items(listOfBooks) {
            book -> ListCard(book) {
                Log.d("TAG","BoolistArea: $it")
            }
        }
    }
}