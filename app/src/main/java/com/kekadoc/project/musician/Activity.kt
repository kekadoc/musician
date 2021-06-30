package com.kekadoc.project.musician

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.kekadoc.project.musician.databinding.ActivityMainBinding
import com.kekadoc.project.musician.feature.album.listening.ui.mediaItemFlow
import com.kekadoc.project.musician.feature.album.network.impl.AlbumServiceCardResponseImpl
import com.kekadoc.project.musician.ui.setMediaItem
import com.kekadoc.project.musician.ui.track.TrackListeningDialogFragment
import com.kekadoc.project.musician.ui.track.TrackListeningViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG: String = "MainActivity-TAG"
    }

    private val listeningViewModel by viewModels<TrackListeningViewModel>()

    lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    private fun navigateToTrackDialog() {
        try {
            val cardImpl = listeningViewModel.card!! as AlbumServiceCardResponseImpl
            val trackId = listeningViewModel.player.value!!.currentMediaItem!!.mediaId.toInt()
            navController.navigate(
                R.id.destination_track_listening,
                TrackListeningDialogFragment.createInput(cardImpl, trackId)
            )
        } catch (e: Exception) {
            Log.e(TAG, "Navigate to track dialog error", e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.nav_host_fragment_activity)
        binding.playerView.setOnClickListener {
            navigateToTrackDialog()
        }
        listeningViewModel.player.onEach {
            Log.e(TAG, "onCreate: $it")
            binding.playerView.player = it
            if (it == null) {
                binding.playerView.hide()
            } else {
                binding.playerView.show()
            }
            it?.mediaItemFlow?.onEach { item ->
                binding.playerView.setMediaItem(item)
            }?.launchIn(lifecycleScope)
        }.launchIn(lifecycleScope)

    }
}