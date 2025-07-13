package dev.sleepy.qrtool.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.widget.Toast

object ContentHandler {

    fun handleScannedContent(context: Context, content: String) {
        when {
            content.startsWith("http://") || content.startsWith("https://") -> openUrl(context, content)
            content.startsWith("mailto:") -> sendEmail(context, content)
            content.startsWith("tel:") -> dialPhoneNumber(context, content)
            content.startsWith("WIFI:") -> connectToWifi(context, content)
            else -> copyToClipboard(context, content)
        }
    }

    private fun openUrl(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open URL: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendEmail(context: Context, emailUri: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse(emailUri)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not send email: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun dialPhoneNumber(context: Context, phoneUri: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse(phoneUri)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not dial number: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun connectToWifi(context: Context, wifiContent: String) {
        // This is a simplified example. Connecting to Wi-Fi programmatically is complex
        // and often requires system permissions (CHANGE_WIFI_STATE, ACCESS_FINE_LOCATION)
        // and specific Android versions/APIs. For a full implementation, consider
        // using WifiManager and handling permissions carefully.
        Toast.makeText(context, "Wi-Fi content detected: $wifiContent (Manual connection may be required)", Toast.LENGTH_LONG).show()
        // Example parsing (simplified):
        val ssid = "SSID:" + wifiContent.substringAfter("S:").substringBefore(";")
        val password = "Password:" + wifiContent.substringAfter("P:").substringBefore(";")
        Toast.makeText(context, "SSID: $ssid, Password: $password", Toast.LENGTH_LONG).show()
    }

    private fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("QR Code Content", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
    }
}