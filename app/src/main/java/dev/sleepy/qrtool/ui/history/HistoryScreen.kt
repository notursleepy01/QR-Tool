package dev.sleepy.qrtool.ui.history

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.sleepy.qrtool.viewmodel.QrCodeViewModel
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

@Composable
fun HistoryScreen(qrCodeViewModel: QrCodeViewModel) {
    val qrCodes = qrCodeViewModel.allQrCodes.observeAsState(initial = emptyList()).value
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("QR Code History")
        Button(onClick = {
            exportHistory(context, qrCodes.map { "${it.content},${it.type},${it.timestamp}" })
        }) {
            Text("Export History")
        }
        LazyColumn {
            items(qrCodes) {
                Text("Content: ${it.content}, Type: ${it.type}, Time: ${it.timestamp}")
            }
        }
    }
}

fun exportHistory(context: Context, data: List<String>) {
    val fileName = "qr_code_history.csv"
    val file = File(context.externalCacheDir, fileName)
    FileOutputStream(file).use {
        fos ->
        data.forEach {
            line ->
            fos.write((line + "\n").toByteArray())
        }
    }

    val uri: Uri = FileProvider.getUriForFile(context, "dev.sleepy.qrtool.fileprovider", file)
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Export QR Code History"))
}