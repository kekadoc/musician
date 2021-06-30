package com.kekadoc.project.musician.feature.album.network.impl

import com.kekadoc.project.musician.feature.album.network.api.AlbumService
import retrofit2.Retrofit

class AlbumNetworkClient(retrofit: Retrofit) {
    val service: AlbumService = retrofit.create(AlbumServiceImpl::class.java)
}