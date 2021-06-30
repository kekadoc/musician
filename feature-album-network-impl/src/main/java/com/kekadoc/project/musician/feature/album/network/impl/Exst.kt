package com.kekadoc.project.musician.feature.album.network.impl

import com.kekadoc.project.musician.feature.album.network.api.Album
import com.kekadoc.project.musician.feature.album.network.api.AlbumServiceCardResponse
import com.kekadoc.project.musician.feature.album.network.api.Library
import com.kekadoc.project.musician.feature.album.network.api.Track

fun AlbumServiceCardResponse.getAlbum(): Album = library.albums[result.id]!!

fun Track.getAuthors(library: Library): String {
    return peopleIds.mapNotNull { peopleId ->
        library.peoples[peopleId]?.name
    }.joinToString(separator = '\u00D7'.toString())
}