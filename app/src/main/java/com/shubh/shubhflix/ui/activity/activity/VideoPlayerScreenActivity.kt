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
import androidx.media3.ui.DefaultTimeBar
import androidx.media3.ui.TimeBar
import com.shubh.shubhflix.R
import com.shubh.shubhflix.databinding.ActivityVideoPlayerScreenBinding
import com.shubh.shubhflix.helper.CustomCountdownTimer
import com.shubh.shubhflix.helper.GlobalFunctions

@UnstableApi
class VideoPlayerScreenActivity : AppCompatActivity() {
    lateinit var binding: ActivityVideoPlayerScreenBinding
    lateinit var player: ExoPlayer
    private var videoUrl = ""
    private var videoDuartion = 0
    private var videoTitle = ""


    private lateinit var ibCrossButton: ImageButton
    lateinit var ibPlayPauseButton: ImageButton
    private lateinit var ibForwardButton: ImageButton
    private lateinit var ibReplayButton: ImageButton
    private lateinit var tvTitle: TextView
    private lateinit var tvDuration: TextView


    private lateinit var exoTimeBar: DefaultTimeBar


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


        //todo get VideoDetails
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
            exoTimeBar = playerView.findViewById<DefaultTimeBar>(R.id.seekBarVideo)
            tvTitle = playerView.findViewById<TextView>(R.id.tvVideoTitle)
            tvDuration = playerView.findViewById<TextView>(R.id.tvVideoDurationLeft)

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
            // Set media item or the video you want to play
            val mediaItem = MediaItem.fromUri(videoUrl)
            player.setMediaItem(mediaItem)

            // Prepare and play
            player.prepare()
            //default when video is ready
            ibPlayPauseButton.setImageResource(R.drawable.ic_pause) // Change to pause icon


            ibPlayPauseButton.setOnClickListener {
                if (player.isPlaying) {
                    player.pause()
                    ibPlayPauseButton.setImageResource(R.drawable.ic_play) // Change to play icon

                } else {
                    player.play()
                    ibPlayPauseButton.setImageResource(R.drawable.ic_pause) // Change to pause icon

                }
            }



            exoTimeBar.addListener(object : TimeBar.OnScrubListener {
                override fun onScrubStart(timeBar: TimeBar, position: Long) {

                }

                override fun onScrubMove(timeBar: TimeBar, position: Long) {
                    player.seekTo(position)
                }

                override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {

                }

            })



            ibForwardButton.setOnClickListener { seekPlayerBy(5000) }
            ibReplayButton.setOnClickListener { seekPlayerBy(-5000) }



            player.addListener(object : Player.Listener {




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

                            updateSeekBar()
                            Log.e(TAG, "onPlaybackStateChanged: Playback state ready")
                        }

                        ExoPlayer.STATE_ENDED -> {
                            // Handle video ending (if needed)
                            Log.d("PlayerActivity", "Playback ended")

                            // Reset the player
                            player.seekTo(0) // Reset to the beginning
                            player.playWhenReady = false // Pause the playback (optional)
                            ibPlayPauseButton.setImageResource(R.drawable.ic_play)



                        }
                    }
                }
            })



            player.playWhenReady = true
            // Sync seek bar with video progress

        }
    }

    override fun onStop() {
        super.onStop()
        player.release() // Release player resources
    }

    override fun onPause() {
        super.onPause()
        player.pause()

    }

    // Function to update the time bar progress
    private fun updateSeekBar() {
        val duration = player.duration
        val position = player.currentPosition

        if (duration > 0) {
            exoTimeBar.setDuration(duration)
            exoTimeBar.setPosition(position)
        }

        binding.playerView.postDelayed({ updateSeekBar() }, 1000)
    }


    // Function to seek player with proper boundary conditions
    private fun seekPlayerBy(milliseconds: Long) {
        val newPosition = player.currentPosition + milliseconds

        // Corner Case 1: Ensure position does not go below 0
        if (newPosition < 0) {
            player.seekTo(0)
            Log.d("Seek", "Cannot rewind beyond 0 seconds")
            return
        }

        // Corner Case 2: Ensure position does not exceed video duration
        if (newPosition > player.duration) {
            player.seekTo(player.duration)
            Log.d("Seek", "Cannot seek beyond video duration")
            return
        }

        // Seek to the valid new position
        player.seekTo(newPosition)
        exoTimeBar.setPosition(newPosition) // Sync with time bar
    }


}