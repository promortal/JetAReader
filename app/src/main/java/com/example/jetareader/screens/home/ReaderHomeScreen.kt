package com.example.jetareader.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.jetareader.components.FABContent
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
            FABContent{}
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
    }
}

@Composable
fun ReadingRightNow(books: List<MBook>, navController: NavController) {

}

@Preview
@Composable
fun ListCard(book: MBook = MBook(id="asdf", title = "Running", authors="Me and You", notes = "Hello World"),
             onPressDetails: (String) -> Unit = {}) {

    val context = LocalContext.current
    val resources = context.resources
    val displayMetrics = resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels / displayMetrics.density
    val spacing = 10.dp

    Card(shape = RoundedCornerShape(29.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier
            .padding(6.dp)
            .height(242.dp)
            .width(202.dp)
            .clickable { onPressDetails.invoke(book.title.toString()) }
    ) {
        Column(modifier = Modifier.width(screenWidth.dp - (spacing * 2)),
            horizontalAlignment = Alignment.Start) {

            Image(painter = rememberAsyncImagePainter(""), contentDescription = "book image",
                modifier = Modifier
                    .height(140.dp)
                    .width(100.dp)
                    .padding(4.dp))

            Spacer(modifier = Modifier.width(50.dp))

            Row(horizontalArrangement = Arrangement.Center) {

            }

        }
    }
}