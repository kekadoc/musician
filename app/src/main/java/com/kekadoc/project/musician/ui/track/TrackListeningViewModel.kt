package com.kekadoc.project.musician.ui.track

import androidx.lifecycle.ViewModel
import com.google.android.exoplayer2.Player
import com.kekadoc.project.musician.feature.album.network.api.AlbumServiceCardResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TrackListeningViewModel : ViewModel() {

    private var _player = MutableStateFlow<Player?>(null)
    val player = _player.asStateFlow()

    private var _card: AlbumServiceCardResponse? = null
    val card: AlbumServiceCardResponse?
        get() = _card

    fun setPlayer(player: Player, card: AlbumServiceCardResponse) {
        _player.value?.release()
        _player.value = player
        this._card = card
    }

}