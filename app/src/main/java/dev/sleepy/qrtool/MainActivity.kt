package dev.sleepy.qrtool

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Room
import dev.sleepy.qrtool.data.QrCodeDatabase
import dev.sleepy.qrtool.repository.QrCodeRepository
import dev.sleepy.qrtool.ui.theme.QRToolTheme
import dev.sleepy.qrtool.viewmodel.QrCodeViewModel
import dev.sleepy.qrtool.viewmodel.QrCodeViewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.ViewModelProvider
import dev.sleepy.qrtool.ui.generation.QRGeneratorScreen
import dev.sleepy.qrtool.ui.scanning.QRScannerScreen
import dev.sleepy.qrtool.ui.history.HistoryScreen
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.sleepy.qrtool.data.QrCodeEntity
import java.util.Date

class MainActivity : ComponentActivity() {
    private lateinit var qrCodeViewModel: QrCodeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = Room.databaseBuilder(
            applicationContext,
            QrCodeDatabase::class.java,
            "qr_code_db"
        ).build()
        val repository = QrCodeRepository(database.qrCodeDao())
        val factory = QrCodeViewModelFactory(repository)
        qrCodeViewModel = ViewModelProvider(this, factory).get(QrCodeViewModel::class.java)

        setContent {
            QRToolTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination
                            NavigationBarItem(
                                icon = { Icon(Icons.Filled.QrCode, contentDescription = "Generate") },
                                label = { Text("Generate") },
                                selected = currentDestination?.route == "generator",
                                onClick = { navController.navigate("generator") }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Filled.CameraAlt, contentDescription = "Scan") },
                                label = { Text("Scan") },
                                selected = currentDestination?.route == "scanner",
                                onClick = { navController.navigate("scanner") }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Filled.History, contentDescription = "History") },
                                label = { Text("History") },
                                selected = currentDestination?.route == "history",
                                onClick = { navController.navigate("history") }
                            )
                        }
                    }
                ) {
                    innerPadding ->
                    NavHost(navController = navController, startDestination = "generator", modifier = Modifier.padding(innerPadding)) {
                        composable("generator") {
                            QRGeneratorScreen()
                        }
                        composable("scanner") {
                            QRScannerScreen(onQrCodeScanned = {
                                scannedContent ->
                                qrCodeViewModel.insert(QrCodeEntity(content = scannedContent, type = "scanned", timestamp = Date().time))
                                // Optionally navigate to a detail screen or history after scan
                            })
                        }
                        composable("history") {
                            HistoryScreen(qrCodeViewModel = qrCodeViewModel)
                        }
                    }
                }
            }
        }
    }
}

