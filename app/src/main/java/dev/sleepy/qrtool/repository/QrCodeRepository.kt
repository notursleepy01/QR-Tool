package dev.sleepy.qrtool.repository

import dev.sleepy.qrtool.data.QrCodeDao
import dev.sleepy.qrtool.data.QrCodeEntity
import kotlinx.coroutines.flow.Flow

class QrCodeRepository(private val qrCodeDao: QrCodeDao) {
    val allQrCodes: Flow<List<QrCodeEntity>> = qrCodeDao.getAllQrCodes()

    suspend fun insertQrCode(qrCode: QrCodeEntity) {
        qrCodeDao.insertQrCode(qrCode)
    }
}