package com.kekadoc.project.musician.module

import android.util.Log
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kekadoc.project.musician.core.network.api.NetworkError
import com.kekadoc.project.musician.core.util.toMap
import com.kekadoc.project.musician.feature.album.network.api.*
import com.kekadoc.project.musician.feature.album.network.impl.*

import com.kekadoc.project.musician.ui.album.AlbumViewModel
import com.kekadoc.project.musician.ui.albums.AlbumListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG: String = "DI-TAG"

object Const {
    const val NAME_TRACK_URL = "trackUrl"
}

val viewModelsModule = module {
    viewModel {
        AlbumViewModel(get<AlbumService>())
    }
    viewModel {
        AlbumListViewModel(get<AlbumService>())
    }
}
val networkModule = module {
    single<Gson> {
        GsonBuilder().create()
    }
    single<GsonConverterFactory> {
        GsonConverterFactory.create(get(Gson::class))
    }
    single<Retrofit> {
        val baseUrl = "https://api.mobimusic.kz/"
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(get(GsonConverterFactory::class))
            .build()
    }
    single<AlbumNetworkClient> {
        AlbumNetworkClient(get(Retrofit::class))
    }
    single<AlbumService> {
        get<AlbumNetworkClient>().service
    }
    factory<String>(named(Const.NAME_TRACK_URL)) {
        trackUrl.random()
    }
}

val fakeModels = module {

    val namedCoverUrl = named("coverPhotoUrl")

    single<String>(namedCoverUrl) {
        "https://images.pexels.com/photos/1323206/pexels-photo-1323206.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"
    }

    factory<Album> { (id: Int) ->
        AlbumImpl(
            id = id,
            parent = null,
            name = "Empty",
            coverUrl = get(namedCoverUrl),
            cover = get(namedCoverUrl),
            year = 2020,
            price = 0.0,
            dir = null,
            state = 0,
            peopleIds = listOf(0, 1),
            duration = 3000,
            trackCount = 10,
            isUserLikes = true
        ).also {
            Log.e(TAG, "Factory Album: $it" )
        }
    } bind AlbumImpl::class
    factory<People> { (id : Int) ->
        PeopleImpl(
            id = id,
            name = "Name",
            dir = null,
            typeName = "typeName",
            productCount = 10,
            productChildCount = 10,
            genres = emptyList(),
            coverFile = null,
            coverUrl = null,
            isUserLikes = true,
            description = null
        )
    } bind PeopleImpl::class
    factory<Category> { (id: Int) ->
        CategoryImpl(
            id = id,
            name = "Name",
            kzName = "kzName",
            enName = "enName",
            haveChild = 0,
            child = emptyList()
        )
    } bind CategoryImpl::class
    factory<Track> { (id: Int) ->
        TrackImpl(
            id = id,
            parent = 0,
            name = "Track $id",
            cover = get(namedCoverUrl),
            coverUrl = get(namedCoverUrl),
            year = 2020,
            price = 0.0,
            dir = "dir",
            state = 0,
            peopleIds = listOf(0, 1, 2),
            duration = 3000,
            isUserLikes = true,
            hasLyrics = true,
            lyrics2 = null
        )
    } bind TrackImpl::class

    factory<Albums> { (ids: List<Int>) ->
        AlbumsImpl(ids)
    } bind AlbumsImpl::class
    factory<Library> { (albums: IntRange?, peoples: IntRange?, categories: IntRange?, tracks: IntRange?) ->
        LibraryImpl(
            albums = run<Map<Int, AlbumImpl>> {
                albums ?: return@run emptyMap()
                albums.toMap { it to get { parametersOf(it) } }
            },
            peoples = run<Map<Int, PeopleImpl>> {
                peoples ?: return@run emptyMap()
                peoples.toMap { it to get { parametersOf(it) } }
            },
            categories = run<Map<Int, CategoryImpl>> {
                categories ?: return@run emptyMap()
                categories.toMap { it to get { parametersOf(it) } }
            },
            tracks = run<Map<Int, TrackImpl>> {
                tracks ?: return@run emptyMap()
                tracks.toMap { it to get { parametersOf(it) } }
            },
        )
    } bind LibraryImpl::class
    factory<AlbumCard> { (id: Int) ->
        AlbumCardImpl(
            id = id,
            categoryIds = listOf(id+1, id+2, id+3),
            productIds = (0..10).toList()
        )
    } bind AlbumCardImpl::class
    single<NetworkError> {
        NetworkErrorImpl(0, "Error message")
    } bind NetworkErrorImpl::class

    factory<AlbumServiceNewsResponse> { (page: Int, limit: Int, pageSize: Int) ->
        val from = page * pageSize
        val to = from + limit
        AlbumServiceNewsResponseImpl(
            error = get(),
            result = get {
                parametersOf((from..to).toList())
            },
            library = get {
                parametersOf(from..to, from..to, null, null)
            }
        )
    } bind AlbumServiceNewsResponseImpl::class
    factory<AlbumServiceCardResponse> { (id: Int) ->
        AlbumServiceCardResponseImpl(
            error = get(),
            result = get {
                parametersOf(id)
            },
            library = get<LibraryImpl> {
                parametersOf(IntRange(id, id), IntRange(0, 3), IntRange(id, id), IntRange(0, id))
            }
        )
    } bind AlbumServiceCardResponseImpl::class

    single<AlbumService>(named("fake")) {
        object : AlbumService {
            override suspend fun load(pageNumber: Int, limit: Int): AlbumServiceNewsResponse {
                return get {
                    parametersOf(pageNumber, limit, 10)
                }
            }
            override suspend fun loadCard(productId: Int): AlbumServiceCardResponse {
                return get {
                    parametersOf(productId)
                }
            }
        }
    }
}

val exoPlayerModule = module {
    factory<Player> {
        SimpleExoPlayer.Builder(androidContext()).build()
    }
}