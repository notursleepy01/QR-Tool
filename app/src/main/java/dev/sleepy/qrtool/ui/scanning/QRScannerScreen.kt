package dev.sleepy.qrtool.ui.scanning

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.Result
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.BinaryBitmap
import com.google.zxing.PlanarYUVLuminanceSource
import java.util.EnumMap
import java.util.concurrent.Executors
import dev.sleepy.qrtool.util.ContentHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material3.FloatingActionButton
import androidx.compose.ui.Alignment
import androidx.camera.core.CameraControl
import androidx.compose.material.icons.filled.BatchPrediction
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Arrangement

@Composable
fun QRScannerScreen(onQrCodeScanned: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasCameraPermission by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) }
    var isBatchScanning by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        hasCameraPermission = it
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission) {
        CameraPreview(onQrCodeScanned = onQrCodeScanned, isBatchScanning = isBatchScanning, onToggleBatchScanning = { isBatchScanning = it })
    } else {
        Text("Camera permission is required to scan QR codes.")
    }
}

@Composable
fun CameraPreview(onQrCodeScanned: (String) -> Unit, isBatchScanning: Boolean, onToggleBatchScanning: (Boolean) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
    var isFlashlightOn by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                val previewView = PreviewView(it)
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(Executors.newSingleThreadExecutor(), QRCodeAnalyzer(onQrCodeScanned, context, isBatchScanning))
                    }

                try {
                    cameraProvider.unbindAll()
                    val camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                    )
                    cameraControl = camera.cameraControl
                } catch (exc: Exception) {
                    Log.e("QRScanner", "Use case binding failed", exc)
                }
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    cameraControl?.enableTorch(!isFlashlightOn)?.addListener({
                        isFlashlightOn = !isFlashlightOn
                    }, ContextCompat.getMainExecutor(context))
                }
            ) {
                Icon(if (isFlashlightOn) Icons.Filled.FlashlightOff else Icons.Filled.FlashlightOn, contentDescription = "Toggle Flashlight")
            }

            FloatingActionButton(
                onClick = {
                    onToggleBatchScanning(!isBatchScanning)
                }
            ) {
                Icon(if (isBatchScanning) Icons.Filled.Stop else Icons.Filled.BatchPrediction, contentDescription = "Toggle Batch Scanning")
            }
        }
    }
}

class QRCodeAnalyzer(private val onQrCodeScanned: (String) -> Unit, private val context: Context, private val isBatchScanning: Boolean) : ImageAnalysis.Analyzer {
    private val reader = MultiFormatReader().apply {
        val hints = EnumMap<DecodeHintType, Any>(DecodeHintType::class.java)
        hints[DecodeHintType.POSSIBLE_FORMATS] = arrayListOf(BarcodeFormat.QR_CODE)
        setHints(hints)
    }

    override fun analyze(imageProxy: androidx.camera.core.ImageProxy) {
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        val image = imageProxy.image

        if (image != null) {
            val buffer = image.planes[0].buffer
            val data = ByteArray(buffer.remaining())
            buffer.get(data)

            val source = PlanarYUVLuminanceSource(
                data,
                image.width,
                image.height,
                0,
                0,
                image.width,
                image.height,
                false
            )
            val bitmap = BinaryBitmap(HybridBinarizer(source))

            try {
                val result: Result = reader.decodeWithState(bitmap)
                onQrCodeScanned(result.text)
                ContentHandler.handleScannedContent(context, result.text)
                if (!isBatchScanning) {
                    imageProxy.close()
                    return
                }
            } catch (e: Exception) {
                // QR code not found or error decoding
            } finally {
                imageProxy.close()
            }
        }
    }
}