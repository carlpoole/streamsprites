package codes.carl.streamsprites.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ConnectionViewModel: ViewModel() {
    private val _connectionStatus = MutableLiveData<Boolean>()
    val connectionStatus: LiveData<Boolean> get() = _connectionStatus

    fun setConnectionStatus(isConnected: Boolean) {
        _connectionStatus.value = isConnected
    }
}