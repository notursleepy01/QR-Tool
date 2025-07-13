package dev.sleepy.qrtool.ui.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sleepy.qrtool.viewmodel.QrCodeViewModel

@Composable
fun HistoryScreen(qrCodeViewModel: QrCodeViewModel) {
    val qrCodes = qrCodeViewModel.allQrCodes.observeAsState(initial = emptyList()).value

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("QR Code History")
        LazyColumn {
            items(qrCodes) {
                Text("Content: ${it.content}, Type: ${it.type}, Time: ${it.timestamp}")
            }
        }
    }
}