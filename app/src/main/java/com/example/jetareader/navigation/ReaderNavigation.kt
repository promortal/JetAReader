package com.example.jetareader.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.jetareader.screens.SplashScreen
import com.example.jetareader.screens.details.DetailScreen
import com.example.jetareader.screens.details.DetailsViewModel
import com.example.jetareader.screens.home.HomeScreen
import com.example.jetareader.screens.home.HomeScreenViewModel
import com.example.jetareader.screens.login.LoginScreen
import com.example.jetareader.screens.search.BookSearchViewModel
import com.example.jetareader.screens.search.SearchScreen
import com.example.jetareader.screens.stats.ReaderStatsScreen
import com.example.jetareader.screens.update.UpdateScreen

@Composable
fun ReaderNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = ReaderScreens.SplashScreen.name) {

        composable(ReaderScreens.SplashScreen.name) {
            SplashScreen(navController = navController)
        }

        composable(ReaderScreens.LoginScreen.name) {
            LoginScreen(navController = navController)
        }

        composable(ReaderScreens.HomeScreen.name) {
            val viewModel = hiltViewModel<HomeScreenViewModel>()
            HomeScreen(navController = navController, viewModel)
        }

        composable(ReaderScreens.SearchScreen.name) {
            val viewModel = hiltViewModel<BookSearchViewModel>()
            SearchScreen(navController = navController, viewModel)
        }

        val detailName = ReaderScreens.DetailScreen.name
        composable("$detailName/{bookId}", arguments = listOf(
            navArgument("bookId") {
                type = NavType.StringType
            }
        )) { backStackEntry ->
            val viewModel = hiltViewModel<DetailsViewModel>()
            backStackEntry.arguments?.getString("bookId").let {
                DetailScreen(navController = navController, viewModel = viewModel, bookId = it.toString())
            }
        }

        composable("${ReaderScreens.UpdateScreen.name}/{bookItemId}", arguments = listOf(
            navArgument("bookItemId") { type = NavType.StringType}
        )) { backStackEntry ->
            backStackEntry.arguments?.getString("bookItemId").let{
                UpdateScreen(navController = navController, bookItemId = it.toString())
            }

        }

        composable(ReaderScreens.ReaderStatsScreen.name) {
            ReaderStatsScreen(navController = navController)
        }
    }


}