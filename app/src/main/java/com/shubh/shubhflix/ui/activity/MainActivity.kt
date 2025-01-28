package com.shubh.shubhflix.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.shubh.shubhflix.R
import com.shubh.shubhflix.databinding.ActivityMainBinding
import com.shubh.shubhflix.helper.ApiState

import com.shubh.shubhflix.hilttest.HiltTest
import com.shubh.shubhflix.ui.activity.activity.VideoPlayerScreenActivity
import com.shubh.shubhflix.ui.activity.recycleradapter.VideoAdapter
import com.shubh.shubhflix.ui.activity.viewmodel.VideoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    private val adapter = VideoAdapter()

    private val viewModel: VideoViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.rvVideoList.adapter = adapter

        adapter.setOnItemClickListener {data->
            val intent = Intent(this, VideoPlayerScreenActivity::class.java).also {
                it.putExtra("url",data.videos.medium.url)
                it.putExtra("duration",data.duration.toString())
                it.putExtra("title",data.user)
                startActivity(it)
            }
        }
        observeList()


        viewModel.fetchVideos("anime")


    }

    private fun observeList() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.apiState.collectLatest { state ->
                    when (state) {
                        is ApiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.rvVideoList.visibility = View.GONE

                        }

                        is ApiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvVideoList.visibility = View.VISIBLE
                            adapter.submitList(state.data)
                        }

                        is ApiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvVideoList.visibility = View.GONE
                            Toast.makeText(this@MainActivity, state.message, Toast.LENGTH_SHORT)
                                .show()

                        }
                    }
                }
            }
        }
    }
}