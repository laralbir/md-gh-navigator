package com.laralbir.mdghnavigator

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.laralbir.mdghnavigator.core.datastore.UserPreferences
import com.laralbir.mdghnavigator.core.datastore.UserPreferencesRepository
import com.laralbir.mdghnavigator.core.designsystem.theme.AppThemeMode
import com.laralbir.mdghnavigator.core.designsystem.theme.MdGhNavigatorTheme
import com.laralbir.mdghnavigator.feature.library.ui.LibraryScreen
import com.laralbir.mdghnavigator.feature.reader.ui.ReaderScreen
import com.laralbir.mdghnavigator.feature.settings.ui.SettingsScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val userPrefs by preferencesRepository.userPreferences.collectAsState(initial = UserPreferences())
            val appThemeMode = when (userPrefs.themeMode.lowercase()) {
                "scholar" -> AppThemeMode.SCHOLAR
                "neon", "neon_dev" -> AppThemeMode.NEON_DEV
                else -> AppThemeMode.PAPER
            }

            MdGhNavigatorTheme(themeMode = appThemeMode) {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "library"
                ) {
                    composable("library") {
                        LibraryScreen(
                            onRepositoryClick = { repo ->
                                val encodedRepoId = Uri.encode(repo.id)
                                val initialDocPath = repo.startFilePath.ifBlank { "README.md" }
                                val docId = "${repo.id}:$initialDocPath"
                                val encodedDocId = Uri.encode(docId)
                                navController.navigate("reader/$encodedRepoId/$encodedDocId")
                            },
                            onSettingsClick = {
                                navController.navigate("settings")
                            }
                        )
                    }

                    composable(
                        route = "reader/{repoId}/{docId}",
                        arguments = listOf(
                            navArgument("repoId") { type = NavType.StringType },
                            navArgument("docId") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val encodedRepoId = backStackEntry.arguments?.getString("repoId").orEmpty()
                        val encodedDocId = backStackEntry.arguments?.getString("docId").orEmpty()
                        val repoId = Uri.decode(encodedRepoId)
                        val docId = Uri.decode(encodedDocId)

                        ReaderScreen(
                            documentId = docId,
                            repoId = repoId,
                            onBackClick = { navController.popBackStack() },
                            onNavigateToDocument = { newDocId ->
                                val newEncRepo = Uri.encode(repoId)
                                val newEncDoc = Uri.encode(newDocId)
                                navController.navigate("reader/$newEncRepo/$newEncDoc")
                            }
                        )
                    }

                    composable("settings") {
                        SettingsScreen(
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
