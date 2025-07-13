package dev.sleepy.qrtool.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [QrCodeEntity::class], version = 1, exportSchema = false)
abstract class QrCodeDatabase : RoomDatabase() {
    abstract fun qrCodeDao(): QrCodeDao
}