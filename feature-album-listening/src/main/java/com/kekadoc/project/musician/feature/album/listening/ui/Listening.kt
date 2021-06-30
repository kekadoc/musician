package com.kekadoc.project.musician.feature.album.listening.ui

import android.util.Log
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

private const val TAG: String = "Listening-TAG"

fun Player.PositionInfo.contentString(): String {
    return "PositionInfo(" +
            "adGroupIndex=${this.adGroupIndex}," +
            "adIndexInAdGroup=${this.adIndexInAdGroup}," +
            "contentPositionMs=${this.contentPositionMs}," +
            "periodIndex=${this.periodIndex}," +
            "periodUid=${this.periodUid}," +
            "positionMs=${this.positionMs}," +
            "windowIndex=${this.windowIndex}," +
            "windowUid=${this.windowUid}," +
            ")"
}
fun MediaMetadata.contentString(): String {
    return "MediaMetadata(" +
            "albumArtist=${this.albumArtist}," +
            "albumTitle=${this.albumTitle}," +
            "artist=${this.artist}," +
            "artworkData=${this.artworkData}," +
            "artworkUri=${this.artworkUri}," +
            "description=${this.description}," +
            "displayTitle=${this.displayTitle}," +
            "extras=${this.extras}," +
            "folderType=${this.folderType}," +
            "isPlayable=${this.isPlayable}," +
            "mediaUri=${this.mediaUri}," +
            "overallRating=${this.overallRating}," +
            "subtitle=${this.subtitle}," +
            "title=${this.title}," +
            "totalTrackCount=${this.totalTrackCount}," +
            "trackNumber=${this.trackNumber}," +
            "userRating=${this.userRating}," +
            "year=${this.year}" +
            ")"
}

class LogListener : Player.Listener {
    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        Log.e(TAG, "onAudioSessionIdChanged: audioSessionId=$audioSessionId")
    }
    override fun onAudioAttributesChanged(audioAttributes: com.google.android.exoplayer2.audio.AudioAttributes) {
        Log.e(TAG, "onAudioAttributesChanged: audioAttributes=$audioAttributes")
    }
    override fun onVolumeChanged(volume: Float) {
        Log.e(TAG, "onVolumeChanged: volume=$volume")
    }
    override fun onSkipSilenceEnabledChanged(skipSilenceEnabled: Boolean) {
        Log.e(TAG, "onSkipSilenceEnabledChanged: skipSilenceEnabled=$skipSilenceEnabled")
    }
    override fun onTimelineChanged(timeline: com.google.android.exoplayer2.Timeline, reason: Int) {
        Log.e(TAG, "onTimelineChanged: timeline=$timeline reason=$reason")
    }
    override fun onTimelineChanged(timeline: com.google.android.exoplayer2.Timeline, manifest: Any?, reason: Int) {
        Log.e(TAG, "onTimelineChanged: timeline=$timeline manifest=$manifest reason=$reason")
    }
    override fun onMediaItemTransition(mediaItem: com.google.android.exoplayer2.MediaItem?, reason: Int) {
        Log.e(TAG, "onMediaItemTransition: mediaItem=$mediaItem reason=$reason")
    }
    override fun onTracksChanged(trackGroups: com.google.android.exoplayer2.source.TrackGroupArray, trackSelections: com.google.android.exoplayer2.trackselection.TrackSelectionArray) {
        Log.e(TAG, "onTracksChanged: trackGroups=$trackGroups trackSelections=$trackSelections")
    }
    override fun onStaticMetadataChanged(metadataList: MutableList<com.google.android.exoplayer2.metadata.Metadata>) {
        Log.e(TAG, "onStaticMetadataChanged: metadataList=$metadataList")
    }
    override fun onMediaMetadataChanged(mediaMetadata: com.google.android.exoplayer2.MediaMetadata) {
        Log.e(TAG, "onMediaMetadataChanged: mediaMetadata=$mediaMetadata")
    }
    override fun onIsLoadingChanged(isLoading: Boolean) {
        Log.e(TAG, "onIsLoadingChanged: isLoading=$isLoading")
    }
    override fun onLoadingChanged(isLoading: Boolean) {
        Log.e(TAG, "onLoadingChanged: isLoading=$isLoading")
    }
    override fun onAvailableCommandsChanged(availableCommands: com.google.android.exoplayer2.Player.Commands) {
        Log.e(TAG, "onAvailableCommandsChanged: availableCommands=$availableCommands")
    }
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        Log.e(TAG, "onPlayerStateChanged: playWhenReady=$playWhenReady playbackState=$playbackState")
    }
    override fun onPlaybackStateChanged(state: Int) {
        Log.e(TAG, "onPlaybackStateChanged: state=$state")
    }
    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        Log.e(TAG, "onPlayWhenReadyChanged: playWhenReady=$playWhenReady reason=$reason")
    }
    override fun onPlaybackSuppressionReasonChanged(playbackSuppressionReason: Int) {
        Log.e(TAG, "onPlaybackSuppressionReasonChanged: playbackSuppressionReason=$playbackSuppressionReason")
    }
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        Log.e(TAG, "onIsPlayingChanged: isPlaying=$isPlaying")
    }
    override fun onRepeatModeChanged(repeatMode: Int) {
        Log.e(TAG, "onRepeatModeChanged: repeatMode=$repeatMode")
    }
    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        Log.e(TAG, "onShuffleModeEnabledChanged: shuffleModeEnabled=$shuffleModeEnabled")
    }
    override fun onPlayerError(error: com.google.android.exoplayer2.ExoPlaybackException) {
        Log.e(TAG, "onPlayerError: error=$error")
    }
    override fun onPositionDiscontinuity(reason: Int) {
        Log.e(TAG, "onPositionDiscontinuity: reason=$reason")
    }
    override fun onPositionDiscontinuity(
        oldPosition: com.google.android.exoplayer2.Player.PositionInfo,
        newPosition: com.google.android.exoplayer2.Player.PositionInfo,
        reason: Int,
    ) {
        Log.e(TAG, "onPositionDiscontinuity: oldPosition=$oldPosition newPosition=$newPosition reason=$reason")
    }
    override fun onPlaybackParametersChanged(playbackParameters: com.google.android.exoplayer2.PlaybackParameters) {
        Log.e(TAG, "onPlaybackParametersChanged: playbackParameters=$playbackParameters")
    }
    override fun onSeekProcessed() {
        Log.e(TAG, "onSeekProcessed: ")
    }
    override fun onEvents(player: com.google.android.exoplayer2.Player, events: com.google.android.exoplayer2.Player.Events) {
        Log.e(TAG, "onEvents: player=$player events=$events")
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
val Player.windowIndexFlow: Flow<Int>
    get() {
        return callbackFlow {
            send(currentWindowIndex)
            val listener = object : Player.Listener {
                override fun onPositionDiscontinuity(
                    oldPosition: Player.PositionInfo,
                    newPosition: Player.PositionInfo,
                    reason: Int
                ) {
                    this@callbackFlow.trySend(newPosition.windowIndex)
                }
            }
            addListener(listener)
            awaitClose {
                removeListener(listener)
            }
        }
    }

@OptIn(ExperimentalCoroutinesApi::class)
val Player.mediaItemFlow: Flow<MediaItem>
    get() {
        return callbackFlow {

            currentMediaItem?.let { send(it) }

            val listener = object : Player.Listener {
                override fun onPositionDiscontinuity(
                    oldPosition: Player.PositionInfo,
                    newPosition: Player.PositionInfo,
                    reason: Int
                ) {
                    this@callbackFlow.trySend(getMediaItemAt(newPosition.windowIndex))
                }
            }
            addListener(listener)
            awaitClose {
                removeListener(listener)
            }
        }
    }
