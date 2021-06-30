package com.kekadoc.project.musician.ui.album

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import com.kekadoc.project.musician.R
import com.kekadoc.project.musician.core.ui.dpToPx
import com.kekadoc.project.musician.core.util.ResultCallback
import com.kekadoc.project.musician.databinding.FragmentAlbumBinding
import com.kekadoc.project.musician.databinding.LayoutTrackViewBinding
import com.kekadoc.project.musician.feature.album.listening.ui.mediaItemFlow
import com.kekadoc.project.musician.feature.album.network.api.AlbumServiceCardResponse
import com.kekadoc.project.musician.feature.album.network.api.Library
import com.kekadoc.project.musician.feature.album.network.api.Track
import com.kekadoc.project.musician.feature.album.network.impl.AlbumServiceCardResponseImpl
import com.kekadoc.project.musician.feature.album.network.impl.getAlbum
import com.kekadoc.project.musician.feature.album.network.impl.getAuthors
import com.kekadoc.project.musician.ui.setMediaItem
import com.kekadoc.project.musician.ui.track.TrackListeningDialogFragment
import com.kekadoc.project.musician.ui.track.TrackListeningViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class AlbumFragment : DialogFragment(R.layout.fragment_album) {

    companion object {
        private const val TAG: String = "AlbumFragment-TAG"
        private const val KET_INPUT = "KeyInputData"
        fun createArguments(id: Int): Bundle {
            return Bundle(1).apply {
                putInt(KET_INPUT, id)
            }
        }
        fun parseArguments(args: Bundle?): Int? {
            val id = args?.getInt(KET_INPUT, -1)
            if (id == null || id < 0) return null
            return id
        }
    }

    private val listeningAlbumViewModel by activityViewModels<TrackListeningViewModel>()
    private val viewModel by viewModel<AlbumViewModel>()

    private val adapter = Adapter(null, object : VH.Callback {
        override fun onClick(vh: VH) {
            navigateToTrackDialog(vh.track!!.id)
        }
    })

    private val binding by viewBinding(FragmentAlbumBinding::bind)

    private fun navigateToTrackDialog(trackId: Int) {
        try {
            val card = viewModel.albumCard.value!! as AlbumServiceCardResponseImpl
            findNavController().navigate(
                R.id.destination_track_listening,
                TrackListeningDialogFragment.createInput(card, trackId)
            )
        } catch (e: Exception) {
            Log.e(TAG, "Navigate to track dialog error", e)
        }
    }

    private fun showLoading() {
        binding.linearProgressIndicator.show()
    }
    private fun hideLoading() {
        binding.linearProgressIndicator.hide()
    }

    private fun showContent(album: AlbumServiceCardResponse) {
        binding.recyclerView.apply {
            visibility = View.VISIBLE
            this@AlbumFragment.adapter.submitData(album)
        }
        binding.imageViewError.apply {
            visibility = View.GONE
            setImageDrawable(null)
        }
        binding.textViewError.apply {
            visibility = View.GONE
            text = null
        }
    }
    private fun hideContent(image: Drawable?, textMessage: String?) {
        binding.recyclerView.isVisible = false
        binding.imageViewError.apply {
            setImageDrawable(image)
            isVisible = image != null
        }
        binding.textViewError.apply {
            text = textMessage
            isVisible = !textMessage.isNullOrEmpty()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        with(binding.recyclerView) {
            val lm = LinearLayoutManager(requireContext())
            layoutManager = lm
            adapter = this@AlbumFragment.adapter
            addItemDecoration(Decorator(requireContext()))
        }
        with(binding.toolbar) {
            setNavigationOnClickListener {
                dismiss()
            }
        }

        viewModel.albumCard.onEach {
            if (it == null) {
                binding.imageViewAlbumCover.setImageDrawable(null)
                binding.textViewAlbumNameTitle.text = null
                binding.textViewAlbumNameSubtitle.text = null
                binding.root.getTransition(R.id.transition_fragment_album).setEnable(false)
            } else {
                val album = it.getAlbum()
                binding.imageViewAlbumCover.load(album.coverUrl)
                binding.textViewAlbumNameTitle.text = album.name
                binding.textViewAlbumNameSubtitle.text = album.name
                if (it.library.tracks.size < 5) {
                    binding.root.getTransition(R.id.transition_fragment_album).setEnable(false)
                } else binding.root.getTransition(R.id.transition_fragment_album).setEnable(true)
            }

        }.launchIn(lifecycleScope)

        val args = arguments
        val input = parseArguments(args)
        if (input == null) {
            val image = AppCompatResources.getDrawable(
                requireContext(), R.drawable.ic_baseline_mood_bad_128
            )
            val text = requireContext().getString(R.string.fragment_album_error_no_data)
            hideContent(image, text)
        } else {
            viewModel.loadCard(input) {
                when(it) {
                    is ResultCallback.State.Process -> {
                        showLoading()
                        val image = AppCompatResources.getDrawable(
                            requireContext(), R.drawable.ic_baseline_downloading_128
                        )
                        val text = null
                        hideContent(image, text)
                    }
                    is ResultCallback.State.Error -> {
                        hideLoading()
                        val image = AppCompatResources.getDrawable(
                            requireContext(), R.drawable.ic_baseline_mood_bad_128
                        )
                        val text = it.fail.localizedMessage
                        hideContent(image, text)
                    }
                    is ResultCallback.State.Success -> {
                        hideLoading()
                        showContent(it.result)
                    }
                }
            }
        }

        binding.playerView.setOnClickListener {
            val trackId = listeningAlbumViewModel.player.value!!.currentMediaItem!!.mediaId.toInt()
            navigateToTrackDialog(trackId)
        }
        listeningAlbumViewModel.player.onEach {
            binding.playerView.player = it
            if (it == null) binding.playerView.hide()
            else binding.playerView.show()
            it?.mediaItemFlow?.onEach { item ->
                binding.playerView.setMediaItem(item)
            }?.launchIn(lifecycleScope)
        }.launchIn(lifecycleScope)

    }

    private object DiffUtilItemCallback : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem.id == newItem.id
        }
        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem.id == newItem.id
                    && oldItem.name == newItem.name
                    && oldItem.cover == newItem.cover
                    && oldItem.coverUrl == newItem.coverUrl
                    && oldItem.year == newItem.year
                    && oldItem.price == newItem.price
                    && oldItem.duration == newItem.duration
                    && oldItem.isUserLikes == newItem.isUserLikes
        }
    }

    private class VH(val binding: LayoutTrackViewBinding,
                     val library: Library,
                     callback: Callback) : RecyclerView.ViewHolder(binding.root) {

        interface Callback {
            fun onClick(vh: VH)
        }

        var track: Track? = null

        init {
            binding.root.setOnClickListener {
                callback.onClick(this)
            }
        }

        fun bind(track: Track) {

            this.track = track

            with(binding) {
                imageViewCover.load(track.coverUrl) {
                    placeholder(R.drawable.ic_image_downloading_72)
                    error(R.drawable.ic_image_error_72)
                    fallback(R.drawable.ic_image_album_72)
                }
                textViewName.text = track.name
                val authors = track.getAuthors(library)
                textViewAuthor.text = authors
                imageViewIsUserLikes.isVisible = track.isUserLikes
            }

        }

    }

    private class Decorator(context: Context) : RecyclerView.ItemDecoration() {
        private val space = context.dpToPx(4f).toInt()
        override fun getItemOffsets(outRect: Rect, view: View,
                                    parent: RecyclerView, state: RecyclerView.State) {
            outRect.set(space, space, space, space)
        }
    }

    private class Adapter(var library: Library? = null,
                          val callback: VH.Callback) : ListAdapter<Track, VH>(DiffUtilItemCallback) {

        private var _inflater: LayoutInflater? = null
        private fun getInflater(context: Context): LayoutInflater {
            return _inflater ?: LayoutInflater.from(context).also {
                _inflater = it
            }
        }

        fun submitData(data: AlbumServiceCardResponse) {
            library = data.library
            val tracks: List<Track> = data.result.productIds.mapNotNull { data.library.tracks[it] }
            submitList(tracks)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.bind(getItem(position))
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val inflater = getInflater(parent.context)
            val binding = LayoutTrackViewBinding.inflate(
                inflater, parent, false
            )
            return VH(binding, library!!, callback)
        }
    }

}