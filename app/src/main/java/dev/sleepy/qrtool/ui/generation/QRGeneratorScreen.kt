package dev.sleepy.qrtool.ui.generation

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import dev.sleepy.qrtool.util.QrCodeUtil
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRGeneratorScreen(context: Context) {
    var textToEncode by remember { mutableStateOf("") }
    var generatedQrCodeBitmap by remember { mutableStateOf<Bitmap?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = textToEncode,
            onValueChange = { textToEncode = it },
            label = { Text("Enter text or URL") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            generatedQrCodeBitmap = QrCodeUtil.generateQrCode(textToEncode)
        }) {
            Text("Generate QR Code")
        }
        Spacer(modifier = Modifier.height(16.dp))
        generatedQrCodeBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Generated QR Code",
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                val file = File(context.externalCacheDir, "qr_code.png")
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                val uri = FileProvider.getUriForFile(context, "dev.sleepy.qrtool.fileprovider", file)
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share QR Code"))
            }) {
                Text("Share QR Code")
            }
        }
    }
}