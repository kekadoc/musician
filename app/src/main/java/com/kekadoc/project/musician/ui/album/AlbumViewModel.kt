package com.kekadoc.project.musician.ui.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kekadoc.project.musician.core.util.ResultCallback
import com.kekadoc.project.musician.core.util.fail
import com.kekadoc.project.musician.core.util.run
import com.kekadoc.project.musician.core.util.success
import com.kekadoc.project.musician.feature.album.network.api.AlbumService
import com.kekadoc.project.musician.feature.album.network.api.AlbumServiceCardResponse
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AlbumViewModel(private val service: AlbumService) : ViewModel() {

    private val _card: MutableStateFlow<AlbumServiceCardResponse?> = MutableStateFlow(null)
    val albumCard = _card.asStateFlow()

    fun loadCard(id: Int, callback: ResultCallback<Int, AlbumServiceCardResponse>): Job {
        return viewModelScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) {
                    callback.run(id)
                }
                delay(1_000)
                val result = service.loadCard(id)
                withContext(Dispatchers.Main) {
                    _card.value = result
                    callback.success(result)
                }
            }catch (e: Throwable) {
                withContext(Dispatchers.Main) {
                    callback.fail(e)
                }
            }
        }
    }


}