package com.kekadoc.project.musician.ui.albums

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import com.kekadoc.project.musician.R
import com.kekadoc.project.musician.core.ui.dpToPx
import com.kekadoc.project.musician.databinding.FragmentAlbumListBinding
import com.kekadoc.project.musician.databinding.LayoutAlbumPreviewBinding
import com.kekadoc.project.musician.feature.album.network.api.Album
import com.kekadoc.project.musician.ui.album.AlbumFragment
import com.kekadoc.project.musician.ui.track.TrackListeningViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class AlbumListFragment : Fragment(R.layout.fragment_album_list) {

    private val listeningAlbumViewModel by activityViewModels<TrackListeningViewModel>()
    private val viewModel by viewModel<AlbumListViewModel>()
    private val binding by viewBinding(FragmentAlbumListBinding::bind)
    private val adapter = Adapter(object : VH.Callback {
        override fun onClick(vh: VH) {
            findNavController().navigate(
                R.id.destination_album,
                AlbumFragment.createArguments(vh.album!!.id)
            )
        }
    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding.recyclerView) {
            val lm = GridLayoutManager(requireContext(), 3)
            layoutManager = lm
            adapter = this@AlbumListFragment.adapter
            addItemDecoration(Decorator(requireContext()))
        }
        viewModel.flow.onEach {
            adapter.submitData(it)
        }.launchIn(lifecycleScope)

    }

    private object DiffUtilItemCallback : DiffUtil.ItemCallback<Album>() {
        override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem.id == newItem.id
        }
        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem.id == newItem.id
                    && oldItem.name == newItem.name
                    && oldItem.cover == newItem.cover
                    && oldItem.coverUrl == newItem.coverUrl
                    && oldItem.year == newItem.year
                    && oldItem.price == newItem.price
                    && oldItem.duration == newItem.duration
                    && oldItem.trackCount == newItem.trackCount
                    && oldItem.isUserLikes == newItem.isUserLikes
        }
    }


    private class VH(val binding: LayoutAlbumPreviewBinding,
                     val callback: Callback) : RecyclerView.ViewHolder(binding.root) {

        interface Callback {
            fun onClick(vh: VH)
        }

        var album: Album? = null

        init {
            val context = binding.root.context
            val padding = context.dpToPx(2f).toInt()

            binding.root.setOnClickListener {
                callback.onClick(this)
            }

            binding.textViewTrackCount.apply {
                val musicDrawable = AppCompatResources.getDrawable(
                    context, R.drawable.ic_baseline_music_note_18
                )
                setCompoundDrawablesRelativeWithIntrinsicBounds(
                    musicDrawable, null, null, null
                )
                compoundDrawablePadding = padding
            }
            binding.textViewDuration.apply {
                val timeDrawable = AppCompatResources.getDrawable(
                    context, R.drawable.ic_baseline_access_time_18
                )
                setCompoundDrawablesWithIntrinsicBounds(
                    null, null, timeDrawable, null
                )
                compoundDrawablePadding = padding
            }

        }

        fun bind(album: Album?) {
            this.album = album
            with(binding) {
                shapeableImageView.load(album?.coverUrl) {
                    placeholder(R.drawable.ic_image_downloading_72)
                    error(R.drawable.ic_image_error_72)
                    fallback(R.drawable.ic_image_album_72)
                }
                textViewName.text = album?.name
                textViewDuration.apply {
                    text = album?.duration?.let { secondToString(it) }
                    isVisible  = !text.isNullOrEmpty()
                }
                imageViewIsUserLikes.isVisible = album?.isUserLikes ?: false
                textViewPrice.apply {
                    text = album?.price.toString()
                    isVisible = album?.price?.let { it > 0 } ?: false
                }
                textViewTrackCount.text = album?.trackCount.toString()
                textViewYear.text = album?.year?.let {
                    if (it <= 0) null else it.toString()
                } ?: '\u221E'.toString()
            }
        }

        private fun secondToString(sec: Int): String {
            val hours = sec / 3_600
            val minutes = (sec % 3_600) / 60
            val seconds = sec % 60
            return when {
                sec < 3_600 -> String.format("%02d:%02d", minutes, seconds)
                sec < 60 -> String.format("%02d", seconds)
                else -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
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

    private class Adapter(val callback: VH.Callback) : PagingDataAdapter<Album, VH>(DiffUtilItemCallback) {

        private var _inflater: LayoutInflater? = null
        private fun getInflater(context: Context): LayoutInflater {
            return _inflater ?: LayoutInflater.from(context).also {
                _inflater = it
            }
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.bind(getItem(position))
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(LayoutAlbumPreviewBinding.inflate(getInflater(parent.context),
                parent, false), callback)
        }
    }

}

