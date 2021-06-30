package com.kekadoc.project.musician.feature.album.network.api

import com.kekadoc.project.musician.core.network.api.NetworkError

sealed interface NetworkResponse<Error, Result, Library> {
    val error: Error
    val result: Result
    val library: Library
}

interface AlbumCard {
    val id: Int
    val categoryIds: List<Int>
    val productIds: List<Int>
}

interface Album {
    val id: Int
    val parent: String?
    val name: String
    val cover: String?
    val coverUrl: String?
    val year: Int
    val price: Double
    val dir: String?
    val state: Int
    val peopleIds: List<Int>
    val duration: Int
    val trackCount: Int
    val isUserLikes: Boolean
}

interface People {
    val id: Int
    val name: String
    val dir: String?
    val typeName: String
    val productCount: Int
    val productChildCount: Int
    val genres: List<String>
    val coverFile: String?
    val coverUrl: String?
    val isUserLikes: Boolean
    val description: String?
}

interface Albums {
    val ids: List<Int>
}

interface Category {
    val id: Int
    val name: String
    val kzName: String
    val enName: String
    val haveChild: Int
    val child: List<String>
}

interface Track {
    val id: Int
    val parent: Int
    val name: String
    val cover: String
    val coverUrl: String
    val year: Int
    val price: Double
    val dir: String
    val state: Int
    val peopleIds: List<Int>
    val duration: Int
    val isUserLikes: Boolean
    val hasLyrics: Boolean
    val lyrics2: String?
    val fileUrl: String?
}

interface Library {
    val albums: Map<Int, Album>
    val peoples: Map<Int, People>
    val categories: Map<Int, Category>
    val tracks: Map<Int, Track>
}

interface AlbumServiceNewsResponse : NetworkResponse<NetworkError, Albums, Library> {
    override val error: NetworkError
    override val result: Albums
    override val library: Library
}
interface AlbumServiceCardResponse : NetworkResponse<NetworkError, AlbumCard, Library> {
    override val error: NetworkError
    override val result: AlbumCard
    override val library: Library
}

interface AlbumService {
    suspend fun load(pageNumber: Int, limit: Int): AlbumServiceNewsResponse
    suspend fun loadCard(productId: Int): AlbumServiceCardResponse
}