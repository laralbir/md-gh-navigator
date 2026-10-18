package com.laralbir.mdghnavigator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.laralbir.mdghnavigator.core.designsystem.component.EmptyStateView
import com.laralbir.mdghnavigator.core.designsystem.component.MdTopAppBar
import com.laralbir.mdghnavigator.core.designsystem.theme.AppThemeMode
import com.laralbir.mdghnavigator.core.designsystem.theme.MdGhNavigatorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MdGhNavigatorTheme(themeMode = AppThemeMode.SCHOLAR) {
                Scaffold(
                    topBar = {
                        MdTopAppBar(
                            title = "MD GitHub Navigator",
                            subtitle = "Lector de Markdown para GitHub & Almacenamiento Local"
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        EmptyStateView(
                            icon = Icons.AutoMirrored.Filled.MenuBook,
                            title = "Bienvenido a MD GitHub Navigator",
                            message = "Añade un repositorio de GitHub o selecciona un directorio local para comenzar tu experiencia de lectura.",
                            actionLabel = "Añadir Repositorio"
                        )
                    }
                }
            }
        }
    }
}
