package com.kekadoc.project.musician.feature.album.network.impl

import com.google.gson.annotations.SerializedName
import com.kekadoc.project.musician.core.network.api.NetworkError
import com.kekadoc.project.musician.feature.album.network.api.*
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

val trackUrl = listOf(
    "https://enazadevkz.cdnvideo.ru/tank3/medialand/2021_05_12/1.mp3",
    "https://enazadevkz.cdnvideo.ru/tank1/sony/A10301A0004574902L_20210331042345765/resources/ad5a61f35b99.mp3"
)

@Serializable
data class AlbumCardImpl(
    @field:SerializedName("productId")
    override val id: Int,
    @field:SerializedName("categoryIds")
    override val categoryIds: List<Int>,
    @field:SerializedName("productChildIds")
    override val productIds: List<Int>
) : AlbumCard

@Serializable
data class AlbumImpl(
    override val id: Int,
    override val parent: String?,
    override val name: String,
    override val cover: String?,
    override val coverUrl: String?,
    override val year: Int,
    override val price: Double,
    override val dir: String?,
    override val state: Int,
    override val peopleIds: List<Int>,
    override val duration: Int,
    override val trackCount: Int,
    override val isUserLikes: Boolean
) : Album {
    init {
        println("AlbumImpl: $this")
    }
}

@Serializable
data class PeopleImpl(
    override val id: Int,
    override val name: String,
    override val dir: String?,
    override val typeName: String,
    override val productCount: Int,
    override val productChildCount: Int,
    override val genres: List<String>,
    @field:SerializedName("cover_file")
    override val coverFile: String?,
    override val coverUrl: String?,
    override val isUserLikes: Boolean,
    override val description: String?
) : People

@Serializable
data class AlbumsImpl(
    @field:SerializedName("albums")
    override val ids: List<Int> = emptyList()
) : Albums

@Serializable
data class CategoryImpl(
    override val id: Int,
    override val name: String,
    @field:SerializedName("kz_name")
    override val kzName: String,
    @field:SerializedName("en_name")
    override val enName: String,
    override val haveChild: Int,
    override val child: List<String>
) : Category

@Serializable
data class TrackImpl(
    override val id: Int = 0,
    override val parent: Int = 0,
    override val name: String = "",
    override val cover: String = "",
    override val coverUrl: String = "",
    override val year: Int = 0,
    override val price: Double = 0.0,
    override val dir: String = "",
    override val state: Int = 0,
    override val peopleIds: List<Int> = emptyList(),
    override val duration: Int = 0,
    override val isUserLikes: Boolean = false,
    override val hasLyrics: Boolean = false,
    override val lyrics2: String? = null,
    override val fileUrl: String? = trackUrl.random()
) : Track

@Serializable
data class LibraryImpl(
    @field:SerializedName("album")
    override val albums: Map<Int, AlbumImpl> = emptyMap(),
    @field:SerializedName("people")
    override val peoples: Map<Int, PeopleImpl> = emptyMap(),
    @field:SerializedName("category")
    override val categories: Map<Int, CategoryImpl> = emptyMap(),
    @field:SerializedName("track")
    override val tracks: Map<Int, TrackImpl> = emptyMap()
) : Library

@Serializable
data class NetworkErrorImpl(override val code: Int,
                            override val message: String?) : NetworkError

@Serializable
data class AlbumServiceNewsResponseImpl(
    @field:SerializedName("error")
    override val error: NetworkErrorImpl,
    @field:SerializedName("response")
    override val result: AlbumsImpl,
    @field:SerializedName("collection")
    override val library: LibraryImpl
) : AlbumServiceNewsResponse

@Serializable
data class AlbumServiceCardResponseImpl(
    @field:SerializedName("error")
    override val error: NetworkErrorImpl,
    @field:SerializedName("response")
    override val result: AlbumCardImpl,
    @field:SerializedName("collection")
    override val library: LibraryImpl
) : AlbumServiceCardResponse

interface AlbumServiceImpl : AlbumService {
    @GET("?method=product.getNews")
    override suspend fun load(@Query("page") pageNumber: Int,
                              @Query("limit") limit: Int): AlbumServiceNewsResponseImpl
    @GET("?method=product.getCard")
    override suspend fun loadCard(@Query("productId") productId: Int): AlbumServiceCardResponseImpl
}

