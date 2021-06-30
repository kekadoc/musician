package com.kekadoc.project.musician.ui.track

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kekadoc.project.musicant.core.serialization.deserializeFromStringOrNull
import com.kekadoc.project.musicant.core.serialization.serializeToStringOrNull
import com.kekadoc.project.musician.databinding.FragmentTrackListeningBinding
import com.kekadoc.project.musician.feature.album.listening.ui.windowIndexFlow
import com.kekadoc.project.musician.feature.album.network.api.AlbumServiceCardResponse
import com.kekadoc.project.musician.feature.album.network.api.Track
import com.kekadoc.project.musician.feature.album.network.impl.AlbumServiceCardResponseImpl
import com.kekadoc.project.musician.feature.album.network.impl.getAlbum
import com.kekadoc.project.musician.feature.album.network.impl.getAuthors
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.get

class TrackListeningDialogFragment : BottomSheetDialogFragment() {

    companion object {
        private const val TAG: String = "TrackListening-TAG"
        private const val KEY_INPUT_CARD = "InputCard"
        private const val KEY_INPUT_TRACK_ID = "InputTrackId"

        fun createInput(input: AlbumServiceCardResponseImpl, targetTrackId: Int): Bundle {
            return Bundle(2).apply {
                putString(KEY_INPUT_CARD, input.serializeToStringOrNull())
                putInt(KEY_INPUT_TRACK_ID, targetTrackId)
            }
        }

        private fun parseCard(data: Bundle?): AlbumServiceCardResponse? {
            return data?.getString(KEY_INPUT_CARD)?.deserializeFromStringOrNull<AlbumServiceCardResponseImpl>()
        }
        private fun parseTrackId(data: Bundle?): Int? {
            return data?.getInt(KEY_INPUT_TRACK_ID, -1).let {
                it?.takeIf {
                    it >= 0
                }
            }
        }

    }

    private val viewModel by activityViewModels<TrackListeningViewModel>()
    private val binding: FragmentTrackListeningBinding by viewBinding {
        FragmentTrackListeningBinding.inflate(it.layoutInflater)
    }

    private val albumCard: AlbumServiceCardResponse by lazy {
        parseCard(arguments) ?: throw IllegalStateException()
    }
    private val trackId: Int by lazy {
        parseTrackId(arguments) ?: throw IllegalStateException()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    private fun applyTrack(track: Track) {
        binding.textViewAlbumName.text = albumCard.library.albums[albumCard.result.id]!!.name
        binding.textViewTrackName.text = track.name
        binding.textViewTrackAuthors.text = track.getAuthors(albumCard.library)
        binding.imageViewCover.load(track.coverUrl)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        view.viewTreeObserver.addOnGlobalLayoutListener {
            val dialog = dialog as BottomSheetDialog?
            val bottomSheet = dialog!!.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)!!
            val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from<View>(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.buttonBack.setOnClickListener {
            dismiss()
        }

        val trackIds = albumCard.result.productIds
        val tracks = trackIds.mapNotNull {
                albumCard.library.tracks[it]
        }
        val mediaItems = tracks.mapNotNull {
                it.fileUrl?.let { fileUrl ->
                    val metadata = MediaMetadata.Builder()
                        .setAlbumArtist(it.getAuthors(albumCard.library))
                        .setAlbumTitle(albumCard.getAlbum().name)
                        .setArtist(it.getAuthors(albumCard.library))
                        .setArtworkUri(Uri.parse(it.coverUrl))
                        .setTitle(it.name)
                        .setExtras(Bundle(1).apply {
                            Log.e(TAG, "onViewCreated: ${it.parent}")
                            putInt("parent", it.parent)
                        })
                        .build()
                    MediaItem.fromUri(fileUrl).buildUpon()
                        .setMediaId(it.id.toString())
                        .setMediaMetadata(metadata)
                        .build()
                }
        }

        val player: Player = run {
            viewModel.player.value?.let {
                val albumId = it.currentMediaItem!!.mediaMetadata.extras!!.getInt("parent", -1)
                if (albumId > 0 && albumId == albumCard.result.id) it else null
            } ?: let {
                val player: Player = get()
                val listener = object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        if (state == Player.STATE_READY) {
                            player.removeListener(this)
                            viewModel.setPlayer(player, albumCard)
                        }
                    }
                }
                player.addListener(listener)
                player.setMediaItems(mediaItems, trackIds.indexOf(trackId), 0)
                player
            }
        }

        player.windowIndexFlow.onEach {
            applyTrack(tracks[it])
        }.launchIn(lifecycleScope)

        binding.playerView.player = player

    }

}