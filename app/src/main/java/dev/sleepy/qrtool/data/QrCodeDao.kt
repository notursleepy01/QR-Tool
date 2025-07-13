package dev.sleepy.qrtool.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QrCodeDao {
    @Insert
    suspend fun insertQrCode(qrCode: QrCodeEntity)

    @Query("SELECT * FROM qr_codes ORDER BY timestamp DESC")
    fun getAllQrCodes(): Flow<List<QrCodeEntity>>
}