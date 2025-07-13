package dev.sleepy.qrtool.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dev.sleepy.qrtool.data.QrCodeEntity
import dev.sleepy.qrtool.repository.QrCodeRepository
import kotlinx.coroutines.launch

class QrCodeViewModel(private val repository: QrCodeRepository) : ViewModel() {

    val allQrCodes = repository.allQrCodes.asLiveData()

    fun insert(qrCode: QrCodeEntity) = viewModelScope.launch {
        repository.insertQrCode(qrCode)
    }
}

class QrCodeViewModelFactory(private val repository: QrCodeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QrCodeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QrCodeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}