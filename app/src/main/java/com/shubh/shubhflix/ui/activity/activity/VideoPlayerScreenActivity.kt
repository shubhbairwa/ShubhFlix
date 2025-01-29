package com.shubh.shubhflix.ui.activity.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.Listener
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.shubh.shubhflix.R
import com.shubh.shubhflix.databinding.ActivityVideoPlayerScreenBinding
import com.shubh.shubhflix.helper.CustomCountdownTimer
import com.shubh.shubhflix.helper.GlobalFunctions

class VideoPlayerScreenActivity : AppCompatActivity() {
    lateinit var binding: ActivityVideoPlayerScreenBinding
    lateinit var player: ExoPlayer
    var videoUrl = ""
    var videoDuartion = 0
    var videoTitle = ""


    lateinit var ibCrossButton: ImageButton
    lateinit var ibPlayPauseButton: ImageButton
    lateinit var ibForwardButton: ImageButton
    lateinit var ibReplayButton: ImageButton
    lateinit var tvTitle: TextView
    lateinit var tvDuration: TextView
    lateinit var seekBar: SeekBar

    private lateinit var customTimer: CustomCountdownTimer

    companion object {
        private const val TAG = "VideoPlayerScreenActivi"
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVideoPlayerScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.videoProgress.visibility = View.VISIBLE



        videoUrl = intent.getStringExtra("url").toString()
        videoDuartion = intent.getStringExtra("duration").toString().toInt()
        videoTitle = intent.getStringExtra("title").toString()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Hide the status bar
        hideSystemBars()
        setupControllerMediaPlayer()
        initializePlayer()


    }

    private fun setupControllerMediaPlayer() {
        binding.apply {
            ibCrossButton =
                playerView.findViewById<ImageButton>(R.id.ibCrossButton)

            ibCrossButton.setOnClickListener {
                finish()
            }


            // Set up a custom control
            ibPlayPauseButton = playerView.findViewById<ImageButton>(R.id.ibPlayPause)
            ibForwardButton = playerView.findViewById<ImageButton>(R.id.ibForward)
            ibReplayButton = playerView.findViewById<ImageButton>(R.id.ibReplay)
            seekBar = playerView.findViewById<SeekBar>(R.id.seekBarVideo)
            tvTitle = playerView.findViewById<TextView>(R.id.tvVideoTitle)
            tvDuration = playerView.findViewById<TextView>(R.id.tvVideoDurationLeft)

            // Set up SeekBar
            seekBar = playerView.findViewById<SeekBar>(R.id.seekBarVideo)
            seekBar.max = 100 // Example: Set max progress to 100 (adjust as needed)

            tvTitle.text = videoTitle
            tvDuration.text = GlobalFunctions.formatSecondsToTime(videoDuartion)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun hideSystemBars() {
        // Hide the status bar using WindowInsetsController
        window.insetsController?.apply {
            hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    @OptIn(UnstableApi::class)
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun initializePlayer() {
        binding.apply {


            // Initialize ExoPlayer
            player = ExoPlayer.Builder(this@VideoPlayerScreenActivity).build()
            playerView.player = player

            ibPlayPauseButton.setOnClickListener {
                if (player.isPlaying) {
                    player.pause()
                    ibPlayPauseButton.setImageResource(R.drawable.ic_play) // Change to play icon
                    customTimer.pause()
                } else {
                    player.play()
                    ibPlayPauseButton.setImageResource(R.drawable.ic_pause) // Change to pause icon
                    customTimer.start()
                }
            }


            // Set media item
            val mediaItem = MediaItem.fromUri(videoUrl)
            player.setMediaItem(mediaItem)

            // Prepare and play
            player.prepare()
            ibPlayPauseButton.setImageResource(R.drawable.ic_pause) // Change to pause icon
            //todo adding counter in reverse
            customTimer = CustomCountdownTimer(
                totalSeconds = videoDuartion.toLong(),
                onTick = { time ->
                    tvDuration.text = time
                },
                onFinish = {
                    tvDuration.text = "00:00:00"
                    player.pause() // Stop the video when the timer finishes
                }
            )

            player.addListener(object : Player.Listener {

                override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                    if (playWhenReady) {
                        // Video is playing, resume the timer
                        customTimer.resume()
                    } else {
                        // Video is paused, pause the timer
                        customTimer.pause()
                    }
                }


                override fun onPositionDiscontinuity(reason: Int) {
                    // Update seekBar position
                    val position = player.currentPosition
                    val duration = player.duration
                    seekBar.progress = ((position.toFloat() / duration) * 100).toInt()
                    Log.e(TAG, "onPositionDiscontinuity duration=>: $duration")
                    Log.e(TAG, "onPositionDiscontinuity position=>: $position")
                }

                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        ExoPlayer.STATE_IDLE -> {
                            // Do nothing or reset UI elements
                        }

                        ExoPlayer.STATE_BUFFERING -> {
                            // Handle buffering state
                            Log.d("PlayerActivity", "Buffering...")
                            binding.videoProgress.visibility = View.VISIBLE
                        }

                        ExoPlayer.STATE_READY -> {
                            binding.videoProgress.visibility = View.INVISIBLE
                            // Player is ready, we can start updating the seekbar
                            //updateSeekBar()
                            //player.get
                        }

                        ExoPlayer.STATE_ENDED -> {
                            // Handle video ending (if needed)
                            Log.d("PlayerActivity", "Playback ended")

                            // Reset the player
                            player.seekTo(0) // Reset to the beginning
                            player.playWhenReady = false // Pause the playback (optional)
                            ibPlayPauseButton.setImageResource(R.drawable.ic_play)
                            customTimer.reset()


                        }
                    }
                }
            })



            player.playWhenReady = true

            customTimer.start()

        }
    }

    override fun onStop() {
        super.onStop()
        player.release() // Release player resources
    }

    override fun onPause() {
        super.onPause()
        player.pause()
        customTimer.pause()
    }


}