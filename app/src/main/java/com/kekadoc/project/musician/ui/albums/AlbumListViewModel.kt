package com.kekadoc.project.musician.ui.albums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.kekadoc.project.musician.feature.album.network.api.AlbumService

class AlbumListViewModel(service: AlbumService) : ViewModel() {

    val flow = Pager(PagingConfig(pageSize = 6)) {
        AlbumPagingSource(service)
    }.flow.cachedIn(viewModelScope)

}